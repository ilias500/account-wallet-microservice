package com.ilias.syrros.wallet.converter;

import com.ilias.syrros.wallet.models.AuditEntity;
import com.ilias.syrros.wallet.service.models.AuditDTO;
import org.springframework.core.convert.converter.Converter;

public class AuditDTOConverter implements Converter<AuditEntity, AuditDTO> {
    @Override
    public AuditDTO convert(AuditEntity auditEntity) {

        return AuditDTO.builder()
                .requestHeader(auditEntity.getRequestHeader())
                .requestMethod(auditEntity.getRequestMethod())
                .requestPayload(auditEntity.getRequestPayload())
                .responsePayload(auditEntity.getResponsePayload())
                .urlPath(auditEntity.getUrlPath())
                .callDuration(auditEntity.getCallDuration())
                .build();
    }
}
