import dayjs from 'dayjs/esm';

import { State } from 'app/entities/enumerations/state.model';

import { IOffice, NewOffice } from './office.model';

export const sampleWithRequiredData: IOffice = {
  id: 16519,
};

export const sampleWithPartialData: IOffice = {
  id: 2612,
  municipality: 'lumen',
  state: 'AL',
  electionDate: dayjs('2023-08-20T12:45'),
};

export const sampleWithFullData: IOffice = {
  id: 19940,
  name: 'nonconforming Music Northeast',
  municipality: 'lest Planner Avon',
  state: 'NY',
  electionDate: dayjs('2023-08-20T03:17'),
};

export const sampleWithNewData: NewOffice = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
