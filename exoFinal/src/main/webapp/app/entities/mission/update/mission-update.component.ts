import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { IMission, Mission } from '../mission.model';
import { MissionService } from '../service/mission.service';

@Component({
  selector: 'jhi-mission-update',
  templateUrl: './mission-update.component.html',
})
export class MissionUpdateComponent implements OnInit {
  isSaving = false;

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    description: [],
  });

  constructor(protected missionService: MissionService, protected activatedRoute: ActivatedRoute, protected fb: FormBuilder) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ mission }) => {
      this.updateForm(mission);
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const mission = this.createFromForm();
    if (mission.id !== undefined) {
      this.subscribeToSaveResponse(this.missionService.update(mission));
    } else {
      this.subscribeToSaveResponse(this.missionService.create(mission));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IMission>>): void {
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

  protected updateForm(mission: IMission): void {
    this.editForm.patchValue({
      id: mission.id,
      name: mission.name,
      description: mission.description,
    });
  }

  protected createFromForm(): IMission {
    return {
      ...new Mission(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      description: this.editForm.get(['description'])!.value,
    };
  }
}
