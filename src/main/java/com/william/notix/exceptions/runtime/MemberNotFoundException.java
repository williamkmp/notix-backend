package com.william.notix.exceptions.runtime;

import lombok.Getter;

@Getter
public class MemberNotFoundException extends RuntimeException {

    private Long userId;

    public MemberNotFoundException() {
        super("Project member not found");
    }

    public MemberNotFoundException(Long userId) {
        super("Project member not found userId: " + userId.toString());
        this.userId = userId;
    }
}
