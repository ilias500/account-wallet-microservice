package com.ilias.syrros.wallet.service.models;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
@Builder
public class AuditDTO {

    private long callDuration;
    private String requestPayload;
    private String responsePayload;
    private String requestHeader;
    private String requestMethod;
    private String urlPath;

}
