package com.william.notix.seeders.imp;

import com.william.notix.entities.Project;
import com.william.notix.entities.Subproject;
import com.william.notix.repositories.ProjectRepository;
import com.william.notix.repositories.SubprojectRepository;
import com.william.notix.seeders.Seeder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(3)
@Component
@RequiredArgsConstructor
public class SubprojectSeeder implements Seeder {

    private final ProjectRepository projectRepository;
    private final SubprojectRepository subprojectRepository;

    @Override
    public void run() throws Exception {
        List<Project> projects = projectRepository.findAll();

        for (Project project : projects) {
            String projectCode = project.getName().split("\\s+")[0];
            LocalDateTime now = LocalDate.now().atStartOfDay();
            List<Subproject> subprojects = List.of(
                new Subproject()
                    .setProject(project)
                    .setName(projectCode + " - Subproject 1")
                    .setStartDate(toDate(now.plusDays(1)))
                    .setEndDate(toDate(now.plusDays(7)))
                    .setCreatedAt(new Date()),
                new Subproject()
                    .setProject(project)
                    .setName(projectCode + " - Subproject 2")
                    .setStartDate(toDate(now))
                    .setEndDate(toDate(now.plusWeeks(3)))
                    .setCreatedAt(new Date()),
                new Subproject()
                    .setProject(project)
                    .setName(projectCode + " - Subproject 3")
                    .setStartDate(toDate(now.minusWeeks(3)))
                    .setEndDate(toDate(now.minusWeeks(2)))
                    .setCreatedAt(new Date())
                // new Subproject()
                //     .setProject(project)
                //     .setName(projectCode + " - Subproject 4")
                //     .setStartDate(toDate(now.plusWeeks(1)))
                //     .setEndDate(toDate(now.plusWeeks(5)))
                //     .setCreatedAt(new Date()),
                // new Subproject()
                //     .setProject(project)
                //     .setName(projectCode + " - Subproject 5")
                //     .setStartDate(toDate(now.plusWeeks(3)))
                //     .setEndDate(toDate(now.plusWeeks(4)))
                //     .setCreatedAt(new Date())
            );

            subprojectRepository.saveAll(subprojects);
        }
    }

    private Date toDate(LocalDateTime dtm) {
        return Date.from(dtm.atZone(ZoneId.systemDefault()).toInstant());
    }
}
