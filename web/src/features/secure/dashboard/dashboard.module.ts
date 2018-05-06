import {NgModule} from "@angular/core";

import {DashboardComponent} from "./dashboard.component";
import {SharedModule} from "../../../shared/shared.module";
import {HomeRoutesModule} from "./dashboard.routes";

@NgModule({
    imports: [
        SharedModule,
        HomeRoutesModule,
    ],
    declarations: [
        DashboardComponent,
    ],
    exports: [],
    providers: [],
})
export class DashboardModule {
}
