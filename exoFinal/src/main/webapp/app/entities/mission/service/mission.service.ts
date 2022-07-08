import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IMission, getMissionIdentifier } from '../mission.model';

export type EntityResponseType = HttpResponse<IMission>;
export type EntityArrayResponseType = HttpResponse<IMission[]>;

@Injectable({ providedIn: 'root' })
export class MissionService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/missions');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(mission: IMission): Observable<EntityResponseType> {
    return this.http.post<IMission>(this.resourceUrl, mission, { observe: 'response' });
  }

  update(mission: IMission): Observable<EntityResponseType> {
    return this.http.put<IMission>(`${this.resourceUrl}/${getMissionIdentifier(mission) as number}`, mission, { observe: 'response' });
  }

  partialUpdate(mission: IMission): Observable<EntityResponseType> {
    return this.http.patch<IMission>(`${this.resourceUrl}/${getMissionIdentifier(mission) as number}`, mission, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IMission>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IMission[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addMissionToCollectionIfMissing(missionCollection: IMission[], ...missionsToCheck: (IMission | null | undefined)[]): IMission[] {
    const missions: IMission[] = missionsToCheck.filter(isPresent);
    if (missions.length > 0) {
      const missionCollectionIdentifiers = missionCollection.map(missionItem => getMissionIdentifier(missionItem)!);
      const missionsToAdd = missions.filter(missionItem => {
        const missionIdentifier = getMissionIdentifier(missionItem);
        if (missionIdentifier == null || missionCollectionIdentifiers.includes(missionIdentifier)) {
          return false;
        }
        missionCollectionIdentifiers.push(missionIdentifier);
        return true;
      });
      return [...missionsToAdd, ...missionCollection];
    }
    return missionCollection;
  }
}
