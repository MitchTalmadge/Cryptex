import {NgModule} from '@angular/core';

import {CryptexSidebarComponent} from './sidebar.component';
import {SharedModule} from "../../shared/shared.module";

@NgModule({
    imports: [SharedModule],
    exports: [CryptexSidebarComponent],
    declarations: [CryptexSidebarComponent],
    providers: [],
})
export class CryptexSidebarModule {

}
