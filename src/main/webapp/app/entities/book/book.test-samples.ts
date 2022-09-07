import dayjs from 'dayjs/esm';

import { IBook, NewBook } from './book.model';

export const sampleWithRequiredData: IBook = {
  id: 37098,
};

export const sampleWithPartialData: IBook = {
  id: 92677,
  releaseDate: dayjs('2022-09-07T03:14'),
};

export const sampleWithFullData: IBook = {
  id: 60289,
  title: 'synthesize',
  description: 'object-oriented Wooden Pizza',
  releaseDate: dayjs('2022-09-06T14:11'),
};

export const sampleWithNewData: NewBook = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
