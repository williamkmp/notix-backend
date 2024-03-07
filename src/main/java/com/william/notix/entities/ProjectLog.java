package com.william.notix.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Entity(name = "project_logs")
@Table(name = "project_logs")
public class ProjectLog {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "message", nullable = false)
    private String message = "<p></p>";

    @Column(name = "title", nullable = false)
    private String title = "Update";

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(
        name = "updatee_id",
        referencedColumnName = "id",
        nullable = false
    )
    private Project updatee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "user_ref_id",
        referencedColumnName = "id",
        nullable = true
    )
    private User refrencedUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(
        name = "subproject_ref_id",
        referencedColumnName = "id",
        nullable = true
    )
    private Subproject refrencedSubproject;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Date createdAt;
}
