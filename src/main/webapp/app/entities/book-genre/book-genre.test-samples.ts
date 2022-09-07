import { IBookGenre, NewBookGenre } from './book-genre.model';

export const sampleWithRequiredData: IBookGenre = {
  id: 88092,
};

export const sampleWithPartialData: IBookGenre = {
  id: 19738,
};

export const sampleWithFullData: IBookGenre = {
  id: 65518,
  name: 'Function-based Sleek Pine',
};

export const sampleWithNewData: NewBookGenre = {
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
