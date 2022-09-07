import dayjs from 'dayjs/esm';
import { IBookGenre } from 'app/entities/book-genre/book-genre.model';
import { IPublisher } from 'app/entities/publisher/publisher.model';
import { IAuthor } from 'app/entities/author/author.model';

export interface IBook {
  id: number;
  title?: string | null;
  description?: string | null;
  releaseDate?: dayjs.Dayjs | null;
  bookGenres?: Pick<IBookGenre, 'id'>[] | null;
  publisher?: Pick<IPublisher, 'id'> | null;
  authors?: Pick<IAuthor, 'id'>[] | null;
}

export type NewBook = Omit<IBook, 'id'> & { id: null };
