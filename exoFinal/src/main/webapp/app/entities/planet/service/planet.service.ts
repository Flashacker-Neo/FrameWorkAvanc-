import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPlanet, getPlanetIdentifier } from '../planet.model';

export type EntityResponseType = HttpResponse<IPlanet>;
export type EntityArrayResponseType = HttpResponse<IPlanet[]>;

@Injectable({ providedIn: 'root' })
export class PlanetService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/planets');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(planet: IPlanet): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(planet);
    return this.http
      .post<IPlanet>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(planet: IPlanet): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(planet);
    return this.http
      .put<IPlanet>(`${this.resourceUrl}/${getPlanetIdentifier(planet) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(planet: IPlanet): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(planet);
    return this.http
      .patch<IPlanet>(`${this.resourceUrl}/${getPlanetIdentifier(planet) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IPlanet>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IPlanet[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addPlanetToCollectionIfMissing(planetCollection: IPlanet[], ...planetsToCheck: (IPlanet | null | undefined)[]): IPlanet[] {
    const planets: IPlanet[] = planetsToCheck.filter(isPresent);
    if (planets.length > 0) {
      const planetCollectionIdentifiers = planetCollection.map(planetItem => getPlanetIdentifier(planetItem)!);
      const planetsToAdd = planets.filter(planetItem => {
        const planetIdentifier = getPlanetIdentifier(planetItem);
        if (planetIdentifier == null || planetCollectionIdentifiers.includes(planetIdentifier)) {
          return false;
        }
        planetCollectionIdentifiers.push(planetIdentifier);
        return true;
      });
      return [...planetsToAdd, ...planetCollection];
    }
    return planetCollection;
  }

  protected convertDateFromClient(planet: IPlanet): IPlanet {
    return Object.assign({}, planet, {
      satellite: planet.satellite?.isValid() ? planet.satellite.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.satellite = res.body.satellite ? dayjs(res.body.satellite) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((planet: IPlanet) => {
        planet.satellite = planet.satellite ? dayjs(planet.satellite) : undefined;
      });
    }
    return res;
  }
}
