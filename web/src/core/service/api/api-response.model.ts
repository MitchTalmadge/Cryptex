/**
 * Represents a response from the API.
 */
export interface ApiResponse {

    /**
     * Whether or not the request succeeded.
     */
    ok: boolean;

    /**
     * An optional machine-readable error message in the case ok was false.
     * Example: "not_found".
     */
    error?: string;

    /**
     * An optional human-readable error message in the case ok was false.
     * Example: "The requested API endpoint could not be found."
     */
    message?: string;

    /**
     * The content of the response, in the case that ok was true.
     */
    content?: any;

}