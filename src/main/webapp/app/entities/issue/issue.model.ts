import { ICandidate } from 'app/entities/candidate/candidate.model';

export interface IIssue {
  id: number;
  name?: string | null;
  description?: string | null;
  candidate?: Pick<ICandidate, 'id'> | null;
}

export type NewIssue = Omit<IIssue, 'id'> & { id: null };
