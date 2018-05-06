import {Component, OnInit} from '@angular/core';
import {SignInService} from "../../core/service/sign-in.service";

@Component({
    selector: 'c-sign-in',
    templateUrl: 'sign-in.component.html',
    styleUrls: ['sign-in.component.css']
})

export class SignInComponent implements OnInit {

    constructor(private signInService: SignInService) {
    }

    ngOnInit() {
    }

    /**
     * Called when the Sign In with Discord button is clicked.
     */
    clickSignIn() {
        this.signInService.getDiscordQueryUrl()
            .then(value => window.location = value.content)
    }
}