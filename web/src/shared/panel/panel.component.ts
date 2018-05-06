import {Component, Directive, Input, OnInit} from '@angular/core';

@Component({
    selector: 'c-panel',
    templateUrl: 'panel.component.html',
    styleUrls: ['panel.component.css']
})

export class PanelComponent implements OnInit {

    constructor() {
    }

    ngOnInit() {
    }
}

@Directive({
    selector: 'c-panel-header'
})
export class PanelHeaderDirective {

}

@Directive({
    selector: 'c-panel-body'
})
export class PanelBodyDirective {

}