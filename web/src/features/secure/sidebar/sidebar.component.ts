import {Component, OnInit} from '@angular/core';

@Component({
    selector: 'c-sidebar',
    templateUrl: 'sidebar.component.html',
    styleUrls: ['sidebar.component.css']
})

export class SidebarComponent implements OnInit {

    /**
     * The links to display in the sidebar.
     */
    readonly links: [{name: string, route: string, strict: boolean}] = [
        {
            name: 'Dashboard',
            route: '/secure/dashboard',
            strict: true
        },
    ];

    constructor() {
    }

    ngOnInit() {
    }

    /**
     * Called when the sign-out link is clicked.
     */
    clickSignOut(): void {

    }

}