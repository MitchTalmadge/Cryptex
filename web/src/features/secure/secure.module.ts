import {NgModule} from '@angular/core';

import {SecureComponent} from './secure.component';
import {HeaderModule} from "./header/header.module";
import {SidebarModule} from "./sidebar/sidebar.module";
import {SharedModule} from "../../shared/shared.module";
import {SecureRoutesModule} from "./secure.routes";

@NgModule({
    imports: [
        SharedModule,
        SecureRoutesModule,

        HeaderModule,
        SidebarModule
    ],
    exports: [],
    declarations: [
        SecureComponent,
    ],
    providers: []
})
export class SecureModule {
}
