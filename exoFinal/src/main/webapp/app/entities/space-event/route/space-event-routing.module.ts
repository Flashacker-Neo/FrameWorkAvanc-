import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { SpaceEventComponent } from '../list/space-event.component';
import { SpaceEventDetailComponent } from '../detail/space-event-detail.component';
import { SpaceEventUpdateComponent } from '../update/space-event-update.component';
import { SpaceEventRoutingResolveService } from './space-event-routing-resolve.service';

const spaceEventRoute: Routes = [
  {
    path: '',
    component: SpaceEventComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: SpaceEventDetailComponent,
    resolve: {
      spaceEvent: SpaceEventRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: SpaceEventUpdateComponent,
    resolve: {
      spaceEvent: SpaceEventRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: SpaceEventUpdateComponent,
    resolve: {
      spaceEvent: SpaceEventRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(spaceEventRoute)],
  exports: [RouterModule],
})
export class SpaceEventRoutingModule {}
