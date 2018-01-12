import {NgModule} from "@angular/core";

import {CryptexAppComponent} from "./app.component";
import {CommonModule} from "@angular/common";
import {CryptexLoaderModule} from "./loader/loader.module";
import {RouterModule} from "@angular/router";
import {CryptexSidebarModule} from "./sidebar/sidebar.module";
import {CryptexHeaderModule} from "./header/header.module";

@NgModule({
    imports: [
        RouterModule,
        CommonModule,
        CryptexHeaderModule,
        CryptexLoaderModule,
        CryptexSidebarModule
    ],
    declarations: [CryptexAppComponent],
    exports: [],
    providers: [],
})
export class AppBootstrapModule {
}
