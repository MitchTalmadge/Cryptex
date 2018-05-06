import {NgModule} from "@angular/core";
import {BrowserModule} from "@angular/platform-browser";
import {AppComponent} from "./app/app.component";
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
    bootstrap: [AppComponent]
})
export class AppModule {
}