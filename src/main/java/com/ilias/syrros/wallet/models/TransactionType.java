package com.ilias.syrros.wallet.models;

import java.util.stream.Stream;

public enum TransactionType {
    DEBIT(1), CREDIT(2);

    private final int type;
    TransactionType(int type) {
        this.type = type;
    }
    public int getType() {
        return type;
    }

    public static TransactionType of(int type) {
        return Stream.of(TransactionType.values())
                .filter(p -> p.getType() == type)
                .findFirst()
                .orElseThrow(IllegalArgumentException::new);
    }
}
