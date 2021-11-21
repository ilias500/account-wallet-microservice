package com.ilias.syrros.wallet.service;

import com.ilias.syrros.wallet.advice.AccountException;
import com.ilias.syrros.wallet.models.Account;
import com.ilias.syrros.wallet.repository.TransactionRepository;
import com.ilias.syrros.wallet.repository.AccountRepository;
import com.ilias.syrros.wallet.service.models.AccountDTO;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
public class AccountServiceTest {

    @TestConfiguration
    static class AccountServiceTestContextConfiguration {

        @Bean
        public AccountService accountService() {
            return new AccountService();
        }
        @Bean
        public MethodValidationPostProcessor methodValidationPostProcessor() {
            return new MethodValidationPostProcessor();
        }
    }

    @Autowired
    private AccountService accountService;
    @MockBean
    private AccountRepository accountRepository;
    @MockBean
    private TransactionRepository transactionRepository;
    Account account1;
    Account account2;

    @Before
    public void setUp() {

        account1 = new Account(1, UUID.randomUUID().toString(), BigDecimal.valueOf(10));
        account2 = new Account(2, UUID.randomUUID().toString(), BigDecimal.valueOf(20));
        createMockito();
    }

    @Test
    public void testFindAll() throws AccountException {

        List<AccountDTO> account = accountService.findAll();
        assertNotNull(account);
        assertEquals(account.get(0).getId(), account1.getId());
        assertEquals(account.get(1).getId(), account2.getId());
    }

    @Test
    public void testFindByAccountNumber() throws AccountException {

        AccountDTO account = accountService.findByAccountNumber(account1.getAccountNumber());
        assertNotNull(account);
        assertEquals(account.getId(), account1.getId());
    }

    @Test
    public void testUpdateBalance() throws AccountException {

        AccountDTO account = accountService.increaseAccountAmount(account1.getAccountNumber(), BigDecimal.valueOf(30));
        assertEquals(account.getId(), account1.getId());
        assertEquals(account.getBalance(), BigDecimal.valueOf(40));
    }

    @Test
    public void testUpdateBalanceDebitSuccess() throws AccountException {

        AccountDTO account = accountService.decreaseAccountAmount(account1.getAccountNumber(), BigDecimal.valueOf(10));
        assertEquals(account.getUserId(), account1.getUserId());
        assertEquals(account.getBalance(), BigDecimal.ZERO);
    }

    @Test
    public void testUpdateBalanceDebitFailure() throws AccountException {

        BigDecimal amount = BigDecimal.valueOf(100);
        try {
            accountService.decreaseAccountAmount(account2.getAccountNumber(), amount);
            fail();
        } catch (AccountException ex){
            assertEquals(ex.getCode(),400);
        }
    }

    private void createMockito(){

        Mockito.when(accountRepository.findAllByOrderByIdAsc()).thenReturn(Arrays.asList(account1, account2));
        Mockito.when(accountRepository.findByAccountNumber(account1.getAccountNumber())).thenReturn(Optional.of(account1));
        Mockito.when(accountRepository.findByAccountNumber(account2.getAccountNumber())).thenReturn(Optional.of(account2));
        Mockito.when(accountRepository.findByUserId(1)).thenReturn(account1);
        Mockito.when(accountRepository.save(account1)).thenReturn(account1);
    }
}
