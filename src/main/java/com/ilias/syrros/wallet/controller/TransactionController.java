package com.ilias.syrros.wallet.controller;

import com.ilias.syrros.wallet.advice.ErrorResponse;
import com.ilias.syrros.wallet.advice.RateLimiterException;
import com.ilias.syrros.wallet.advice.AccountException;
import com.ilias.syrros.wallet.aspect.RateLimit;
import com.ilias.syrros.wallet.service.contracts.ITransactionService;
import com.ilias.syrros.wallet.service.contracts.IAccountService;
import com.ilias.syrros.wallet.service.models.TransactionDTO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/api/v1")
public class TransactionController {

    Logger logger = LoggerFactory.getLogger(TransactionController.class);

    @Autowired
    private ITransactionService transactionService;
    @Autowired
    private IAccountService accountService;

    @GetMapping("/accounts/{accountNumber}/transactions")
    @RateLimit(limit = 3)
    @ApiOperation(value = "Find all transactions by given account number", notes = "Returns a collection of transactions by given account number")
    public ResponseEntity<?> getByAccountNumber(@PathVariable("accountNumber") String accountNumber,
                                            @RequestParam(defaultValue = "0") Integer pageNumber,
                                            @RequestParam(defaultValue = "10") Integer pageSize) throws RateLimiterException {

        logger.info("TransactionController getByAccountNumber method calls for getting all transactions");

        List<TransactionDTO> transactions = transactionService.getTransactionsByAccountNumber(accountNumber, pageNumber, pageSize);

        if(!transactions.isEmpty() && transactions.size() > 0){
            return ResponseEntity.ok().body(
                    transactions
                            .stream()
                            .collect(Collectors.toList())
            );
        }
        else
            throw new AccountException(404, "Transactions not found");
    }

    @GetMapping("/accounts/{accountNumber}/transactions/dates")
    @RateLimit(limit = 3)
    @ApiOperation(value = "Find all transactions by given account in dates ranges", notes = "Returns a collection of transactions by given accountId and range dates")
    public ResponseEntity<?> getByAccountNumberAndRangeDates(@PathVariable("accountNumber") String accountNumber,
                                                         @RequestParam("dateFrom")
                                                         @DateTimeFormat(pattern = "dd.MM.yyyy") Date dateFrom,
                                                         @RequestParam("dateTo")
                                                         @DateTimeFormat(pattern = "dd.MM.yyyy") Date dateTo,
                                                         @RequestParam(defaultValue = "0") Integer pageNumber,
                                                         @RequestParam(defaultValue = "10") Integer pageSize) throws RateLimiterException {

        logger.info("TransactionController getByAccountId method calls for getting all transactions");
        LocalDateTime from = LocalDateTime.ofInstant(dateFrom.toInstant(), ZoneId.systemDefault());
        LocalDateTime to = LocalDateTime.ofInstant(dateTo.toInstant(), ZoneId.systemDefault());
        List<TransactionDTO> transactions = transactionService.getTransactionsByAccountNumberAndRangDates(accountNumber, from, to, pageNumber, pageSize);

        if(!transactions.isEmpty() && transactions.size() > 0){
            return ResponseEntity.ok().body(
                    transactions
                            .stream()
                            .collect(Collectors.toList())
            );
        }
        else
            throw new AccountException(404, "Transactions not found");
    }

    @PostMapping("/transaction")
    @RateLimit(limit = 3)
    @ApiOperation(value = "Add transaction of a given account and adjust balance", notes = "Create transaction and adjust balance")
    public ResponseEntity<?> creditOrDebit(@RequestBody TransactionDTO transaction) throws RateLimiterException{

        logger.info("TransactionController add method is calling");

        int typeId = transaction.getTypeId();
        BigDecimal amount = transaction.getAmount();
        String accountNumber = transaction.getAccountNumber();

        if (typeId != 1 && typeId != 2)
            return new ResponseEntity<>(new ErrorResponse("TypeId should be debit or credit", 400), HttpStatus.BAD_REQUEST);
        if (amount.signum() == -1)
            return new ResponseEntity<>(new ErrorResponse("Balance should not be negative", 400), HttpStatus.BAD_REQUEST);
        if (typeId == 1 && accountService.findByAccountNumber(transaction.getAccountNumber()).getBalance().compareTo(transaction.getAmount()) == -1)
            return new ResponseEntity<>(new ErrorResponse("There is no enough balance", 400), HttpStatus.BAD_REQUEST);

        TransactionDTO createdTransaction;
        try {
            createdTransaction = transactionService.createTransactionAndChangeBalance(typeId, amount, accountNumber);
            logger.info("TransactionController created transaction = " + createdTransaction.getTransactionId());
        }
        catch (AccountException exc){
            logger.error("TransactionController creditOrDebit method has an error");
            return new ResponseEntity<>(HttpStatus.UNPROCESSABLE_ENTITY);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

}
