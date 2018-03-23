import {Component, OnDestroy, OnInit} from '@angular/core';
import {NavigationEnd, Router} from "@angular/router";
import {Subscription} from "rxjs/Subscription";

@Component({
    selector: 'c-header',
    templateUrl: 'header.component.html',
    styleUrls: ['header.component.css']
})

export class CryptexHeaderComponent implements OnInit, OnDestroy {

    ngOnInit(): void {
    }

    ngOnDestroy(): void {
    }


}