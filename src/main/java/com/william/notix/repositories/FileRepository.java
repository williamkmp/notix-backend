package com.william.notix.repositories;

import com.william.notix.entities.File;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface FileRepository extends JpaRepository<File, Long> {

    @Query(
        """
            SELECT f 
            FROM projects p
                JOIN p.fileDetails fd
                JOIN fd.file f
            WHERE p.id = :projectId
        """
    )
    public List<File> findAllByProject(@Param("projectId") Long projectId); 
}
