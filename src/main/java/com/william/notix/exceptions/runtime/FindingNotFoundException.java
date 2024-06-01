package com.william.notix.exceptions.runtime;

import lombok.Getter;

@Getter
public class FindingNotFoundException extends RuntimeException {

    private Long findingId;

    public FindingNotFoundException() {
        super("Finding not found");
    }

    public FindingNotFoundException(Long findingId) {
        super("Finding not found id: " + findingId.toString());
        this.findingId = findingId;
    }
}
