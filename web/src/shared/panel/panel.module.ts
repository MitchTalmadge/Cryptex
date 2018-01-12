import {NgModule} from '@angular/core';

import {CryptexPanelBodyDirective, CryptexPanelComponent, CryptexPanelHeaderDirective} from './panel.component';
import {CommonModule} from "@angular/common";

@NgModule({
    imports: [
        CommonModule
    ],
    exports: [
        CryptexPanelComponent,
        CryptexPanelHeaderDirective,
        CryptexPanelBodyDirective
    ],
    declarations: [
        CryptexPanelComponent,
        CryptexPanelHeaderDirective,
        CryptexPanelBodyDirective
    ],
    providers: [],
})
export class CryptexPanelModule {
}
