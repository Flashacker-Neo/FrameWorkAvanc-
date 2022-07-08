import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IMission } from '../mission.model';
import { MissionService } from '../service/mission.service';

@Component({
  templateUrl: './mission-delete-dialog.component.html',
})
export class MissionDeleteDialogComponent {
  mission?: IMission;

  constructor(protected missionService: MissionService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.missionService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
