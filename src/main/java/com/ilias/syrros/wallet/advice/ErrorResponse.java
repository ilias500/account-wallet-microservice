package com.ilias.syrros.wallet.advice;

import lombok.*;

@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
public class ErrorResponse {

    private String exception;
    private int code;

    public ErrorResponse(String exception, int code){
        this.exception = exception;
        this.code = code;
    }
}
