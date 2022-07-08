import { Component, OnInit, ElementRef } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { FormBuilder, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { ISpaceEvent, SpaceEvent } from '../space-event.model';
import { SpaceEventService } from '../service/space-event.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IMission } from 'app/entities/mission/mission.model';
import { MissionService } from 'app/entities/mission/service/mission.service';
import { SpaceEventType } from 'app/entities/enumerations/space-event-type.model';

@Component({
  selector: 'jhi-space-event-update',
  templateUrl: './space-event-update.component.html',
})
export class SpaceEventUpdateComponent implements OnInit {
  isSaving = false;
  spaceEventTypeValues = Object.keys(SpaceEventType);

  missionsCollection: IMission[] = [];

  editForm = this.fb.group({
    id: [],
    name: [null, [Validators.required]],
    date: [null, [Validators.required]],
    description: [null, [Validators.required]],
    photo: [null, [Validators.required]],
    photoContentType: [],
    type: [null, [Validators.required]],
    mission: [],
  });

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected spaceEventService: SpaceEventService,
    protected missionService: MissionService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute,
    protected fb: FormBuilder
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ spaceEvent }) => {
      this.updateForm(spaceEvent);

      this.loadRelationshipsOptions();
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('exoFinalApp.error', { message: err.message })),
    });
  }

  clearInputImage(field: string, fieldContentType: string, idInput: string): void {
    this.editForm.patchValue({
      [field]: null,
      [fieldContentType]: null,
    });
    if (idInput && this.elementRef.nativeElement.querySelector('#' + idInput)) {
      this.elementRef.nativeElement.querySelector('#' + idInput).value = null;
    }
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const spaceEvent = this.createFromForm();
    if (spaceEvent.id !== undefined) {
      this.subscribeToSaveResponse(this.spaceEventService.update(spaceEvent));
    } else {
      this.subscribeToSaveResponse(this.spaceEventService.create(spaceEvent));
    }
  }

  trackMissionById(_index: number, item: IMission): number {
    return item.id!;
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ISpaceEvent>>): void {
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

  protected updateForm(spaceEvent: ISpaceEvent): void {
    this.editForm.patchValue({
      id: spaceEvent.id,
      name: spaceEvent.name,
      date: spaceEvent.date,
      description: spaceEvent.description,
      photo: spaceEvent.photo,
      photoContentType: spaceEvent.photoContentType,
      type: spaceEvent.type,
      mission: spaceEvent.mission,
    });

    this.missionsCollection = this.missionService.addMissionToCollectionIfMissing(this.missionsCollection, spaceEvent.mission);
  }

  protected loadRelationshipsOptions(): void {
    this.missionService
      .query({ filter: 'spaceevent-is-null' })
      .pipe(map((res: HttpResponse<IMission[]>) => res.body ?? []))
      .pipe(
        map((missions: IMission[]) => this.missionService.addMissionToCollectionIfMissing(missions, this.editForm.get('mission')!.value))
      )
      .subscribe((missions: IMission[]) => (this.missionsCollection = missions));
  }

  protected createFromForm(): ISpaceEvent {
    return {
      ...new SpaceEvent(),
      id: this.editForm.get(['id'])!.value,
      name: this.editForm.get(['name'])!.value,
      date: this.editForm.get(['date'])!.value,
      description: this.editForm.get(['description'])!.value,
      photoContentType: this.editForm.get(['photoContentType'])!.value,
      photo: this.editForm.get(['photo'])!.value,
      type: this.editForm.get(['type'])!.value,
      mission: this.editForm.get(['mission'])!.value,
    };
  }
}
