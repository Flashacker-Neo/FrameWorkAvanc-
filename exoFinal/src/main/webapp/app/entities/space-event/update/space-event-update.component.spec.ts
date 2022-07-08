import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { SpaceEventService } from '../service/space-event.service';
import { ISpaceEvent, SpaceEvent } from '../space-event.model';
import { IMission } from 'app/entities/mission/mission.model';
import { MissionService } from 'app/entities/mission/service/mission.service';

import { SpaceEventUpdateComponent } from './space-event-update.component';

describe('SpaceEvent Management Update Component', () => {
  let comp: SpaceEventUpdateComponent;
  let fixture: ComponentFixture<SpaceEventUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let spaceEventService: SpaceEventService;
  let missionService: MissionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [SpaceEventUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(SpaceEventUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(SpaceEventUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    spaceEventService = TestBed.inject(SpaceEventService);
    missionService = TestBed.inject(MissionService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call mission query and add missing value', () => {
      const spaceEvent: ISpaceEvent = { id: 456 };
      const mission: IMission = { id: 36423 };
      spaceEvent.mission = mission;

      const missionCollection: IMission[] = [{ id: 99056 }];
      jest.spyOn(missionService, 'query').mockReturnValue(of(new HttpResponse({ body: missionCollection })));
      const expectedCollection: IMission[] = [mission, ...missionCollection];
      jest.spyOn(missionService, 'addMissionToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ spaceEvent });
      comp.ngOnInit();

      expect(missionService.query).toHaveBeenCalled();
      expect(missionService.addMissionToCollectionIfMissing).toHaveBeenCalledWith(missionCollection, mission);
      expect(comp.missionsCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const spaceEvent: ISpaceEvent = { id: 456 };
      const mission: IMission = { id: 72152 };
      spaceEvent.mission = mission;

      activatedRoute.data = of({ spaceEvent });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(spaceEvent));
      expect(comp.missionsCollection).toContain(mission);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<SpaceEvent>>();
      const spaceEvent = { id: 123 };
      jest.spyOn(spaceEventService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ spaceEvent });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: spaceEvent }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(spaceEventService.update).toHaveBeenCalledWith(spaceEvent);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<SpaceEvent>>();
      const spaceEvent = new SpaceEvent();
      jest.spyOn(spaceEventService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ spaceEvent });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: spaceEvent }));
      saveSubject.complete();

      // THEN
      expect(spaceEventService.create).toHaveBeenCalledWith(spaceEvent);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<SpaceEvent>>();
      const spaceEvent = { id: 123 };
      jest.spyOn(spaceEventService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ spaceEvent });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(spaceEventService.update).toHaveBeenCalledWith(spaceEvent);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Tracking relationships identifiers', () => {
    describe('trackMissionById', () => {
      it('Should return tracked Mission primary key', () => {
        const entity = { id: 123 };
        const trackResult = comp.trackMissionById(0, entity);
        expect(trackResult).toEqual(entity.id);
      });
    });
  });
});
