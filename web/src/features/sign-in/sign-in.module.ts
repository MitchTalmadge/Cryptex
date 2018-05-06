import {NgModule} from '@angular/core';

import {SignInComponent} from './sign-in.component';
import {SharedModule} from "../../shared/shared.module";
import {SignInRoutesModule} from "./sign-in.routes";

@NgModule({
    imports: [SharedModule,
        SignInRoutesModule],
    exports: [],
    declarations: [SignInComponent],
    providers: [],
})
export class SignInModule {
}
