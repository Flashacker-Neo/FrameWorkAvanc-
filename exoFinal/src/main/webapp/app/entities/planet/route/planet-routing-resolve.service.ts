import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPlanet, Planet } from '../planet.model';
import { PlanetService } from '../service/planet.service';

@Injectable({ providedIn: 'root' })
export class PlanetRoutingResolveService implements Resolve<IPlanet> {
  constructor(protected service: PlanetService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPlanet> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((planet: HttpResponse<Planet>) => {
          if (planet.body) {
            return of(planet.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Planet());
  }
}
