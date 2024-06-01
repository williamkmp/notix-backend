package com.william.notix.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.Date;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@Entity(name = "findings")
@Table(name = "findings")
public class Finding {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name = "";

    @Embedded
    private FindingDetail findingDetail;

    @Embedded
    private CvssDetail cvssDetail;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User creator;

    @OneToMany(mappedBy = "finding")
    private List<FindingMemberRecord> memberRecords;

    @OneToMany(mappedBy = "finding")
    private List<TestResult> testResults;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "subproject_id", nullable = false)
    private Subproject subproject;

    @OneToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @OneToOne(fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "image_id", referencedColumnName = "id", nullable = true)
    private File image;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Date createdAt;
}
