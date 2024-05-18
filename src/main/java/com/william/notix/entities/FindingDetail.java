package com.william.notix.entities;

import com.william.notix.utils.values.FINDING_CATEGORY;
import com.william.notix.utils.values.FINDING_IMPACT;
import com.william.notix.utils.values.FINDING_LOCATION;
import com.william.notix.utils.values.FINDING_METHOD;
import com.william.notix.utils.values.FINDING_PROBABILITY;
import com.william.notix.utils.values.FINDING_STATUS;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Embeddable
public class FindingDetail {

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private FINDING_STATUS status = FINDING_STATUS.NOT_RETESTED;

    @Column(name = "category", nullable = true)
    @Enumerated(EnumType.STRING)
    private FINDING_CATEGORY category;

    @Column(name = "location", nullable = true)
    @Enumerated(EnumType.STRING)
    private FINDING_LOCATION location;

    @Column(name = "method", nullable = true)
    @Enumerated(EnumType.STRING)
    private FINDING_METHOD method;

    @Column(name = "probability", nullable = true)
    @Enumerated(EnumType.ORDINAL)
    private FINDING_PROBABILITY probability;

    @Column(name = "impact", nullable = true)
    @Enumerated(EnumType.ORDINAL)
    private FINDING_IMPACT impact;
}
