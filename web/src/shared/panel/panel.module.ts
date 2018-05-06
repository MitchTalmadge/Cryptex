import {NgModule} from '@angular/core';

import {PanelBodyDirective, PanelComponent, PanelHeaderDirective} from './panel.component';
import {CommonModule} from "@angular/common";

@NgModule({
    imports: [
        CommonModule
    ],
    exports: [
        PanelComponent,
        PanelHeaderDirective,
        PanelBodyDirective
    ],
    declarations: [
        PanelComponent,
        PanelHeaderDirective,
        PanelBodyDirective
    ],
    providers: [],
})
export class PanelModule {
}
