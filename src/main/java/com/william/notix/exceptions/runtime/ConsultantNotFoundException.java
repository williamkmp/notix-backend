package com.william.notix.exceptions.runtime;

import lombok.Getter;

@Getter
public class ConsultantNotFoundException extends RuntimeException {

    private Long userId;

    public ConsultantNotFoundException() {
        super("Consultant not found");
    }

    public ConsultantNotFoundException(Long userId) {
        super("Consultantt not found userId: " + userId.toString());
        this.userId = userId;
    }
}
