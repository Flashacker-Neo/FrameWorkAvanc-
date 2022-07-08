import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IMission, Mission } from '../mission.model';

import { MissionService } from './mission.service';

describe('Mission Service', () => {
  let service: MissionService;
  let httpMock: HttpTestingController;
  let elemDefault: IMission;
  let expectedResult: IMission | IMission[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(MissionService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      name: 'AAAAAAA',
      description: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Mission', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Mission()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Mission', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          description: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Mission', () => {
      const patchObject = Object.assign(
        {
          description: 'BBBBBB',
        },
        new Mission()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Mission', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          description: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Mission', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addMissionToCollectionIfMissing', () => {
      it('should add a Mission to an empty array', () => {
        const mission: IMission = { id: 123 };
        expectedResult = service.addMissionToCollectionIfMissing([], mission);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(mission);
      });

      it('should not add a Mission to an array that contains it', () => {
        const mission: IMission = { id: 123 };
        const missionCollection: IMission[] = [
          {
            ...mission,
          },
          { id: 456 },
        ];
        expectedResult = service.addMissionToCollectionIfMissing(missionCollection, mission);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Mission to an array that doesn't contain it", () => {
        const mission: IMission = { id: 123 };
        const missionCollection: IMission[] = [{ id: 456 }];
        expectedResult = service.addMissionToCollectionIfMissing(missionCollection, mission);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(mission);
      });

      it('should add only unique Mission to an array', () => {
        const missionArray: IMission[] = [{ id: 123 }, { id: 456 }, { id: 58711 }];
        const missionCollection: IMission[] = [{ id: 123 }];
        expectedResult = service.addMissionToCollectionIfMissing(missionCollection, ...missionArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const mission: IMission = { id: 123 };
        const mission2: IMission = { id: 456 };
        expectedResult = service.addMissionToCollectionIfMissing([], mission, mission2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(mission);
        expect(expectedResult).toContain(mission2);
      });

      it('should accept null and undefined values', () => {
        const mission: IMission = { id: 123 };
        expectedResult = service.addMissionToCollectionIfMissing([], null, mission, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(mission);
      });

      it('should return initial array if no Mission is added', () => {
        const missionCollection: IMission[] = [{ id: 123 }];
        expectedResult = service.addMissionToCollectionIfMissing(missionCollection, undefined, null);
        expect(expectedResult).toEqual(missionCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
