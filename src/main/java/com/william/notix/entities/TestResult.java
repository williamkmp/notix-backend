package com.william.notix.entities;

import com.william.notix.utils.values.FINDING_STATUS;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.hibernate.annotations.CreationTimestamp;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@Entity(name = "test_results")
@Table(name = "test_results")
public class TestResult {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User tester;

    @ManyToOne
    @JoinColumn(
        name = "finding_id",
        referencedColumnName = "id",
        nullable = false
    )
    private Finding finding;

    @Column(name = "name", nullable = false)
    private String version = "";

    @Column(name = "name", nullable = false)
    private FINDING_STATUS status = FINDING_STATUS.NOT_RETESTED;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String content = "<p></p>";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Date createdAt;
}
