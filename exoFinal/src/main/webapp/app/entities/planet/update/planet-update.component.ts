import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';

import { IPlanet, Planet } from '../planet.model';
import { PlanetService } from '../service/planet.service';
import { PlanetTypes } from 'app/entities/enumerations/planet-types.model';

@Component({
  selector: 'jhi-planet-update',
  templateUrl: './planet-update.component.html',
})
export class PlanetUpdateComponent implements OnInit {
  isSaving = false;
  planetTypesValues = Object.keys(PlanetTypes);

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required, Validators.minLength(3)]],
    distance: [null, [Validators.required, Validators.min(1)]],
    type: [null, [Validators.required]],
    satellite: [],
  });

  constructor(protected planetService: PlanetService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ planet }) => {
      if (planet.id === undefined) {
        const today = dayjs().startOf('day');
        planet.satellite = today;
      }

      this.updateForm(planet);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const planet = this.createFromForm();
    if (planet.id !== undefined) {
      this.subscribeToSaveResponse(this.planetService.update(planet));
    } else {
      this.subscribeToSaveResponse(this.planetService.create(planet));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPlanet>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(planet: IPlanet): void {
    this.editForm.patchValue({
      id: planet.id,
      name: planet.name,
      distance: planet.distance,
      type: planet.type,
      satellite: planet.satellite ? planet.satellite.format(DATE_TIME_FORMAT) : null,
    });
  }

  protected createFromForm(): IPlanet {
    return {
      ...new Planet(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      distance: this.editForm.get(['distance'])!.value,
      type: this.editForm.get(['type'])!.value,
      satellite: this.editForm.get(['satellite'])!.value ? dayjs(this.editForm.get(['satellite'])!.value, DATE_TIME_FORMAT) : undefined,
    };
  }
}
