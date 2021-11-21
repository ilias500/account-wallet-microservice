package com.ilias.syrros.wallet.controller;

import com.ilias.syrros.wallet.advice.RateLimiterException;
import com.ilias.syrros.wallet.advice.AccountException;
import com.ilias.syrros.wallet.annotation.Audit;
import com.ilias.syrros.wallet.aspect.RateLimit;
import com.ilias.syrros.wallet.converter.AccountDtoConverter;
import com.ilias.syrros.wallet.service.contracts.IAccountService;
import com.ilias.syrros.wallet.service.models.AccountDTO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/api/v1")
public class AccountController {

    Logger logger = LoggerFactory.getLogger(AccountController.class);
    @Autowired
    private IAccountService accountService;
    private AccountDtoConverter accountDtoConverter = new AccountDtoConverter();

    @GetMapping("/account")
    @RateLimit(limit = 3)
    @Audit
    @ApiOperation(value = "Find all accounts", notes = "Returns a collection of accounts")
    public ResponseEntity<?> get() throws RateLimiterException {

        logger.info("AccountController get method calls for getting all accounts");

        List<AccountDTO> accounts = accountService.findAll();

        if (!CollectionUtils.isEmpty(accounts)){
            return ResponseEntity.ok().body(
                    accounts
                    .stream()
                    .collect(Collectors.toList())
            );
        }
        else {
            throw new AccountException(404, "Accounts not found");
        }
    }

    @GetMapping("/account/{accountNumber}")
    @RateLimit(limit = 3)
    @Audit
    @ApiOperation(value = "Find account of a given accountNumber", notes = "Returns a account by given accountNumber")
    public ResponseEntity<AccountDTO> getByAccountNumber(@PathVariable("accountNumber") String accountNumber) throws RateLimiterException{

        logger.info("accountController getByAccountNumber method calls for getting account from accountNumber");

        return Optional.ofNullable(accountService.findByAccountNumber(accountNumber))
                .map(account -> new ResponseEntity<>(account, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/user/{userId}/account")
    @RateLimit(limit = 3)
    @Audit
    @ApiOperation(value = "Find account of a given userId", notes = "Returns a account by given userId")
    public ResponseEntity<?> getByUserId(@PathVariable("userId") long userId) throws RateLimiterException{

        logger.info("accountController getByUserId method calls for getting account from userId");

        return Optional.ofNullable(accountService.findByUserId(userId))
                .map(account -> new ResponseEntity<>(account, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping("/user/{userId}/account/create")
    @RateLimit(limit = 3)
    @Audit
    @ApiOperation(value = "Create account of a given user", notes = "Create a account and return created account")
    public ResponseEntity<?> create(@PathVariable("userId") long userId, @RequestBody AccountDTO account) throws RateLimiterException{

        logger.info("accountController create method calls for creating account");
        BigDecimal balance = account.getBalance();
        String accountNumber = account.getAccountNumber();
        if (balance.signum() == -1)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        AccountDTO createdAccount;

        try{
            createdAccount = accountService.createAccount(userId, accountNumber, balance);
            logger.info("accountController create method created account");
        }
        catch (AccountException exc){
            logger.error("accountController create method has an error");
            throw new AccountException(HttpStatus.UNPROCESSABLE_ENTITY.value(), exc.getDetail());

            //return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
    }
}
