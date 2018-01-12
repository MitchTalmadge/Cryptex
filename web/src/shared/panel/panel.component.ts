import {Component, Directive, Input, OnInit} from '@angular/core';

@Component({
    selector: 'c-panel',
    templateUrl: 'panel.component.html',
    styleUrls: ['panel.component.css']
})

export class CryptexPanelComponent implements OnInit {

    constructor() {
    }

    ngOnInit() {
    }
}

@Directive({
    selector: 'c-panel-header'
})
export class CryptexPanelHeaderDirective {

}

@Directive({
    selector: 'c-panel-body'
})
export class CryptexPanelBodyDirective {

}