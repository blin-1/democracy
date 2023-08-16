import { State } from 'app/entities/enumerations/state.model';

import { IAddress, NewAddress } from './address.model';

export const sampleWithRequiredData: IAddress = {
  id: 13628,
  line1: 'Checking Unbranded Implementation',
  city: 'New Freeda',
  state: 'AL',
  zip: 'Concre',
};

export const sampleWithPartialData: IAddress = {
  id: 32392,
  line1: 'man',
  line2: 'eyeballs',
  city: 'Fort Jarodchester',
  state: 'AL',
  zip: 'Keyboa',
};

export const sampleWithFullData: IAddress = {
  id: 14815,
  line1: 'Diesel application ampere',
  line2: 'transmitting wireless',
  city: 'Watsicafurt',
  state: 'AL',
  zip: 'compre',
};

export const sampleWithNewData: NewAddress = {
  line1: 'volt Gender hacking',
  city: 'Rogers',
  state: 'NJ',
  zip: 'mishan',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
