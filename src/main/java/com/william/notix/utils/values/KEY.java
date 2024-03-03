package com.william.notix.utils.values;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class KEY {

    /**
     * Key for accessing user information for authenticated request, for auditing purposes
     */
    public static final String REQUEST_ATTRIBUTE_CALLER_INFO =
        "NOTIX:REQUEST_CALLER_INFORMTAION";

    /**
     * Key for validation root error in form level, see api specification for further detail
     */
    public static final String RESPONSE_ERROR_ROOT = "@root";

    /**
     * STOMP Header key for accessing caller's user id
     */
    public static final String STOMP_CALLER_USER_ID = "CallerId";

    /**
     * Header key for accessing caller's user id in STOMP Frame or HTTP
     */
    public static final String CALLER_SESSION_UUID = "SessionUUID";
}
