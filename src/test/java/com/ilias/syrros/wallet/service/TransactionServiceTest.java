package com.ilias.syrros.wallet.service;

import com.ilias.syrros.wallet.advice.AccountException;
import com.ilias.syrros.wallet.converter.AccountDtoConverter;
import com.ilias.syrros.wallet.models.Account;
import com.ilias.syrros.wallet.models.Transaction;
import com.ilias.syrros.wallet.models.TransactionType;
import com.ilias.syrros.wallet.repository.TransactionRepository;
import com.ilias.syrros.wallet.repository.AccountRepository;
import com.ilias.syrros.wallet.service.models.TransactionDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
public class TransactionServiceTest {

    @TestConfiguration
    static class TransactionServiceTestContextConfiguration {

        @Bean
        public TransactionService transactionService() {
            return new TransactionService();
        }
        @Bean
        public MethodValidationPostProcessor methodValidationPostProcessor() {
            return new MethodValidationPostProcessor();
        }
    }

    @Autowired
    private TransactionService transactionService;
    private AccountDtoConverter accountDtoConverter = new AccountDtoConverter();
    @MockBean
    private AccountRepository accountRepository;
    @MockBean
    private TransactionRepository transactionRepository;

    @MockBean
    private AccountService accountService;

    private Account account1;
    private Account account2;
    private Transaction transactionCredit;
    private Transaction transactionDebit;

    @Before
    public void setUp() throws AccountException {

        account1 = new Account(1, UUID.randomUUID().toString(), BigDecimal.ZERO);
        account2 = new Account(2, UUID.randomUUID().toString(), BigDecimal.valueOf(10));
        transactionCredit = new Transaction(TransactionType.DEBIT, BigDecimal.valueOf(20), account1);
        transactionDebit = new Transaction(TransactionType.CREDIT, BigDecimal.valueOf(10), account2);
        createMockito();
    }

    @Test
    public void testGetTransactionsByAccountId_Success() throws AccountException {
        List<TransactionDTO> found = transactionService.getTransactionsByAccountId(account1.getId(), 0, 10);
        assertNotNull(found);
        assertTrue(found.size() == 1);
    }

    @Test
    public void testCreateTransaction_DebitFailure() throws AccountException {

        BigDecimal amount = BigDecimal.valueOf(100);
        Mockito.when(accountService.decreaseAccountAmount(account2.getAccountNumber(),amount)).
                thenThrow(new AccountException());
        Mockito.when(transactionRepository.save(Mockito.any(Transaction.class))).thenReturn(transactionDebit);
    }
    @Test
    public void testCreateTransaction_SuccessCredit() throws AccountException {

        BigDecimal amount = BigDecimal.valueOf(100);
        Mockito.when(accountService.increaseAccountAmount(account1.getAccountNumber(), amount)).thenReturn(accountDtoConverter.convert(account1));
        Mockito.when(transactionRepository.save(Mockito.any(Transaction.class))).thenReturn(transactionCredit);
        TransactionDTO found = transactionService.createTransactionAndChangeBalance(2, amount, account1.getAccountNumber());
        assertNotNull(found);
    }

    private void createMockito(){

        Mockito.when(accountService.findById(account1.getId())).thenReturn(accountDtoConverter.convert(account1));
        Mockito.when(accountService.findByAccountNumber(account1.getAccountNumber())).thenReturn(accountDtoConverter.convert(account1));
        Mockito.when(transactionRepository.findByAccountId(account1.getId(), PageRequest.of(0,10))).thenReturn(Arrays.asList(transactionCredit));
        Mockito.when(transactionRepository.findByAccountId(account2.getId(), PageRequest.of(0, 10))).thenReturn(Arrays.asList(transactionCredit));
        Mockito.when(accountService.findById(account2.getId())).thenReturn(accountDtoConverter.convert(account2));
        Mockito.when(accountService.findByAccountNumber(account2.getAccountNumber())).thenReturn(accountDtoConverter.convert(account2));
        Mockito.when(accountRepository.findByAccountNumber(account1.getAccountNumber())).thenReturn(Optional.of(account1));
    }
}
