import { IOffice } from 'app/entities/office/office.model';
import { IAddress } from 'app/entities/address/address.model';

export interface ICandidate {
  id: number;
  firstName?: string | null;
  lastName?: string | null;
  email?: string | null;
  pic?: string | null;
  picContentType?: string | null;
  office?: Pick<IOffice, 'id'> | null;
  address?: Pick<IAddress, 'id'> | null;
}

export type NewCandidate = Omit<ICandidate, 'id'> & { id: null };
