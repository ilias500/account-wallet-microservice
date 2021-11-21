package com.ilias.syrros.wallet.service.models;

import lombok.*;

import java.math.BigDecimal;

@Data
@Setter
@Getter
@Builder
@NoArgsConstructor
public class TransactionDTO {

    private long transactionId;
    private int typeId;
    private String transactionType;
    private BigDecimal amount;
    private String accountNumber;

    public TransactionDTO(long transactionId, int typeId, String transactionType, BigDecimal amount, String accountNumber) {
        this.transactionId = transactionId;
        this.typeId = typeId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.accountNumber = accountNumber;
    }
}
