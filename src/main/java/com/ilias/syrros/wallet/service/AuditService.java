package com.ilias.syrros.wallet.service;

import com.ilias.syrros.wallet.converter.AuditDTOConverter;
import com.ilias.syrros.wallet.models.AuditEntity;
import com.ilias.syrros.wallet.repository.AuditRepository;
import com.ilias.syrros.wallet.service.contracts.IAuditService;
import com.ilias.syrros.wallet.service.models.AuditDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
@PropertySource("classpath:application.properties")
public class AuditService implements IAuditService {
    @Autowired
    private AuditRepository auditRepository;
    private AuditDTOConverter auditDtoConverter = new AuditDTOConverter();


    @Override
    public AuditDTO createAuditEntry(AuditDTO auditDTO) {
        AuditEntity auditEntity = new AuditEntity();
                auditEntity.setRequestHeader(auditDTO.getRequestHeader());
                auditEntity.setRequestMethod(auditDTO.getRequestMethod());
                auditEntity.setRequestPayload(auditDTO.getRequestPayload());
                auditEntity.setResponsePayload(auditDTO.getResponsePayload());
                auditEntity.setUrlPath(auditDTO.getUrlPath());
                auditEntity.setCallDuration(auditDTO.getCallDuration());
                auditEntity.setCreatedAuditDate(LocalDateTime.now());
        AuditEntity createdAuditEntity = auditRepository.save(auditEntity);
        return auditDtoConverter.convert(createdAuditEntity);
    }
}
