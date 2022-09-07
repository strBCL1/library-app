import { IUser } from 'app/entities/user/user.model';
import { IBook } from 'app/entities/book/book.model';

export interface IAuthor {
  id: number;
  user?: Pick<IUser, 'id'> | null;
  books?: Pick<IBook, 'id'>[] | null;
}

export type NewAuthor = Omit<IAuthor, 'id'> & { id: null };
