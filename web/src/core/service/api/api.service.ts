import {Injectable} from "@angular/core";
import {Observable} from "rxjs";
import {HttpClient, HttpErrorResponse, HttpEvent, HttpHeaders} from "@angular/common/http";
import {ApiResponse} from "./api-response.model";
import {catchError} from "rxjs/operators";

@Injectable()
export class ApiService {

    private apiUrl: string = "/api/";
    private headers: HttpHeaders = new HttpHeaders({
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'X-Requested-With': 'XMLHttpRequest'
    });

    constructor(private http: HttpClient) {
    }

    public getApiUrlFromEndpoint(endpoint: string): string {
        return this.apiUrl + ApiService.removeTrailingSlash(endpoint);
    }

    private static removeTrailingSlash(path: string): string {
        if (path && path.startsWith("/"))
            path = path.substring(1);
        return path;
    }

    /**
     * When an HTTP method results in an error, whether client-sided or server-sided, this method is called.
     * @param {HttpErrorResponse} error The error that was caught.
     * @param {Observable<ApiResponse | any>} caught The observable related to the error.
     * @returns {Observable<ApiResponse | any>} The un-altered caught parameter.
     */
    private static handleError(error: HttpErrorResponse, caught: Observable<any>): Observable<any> {
        if (error.error instanceof ErrorEvent) {
            console.error('A client-side connection error occurred while accessing ' + error.url + ': ', error.error.message);
        }

        return Observable.throw(error.error);
    }

    public get(path: string, additionalHeaders?: HttpHeaders): Promise<ApiResponse> {
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

        return this.http.get<ApiResponse>(`${this.apiUrl}${ApiService.removeTrailingSlash(path)}`, options)
            .pipe(catchError(ApiService.handleError))
            .toPromise();
    }

    public post(path: string, data: any): Promise<ApiResponse> {
        let options = {headers: this.headers};
        return this.http.post(`${this.apiUrl}${ApiService.removeTrailingSlash(path)}`, JSON.stringify(data), options)
            .pipe(catchError(ApiService.handleError))
            .toPromise();
    }

    public put(path: string, data: any): Promise<ApiResponse> {
        let options = {headers: this.headers};
        return this.http.put(`${this.apiUrl}${ApiService.removeTrailingSlash(path)}`, JSON.stringify(data), options)
            .pipe(catchError(ApiService.handleError))
            .toPromise();
    }

    public patch(path: string, data?: any): Promise<ApiResponse> {
        let options = {headers: this.headers};
        return this.http.patch(`${this.apiUrl}${ApiService.removeTrailingSlash(path)}`, data != null ? JSON.stringify(data) : undefined, options)
            .pipe(catchError(ApiService.handleError))
            .toPromise();
    }

    public delete(path: string): Promise<ApiResponse> {
        let options = {headers: this.headers};
        return this.http.delete(`${this.apiUrl}${ApiService.removeTrailingSlash(path)}`, options)
            .pipe(catchError(ApiService.handleError))
            .toPromise();
    }

}