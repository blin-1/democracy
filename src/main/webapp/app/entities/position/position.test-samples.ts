import { IPosition, NewPosition } from './position.model';

export const sampleWithRequiredData: IPosition = {
  id: 23088,
  available: 16431,
};

export const sampleWithPartialData: IPosition = {
  id: 15668,
  available: 9916,
};

export const sampleWithFullData: IPosition = {
  id: 22957,
  available: 24678,
};

export const sampleWithNewData: NewPosition = {
  available: 8582,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
