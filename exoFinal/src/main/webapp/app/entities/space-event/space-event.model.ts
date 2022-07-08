import dayjs from 'dayjs/esm';
import { IMission } from 'app/entities/mission/mission.model';
import { SpaceEventType } from 'app/entities/enumerations/space-event-type.model';

export interface ISpaceEvent {
  id?: number;
  name?: string;
  date?: dayjs.Dayjs;
  description?: string;
  photoContentType?: string;
  photo?: string;
  type?: SpaceEventType;
  mission?: IMission | null;
}

export class SpaceEvent implements ISpaceEvent {
  constructor(
    public id?: number,
    public name?: string,
    public date?: dayjs.Dayjs,
    public description?: string,
    public photoContentType?: string,
    public photo?: string,
    public type?: SpaceEventType,
    public mission?: IMission | null
  ) {}
}

export function getSpaceEventIdentifier(spaceEvent: ISpaceEvent): number | undefined {
  return spaceEvent.id;
}
