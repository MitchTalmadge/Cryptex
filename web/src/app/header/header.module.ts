import {NgModule} from '@angular/core';

import {CryptexHeaderComponent} from './header.component';
import {SharedModule} from "../../shared/shared.module";

@NgModule({
    imports: [SharedModule],
    exports: [CryptexHeaderComponent],
    declarations: [CryptexHeaderComponent],
    providers: [],
})
export class CryptexHeaderModule {
}
