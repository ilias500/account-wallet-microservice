package com.ilias.syrros.wallet.models;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Setter
@Getter
@Entity
@NoArgsConstructor
@Table(name = "auditAction")
public class AuditEntity {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Lob
    @Column(name = "header")
    private String requestHeader;

    @Lob
    @Column(name = "req_payload")
    private String requestPayload;

    @Lob
    @Column(name = "resp_payload")
    private String responsePayload;

    @Column(name = "req_method")
    private String requestMethod;

    @Column(name = "url_path")
    private String urlPath;

    @Column(name = "call_duration")
    private long callDuration;

    @Column(name = "created_date")
    private LocalDateTime createdAuditDate;

}
