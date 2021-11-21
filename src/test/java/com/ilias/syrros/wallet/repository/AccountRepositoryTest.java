package com.ilias.syrros.wallet.repository;

import com.ilias.syrros.wallet.models.Account;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class AccountRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private AccountRepository accountRepository;
    private Account account1;
    private Account account2;

    @Before
    public void before(){

        account1 = new Account(1, UUID.randomUUID().toString(), BigDecimal.valueOf(10));
        account2 = new Account(2, UUID.randomUUID().toString(), BigDecimal.valueOf(20));
        entityManager.persist(account1);
        entityManager.persist(account2);
        entityManager.flush();
    }

    @Test
    public void testFindByAccountIdIfAccount() {

        Optional<Account> account = accountRepository.findById(account1.getId());
        assertTrue(account.isPresent());
        assertEquals(account.get().getBalance(), account1.getBalance());
        assertEquals(account.get().getUserId(), account1.getUserId());
    }

    @Test
    public void testFindByAccountIdIfNotExistAccount() {

        long accountId = 100;
        Optional<Account> found = accountRepository.findById(accountId);
        assertTrue(!found.isPresent());
    }

    @Test
    public void testFindByUserIdIfAccount() {

        long userId = 1;
        Account account = accountRepository.findByUserId(userId);
        assertEquals(account.getUserId(),1);
        assertEquals(account.getBalance(), BigDecimal.valueOf(10));
    }

    @Test
    public void testUpdateBalance() {

        Optional<Account> found = accountRepository.findById(account1.getId());
        Account updated = found.get();
        updated.setBalance(BigDecimal.valueOf(100));
        Account found1 = accountRepository.save(updated);
        assertNotNull(found1);
        assertEquals(found1.getBalance(), BigDecimal.valueOf(100));
    }

    @After
    public void after(){
    }

}
