package com.william.notix.seeders.imp;

import com.william.notix.dto.parameter.CreateFindingParameter;
import com.william.notix.entities.Subproject;
import com.william.notix.entities.User;
import com.william.notix.repositories.SubprojectRepository;
import com.william.notix.seeders.Seeder;
import com.william.notix.services.FindingService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(4)
@Component
@RequiredArgsConstructor
public class FindingSeeder implements Seeder {

    private final SubprojectRepository subprojectRepository;
    private final FindingService findingService;

    @Override
    public void run() throws Exception {
        List<Subproject> subprojects = subprojectRepository.findAll();

        for (Subproject subproject : subprojects) {
            String subprojectName = subproject.getName();
            User creator = subproject.getProject().getOwner();
            findingService.create(
                new CreateFindingParameter()
                    .setCreatorId(creator.getId())
                    .setFindingName(subprojectName + "- Finding")
                    .setSubprojectId(subproject.getId())
            );
        }
    }
}
