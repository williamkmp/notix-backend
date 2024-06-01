package com.william.notix.entities;

import com.william.notix.utils.values.FINDING_CATEGORY;
import com.william.notix.utils.values.FINDING_IMPACT;
import com.william.notix.utils.values.FINDING_LIKELIHOOD;
import com.william.notix.utils.values.FINDING_LOCATION;
import com.william.notix.utils.values.FINDING_METHOD;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@Accessors(chain = true)
@Embeddable
public class FindingDetail {

    @Column(name = "is_informational", nullable = true)
    private boolean isInformational = false;

    @Column(name = "category", nullable = true)
    @Enumerated(EnumType.STRING)
    private FINDING_CATEGORY category;

    @Column(name = "location", nullable = true)
    @Enumerated(EnumType.STRING)
    private FINDING_LOCATION location;

    @Column(name = "method", nullable = true)
    @Enumerated(EnumType.STRING)
    private FINDING_METHOD method;

    @Column(name = "likelihood", nullable = true)
    @Enumerated(EnumType.ORDINAL)
    private FINDING_LIKELIHOOD likelihood;

    @Column(name = "impact", nullable = true)
    @Enumerated(EnumType.ORDINAL)
    private FINDING_IMPACT impact;
}
