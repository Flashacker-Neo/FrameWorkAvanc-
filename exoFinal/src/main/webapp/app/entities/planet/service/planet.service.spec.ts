import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { PlanetTypes } from 'app/entities/enumerations/planet-types.model';
import { IPlanet, Planet } from '../planet.model';

import { PlanetService } from './planet.service';

describe('Planet Service', () => {
  let service: PlanetService;
  let httpMock: HttpTestingController;
  let elemDefault: IPlanet;
  let expectedResult: IPlanet | IPlanet[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(PlanetService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      name: 'AAAAAAA',
      distance: 0,
      type: PlanetTypes.GAZ,
      satellite: currentDate,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          satellite: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Planet', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
          satellite: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          satellite: currentDate,
        },
        returnedFromService
      );

      service.create(new Planet()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Planet', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          distance: 1,
          type: 'BBBBBB',
          satellite: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          satellite: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Planet', () => {
      const patchObject = Object.assign(
        {
          name: 'BBBBBB',
          satellite: currentDate.format(DATE_TIME_FORMAT),
        },
        new Planet()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          satellite: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Planet', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          distance: 1,
          type: 'BBBBBB',
          satellite: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          satellite: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Planet', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addPlanetToCollectionIfMissing', () => {
      it('should add a Planet to an empty array', () => {
        const planet: IPlanet = { id: 123 };
        expectedResult = service.addPlanetToCollectionIfMissing([], planet);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(planet);
      });

      it('should not add a Planet to an array that contains it', () => {
        const planet: IPlanet = { id: 123 };
        const planetCollection: IPlanet[] = [
          {
            ...planet,
          },
          { id: 456 },
        ];
        expectedResult = service.addPlanetToCollectionIfMissing(planetCollection, planet);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Planet to an array that doesn't contain it", () => {
        const planet: IPlanet = { id: 123 };
        const planetCollection: IPlanet[] = [{ id: 456 }];
        expectedResult = service.addPlanetToCollectionIfMissing(planetCollection, planet);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(planet);
      });

      it('should add only unique Planet to an array', () => {
        const planetArray: IPlanet[] = [{ id: 123 }, { id: 456 }, { id: 7120 }];
        const planetCollection: IPlanet[] = [{ id: 123 }];
        expectedResult = service.addPlanetToCollectionIfMissing(planetCollection, ...planetArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const planet: IPlanet = { id: 123 };
        const planet2: IPlanet = { id: 456 };
        expectedResult = service.addPlanetToCollectionIfMissing([], planet, planet2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(planet);
        expect(expectedResult).toContain(planet2);
      });

      it('should accept null and undefined values', () => {
        const planet: IPlanet = { id: 123 };
        expectedResult = service.addPlanetToCollectionIfMissing([], null, planet, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(planet);
      });

      it('should return initial array if no Planet is added', () => {
        const planetCollection: IPlanet[] = [{ id: 123 }];
        expectedResult = service.addPlanetToCollectionIfMissing(planetCollection, undefined, null);
        expect(expectedResult).toEqual(planetCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
