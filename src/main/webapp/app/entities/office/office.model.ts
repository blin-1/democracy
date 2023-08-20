import { State } from 'app/entities/enumerations/state.model';
import { YesNo } from 'app/entities/enumerations/yes-no.model';

export interface IOffice {
  id: number;
  state?: keyof typeof State | null;
  municipality?: string | null;
  federal?: keyof typeof YesNo | null;
}

export type NewOffice = Omit<IOffice, 'id'> & { id: null };
