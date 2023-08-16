import { State } from 'app/entities/enumerations/state.model';

export interface IAddress {
  id: number;
  line1?: string | null;
  line2?: string | null;
  city?: string | null;
  state?: keyof typeof State | null;
  zip?: string | null;
}

export type NewAddress = Omit<IAddress, 'id'> & { id: null };
