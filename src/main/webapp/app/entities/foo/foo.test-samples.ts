import { IFoo, NewFoo } from './foo.model';

export const sampleWithRequiredData: IFoo = {
  id: 75191,
  name: '24/7 sky HTTP',
};

export const sampleWithPartialData: IFoo = {
  id: 109,
  name: 'calculate Intranet AI',
};

export const sampleWithFullData: IFoo = {
  id: 77214,
  name: 'Concrete',
};

export const sampleWithNewData: NewFoo = {
  name: 'bypass SAS multi-state',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
