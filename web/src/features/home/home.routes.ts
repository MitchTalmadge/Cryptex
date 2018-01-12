import {NgModule} from "@angular/core";
import {RouterModule, Routes} from "@angular/router";
import {CryptexHomeComponent} from "./home.component";

const routes: Routes = [
    {
        path: '',
        component: CryptexHomeComponent
    }
];

@NgModule({
    imports: [
        RouterModule.forChild(routes)
    ],
    exports: [
        RouterModule
    ],
    providers: []
})
export class CryptexHomeRoutesModule {
}