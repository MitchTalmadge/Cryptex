import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {HttpClient, HttpErrorResponse, HttpHeaders} from "@angular/common/http";
import {CryptexAPIResponse} from "./api-response.model";
import {catchError} from "rxjs/operators";

@Injectable()
export class CryptexAPIService {

    private apiUrl: string = "/api/";
    private headers: HttpHeaders = new HttpHeaders({
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'X-Requested-With': 'XMLHttpRequest'
    });

    constructor(private http: HttpClient) {
    }

    public getApiUrlFromEndpoint(endpoint: string): string {
        return this.apiUrl + CryptexAPIService.removeTrailingSlash(endpoint);
    }

    private static removeTrailingSlash(path: string): string {
        if (path && path.startsWith("/"))
            path = path.substring(1);
        return path;
    }

    /**
     * When an HTTP method results in an error, whether client-sided or server-sided, this method is called.
     * @param {HttpErrorResponse} error The error that was caught.
     * @param {Observable<CryptexAPIResponse | any>} caught The observable related to the error.
     * @returns {Observable<CryptexAPIResponse | any>} The un-altered caught parameter.
     */
    private static handleError(error: HttpErrorResponse, caught: Observable<CryptexAPIResponse | any>): Observable<CryptexAPIResponse | any> {
        if (error.error instanceof ErrorEvent) {
            console.error('A client-side connection error occurred while accessing ' + error.url + ': ', error.error.message);
        }

        return caught;
    }

    public get(path: string, additionalHeaders?: HttpHeaders): Observable<CryptexAPIResponse | any> {
        let options;
        if (additionalHeaders) {
            // Copy the current headers.
            let newHeaders: HttpHeaders = new HttpHeaders();
            this.headers.keys().forEach(value => newHeaders.set(value, this.headers.getAll(value)));

            // Append the additional headers.
            additionalHeaders.keys().forEach(value => newHeaders.append(value, additionalHeaders.getAll(value)));

            options = {headers: newHeaders};
        }
        else options = {headers: this.headers};

        return this.http.get<CryptexAPIResponse>(`${this.apiUrl}${CryptexAPIService.removeTrailingSlash(path)}`, options)
            .pipe(catchError(CryptexAPIService.handleError));
    }

    public post(path: string, data: any): Observable<CryptexAPIResponse | any> {
        let options = {headers: this.headers};
        return this.http.post(`${this.apiUrl}${CryptexAPIService.removeTrailingSlash(path)}`, JSON.stringify(data), options)
            .pipe(catchError(CryptexAPIService.handleError));
    }

    public put(path: string, data: any): Observable<CryptexAPIResponse | any> {
        let options = {headers: this.headers};
        return this.http.put(`${this.apiUrl}${CryptexAPIService.removeTrailingSlash(path)}`, JSON.stringify(data), options)
            .pipe(catchError(CryptexAPIService.handleError));
    }

    public patch(path: string, data?: any): Observable<CryptexAPIResponse | any> {
        let options = {headers: this.headers};
        return this.http.patch(`${this.apiUrl}${CryptexAPIService.removeTrailingSlash(path)}`, data != null ? JSON.stringify(data) : undefined, options)
            .pipe(catchError(CryptexAPIService.handleError));
    }

    public delete(path: string): Observable<CryptexAPIResponse | any> {
        let options = {headers: this.headers};
        return this.http.delete(`${this.apiUrl}${CryptexAPIService.removeTrailingSlash(path)}`, options)
            .pipe(catchError(CryptexAPIService.handleError));
    }

}