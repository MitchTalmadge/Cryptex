import {NgModule} from "@angular/core";
import {BrowserModule} from "@angular/platform-browser";
import {CryptexAppComponent} from "./app/app.component";
import {CoreModule} from "./core/core.module";
import {AppRoutesModule} from "./app.routes";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {AppBootstrapModule} from "./app/app-bootstrap.module";

@NgModule({
    imports: [
        BrowserModule,
        BrowserAnimationsModule,
        AppRoutesModule,
        CoreModule,

        AppBootstrapModule
    ],
    declarations: [],
    providers: [],
    bootstrap: [CryptexAppComponent]
})
export class AppModule {
}