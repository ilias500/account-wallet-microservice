package com.ilias.syrros.wallet.repository;

import com.ilias.syrros.wallet.advice.AccountException;
import com.ilias.syrros.wallet.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
@Transactional(rollbackOn = AccountException.class)
public interface AccountRepository extends JpaRepository<Account, Long> {

    List<Account> findAllByOrderByIdAsc();

    Optional<Account> findByAccountNumber(String accountNumber);

    Account findByUserId(long userId);

}
