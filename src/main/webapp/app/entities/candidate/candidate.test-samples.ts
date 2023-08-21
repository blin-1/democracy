import { Party } from 'app/entities/enumerations/party.model';

import { ICandidate, NewCandidate } from './candidate.model';

export const sampleWithRequiredData: ICandidate = {
  id: 8756,
  firstName: 'Cole',
  lastName: 'Hamill',
  email: 'Teagan62@yahoo.com',
  party: 'I',
  pic: '../fake-data/blob/hipster.png',
  picContentType: 'unknown',
  bio: '../fake-data/blob/hipster.txt',
};

export const sampleWithPartialData: ICandidate = {
  id: 13108,
  firstName: 'Maribel',
  lastName: 'Ankunding',
  email: 'Else.Bruen36@hotmail.com',
  party: 'U',
  pic: '../fake-data/blob/hipster.png',
  picContentType: 'unknown',
  bio: '../fake-data/blob/hipster.txt',
};

export const sampleWithFullData: ICandidate = {
  id: 17818,
  firstName: 'Rowan',
  lastName: 'Larkin',
  email: 'Zackary6@hotmail.com',
  party: 'U',
  pic: '../fake-data/blob/hipster.png',
  picContentType: 'unknown',
  bio: '../fake-data/blob/hipster.txt',
};

export const sampleWithNewData: NewCandidate = {
  firstName: 'Kitty',
  lastName: 'Boyle',
  email: 'Cristal.Schmeler@yahoo.com',
  party: 'R',
  pic: '../fake-data/blob/hipster.png',
  picContentType: 'unknown',
  bio: '../fake-data/blob/hipster.txt',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
