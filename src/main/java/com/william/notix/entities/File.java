package com.william.notix.entities;

import java.util.Date;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@Entity(name = "files")
@Table(name = "files")
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "file", nullable = false)
    private byte[] bytes;

    @Column(name = "content_type", nullable = false)
    private String contentType;

    @Column(name = "name", nullable = true)
    private String name;

    @OneToOne(mappedBy = "file")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "project_file_detail_id", referencedColumnName = "id" , nullable = true)
    private ProjectFileDetail projectDetail;

    @OneToOne(mappedBy = "file")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "subproject_file_detail_id", referencedColumnName = "id" , nullable = true)
    private SubprojectFileDetail subprojectDetail;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Date createdAt;
}
