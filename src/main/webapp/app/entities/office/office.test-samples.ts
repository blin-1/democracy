import { State } from 'app/entities/enumerations/state.model';
import { YesNo } from 'app/entities/enumerations/yes-no.model';

import { IOffice, NewOffice } from './office.model';

export const sampleWithRequiredData: IOffice = {
  id: 11074,
};

export const sampleWithPartialData: IOffice = {
  id: 5954,
  federal: 'Y',
};

export const sampleWithFullData: IOffice = {
  id: 2612,
  state: 'AL',
  municipality: 'Future Phased eek',
  federal: 'Y',
};

export const sampleWithNewData: NewOffice = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
