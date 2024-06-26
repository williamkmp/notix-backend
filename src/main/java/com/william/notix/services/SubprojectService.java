package com.william.notix.services;

import com.william.notix.entities.Project;
import com.william.notix.entities.Subproject;
import com.william.notix.exceptions.runtime.ResourceNotFoundException;
import com.william.notix.repositories.ProjectRepository;
import com.william.notix.repositories.SubprojectRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SubprojectService {

    private final ProjectRepository projectRepository;
    private final SubprojectRepository subprojectRepository;

    /**
     * find subproject by subproject id
     *
     * @param subprojectId {@link Long} subproject id
     * @return {@link Optional}<{@link Subproject}> subproject
     */
    public Optional<Subproject> findById(@NonNull Long subprojectId) {
        return subprojectRepository.findById(subprojectId);
    }

    /**
     * create new subproject for a given project
     *
     * @param projectId {@link Long} project id
     * @param newSubproject {@link Subproject} new subproject data
     * @return {@link Optional}<{@link Subproject}> created subproject data
     */
    @Transactional
    public Optional<Subproject> addSubproject(
        @NonNull Long projectId,
        @NonNull Subproject newSubproject
    ) {
        try {
            Project project = projectRepository
                .findById(projectId)
                .orElseThrow(ResourceNotFoundException::new);
            newSubproject.setProject(project);
            Subproject savedSubproject = subprojectRepository.save(
                newSubproject
            );
            return Optional.of(savedSubproject);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * get all subproject based of a given project id sorted by created at date
     *
     * @param projectId {@link Long} project id
     * @return {@link List}<{@link Subproject}>
     */
    @Transactional
    public List<Subproject> findAllByProject(@NonNull Long projectId) {
        Project project = projectRepository
            .findById(projectId)
            .orElseThrow(ResourceNotFoundException::new);
        return subprojectRepository.findAllByProject(project.getId());
    }
}
