import {NgModule} from "@angular/core";
import {RouterModule, Routes} from "@angular/router";

const routes: Routes = [
    {
        path: '',
        loadChildren: './features/sign-in/sign-in.module#SignInModule'
    },
    {
        path: 'secure',
        loadChildren: './features/secure/secure.module#SecureModule'
    },
    {
        path: '**',
        redirectTo: '/',
        pathMatch: 'full'
    }
];

/**
 * The root routing module. Other routes can be found next to their respective features.
 */
@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule],
})
export class AppRoutesModule {
}