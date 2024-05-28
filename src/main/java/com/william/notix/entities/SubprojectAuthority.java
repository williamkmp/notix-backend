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
@Entity(name = "subproject_authorities")
@Table(name = "subproject_authorities")
public class SubprojectAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(
        name = "subproject_id",
        referencedColumnName = "id",
        nullable = false
    )
    private Subproject subproject;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(
        name = "project_authority_id",
        referencedColumnName = "id",
        nullable = false
    )
    private ProjectAuthority projectAuthority;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Date createdAt;
}
