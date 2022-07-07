import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { environment } from "src/environments/environment";

@Injectable({
    providedIn: 'root'
})
export class MovieDbApi {

    private apiEndPoint: string = environment.moviedb.endpoint;
    private apiKey: string = environment.moviedb.apiKey;

    constructor(private http: HttpClient) {}

    getList() {
        return this.http.get(`${this.apiEndPoint}/discover/movie`, {
            params: {
                api_key: this.apiKey,
                language: 'fr-FR'
            }
        });
    }
}