import {NgModule} from "@angular/core";

import {CryptexLoaderComponent} from "./loader.component";
import {SharedModule} from "../../shared/shared.module";

@NgModule({
    imports: [
        SharedModule
    ],
    declarations: [
        CryptexLoaderComponent
    ],
    exports: [
        CryptexLoaderComponent
    ],
    providers: [],
})
export class LoaderModule {
}
