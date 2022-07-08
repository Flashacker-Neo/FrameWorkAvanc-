import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { ISpaceEvent, SpaceEvent } from '../space-event.model';
import { SpaceEventService } from '../service/space-event.service';

@Injectable({ providedIn: 'root' })
export class SpaceEventRoutingResolveService implements Resolve<ISpaceEvent> {
  constructor(protected service: SpaceEventService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<ISpaceEvent> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((spaceEvent: HttpResponse<SpaceEvent>) => {
          if (spaceEvent.body) {
            return of(spaceEvent.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new SpaceEvent());
  }
}
