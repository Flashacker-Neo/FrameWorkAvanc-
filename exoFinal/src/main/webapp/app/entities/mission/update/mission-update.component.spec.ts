import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { MissionService } from '../service/mission.service';
import { IMission, Mission } from '../mission.model';

import { MissionUpdateComponent } from './mission-update.component';

describe('Mission Management Update Component', () => {
  let comp: MissionUpdateComponent;
  let fixture: ComponentFixture<MissionUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let missionService: MissionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [MissionUpdateComponent],
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
      .overrideTemplate(MissionUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(MissionUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    missionService = TestBed.inject(MissionService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const mission: IMission = { id: 456 };

      activatedRoute.data = of({ mission });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(mission));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Mission>>();
      const mission = { id: 123 };
      jest.spyOn(missionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ mission });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: mission }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(missionService.update).toHaveBeenCalledWith(mission);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Mission>>();
      const mission = new Mission();
      jest.spyOn(missionService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ mission });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: mission }));
      saveSubject.complete();

      // THEN
      expect(missionService.create).toHaveBeenCalledWith(mission);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Mission>>();
      const mission = { id: 123 };
      jest.spyOn(missionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ mission });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(missionService.update).toHaveBeenCalledWith(mission);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
