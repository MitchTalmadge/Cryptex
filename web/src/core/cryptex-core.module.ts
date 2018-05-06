import {ErrorHandler, NgModule, Optional, SkipSelf} from "@angular/core";
import {CryptexLoaderService} from "./service/loader.service";
import {CryptexErrorHandler} from "./error-handler";
import {HttpClientModule} from "@angular/common/http";
import {CryptexAPIService} from "./service/api/cryptex-a-p-i.service";

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
            useClass: CryptexErrorHandler
        },
        CryptexAPIService,
        CryptexLoaderService,
    ],
})
export class CryptexCoreModule {

    constructor(@Optional() @SkipSelf() otherCoreModule: CryptexCoreModule) {
        if (otherCoreModule) {
            throw new Error("The Core Module was imported twice. It can only be imported once (in the root module)");
        }
    }

}
