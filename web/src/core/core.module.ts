import {ErrorHandler, NgModule, Optional, SkipSelf} from "@angular/core";
import {LoaderService} from "./service/loader.service";
import {ErrorHandlerProvider} from "./error-handler.provider";
import {HttpClientModule} from "@angular/common/http";
import {ApiService} from "./service/api/api.service";
import {SignInService} from "./service/sign-in.service";

/**
 * This module contains the service and other things which should only load once in the application.
 */
@NgModule({
    imports: [
        HttpClientModule,
    ],
    declarations: [],
    exports: [],
    providers: [
        {
            provide: ErrorHandler,
            useClass: ErrorHandlerProvider
        },
        ApiService,
        LoaderService,
        SignInService,
    ],
})
export class CoreModule {

    constructor(@Optional() @SkipSelf() otherCoreModule: CoreModule) {
        if (otherCoreModule) {
            throw new Error("The Core Module was imported twice. It can only be imported once (in the root module)");
        }
    }

}
