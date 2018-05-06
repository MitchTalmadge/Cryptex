import {Component} from "@angular/core";
import {LoaderService} from "../../core/service/loader.service";

@Component({
    selector: 'c-loader',
    templateUrl: 'loader.component.html',
    styleUrls: ['loader.component.css']
})
export class CryptexLoaderComponent {

    loading: boolean;

    constructor(loaderService: LoaderService) {
        loaderService.isLoading().subscribe(loading => this.loading = loading);
    }

}