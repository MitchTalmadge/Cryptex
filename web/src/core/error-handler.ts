import {ErrorHandler} from "@angular/core";

export class CryptexErrorHandler implements ErrorHandler {
    handleError(error) {
        //TODO: Raygun implementation
        console.error("Uncaught Error: " + error);
    }
}