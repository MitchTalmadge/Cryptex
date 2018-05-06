import {NgModule} from '@angular/core';

import {HeaderComponent} from './header.component';
import {SharedModule} from "../../../shared/shared.module";

@NgModule({
    imports: [SharedModule],
    exports: [HeaderComponent],
    declarations: [HeaderComponent],
    providers: [],
})
export class HeaderModule {
}
