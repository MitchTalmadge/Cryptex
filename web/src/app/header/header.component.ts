import {Component, OnDestroy, OnInit} from '@angular/core';
import {NavigationEnd, Router} from "@angular/router";
import {Subscription} from "rxjs/Subscription";

@Component({
    selector: 'c-header',
    templateUrl: 'header.component.html',
    styleUrls: ['header.component.css']
})

export class CryptexHeaderComponent implements OnInit, OnDestroy {

    /**
     * The subscription to the router events.
     */
    private routerSubscription: Subscription;

    /**
     * The contents of the header for various routes.
     */
    readonly contents: [{ title: string, subtitle?: string, route: string, strict: boolean }] = [
        {
            title: 'Mitch Talmadge',
            subtitle: '< Software Engineer />',
            route: '/',
            strict: true
        },
        {
            title: 'About Mitch',
            route: '/about',
            strict: true
        },
        {
            title: 'Technical Skills',
            subtitle: 'Relevant Technologies and Experiences',
            route: '/skills',
            strict: true
        },
        {
            title: 'Certifications',
            route: '/certifications',
            strict: true
        },
        {
            title: 'Awards',
            route: '/awards',
            strict: true
        },
        {
            title: 'Contact Mitch',
            route: '/contact',
            strict: true
        }
    ];

    /**
     * The header contents for the current route.
     */
    currentContents: { title: string, subtitle?: string, route: string, strict: boolean } = null;

    constructor(private router: Router) {
    }

    ngOnInit() {
        // Listen to route changes to update the current contents of the header.
        this.routerSubscription = this.router.events
            .filter(event => event instanceof NavigationEnd)
            .subscribe((event: NavigationEnd) => {
                // Iterate over all header contents to find a matching route.
                for (let contents of this.contents) {
                    // Strict routes must exactly match the url.
                    if (contents.strict) {
                        if (event.urlAfterRedirects == contents.route) {
                            this.currentContents = contents;
                            break;
                        }
                    } else {
                        if (event.urlAfterRedirects.startsWith(contents.route)) {
                            this.currentContents = contents;
                            break;
                        }
                    }
                }
            });
    }

    ngOnDestroy(): void {
        this.routerSubscription.unsubscribe();
    }
}