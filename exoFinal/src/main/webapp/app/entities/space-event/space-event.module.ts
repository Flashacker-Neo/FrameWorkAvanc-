import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { SpaceEventComponent } from './list/space-event.component';
import { SpaceEventDetailComponent } from './detail/space-event-detail.component';
import { SpaceEventUpdateComponent } from './update/space-event-update.component';
import { SpaceEventDeleteDialogComponent } from './delete/space-event-delete-dialog.component';
import { SpaceEventRoutingModule } from './route/space-event-routing.module';

@NgModule({
  imports: [SharedModule, SpaceEventRoutingModule],
  declarations: [SpaceEventComponent, SpaceEventDetailComponent, SpaceEventUpdateComponent, SpaceEventDeleteDialogComponent],
  entryComponents: [SpaceEventDeleteDialogComponent],
})
export class SpaceEventModule {}
