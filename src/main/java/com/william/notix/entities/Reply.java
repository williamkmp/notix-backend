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
@Entity(name = "replies")
@Table(name = "replies")
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User creator;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(
        name = "discussion_id",
        referencedColumnName = "id",
        nullable = false
    )
    private Discussion discussion;

    @Column(name = "message", nullable = false, columnDefinition = "TEXT")
    private String content = "<p></p>";

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Date createdAt;
}
