import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { PlanetComponent } from '../list/planet.component';
import { PlanetDetailComponent } from '../detail/planet-detail.component';
import { PlanetUpdateComponent } from '../update/planet-update.component';
import { PlanetRoutingResolveService } from './planet-routing-resolve.service';

const planetRoute: Routes = [
  {
    path: '',
    component: PlanetComponent,
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: PlanetDetailComponent,
    resolve: {
      planet: PlanetRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: PlanetUpdateComponent,
    resolve: {
      planet: PlanetRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: PlanetUpdateComponent,
    resolve: {
      planet: PlanetRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(planetRoute)],
  exports: [RouterModule],
})
export class PlanetRoutingModule {}
