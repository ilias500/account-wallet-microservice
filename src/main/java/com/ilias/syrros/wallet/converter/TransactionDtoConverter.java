package com.ilias.syrros.wallet.converter;

import com.ilias.syrros.wallet.models.Transaction;
import com.ilias.syrros.wallet.service.models.TransactionDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class TransactionDtoConverter implements Converter<Transaction, TransactionDTO> {

    @Override
    public TransactionDTO convert(Transaction transaction) {

        return TransactionDTO.builder()
                .transactionId(transaction.getId())
                .accountNumber(transaction.getAccount().getAccountNumber())
                .amount(transaction.getAmount())
                .transactionType(transaction.getTransactionType().name())
                .build();
    }
}
