import { Component } from '@angular/core';
import { MovieDbApi } from 'src/app/services/api.service';

@Component({
  selector: 'movies-list',
  templateUrl: './movies-list.component.html',
  styleUrls: ['./movies-list.component.scss']
})
export class MoviesListComponent {

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
