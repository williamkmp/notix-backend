package com.william.notix.entities;

import com.william.notix.utils.values.CVSS_VALUE;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@Entity(name = "cvss_details")
@Table(name = "cvss_details")
public class CvssDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(mappedBy = "cvssDetail")
    private Finding finding;

    @Enumerated(EnumType.STRING)
    @Column(name = "AV", nullable = false)
    private CVSS_VALUE AV = CVSS_VALUE.C0;

    @Enumerated(EnumType.STRING)
    @Column(name = "AC", nullable = false)
    private CVSS_VALUE AC = CVSS_VALUE.C0;

    @Enumerated(EnumType.STRING)
    @Column(name = "AT", nullable = false)
    private CVSS_VALUE AT = CVSS_VALUE.C0;

    @Enumerated(EnumType.STRING)
    @Column(name = "PR", nullable = false)
    private CVSS_VALUE PR = CVSS_VALUE.C0;

    @Enumerated(EnumType.STRING)
    @Column(name = "UI", nullable = false)
    private CVSS_VALUE UI = CVSS_VALUE.C0;

    @Enumerated(EnumType.STRING)
    @Column(name = "VC", nullable = false)
    private CVSS_VALUE VC = CVSS_VALUE.C2;

    @Enumerated(EnumType.STRING)
    @Column(name = "VI", nullable = false)
    private CVSS_VALUE VI = CVSS_VALUE.C2;

    @Enumerated(EnumType.STRING)
    @Column(name = "VA", nullable = false)
    private CVSS_VALUE VA = CVSS_VALUE.C2;

    @Enumerated(EnumType.STRING)
    @Column(name = "SC", nullable = false)
    private CVSS_VALUE SC = CVSS_VALUE.C2;

    @Enumerated(EnumType.STRING)
    @Column(name = "SI", nullable = false)
    private CVSS_VALUE SI = CVSS_VALUE.C2;

    @Enumerated(EnumType.STRING)
    @Column(name = "SA", nullable = false)
    private CVSS_VALUE SA = CVSS_VALUE.C2;

    @Enumerated(EnumType.STRING)
    @Column(name = "S", nullable = false)
    private CVSS_VALUE S = CVSS_VALUE.C0;

    @Enumerated(EnumType.STRING)
    @Column(name = "AU", nullable = false)
    private CVSS_VALUE AU = CVSS_VALUE.C0;

    @Enumerated(EnumType.STRING)
    @Column(name = "R", nullable = false)
    private CVSS_VALUE R = CVSS_VALUE.C0;

    @Enumerated(EnumType.STRING)
    @Column(name = "V", nullable = false)
    private CVSS_VALUE V = CVSS_VALUE.C0;

    @Enumerated(EnumType.STRING)
    @Column(name = "RE", nullable = false)
    private CVSS_VALUE RE = CVSS_VALUE.C0;

    @Enumerated(EnumType.STRING)
    @Column(name = "U", nullable = false)
    private CVSS_VALUE U = CVSS_VALUE.C0;

    @Enumerated(EnumType.STRING)
    @Column(name = "MAV", nullable = false)
    private CVSS_VALUE MAV = CVSS_VALUE.C0;

    @Enumerated(EnumType.STRING)
    @Column(name = "MAC", nullable = false)
    private CVSS_VALUE MAC = CVSS_VALUE.C0;

    @Enumerated(EnumType.STRING)
    @Column(name = "MAT", nullable = false)
    private CVSS_VALUE MAT = CVSS_VALUE.C0;

    @Enumerated(EnumType.STRING)
    @Column(name = "MPR", nullable = false)
    private CVSS_VALUE MPR = CVSS_VALUE.C0;

    @Enumerated(EnumType.STRING)
    @Column(name = "MUI", nullable = false)
    private CVSS_VALUE MUI = CVSS_VALUE.C0;

    @Enumerated(EnumType.STRING)
    @Column(name = "MVC", nullable = false)
    private CVSS_VALUE MVC = CVSS_VALUE.C0;

    @Enumerated(EnumType.STRING)
    @Column(name = "MVI", nullable = false)
    private CVSS_VALUE MVI = CVSS_VALUE.C0;

    @Enumerated(EnumType.STRING)
    @Column(name = "MVA", nullable = false)
    private CVSS_VALUE MVA = CVSS_VALUE.C0;

    @Enumerated(EnumType.STRING)
    @Column(name = "MSC", nullable = false)
    private CVSS_VALUE MSC = CVSS_VALUE.C0;

    @Enumerated(EnumType.STRING)
    @Column(name = "MSI", nullable = false)
    private CVSS_VALUE MSI = CVSS_VALUE.C0;

    @Enumerated(EnumType.STRING)
    @Column(name = "MSA", nullable = false)
    private CVSS_VALUE MSA = CVSS_VALUE.C0;

    @Enumerated(EnumType.STRING)
    @Column(name = "CR", nullable = false)
    private CVSS_VALUE CR = CVSS_VALUE.C0;

    @Enumerated(EnumType.STRING)
    @Column(name = "IR", nullable = false)
    private CVSS_VALUE IR = CVSS_VALUE.C0;

    @Enumerated(EnumType.STRING)
    @Column(name = "AR", nullable = false)
    private CVSS_VALUE AR = CVSS_VALUE.C0;

    @Enumerated(EnumType.STRING)
    @Column(name = "E", nullable = false)
    private CVSS_VALUE E = CVSS_VALUE.C0;
}
