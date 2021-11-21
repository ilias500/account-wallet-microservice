package com.ilias.syrros.wallet.service.models;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Setter
@Getter
@Builder
public class AccountDTO {

    private long id;
    private long userId;
    private String accountNumber;
    private BigDecimal balance;
    private Date lastUpdated;

}
