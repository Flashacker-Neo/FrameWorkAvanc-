import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { PlanetComponent } from './list/planet.component';
import { PlanetDetailComponent } from './detail/planet-detail.component';
import { PlanetUpdateComponent } from './update/planet-update.component';
import { PlanetDeleteDialogComponent } from './delete/planet-delete-dialog.component';
import { PlanetRoutingModule } from './route/planet-routing.module';

@NgModule({
  imports: [SharedModule, PlanetRoutingModule],
  declarations: [PlanetComponent, PlanetDetailComponent, PlanetUpdateComponent, PlanetDeleteDialogComponent],
  entryComponents: [PlanetDeleteDialogComponent],
})
export class PlanetModule {}
