import { IIssue } from 'app/entities/issue/issue.model';
import { ICandidate } from 'app/entities/candidate/candidate.model';

export interface IPosition {
  id: number;
  statement?: string | null;
  issue?: Pick<IIssue, 'id'> | null;
  candidate?: Pick<ICandidate, 'id'> | null;
}

export type NewPosition = Omit<IPosition, 'id'> & { id: null };
