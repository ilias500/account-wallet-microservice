package com.ilias.syrros.wallet.service.contracts;

import com.ilias.syrros.wallet.service.models.AuditDTO;
import org.springframework.scheduling.annotation.Async;

public interface IAuditService {

    @Async
    AuditDTO createAuditEntry (AuditDTO auditDTO);
}
