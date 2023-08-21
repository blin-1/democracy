import { IOffice } from 'app/entities/office/office.model';
import { Party } from 'app/entities/enumerations/party.model';

export interface ICandidate {
  id: number;
  firstName?: string | null;
  lastName?: string | null;
  email?: string | null;
  party?: keyof typeof Party | null;
  pic?: string | null;
  picContentType?: string | null;
  bio?: string | null;
  office?: Pick<IOffice, 'id'> | null;
}

export type NewCandidate = Omit<ICandidate, 'id'> & { id: null };
