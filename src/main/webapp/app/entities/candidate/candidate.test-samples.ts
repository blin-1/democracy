import { ICandidate, NewCandidate } from './candidate.model';

export const sampleWithRequiredData: ICandidate = {
  id: 24569,
  firstName: 'Jack',
  lastName: 'Dooley',
  email: 'Dale50@hotmail.com',
  pic: '../fake-data/blob/hipster.png',
  picContentType: 'unknown',
};

export const sampleWithPartialData: ICandidate = {
  id: 8509,
  firstName: 'Lilyan',
  lastName: 'Jacobs',
  email: 'Maribel11@gmail.com',
  pic: '../fake-data/blob/hipster.png',
  picContentType: 'unknown',
};

export const sampleWithFullData: ICandidate = {
  id: 30527,
  firstName: 'Reanna',
  lastName: 'Hickle',
  email: 'Zion_Stark@yahoo.com',
  pic: '../fake-data/blob/hipster.png',
  picContentType: 'unknown',
};

export const sampleWithNewData: NewCandidate = {
  firstName: 'Cassidy',
  lastName: 'Schuppe',
  email: 'Colin9@yahoo.com',
  pic: '../fake-data/blob/hipster.png',
  picContentType: 'unknown',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
