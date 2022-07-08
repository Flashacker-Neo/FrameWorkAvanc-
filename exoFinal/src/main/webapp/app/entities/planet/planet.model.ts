import dayjs from 'dayjs/esm';
import { PlanetTypes } from 'app/entities/enumerations/planet-types.model';

export interface IPlanet {
  id?: number;
  name?: string;
  distance?: number;
  type?: PlanetTypes;
  satellite?: dayjs.Dayjs | null;
}

export class Planet implements IPlanet {
  constructor(
    public id?: number,
    public name?: string,
    public distance?: number,
    public type?: PlanetTypes,
    public satellite?: dayjs.Dayjs | null
  ) {}
}

export function getPlanetIdentifier(planet: IPlanet): number | undefined {
  return planet.id;
}
