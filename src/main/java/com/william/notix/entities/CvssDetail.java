package com.william.notix.entities;

import com.william.notix.utils.values.CVSS_ATTACK_COMPLEXITY;
import com.william.notix.utils.values.CVSS_ATTACK_REQUIREMENTS;
import com.william.notix.utils.values.CVSS_ATTACK_VECTOR;
import com.william.notix.utils.values.CVSS_PRIVILEGES_REQUIRED;
import com.william.notix.utils.values.CVSS_SUBSEQUENT_AVAILABILITY;
import com.william.notix.utils.values.CVSS_SUBSEQUENT_CONFIDENTIALITY;
import com.william.notix.utils.values.CVSS_SUBSEQUENT_INTEGRITY;
import com.william.notix.utils.values.CVSS_USER_INTERACTION;
import com.william.notix.utils.values.CVSS_VULNERABLE_AVAILABILITY;
import com.william.notix.utils.values.CVSS_VULNERABLE_CONFIDENTIALITY;
import com.william.notix.utils.values.CVSS_VULNERABLE_INTEGRITY;

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
    @Column(name = "attack_vector", nullable = false)
    private CVSS_ATTACK_VECTOR attackVector = CVSS_ATTACK_VECTOR.NETWORK;

    @Enumerated(EnumType.STRING)
    @Column(name = "attack_complexity", nullable = false)
    private CVSS_ATTACK_COMPLEXITY attackComplexity = CVSS_ATTACK_COMPLEXITY.LOW;

    @Enumerated(EnumType.STRING)
    @Column(name = "attack_requirements", nullable = false)
    private CVSS_ATTACK_REQUIREMENTS attackRequirements = CVSS_ATTACK_REQUIREMENTS.NONE;

    @Enumerated(EnumType.STRING)
    @Column(name = "privileges_required", nullable = false)
    private CVSS_PRIVILEGES_REQUIRED privilegesRequired = CVSS_PRIVILEGES_REQUIRED.NONE;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_interaction", nullable = false)
    private CVSS_USER_INTERACTION userInteraction = CVSS_USER_INTERACTION.NONE;

    @Enumerated(EnumType.STRING)
    @Column(name = "vulnerable_confidentiality", nullable = false)
    private CVSS_VULNERABLE_CONFIDENTIALITY vulnerableConfidentiality = CVSS_VULNERABLE_CONFIDENTIALITY.NONE;

    @Enumerated(EnumType.STRING)
    @Column(name = "vulnerable_integrity", nullable = false)
    private CVSS_VULNERABLE_INTEGRITY vulnerableIntegrity = CVSS_VULNERABLE_INTEGRITY.NONE;

    @Enumerated(EnumType.STRING)
    @Column(name = "vulnerable_availability", nullable = false)
    private CVSS_VULNERABLE_AVAILABILITY vulnerableAvailability = CVSS_VULNERABLE_AVAILABILITY.NONE;

    @Enumerated(EnumType.STRING)
    @Column(name = "subsequent_confidentiality", nullable = false)
    private CVSS_SUBSEQUENT_CONFIDENTIALITY subsequentConfidentiality = CVSS_SUBSEQUENT_CONFIDENTIALITY.NONE;

    @Enumerated(EnumType.STRING)
    @Column(name = "subsequent_integrity", nullable = false)
    private CVSS_SUBSEQUENT_INTEGRITY subsequentIntegrity = CVSS_SUBSEQUENT_INTEGRITY.NONE;

    @Enumerated(EnumType.STRING)
    @Column(name = "subsequent_availability", nullable = false)
    private CVSS_SUBSEQUENT_AVAILABILITY subsequentAvailability = CVSS_SUBSEQUENT_AVAILABILITY.NONE;
}
