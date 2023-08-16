import { IIssue, NewIssue } from './issue.model';

export const sampleWithRequiredData: IIssue = {
  id: 11394,
  name: 'Modern Borders',
  description: '../fake-data/blob/hipster.txt',
};

export const sampleWithPartialData: IIssue = {
  id: 932,
  name: 'pixel Bicycle as',
  description: '../fake-data/blob/hipster.txt',
};

export const sampleWithFullData: IIssue = {
  id: 29647,
  name: 'capacitor',
  description: '../fake-data/blob/hipster.txt',
};

export const sampleWithNewData: NewIssue = {
  name: 'withdrawal',
  description: '../fake-data/blob/hipster.txt',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
