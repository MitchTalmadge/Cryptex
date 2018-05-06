import {ErrorHandler} from "@angular/core";

export class ErrorHandlerProvider implements ErrorHandler {
    handleError(error) {
        //TODO: Raygun implementation
        console.error("Uncaught Error: " + error);
    }
}