package com.ilias.syrros.wallet.converter;

import com.ilias.syrros.wallet.models.Account;
import com.ilias.syrros.wallet.service.models.AccountDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class AccountDtoConverter implements Converter<Account, AccountDTO> {

    @Override
    public AccountDTO convert(Account account) {

        return AccountDTO.builder()
                .id(account.getId())
                .userId(account.getUserId())
                .accountNumber(account.getAccountNumber())
                .balance(account.getBalance())
                .lastUpdated(account.getLastUpdated())
                .build();
    }
}
