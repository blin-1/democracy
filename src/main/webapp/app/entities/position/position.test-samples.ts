import { IPosition, NewPosition } from './position.model';

export const sampleWithRequiredData: IPosition = {
  id: 23088,
  statement: '../fake-data/blob/hipster.txt',
};

export const sampleWithPartialData: IPosition = {
  id: 16431,
  statement: '../fake-data/blob/hipster.txt',
};

export const sampleWithFullData: IPosition = {
  id: 15668,
  statement: '../fake-data/blob/hipster.txt',
};

export const sampleWithNewData: NewPosition = {
  statement: '../fake-data/blob/hipster.txt',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
