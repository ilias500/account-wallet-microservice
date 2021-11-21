package com.ilias.syrros.wallet.repository;

import com.ilias.syrros.wallet.models.AuditEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditRepository extends JpaRepository<AuditEntity, Long> {

}
