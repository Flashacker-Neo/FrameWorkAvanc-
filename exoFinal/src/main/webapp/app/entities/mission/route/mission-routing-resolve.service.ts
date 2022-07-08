import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IMission, Mission } from '../mission.model';
import { MissionService } from '../service/mission.service';

@Injectable({ providedIn: 'root' })
export class MissionRoutingResolveService implements Resolve<IMission> {
  constructor(protected service: MissionService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IMission> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((mission: HttpResponse<Mission>) => {
          if (mission.body) {
            return of(mission.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Mission());
  }
}
