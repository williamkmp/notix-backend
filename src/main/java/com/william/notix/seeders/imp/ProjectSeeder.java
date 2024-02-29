package com.william.notix.seeders.imp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.william.notix.entities.Project;
import com.william.notix.entities.User;
import com.william.notix.exceptions.runtime.UserNotFoundException;
import com.william.notix.seeders.Seeder;
import com.william.notix.services.ProjectService;
import com.william.notix.services.UserService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Order(2)
@Component
@RequiredArgsConstructor
public class ProjectSeeder implements Seeder {
    
    private final UserService userService;
    private final ProjectService projectService;
    
    @Override
    @Transactional
    public void run() throws Exception {
        User william = userService.findById(1l)
            .orElseThrow(UserNotFoundException::new);
        
        projectService.createProject(
            new Project()
                .setName("PC01 - Testy")
                .setStartDate(toDate(LocalDate.now().atStartOfDay().plusDays(1)))
                .setEndDate(toDate(LocalDate.now().atStartOfDay().plusDays(7))), 
            william.getId()
        );

        projectService.createProject(
            new Project()
                .setName("PC02 - Notix")
                .setStartDate(toDate(LocalDate.now().atStartOfDay()))
                .setEndDate(toDate(LocalDate.now().atStartOfDay().plusWeeks(3))), 
            william.getId()
        );

        projectService.createProject(
            new Project()
                .setName("PC03 - Taskly")
                .setStartDate(toDate(LocalDate.now().atStartOfDay().minusWeeks(1)))
                .setEndDate(toDate(LocalDate.now().atStartOfDay().minusDays(3))), 
            william.getId()
        );
    }

    private Date toDate(LocalDateTime dtm) {
        return Date.from(dtm.atZone(ZoneId.systemDefault()).toInstant());
    }
    
}
