package com.william.notix.actions.finding_get;

import com.william.notix.annotations.authenticated.Authenticated;
import com.william.notix.annotations.caller.Caller;
import com.william.notix.dto.FindingDto;
import com.william.notix.dto.UserDto;
import com.william.notix.dto.response.Response;
import com.william.notix.entities.Finding;
import com.william.notix.entities.User;
import com.william.notix.exceptions.http.ForbiddenHttpException;
import com.william.notix.exceptions.http.InternalServerErrorHttpException;
import com.william.notix.exceptions.http.ResourceNotFoundHttpException;
import com.william.notix.exceptions.runtime.FindingNotFoundException;
import com.william.notix.exceptions.runtime.ForbiddenException;
import com.william.notix.exceptions.runtime.UnauthorizedException;
import com.william.notix.services.AuthorityService;
import com.william.notix.services.FindingService;
import com.william.notix.services.UserService;
import com.william.notix.utils.values.ROLE;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Controller("finidingGetAction")
@RequiredArgsConstructor
public class Action {

    private final AuthorityService authorityService;
    private final FindingService findingService;
    private final UserService userService;

    @GetMapping("/api/finding/{findingId}")
    @Authenticated(true)
    public Response<ResponseBody> action(
        @PathVariable("findingId") Long findingId,
        @Caller User user
    ) {
        try {
            Finding finding = findingService
                .findById(findingId)
                .orElseThrow(FindingNotFoundException::new);

            ROLE userRole = authorityService
                .getRoleOfFinding(user, finding)
                .orElseThrow(UnauthorizedException::new);

            FindingDto findingData = findingService.mapToDto(finding);
            UserDto creatorData = userService.mapToDto(finding.getCreator());

            Thread.sleep(3000);

            return new Response<ResponseBody>()
                .setData(
                    new ResponseBody()
                        .setCreator(creatorData)
                        .setFinding(findingData)
                        .setRole(userRole)
                );
        } catch (FindingNotFoundException e) {
            throw new ResourceNotFoundHttpException();
        } catch (ForbiddenException e) {
            throw new ForbiddenHttpException();
        } catch (Exception e) {
            log.error("Error [GET] /api/finding/{}", findingId);
            e.printStackTrace();
            throw new InternalServerErrorHttpException();
        }
    }
}
