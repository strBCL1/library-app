import { IBook } from 'app/entities/book/book.model';

export interface IBookGenre {
  id: number;
  name?: string | null;
  books?: Pick<IBook, 'id'>[] | null;
}

export type NewBookGenre = Omit<IBookGenre, 'id'> & { id: null };
