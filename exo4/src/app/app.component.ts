import { Component } from '@angular/core';
import { MovieDbApi } from './services/api.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {

  movies: any = [];

  constructor(private moviedbApi: MovieDbApi) {
    moviedbApi.getList().subscribe(
      (response: any) => {
        console.log(response);
        this.movies = response.results;
      }
    );
  }

}
