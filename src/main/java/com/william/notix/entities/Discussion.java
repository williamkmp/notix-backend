package com.william.notix.entities;

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

@Data
@Accessors(chain = true)
@NoArgsConstructor
@Entity(name = "discussions")
@Table(name = "discussions")
public class Discussion {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User creator;

    @ManyToOne
    @JoinColumn(
        name = "finding_id",
        referencedColumnName = "id",
        nullable = false
    )
    private Finding finding;

    @Column(name = "is_open", nullable = false)
    private Boolean isOpen = Boolean.TRUE;

    @OneToMany(mappedBy = "discussion", fetch = FetchType.LAZY)
    private List<Reply> replies;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Date createdAt;
}
