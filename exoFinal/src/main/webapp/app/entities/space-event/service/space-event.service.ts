import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { DATE_FORMAT } from 'app/config/input.constants';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISpaceEvent, getSpaceEventIdentifier } from '../space-event.model';

export type EntityResponseType = HttpResponse<ISpaceEvent>;
export type EntityArrayResponseType = HttpResponse<ISpaceEvent[]>;

@Injectable({ providedIn: 'root' })
export class SpaceEventService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/space-events');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(spaceEvent: ISpaceEvent): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(spaceEvent);
    return this.http
      .post<ISpaceEvent>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(spaceEvent: ISpaceEvent): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(spaceEvent);
    return this.http
      .put<ISpaceEvent>(`${this.resourceUrl}/${getSpaceEventIdentifier(spaceEvent) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(spaceEvent: ISpaceEvent): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(spaceEvent);
    return this.http
      .patch<ISpaceEvent>(`${this.resourceUrl}/${getSpaceEventIdentifier(spaceEvent) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<ISpaceEvent>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<ISpaceEvent[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addSpaceEventToCollectionIfMissing(
    spaceEventCollection: ISpaceEvent[],
    ...spaceEventsToCheck: (ISpaceEvent | null | undefined)[]
  ): ISpaceEvent[] {
    const spaceEvents: ISpaceEvent[] = spaceEventsToCheck.filter(isPresent);
    if (spaceEvents.length > 0) {
      const spaceEventCollectionIdentifiers = spaceEventCollection.map(spaceEventItem => getSpaceEventIdentifier(spaceEventItem)!);
      const spaceEventsToAdd = spaceEvents.filter(spaceEventItem => {
        const spaceEventIdentifier = getSpaceEventIdentifier(spaceEventItem);
        if (spaceEventIdentifier == null || spaceEventCollectionIdentifiers.includes(spaceEventIdentifier)) {
          return false;
        }
        spaceEventCollectionIdentifiers.push(spaceEventIdentifier);
        return true;
      });
      return [...spaceEventsToAdd, ...spaceEventCollection];
    }
    return spaceEventCollection;
  }

  protected convertDateFromClient(spaceEvent: ISpaceEvent): ISpaceEvent {
    return Object.assign({}, spaceEvent, {
      date: spaceEvent.date?.isValid() ? spaceEvent.date.format(DATE_FORMAT) : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.date = res.body.date ? dayjs(res.body.date) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((spaceEvent: ISpaceEvent) => {
        spaceEvent.date = spaceEvent.date ? dayjs(spaceEvent.date) : undefined;
      });
    }
    return res;
  }
}
