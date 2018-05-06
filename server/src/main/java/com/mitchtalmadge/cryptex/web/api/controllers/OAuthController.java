package com.mitchtalmadge.cryptex.web.api.controllers;

import com.mitchtalmadge.cryptex.web.api.APIControllerAbstract;
import com.mitchtalmadge.cryptex.web.api.APIResponse;
import com.mitchtalmadge.cryptex.web.api.annotations.APIController;
import com.mitchtalmadge.cryptex.web.api.util.ApiUtils;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Handles requests related to OAuth2 security.
 */
@APIController
public class OAuthController extends APIControllerAbstract {

    /**
     * Generates an OAuth2 query URL for accessing a Discord user's information.
     *
     * @param httpServletRequest The request.
     * @return The generated URL, which the browser should redirect to.
     */
    @RequestMapping(
            value = "oauth/discord/query",
            method = RequestMethod.GET
    )
    public ResponseEntity<?> getDiscordQueryUrl(HttpServletRequest httpServletRequest) {
        try {
            OAuthClientRequest request = OAuthClientRequest
                    .authorizationLocation("https://discordapp.com/api/oauth2/authorize")
                    .setClientId("399825573310103552")
                    .setRedirectURI(ApiUtils.getRootUrlFromRequest(httpServletRequest) + "api/oauth/discord/redirect")
                    .setScope("identify")
                    .setResponseType("code")
                    .buildQueryMessage();

            return APIResponse.statusOk(request.getLocationUri());
        } catch (OAuthSystemException e) {
            logService.logException(getClass(), e, "Could not generate Discord Query URL.");
            return APIResponse.statusInternalServerError();
        }
    }

    /**
     * Called by the client upon authenticating with the query URL.
     * Using the received data, determines where to redirect the client.
     *
     * @param httpServletRequest  The request.
     * @param httpServletResponse The response.
     * @param error               An error code if applicable.
     * @param error_description   A human readable error message if applicable.
     * @param error_uri           A URL to a human-readable web page with info on the error if applicable.
     * @param code                The code, if there was no error.
     * @param state               The state if provided.
     */
    @RequestMapping(
            value = "oauth/discord/redirect",
            method = RequestMethod.GET
    )
    public ResponseEntity<?> handleDiscordRedirect(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String error_description,
            @RequestParam(required = false) String error_uri,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String state) {

        // Redirect back to root of application.
        httpServletResponse.setStatus(HttpServletResponse.SC_FOUND);
        httpServletResponse.setHeader("Location", ApiUtils.getRootUrlFromRequest(httpServletRequest));

        return null;
    }

}
