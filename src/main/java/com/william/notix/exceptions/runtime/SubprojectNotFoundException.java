package com.william.notix.exceptions.runtime;

import lombok.Getter;

@Getter
public class SubprojectNotFoundException extends RuntimeException {

    private Long subprojectId;

    public SubprojectNotFoundException() {
        super("Subproject not found");
    }

    public SubprojectNotFoundException(Long subprojectId) {
        super("Subproject not found id: " + subprojectId.toString());
        this.subprojectId = subprojectId;
    }
}
