import { IOffice } from 'app/entities/office/office.model';

export interface ICandidate {
  id: number;
  firstName?: string | null;
  lastName?: string | null;
  email?: string | null;
  pic?: string | null;
  picContentType?: string | null;
  office?: Pick<IOffice, 'id'> | null;
}

export type NewCandidate = Omit<ICandidate, 'id'> & { id: null };
