import {NgModule} from "@angular/core";

import {CryptexHomeComponent} from "./home.component";
import {SharedModule} from "../../shared/shared.module";
import {CryptexHomeRoutesModule} from "./home.routes";

@NgModule({
    imports: [
        SharedModule,
        CryptexHomeRoutesModule,
    ],
    declarations: [
        CryptexHomeComponent,
    ],
    exports: [],
    providers: [],
})
export class CryptexHomeModule {
}
