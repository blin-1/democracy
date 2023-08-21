import dayjs from 'dayjs/esm';
import { State } from 'app/entities/enumerations/state.model';

export interface IOffice {
  id: number;
  name?: string | null;
  municipality?: string | null;
  state?: keyof typeof State | null;
  electionDate?: dayjs.Dayjs | null;
}

export type NewOffice = Omit<IOffice, 'id'> & { id: null };
