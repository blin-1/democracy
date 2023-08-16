import { ICandidate } from 'app/entities/candidate/candidate.model';
import { IIssue } from 'app/entities/issue/issue.model';

export interface IPosition {
  id: number;
  available?: number | null;
  candidate?: Pick<ICandidate, 'id'> | null;
  issue?: Pick<IIssue, 'id'> | null;
}

export type NewPosition = Omit<IPosition, 'id'> & { id: null };
