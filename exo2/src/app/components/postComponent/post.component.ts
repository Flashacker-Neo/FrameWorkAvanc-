import { Component } from "@angular/core";
import { FormGroup, FormControl } from '@angular/forms';

@Component({
    selector: 'post-component',
    templateUrl: './post.component.html',
    styleUrls: ['post.component.scss']
})
export class PostComponent {

    form = new FormGroup({
        title: new FormControl(),
        body: new FormControl()
    });

    data: Post[] = [
        {title: 'title1', body: 'body1'},
        {title: 'title2', body: 'body2'},
        {title: 'title3', body: 'body3'},
        {title: 'title4', body: 'body4'},
    ];

    onSubmit() {
        this.data.push(this.form.value);
    }
}

interface Post {
    title: string;
    body: string;
}