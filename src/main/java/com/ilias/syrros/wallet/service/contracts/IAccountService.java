package com.ilias.syrros.wallet.service.contracts;

import com.ilias.syrros.wallet.advice.AccountException;
import com.ilias.syrros.wallet.service.models.AccountDTO;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public interface IAccountService {

    List<AccountDTO> findAll() throws AccountException;

    AccountDTO findById(@NotNull long id) throws AccountException;

    AccountDTO findByAccountNumber(@NotNull String accountNumber) throws AccountException;

    AccountDTO findByUserId(@NotNull long userId) throws AccountException;

    AccountDTO createAccount(@NotNull long userId, String accountNumber, @NotNull BigDecimal balance) throws AccountException;

    AccountDTO increaseAccountAmount(@NotNull String accountNumber, @NotNull BigDecimal amount) throws AccountException;

    AccountDTO decreaseAccountAmount(@NotNull String accountNumber, @NotNull BigDecimal amount) throws AccountException;
}
