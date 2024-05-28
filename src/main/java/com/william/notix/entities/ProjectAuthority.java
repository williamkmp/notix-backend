package com.william.notix.entities;

import com.william.notix.utils.values.PROJECT_ROLE;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
@Entity(name = "project_authorities")
@Table(name = "project_authorities")
public class ProjectAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    private Project project;

    @Column(name = "role", nullable = false)
    private PROJECT_ROLE role = PROJECT_ROLE.MEMBER;

    @OneToMany(mappedBy = "projectAuthority", fetch = FetchType.LAZY)
    private List<SubprojectAuthority> subprojectAuthorities;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Date createdAt;
}
