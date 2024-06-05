package com.william.notix.actions.finding_update_detail;

import com.william.notix.annotations.caller.Caller;
import com.william.notix.annotations.session_uuid.SessionUuid;
import com.william.notix.dto.parameter.UpdateFindingPropertyParameter;
import com.william.notix.entities.Finding;
import com.william.notix.entities.User;
import com.william.notix.exceptions.runtime.FindingNotFoundException;
import com.william.notix.services.FindingService;
import com.william.notix.utils.values.FINDING_PROPERTY;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

@Controller("finidngUpdateDetail")
@RequiredArgsConstructor
public class Action {

    private final FindingService findingService;

    @MessageMapping("/finding/{findingId}/property.update")
    public void handle(
        @DestinationVariable Long findingId,
        @Payload Request payload,
        @Caller User user,
        @SessionUuid String sessionUuid
    ) {
        try {
            Finding finding = findingService
                .findById(findingId)
                .orElseThrow(FindingNotFoundException::new);

            FINDING_PROPERTY targetProperty = payload.getProperty();
            String newValue = payload.getValue();

            findingService.updateProperty(
                new UpdateFindingPropertyParameter()
                    .setFinding(finding)
                    .setActor(user)
                    .setTargetProperty(targetProperty)
                    .setValue(newValue)
            );
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
