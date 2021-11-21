package com.ilias.syrros.wallet.service;

import com.ilias.syrros.wallet.advice.AccountException;
import com.ilias.syrros.wallet.converter.AccountDtoConverter;
import com.ilias.syrros.wallet.models.Account;
import com.ilias.syrros.wallet.repository.TransactionRepository;
import com.ilias.syrros.wallet.repository.AccountRepository;
import com.ilias.syrros.wallet.service.contracts.IAccountService;
import com.ilias.syrros.wallet.service.models.AccountDTO;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@PropertySource("classpath:application.properties")
public class AccountService implements IAccountService {

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;
    private AccountDtoConverter accountDtoConverter = new AccountDtoConverter();

    @Override
    @Transactional(rollbackFor = AccountException.class)
    public List<AccountDTO> findAll() throws AccountException {

        log.info("Getting all accounts");
        return accountRepository.findAllByOrderByIdAsc().stream().map(w -> accountDtoConverter.convert(w)).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = AccountException.class)
    public AccountDTO findById(@NotNull long id) throws AccountException {

        Optional<Account> optionalAccount =  accountRepository.findById(id);
        if (optionalAccount.isPresent()){
            return accountDtoConverter.convert(optionalAccount.get());
        }
        else {
            throw new AccountException(404, "Account Not Found Exception");
        }
    }

    @Override
    @Transactional(rollbackFor = AccountException.class)
    public AccountDTO findByAccountNumber(@NotNull String accountNumber) throws AccountException {

        Optional<Account> optionalAccount = accountRepository.findByAccountNumber(accountNumber);
        if (optionalAccount.isPresent()){
            return accountDtoConverter.convert(optionalAccount.get());
        }
        else {
            throw new AccountException(404, "Account Not Found Exception");
        }
    }

    @Override
    public AccountDTO findByUserId(@NotNull long userId) throws AccountException {

        Account createdAccount = accountRepository.findByUserId(userId);
        if(createdAccount == null) return null;
        return accountDtoConverter.convert(createdAccount);
    }

    @Override
    @Transactional(rollbackFor = AccountException.class)
    public AccountDTO createAccount(@NotNull long userId, String accountNumber, @NotNull BigDecimal balance) throws AccountException {

        try {
            Account existAccount = accountRepository.findByUserId(userId);
            if(existAccount != null)
                throw new AccountException(409, "Account is already exists - createAccount method");
            Account addAccount = new Account(userId, accountNumber == null ? UUID. randomUUID().toString() : accountNumber, balance);
            Account createdAccount = accountRepository.save(addAccount);
            if (createdAccount != null) {
                return accountDtoConverter.convert(createdAccount);
            }
            else {
                throw new AccountException(422, "Account could not be created");
            }
        }
        catch (ObjectNotFoundException e) {
            throw new AccountException(404, "No Suitable Account found");
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = AccountException.class)
    public AccountDTO increaseAccountAmount(@NotNull String accountNumber, @NotNull BigDecimal amount) throws AccountException {

        try {
            Optional<Account> currentAccount = accountRepository.findByAccountNumber(accountNumber);
            if (!currentAccount.isPresent())
                throw new AccountException(404, "addAccountAmount could not work - addAccountAmount method");
            BigDecimal currentBalance = currentAccount.get().getBalance();
            currentAccount.get().setBalance(currentBalance.add(amount));
            currentAccount.get().setLastUpdated(new Date());
            Account updatedAccount = accountRepository.save(currentAccount.get());

            if (updatedAccount != null)
                return accountDtoConverter.convert(updatedAccount);
            else
              throw new AccountException(404, "addAccountAmount could not work - addAccountAmount method");
        }
        catch (Exception ex){
            throw  new AccountException(400, "addAccountAmount method exception");
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.SERIALIZABLE, rollbackFor = AccountException.class)
    public AccountDTO decreaseAccountAmount(@NotNull String accountNumber, @NotNull BigDecimal amount) throws AccountException {

        try{
            Optional<Account> currentAccount = accountRepository.findByAccountNumber(accountNumber);
            if (!currentAccount.isPresent())
                throw new AccountException(404, "addAccountAmount could not work - addAccountAmount method");

            BigDecimal currentBalance = currentAccount.get().getBalance();
            if (currentBalance.compareTo(amount) == -1)
                throw new AccountException(404, "No enough balance for this operation");
            currentAccount.get().setBalance(currentBalance.subtract(amount));
            currentAccount.get().setLastUpdated(new Date());
            Account updatedAccount = accountRepository.save(currentAccount.get());
            if (updatedAccount != null) {
                return accountDtoConverter.convert(updatedAccount);
            }
            else {
                throw new AccountException(404, "addAccountAmount could not work - addAccountAmount method");
            }
        }
        catch (Exception ex) {
            throw  new AccountException(400, "addAccountAmount method exception");
        }
    }
}
