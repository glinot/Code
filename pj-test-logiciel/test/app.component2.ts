import { Component } from '@angular/core';

export class MathieuLochette {
    id: number;
    instrument: string;
}

@Component({
    selector: 'my-app',
    templateUrl: './app.component2.html'
})
export class AppComponent {
    title = 'Mathieu Lochette fait de la guitare électrique';
    mlochet : MathieuLochette = {
        id: 1,
        instrument: 'Guitare Électrique'
    };
}
