package com.william.notix.services;

import com.william.notix.dto.CreateFindingParameterDto;
import com.william.notix.dto.FindingDto;
import com.william.notix.dto.PreviewDto;
import com.william.notix.entities.CvssDetail;
import com.william.notix.entities.Document;
import com.william.notix.entities.Finding;
import com.william.notix.entities.FindingDetail;
import com.william.notix.entities.Subproject;
import com.william.notix.entities.User;
import com.william.notix.exceptions.runtime.FindingNotFoundException;
import com.william.notix.exceptions.runtime.UserNotFoundException;
import com.william.notix.repositories.DocumentRepository;
import com.william.notix.repositories.FindingRepository;
import com.william.notix.repositories.SubprojectRepository;
import com.william.notix.repositories.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindingService {

    private final SubprojectRepository subprojectRepository;
    private final DocumentRepository documentRepository;
    private final FindingRepository findingRepository;
    private final UserRepository userRepository;

    @Transactional
    public Finding create(@NonNull CreateFindingParameterDto param)
        throws FindingNotFoundException, UserNotFoundException {
        Subproject subproject = subprojectRepository
            .findById(param.getSubprojectId())
            .orElseThrow(FindingNotFoundException::new);

        User creator = userRepository
            .findById(param.getCreatorId())
            .orElseThrow(UserNotFoundException::new);

        Document document = documentRepository.save(new Document());

        return findingRepository.save(
            new Finding()
                .setName(param.getFindingName())
                .setSubproject(subproject)
                .setCreator(creator)
                .setDocument(document)
                .setFindingDetail(new FindingDetail())
                .setCvssDetail(new CvssDetail())
        );
    }

    public Optional<Finding> findById(@NonNull Long findingId) {
        return findingRepository.findById(findingId);
    }

    @Transactional
    public FindingDto mapToDto(@NonNull Finding finding) {
        User creator = finding.getCreator();
        String imageId = Objects.nonNull(finding.getImage())
            ? finding.getImage().getId().toString()
            : null;

        return new FindingDto()
            .setId(finding.getId().toString())
            .setName(finding.getName())
            .setImageId(imageId)
            .setCvssDetail(finding.getCvssDetail())
            .setFindingDetail(finding.getFindingDetail())
            .setCreatorId(creator.getId().toString())
            .setCreatedAt(finding.getCreatedAt());
    }

    @Transactional
    public List<Finding> findAllBySubproject(@NonNull Subproject subproject) {
        return findingRepository.findAllBySubproject(subproject.getId());
    }

    public PreviewDto createPreview(Finding finding) {
        String imageId = Objects.nonNull(finding.getImage())
            ? finding.getImage().getId().toString()
            : null;

        return new PreviewDto()
            .setId(finding.getId().toString())
            .setName(finding.getName())
            .setImageId(imageId);
    }

    @Transactional
    public void addMember(@NonNull List<User> user, @NonNull Finding finding) {
        // TODO: implement this
    }
}
