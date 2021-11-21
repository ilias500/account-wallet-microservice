package com.ilias.syrros.wallet.advice;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class AccountException extends RuntimeException {

    private String detail;
    private int code;

    public AccountException(int code, String detail) {
        this.detail = detail;
        this.code = code;
    }
}
