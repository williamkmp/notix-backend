package com.william.notix.entities;

import com.william.notix.utils.values.CVSS_VALUE;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@Embeddable
public class Cvss {

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "AV", nullable = false)
    private CVSS_VALUE AV = CVSS_VALUE.C0;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "AC", nullable = false)
    private CVSS_VALUE AC = CVSS_VALUE.C0;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "AT", nullable = false)
    private CVSS_VALUE AT = CVSS_VALUE.C0;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "PR", nullable = false)
    private CVSS_VALUE PR = CVSS_VALUE.C0;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "UI", nullable = false)
    private CVSS_VALUE UI = CVSS_VALUE.C0;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "VC", nullable = false)
    private CVSS_VALUE VC = CVSS_VALUE.C2;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "VI", nullable = false)
    private CVSS_VALUE VI = CVSS_VALUE.C2;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "VA", nullable = false)
    private CVSS_VALUE VA = CVSS_VALUE.C2;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "SC", nullable = false)
    private CVSS_VALUE SC = CVSS_VALUE.C2;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "SI", nullable = false)
    private CVSS_VALUE SI = CVSS_VALUE.C2;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "SA", nullable = false)
    private CVSS_VALUE SA = CVSS_VALUE.C2;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "S", nullable = false)
    private CVSS_VALUE S = CVSS_VALUE.C0;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "AU", nullable = false)
    private CVSS_VALUE AU = CVSS_VALUE.C0;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "R", nullable = false)
    private CVSS_VALUE R = CVSS_VALUE.C0;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "V", nullable = false)
    private CVSS_VALUE V = CVSS_VALUE.C0;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "RE", nullable = false)
    private CVSS_VALUE RE = CVSS_VALUE.C0;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "U", nullable = false)
    private CVSS_VALUE U = CVSS_VALUE.C0;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "MAV", nullable = false)
    private CVSS_VALUE MAV = CVSS_VALUE.C0;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "MAC", nullable = false)
    private CVSS_VALUE MAC = CVSS_VALUE.C0;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "MAT", nullable = false)
    private CVSS_VALUE MAT = CVSS_VALUE.C0;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "MPR", nullable = false)
    private CVSS_VALUE MPR = CVSS_VALUE.C0;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "MUI", nullable = false)
    private CVSS_VALUE MUI = CVSS_VALUE.C0;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "MVC", nullable = false)
    private CVSS_VALUE MVC = CVSS_VALUE.C0;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "MVI", nullable = false)
    private CVSS_VALUE MVI = CVSS_VALUE.C0;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "MVA", nullable = false)
    private CVSS_VALUE MVA = CVSS_VALUE.C0;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "MSC", nullable = false)
    private CVSS_VALUE MSC = CVSS_VALUE.C0;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "MSI", nullable = false)
    private CVSS_VALUE MSI = CVSS_VALUE.C0;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "MSA", nullable = false)
    private CVSS_VALUE MSA = CVSS_VALUE.C0;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "CR", nullable = false)
    private CVSS_VALUE CR = CVSS_VALUE.C0;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "IR", nullable = false)
    private CVSS_VALUE IR = CVSS_VALUE.C0;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "AR", nullable = false)
    private CVSS_VALUE AR = CVSS_VALUE.C0;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "E", nullable = false)
    private CVSS_VALUE E = CVSS_VALUE.C0;
}
