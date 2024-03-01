package com.william.notix.utils.values;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TOPIC {

    /**
     * STOMP destination url for /topic/user/{userId}/project/preview
     *
     * @param userId {@link Long} user id
     * @return {@link String} STOMP destination path
     */
    public static String userProjectPreviews(Long userId) {
        return "/topic/user/" + userId.toString() + "/project/previews";
    }
}
