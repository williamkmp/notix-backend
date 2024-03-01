package com.william.notix.entities;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@Entity(name = "user_logs")
@Table(name = "user_logs")
public class UserLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "message", nullable = false)
    private String message;

    @OneToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "updatee_id", referencedColumnName = "id", nullable = false)
    private User updatee;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_ref_id", referencedColumnName = "id", nullable = true)
    private User refrencedUser;

    @OneToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "project_ref_id", referencedColumnName = "id", nullable = true)
    private Project refrencedProject;

    @OneToOne(fetch = FetchType.LAZY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "subproject_ref_id", referencedColumnName = "id", nullable = true)
    private Subproject refrencedSubproject;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Date createdAt;
}
