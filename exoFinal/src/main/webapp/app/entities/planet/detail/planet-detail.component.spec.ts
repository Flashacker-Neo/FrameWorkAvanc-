import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { PlanetDetailComponent } from './planet-detail.component';

describe('Planet Management Detail Component', () => {
  let comp: PlanetDetailComponent;
  let fixture: ComponentFixture<PlanetDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PlanetDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ planet: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(PlanetDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(PlanetDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load planet on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.planet).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
