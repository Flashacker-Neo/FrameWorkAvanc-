import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import dayjs from 'dayjs/esm';

import { DATE_FORMAT } from 'app/config/input.constants';
import { SpaceEventType } from 'app/entities/enumerations/space-event-type.model';
import { ISpaceEvent, SpaceEvent } from '../space-event.model';

import { SpaceEventService } from './space-event.service';

describe('SpaceEvent Service', () => {
  let service: SpaceEventService;
  let httpMock: HttpTestingController;
  let elemDefault: ISpaceEvent;
  let expectedResult: ISpaceEvent | ISpaceEvent[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(SpaceEventService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      name: 'AAAAAAA',
      date: currentDate,
      description: 'AAAAAAA',
      photoContentType: 'image/png',
      photo: 'AAAAAAA',
      type: SpaceEventType.LAUNCH,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          date: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a SpaceEvent', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
          date: currentDate.format(DATE_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          date: currentDate,
        },
        returnedFromService
      );

      service.create(new SpaceEvent()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a SpaceEvent', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          date: currentDate.format(DATE_FORMAT),
          description: 'BBBBBB',
          photo: 'BBBBBB',
          type: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          date: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a SpaceEvent', () => {
      const patchObject = Object.assign(
        {
          name: 'BBBBBB',
          type: 'BBBBBB',
        },
        new SpaceEvent()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          date: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of SpaceEvent', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          name: 'BBBBBB',
          date: currentDate.format(DATE_FORMAT),
          description: 'BBBBBB',
          photo: 'BBBBBB',
          type: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          date: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a SpaceEvent', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addSpaceEventToCollectionIfMissing', () => {
      it('should add a SpaceEvent to an empty array', () => {
        const spaceEvent: ISpaceEvent = { id: 123 };
        expectedResult = service.addSpaceEventToCollectionIfMissing([], spaceEvent);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(spaceEvent);
      });

      it('should not add a SpaceEvent to an array that contains it', () => {
        const spaceEvent: ISpaceEvent = { id: 123 };
        const spaceEventCollection: ISpaceEvent[] = [
          {
            ...spaceEvent,
          },
          { id: 456 },
        ];
        expectedResult = service.addSpaceEventToCollectionIfMissing(spaceEventCollection, spaceEvent);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a SpaceEvent to an array that doesn't contain it", () => {
        const spaceEvent: ISpaceEvent = { id: 123 };
        const spaceEventCollection: ISpaceEvent[] = [{ id: 456 }];
        expectedResult = service.addSpaceEventToCollectionIfMissing(spaceEventCollection, spaceEvent);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(spaceEvent);
      });

      it('should add only unique SpaceEvent to an array', () => {
        const spaceEventArray: ISpaceEvent[] = [{ id: 123 }, { id: 456 }, { id: 49963 }];
        const spaceEventCollection: ISpaceEvent[] = [{ id: 123 }];
        expectedResult = service.addSpaceEventToCollectionIfMissing(spaceEventCollection, ...spaceEventArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const spaceEvent: ISpaceEvent = { id: 123 };
        const spaceEvent2: ISpaceEvent = { id: 456 };
        expectedResult = service.addSpaceEventToCollectionIfMissing([], spaceEvent, spaceEvent2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(spaceEvent);
        expect(expectedResult).toContain(spaceEvent2);
      });

      it('should accept null and undefined values', () => {
        const spaceEvent: ISpaceEvent = { id: 123 };
        expectedResult = service.addSpaceEventToCollectionIfMissing([], null, spaceEvent, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(spaceEvent);
      });

      it('should return initial array if no SpaceEvent is added', () => {
        const spaceEventCollection: ISpaceEvent[] = [{ id: 123 }];
        expectedResult = service.addSpaceEventToCollectionIfMissing(spaceEventCollection, undefined, null);
        expect(expectedResult).toEqual(spaceEventCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
