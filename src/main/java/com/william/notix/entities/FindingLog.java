package com.william.notix.entities;

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
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@Entity(name = "finding_logs")
@Table(name = "finding_logs")
public class FindingLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title = "Update";

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String message = "<p></p>";

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(
        name = "actor_id",
        referencedColumnName = "id",
        nullable = false
    )
    private User actor;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(
        name = "user_ref_id",
        referencedColumnName = "id",
        nullable = false
    )
    private User userRef;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(
        name = "finding_id",
        referencedColumnName = "id",
        nullable = false
    )
    private Finding finding;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Date createdAt;
}
