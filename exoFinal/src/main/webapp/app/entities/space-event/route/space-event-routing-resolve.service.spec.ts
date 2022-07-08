import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, ActivatedRoute, Router, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { ISpaceEvent, SpaceEvent } from '../space-event.model';
import { SpaceEventService } from '../service/space-event.service';

import { SpaceEventRoutingResolveService } from './space-event-routing-resolve.service';

describe('SpaceEvent routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: SpaceEventRoutingResolveService;
  let service: SpaceEventService;
  let resultSpaceEvent: ISpaceEvent | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              paramMap: convertToParamMap({}),
            },
          },
        },
      ],
    });
    mockRouter = TestBed.inject(Router);
    jest.spyOn(mockRouter, 'navigate').mockImplementation(() => Promise.resolve(true));
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRoute).snapshot;
    routingResolveService = TestBed.inject(SpaceEventRoutingResolveService);
    service = TestBed.inject(SpaceEventService);
    resultSpaceEvent = undefined;
  });

  describe('resolve', () => {
    it('should return ISpaceEvent returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultSpaceEvent = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultSpaceEvent).toEqual({ id: 123 });
    });

    it('should return new ISpaceEvent if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultSpaceEvent = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultSpaceEvent).toEqual(new SpaceEvent());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as SpaceEvent })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultSpaceEvent = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultSpaceEvent).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
