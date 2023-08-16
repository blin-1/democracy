export interface IIssue {
  id: number;
  name?: string | null;
  description?: string | null;
}

export type NewIssue = Omit<IIssue, 'id'> & { id: null };
