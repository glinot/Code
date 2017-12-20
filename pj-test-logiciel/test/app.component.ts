import { Component } from '@angular/core';

export class Hero {
    id: number;
    name: string;
}

var a = {b : 1}

@Component({
    selector: 'my-app',
    template: `<h1>{{ title }}</h1>
    <h1>Hero #{{ hero.id }}</h1>
    <h2>{{hero.name}}</h2>`
});

export class AppComponent {
    title = "J'aime le yo-yo";
    hero : Hero = {
        id: 1,
        name: 'Windstorm'
    };
}
