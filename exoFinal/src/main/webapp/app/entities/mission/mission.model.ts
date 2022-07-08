export interface IMission {
  id?: number;
  name?: string;
  description?: string | null;
}

export class Mission implements IMission {
  constructor(public id?: number, public name?: string, public description?: string | null) {}
}

export function getMissionIdentifier(mission: IMission): number | undefined {
  return mission.id;
}
