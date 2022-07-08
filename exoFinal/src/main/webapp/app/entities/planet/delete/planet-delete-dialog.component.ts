import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IPlanet } from '../planet.model';
import { PlanetService } from '../service/planet.service';

@Component({
  templateUrl: './planet-delete-dialog.component.html',
})
export class PlanetDeleteDialogComponent {
  planet?: IPlanet;

  constructor(protected planetService: PlanetService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.planetService.delete(id).subscribe(() => {
      this.activeModal.close('deleted');
    });
  }
}
