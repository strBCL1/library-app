import { IPublisher, NewPublisher } from './publisher.model';

export const sampleWithRequiredData: IPublisher = {
  id: 65979,
};

export const sampleWithPartialData: IPublisher = {
  id: 67401,
  firstName: 'Elmira',
};

export const sampleWithFullData: IPublisher = {
  id: 40994,
  firstName: 'Augustus',
  lastName: 'Cruickshank',
  address: 'Chair calculate',
  phoneNumber: 'mobile withd',
};

export const sampleWithNewData: NewPublisher = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
