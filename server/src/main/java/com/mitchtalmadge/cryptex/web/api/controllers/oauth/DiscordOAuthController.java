package com.mitchtalmadge.cryptex.web.api.controllers.oauth;

import com.mitchtalmadge.cryptex.web.api.APIControllerAbstract;
import com.mitchtalmadge.cryptex.web.api.APIResponse;
import com.mitchtalmadge.cryptex.web.api.annotations.APIController;
import com.mitchtalmadge.cryptex.web.api.util.ApiUtils;
import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthBearerClientRequest;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAccessTokenResponse;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.client.response.OAuthResourceResponse;
import org.apache.oltu.oauth2.common.OAuth;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;
import org.jetbrains.annotations.Nullable;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles requests related to OAuth2 security.
 */
@APIController
public class DiscordOAuthController extends APIControllerAbstract {

    /**
     * The client ID of the application used for OAuth2 authentication requests.
     */
    private static final String CLIENT_ID = System.getenv("DISCORD_OAUTH_CLIENT_ID");

    /**
     * The client secret of the application used for OAuth2 authentication requests.
     */
    private static final String CLIENT_SECRET = System.getenv("DISCORD_OAUTH_CLIENT_SECRET");

    /**
     * Generates an OAuth2 authorization URL for accessing a user's information.
     *
     * @param httpServletRequest The request.
     * @return The generated URL, which the browser should redirect to.
     */
    @RequestMapping(
            value = "oauth/discord/authorize",
            method = RequestMethod.GET
    )
    public ResponseEntity<?> getAuthorizationUrl(HttpServletRequest httpServletRequest) {

        // Make sure we have a client ID and secret.
        if (CLIENT_ID == null || CLIENT_SECRET == null) {
            logService.logError(getClass(), "An OAuth2 request was made but could not be fulfilled due to missing client ID and/or secret.");
            return APIResponse.statusInternalServerError();
        }

        try {
            OAuthClientRequest request = OAuthClientRequest
                    .authorizationLocation("https://discordapp.com/api/oauth2/authorize")
                    .setClientId(CLIENT_ID)
                    .setRedirectURI(ApiUtils.getRootUrlFromRequest(httpServletRequest) + "api/oauth/discord/redirect")
                    .setScope("identify")
                    .setResponseType("code")
                    .buildQueryMessage();

            return APIResponse.statusOk(request.getLocationUri());
        } catch (OAuthSystemException e) {
            logService.logException(getClass(), e, "Could not generate authorization request.");
            return APIResponse.statusInternalServerError();
        }
    }

    /**
     * Called by the client upon authenticating with the authorization URL.
     * Using the received data, determines where to redirect the client.
     *
     * @param httpServletRequest  The request.
     * @param httpServletResponse The response.
     * @param error               An error code if applicable.
     * @param error_description   A human readable error message if applicable.
     * @param error_uri           A URL to a human-readable web page with info on the error if applicable.
     * @param code                The code, supplied by the client.
     */
    @RequestMapping(
            value = "oauth/discord/redirect",
            method = RequestMethod.GET
    )
    public ResponseEntity<?> handleRedirect(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse,
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String error_description,
            @RequestParam(required = false) String error_uri,
            @RequestParam(required = false) String code) {

        // Check for errors
        if (error != null) {
            // Redirect the query parameters so that the client may display an error.
            Map<String, String> queryParams = new HashMap<>();

            // error
            queryParams.put("error", error);

            // error_description
            if (error_description != null)
                queryParams.put("error_description", error_description);

            // error_uri
            if (error_uri != null)
                queryParams.put("error_uri", error_uri);

            redirectToRoot(httpServletRequest, httpServletResponse, queryParams);
            return null;
        }

        // Make sure a code was received.
        if (code == null) {
            logService.logError(getClass(), "No code was received from the OAuth2 redirect.");

            redirectToRoot(httpServletRequest, httpServletResponse, null);
            return null;
        }

        // Obtain the OAuth2 token from the code.
        try {
            OAuthClientRequest request = OAuthClientRequest
                    .tokenLocation("https://discordapp.com/api/oauth2/token")
                    .setGrantType(GrantType.AUTHORIZATION_CODE)
                    .setClientId(CLIENT_ID)
                    .setClientSecret(CLIENT_SECRET)
                    .setRedirectURI(ApiUtils.getRootUrlFromRequest(httpServletRequest) + "api/oauth/discord/redirect")
                    .setCode(code)
                    .buildBodyMessage();

            // Discord's CloudFlare protection requires a standard user agent.
            request.setHeader("User-Agent", "Mozilla/5.0");

            OAuthClient client = new OAuthClient(new URLConnectionClient());
            OAuthJSONAccessTokenResponse tokenResponse = client.accessToken(request);

            String token = tokenResponse.getAccessToken();

            OAuthClientRequest oAuthBearerClientRequest = new OAuthBearerClientRequest("https://discordapp.com/api/users/@me")
                    .setAccessToken(token)
                    .buildHeaderMessage();

            // Discord's CloudFlare protection requires a standard user agent.
            oAuthBearerClientRequest.setHeader("User-Agent", "Mozilla/5.0");

            OAuthResourceResponse oAuthResourceResponse = client.resource(oAuthBearerClientRequest, OAuth.HttpMethod.GET, OAuthResourceResponse.class);

            return APIResponse.statusOk(oAuthResourceResponse.getBody());
        } catch (OAuthSystemException e) {
            logService.logException(getClass(), e, "Could not generate token request.");
            return APIResponse.statusInternalServerError();
        } catch (OAuthProblemException e) {
            logService.logException(getClass(), e, "Could not exchange code for token.");
            return APIResponse.statusInternalServerError();
        }

        /*// Redirect back to root of application.
        redirectToRoot(httpServletRequest, httpServletResponse, null);
        return null;*/
    }

    /**
     * Writes necessary redirect info to the response so that it redirects to the root of the application.
     *
     * @param request     The original request.
     * @param response    The response.
     * @param queryParams Optional query parameters for the url.
     */
    private void redirectToRoot(
            HttpServletRequest request,
            HttpServletResponse response,
            @Nullable Map<String, String> queryParams
    ) {

        // Add encoded query parameters if provided.
        String queryParamsString = "";
        if (queryParams != null) {
            queryParamsString = queryParams.entrySet().stream()
                    .map(entry -> {
                        try {
                            return URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8.toString()) + "=" + URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString());
                        } catch (UnsupportedEncodingException e) {
                            logService.logException(getClass(), e, "Invalid charset when encoding query parameters for redirect.");
                        }

                        return "";
                    })
                    .reduce((s, s2) -> s + "& " + s2)
                    .orElse("");
        }

        // Redirect.
        response.setStatus(HttpServletResponse.SC_FOUND);
        response.setHeader("Location", ApiUtils.getRootUrlFromRequest(request) + (queryParamsString.isEmpty() ? "" : "?" + queryParamsString));
    }

}
