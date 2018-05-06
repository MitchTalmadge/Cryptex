import {Component} from "@angular/core";
import {CryptexLoaderService} from "../../core/service/loader.service";

@Component({
    selector: 'c-loader',
    templateUrl: 'loader.component.html',
    styleUrls: ['loader.component.css']
})
export class CryptexLoaderComponent {

    loading: boolean;

    constructor(loaderService: CryptexLoaderService) {
        loaderService.isLoading().subscribe(loading => this.loading = loading);
    }

}