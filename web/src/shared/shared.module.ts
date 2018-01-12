import {NgModule} from "@angular/core";
import {CommonModule} from "@angular/common";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {RouterModule} from "@angular/router";
import {CryptexPanelModule} from "./panel/panel.module";

/**
 * This module is dedicated to highly re-usable components that are used often in feature components (pages, etc)
 */
@NgModule({
    imports: [
        CommonModule,
        FormsModule,
        ReactiveFormsModule,
        RouterModule,

        // Components
        CryptexPanelModule
    ],
    declarations: [],
    exports: [
        CommonModule,
        FormsModule,
        ReactiveFormsModule,
        RouterModule,

        // Components
        CryptexPanelModule
    ],
    providers: [],
})
export class SharedModule {
}
