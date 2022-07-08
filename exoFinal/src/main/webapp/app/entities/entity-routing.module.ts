import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'space-event',
        data: { pageTitle: 'SpaceEvents' },
        loadChildren: () => import('./space-event/space-event.module').then(m => m.SpaceEventModule),
      },
      {
        path: 'mission',
        data: { pageTitle: 'Missions' },
        loadChildren: () => import('./mission/mission.module').then(m => m.MissionModule),
      },
      {
        path: 'planet',
        data: { pageTitle: 'Planets' },
        loadChildren: () => import('./planet/planet.module').then(m => m.PlanetModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
