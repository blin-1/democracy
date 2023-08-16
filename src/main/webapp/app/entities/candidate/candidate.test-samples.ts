import { ICandidate, NewCandidate } from './candidate.model';

export const sampleWithRequiredData: ICandidate = {
  id: 14893,
  firstName: 'Dock',
  lastName: 'Ernser',
  email: 'Emilie.Welch@gmail.com',
  pic: '../fake-data/blob/hipster.png',
  picContentType: 'unknown',
  imageUrl: 'variant Lead',
};

export const sampleWithPartialData: ICandidate = {
  id: 10141,
  firstName: 'Adrianna',
  lastName: 'Waters',
  email: 'Reanna_Hickle@hotmail.com',
  pic: '../fake-data/blob/hipster.png',
  picContentType: 'unknown',
  imageUrl: 'matrix Automotive',
};

export const sampleWithFullData: ICandidate = {
  id: 26840,
  firstName: 'Colin',
  lastName: 'Stanton',
  email: 'Kitty79@gmail.com',
  pic: '../fake-data/blob/hipster.png',
  picContentType: 'unknown',
  imageUrl: 'Demigender Books',
};

export const sampleWithNewData: NewCandidate = {
  firstName: 'Verner',
  lastName: 'Nolan',
  email: 'Delbert_Brekke@hotmail.com',
  pic: '../fake-data/blob/hipster.png',
  picContentType: 'unknown',
  imageUrl: 'Northwest',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
