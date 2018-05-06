package com.mitchtalmadge.cryptex.web.api.util;

import javax.servlet.http.HttpServletRequest;

/**
 * A collection of utility methods for working with API endpoint requests and responses.
 */
public class ApiUtils {

    /**
     * From a request, extracts the root url.
     * For example, a request to "https://example.com:8080/api/users" would return "https://example.com:8080/".
     *
     * @param request The request that was made.
     * @return The root URL based on the request's accessed URL.
     */
    public static String getRootUrlFromRequest(HttpServletRequest request) {
        return request.getScheme() + "://" +        // "http(s)" + "://
                request.getServerName() +           // "example.com"
                ":" + request.getServerPort() +     // ":" + "8080"
                "/";
    }

}
