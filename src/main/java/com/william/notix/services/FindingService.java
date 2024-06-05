package com.william.notix.services;

import com.william.notix.dto.FindingDto;
import com.william.notix.dto.FindingPropertyDto;
import com.william.notix.dto.PreviewDto;
import com.william.notix.dto.parameter.CreateFindingParameter;
import com.william.notix.dto.parameter.UpdateFindingPropertyParameter;
import com.william.notix.entities.Cvss;
import com.william.notix.entities.Document;
import com.william.notix.entities.Finding;
import com.william.notix.entities.FindingProperty;
import com.william.notix.entities.FindingLog;
import com.william.notix.entities.Subproject;
import com.william.notix.entities.User;
import com.william.notix.exceptions.runtime.FindingNotFoundException;
import com.william.notix.exceptions.runtime.UserNotFoundException;
import com.william.notix.repositories.DocumentRepository;
import com.william.notix.repositories.FindingRepository;
import com.william.notix.repositories.SubprojectRepository;
import com.william.notix.repositories.UserRepository;
import com.william.notix.utils.values.FINDING_CATEGORY;
import com.william.notix.utils.values.FINDING_IMPACT;
import com.william.notix.utils.values.FINDING_LIKELIHOOD;
import com.william.notix.utils.values.FINDING_LOCATION;
import com.william.notix.utils.values.FINDING_METHOD;
import com.william.notix.utils.values.FINDING_PROPERTY;
import com.william.notix.utils.values.KEY;
import com.william.notix.utils.values.TOPIC;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FindingService {

    private final FindingLogService logService;
    private final SubprojectRepository subprojectRepository;
    private final DocumentRepository documentRepository;
    private final FindingRepository findingRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate socket;

    @Transactional
    public Finding create(@NonNull CreateFindingParameter param)
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
                .setFindingProperty(new FindingProperty())
                .setCvss(new Cvss())
        );
    }

    public Optional<Finding> findById(@NonNull Long findingId) {
        return findingRepository.findById(findingId);
    }

    @Transactional
    public FindingDto mapToDto(@NonNull Finding finding) {
        User creator = finding.getCreator();

        return new FindingDto()
            .setId(finding.getId().toString())
            .setName(finding.getName())
            .setCvssDetail(finding.getCvss())
            .setFindingDetail(finding.getFindingProperty())
            .setCreatorId(creator.getId().toString())
            .setCreatedAt(finding.getCreatedAt());
    }

    @Transactional
    public List<Finding> findAllBySubproject(@NonNull Subproject subproject) {
        return findingRepository.findAllBySubproject(subproject.getId());
    }

    @Transactional
    public void updateProperty(
        @NonNull UpdateFindingPropertyParameter parameter
    ) {
        // Destructuring param
        FINDING_PROPERTY targetProperty = parameter.getTargetProperty();
        String sessionToken = parameter.getSessionToken();
        Finding finding = parameter.getFinding();
        String newValue = parameter.getValue();
        User actor = parameter.getActor();

        // Updating finding detail targeted property
        FindingProperty detail =
            switch (targetProperty) {
                case CATEGORY -> finding
                    .getFindingProperty()
                    .setCategory(FINDING_CATEGORY.valueOf(newValue));
                case LOCATION -> finding
                    .getFindingProperty()
                    .setLocation(FINDING_LOCATION.valueOf(newValue));
                case METHOD -> finding
                    .getFindingProperty()
                    .setMethod(FINDING_METHOD.valueOf(newValue));
                case ENVIRONMENT -> finding
                    .getFindingProperty()
                    .setEnvironment(newValue);
                case APPLICATION_NAME -> finding
                    .getFindingProperty()
                    .setApplicationName(newValue);
                case IMAPCT -> finding
                    .getFindingProperty()
                    .setImpact(FINDING_IMPACT.valueOf(newValue));
                case LIKELIHOOD -> finding
                    .getFindingProperty()
                    .setLikelihood(FINDING_LIKELIHOOD.valueOf(newValue));
            };
        finding.setFindingProperty(detail);
        Finding updatedFinding = findingRepository.save(finding);

        // Publish changes
        socket.convertAndSend(
            TOPIC.findingProperty(finding.getId()),
            new FindingPropertyDto()
                .setProperty(targetProperty)
                .setValue(newValue),
            Map.ofEntries(
                Map.entry(KEY.STOMP_HEADER_CALLER_SESSION_UUID, sessionToken),
                Map.entry(KEY.STOMP_HEADER_CALLER_USER_ID, actor.getId().toString())
            )
        );

        // Publish finding log 
        FindingLog updateLog = logService
            .createDetailChangeLog("Application Name", newValue)
            .setFinding(updatedFinding)
            .setActor(actor);
        logService.publishLog(updateLog);
    }

    public PreviewDto createPreview(Finding finding) {
        return new PreviewDto()
            .setId(finding.getId().toString())
            .setName(finding.getName());
    }
}
