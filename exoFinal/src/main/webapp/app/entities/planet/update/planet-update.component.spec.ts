import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { PlanetService } from '../service/planet.service';
import { IPlanet, Planet } from '../planet.model';

import { PlanetUpdateComponent } from './planet-update.component';

describe('Planet Management Update Component', () => {
  let comp: PlanetUpdateComponent;
  let fixture: ComponentFixture<PlanetUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let planetService: PlanetService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [PlanetUpdateComponent],
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
      .overrideTemplate(PlanetUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PlanetUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    planetService = TestBed.inject(PlanetService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const planet: IPlanet = { id: 456 };

      activatedRoute.data = of({ planet });
      comp.ngOnInit();

      expect(comp.editForm.value).toEqual(expect.objectContaining(planet));
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Planet>>();
      const planet = { id: 123 };
      jest.spyOn(planetService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ planet });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: planet }));
      saveSubject.complete();

      // THEN
      expect(comp.previousState).toHaveBeenCalled();
      expect(planetService.update).toHaveBeenCalledWith(planet);
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Planet>>();
      const planet = new Planet();
      jest.spyOn(planetService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ planet });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: planet }));
      saveSubject.complete();

      // THEN
      expect(planetService.create).toHaveBeenCalledWith(planet);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<Planet>>();
      const planet = { id: 123 };
      jest.spyOn(planetService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ planet });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(planetService.update).toHaveBeenCalledWith(planet);
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
