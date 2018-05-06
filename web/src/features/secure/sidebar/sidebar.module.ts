import {NgModule} from '@angular/core';

import {SidebarComponent} from './sidebar.component';
import {SharedModule} from "../../../shared/shared.module";

@NgModule({
    imports: [SharedModule],
    exports: [SidebarComponent],
    declarations: [SidebarComponent],
    providers: [],
})
export class SidebarModule {

}
