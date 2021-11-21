package com.ilias.syrros.wallet.repository;

import com.ilias.syrros.wallet.advice.AccountException;
import com.ilias.syrros.wallet.models.Account;
import com.ilias.syrros.wallet.models.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@Transactional(rollbackOn = AccountException.class)
public interface TransactionRepository extends PagingAndSortingRepository<Transaction, Long> {

    List<Transaction> findByAccountId(long accountId, Pageable pageable);

    List<Transaction> findByAccountAccountNumber(String accountNumber, Pageable pageable);

    List<Transaction> findByAccountIdAndLastUpdatedBetween(
            @Param("account") long accountId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate, Pageable pageable);

    List<Transaction> findByAccountAccountNumberAndLastUpdatedBetween(
            @Param("accountNumber") String accountNumber,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate, Pageable pageable);
}
