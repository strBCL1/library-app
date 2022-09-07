import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import dayjs from 'dayjs/esm';
import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IBook, NewBook } from '../book.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IBook for edit and NewBookFormGroupInput for create.
 */
type BookFormGroupInput = IBook | PartialWithRequiredKeyOf<NewBook>;

/**
 * Type that converts some properties for forms.
 */
type FormValueOf<T extends IBook | NewBook> = Omit<T, 'releaseDate'> & {
  releaseDate?: string | null;
};

type BookFormRawValue = FormValueOf<IBook>;

type NewBookFormRawValue = FormValueOf<NewBook>;

type BookFormDefaults = Pick<NewBook, 'id' | 'releaseDate' | 'bookGenres' | 'authors'>;

type BookFormGroupContent = {
  id: FormControl<BookFormRawValue['id'] | NewBook['id']>;
  title: FormControl<BookFormRawValue['title']>;
  description: FormControl<BookFormRawValue['description']>;
  releaseDate: FormControl<BookFormRawValue['releaseDate']>;
  bookGenres: FormControl<BookFormRawValue['bookGenres']>;
  publisher: FormControl<BookFormRawValue['publisher']>;
  authors: FormControl<BookFormRawValue['authors']>;
};

export type BookFormGroup = FormGroup<BookFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class BookFormService {
  createBookFormGroup(book: BookFormGroupInput = { id: null }): BookFormGroup {
    const bookRawValue = this.convertBookToBookRawValue({
      ...this.getFormDefaults(),
      ...book,
    });
    return new FormGroup<BookFormGroupContent>({
      id: new FormControl(
        { value: bookRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      title: new FormControl(bookRawValue.title, {
        validators: [Validators.maxLength(200)],
      }),
      description: new FormControl(bookRawValue.description, {
        validators: [Validators.maxLength(500)],
      }),
      releaseDate: new FormControl(bookRawValue.releaseDate),
      bookGenres: new FormControl(bookRawValue.bookGenres ?? []),
      publisher: new FormControl(bookRawValue.publisher),
      authors: new FormControl(bookRawValue.authors ?? []),
    });
  }

  getBook(form: BookFormGroup): IBook | NewBook {
    return this.convertBookRawValueToBook(form.getRawValue() as BookFormRawValue | NewBookFormRawValue);
  }

  resetForm(form: BookFormGroup, book: BookFormGroupInput): void {
    const bookRawValue = this.convertBookToBookRawValue({ ...this.getFormDefaults(), ...book });
    form.reset(
      {
        ...bookRawValue,
        id: { value: bookRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): BookFormDefaults {
    const currentTime = dayjs();

    return {
      id: null,
      releaseDate: currentTime,
      bookGenres: [],
      authors: [],
    };
  }

  private convertBookRawValueToBook(rawBook: BookFormRawValue | NewBookFormRawValue): IBook | NewBook {
    return {
      ...rawBook,
      releaseDate: dayjs(rawBook.releaseDate, DATE_TIME_FORMAT),
    };
  }

  private convertBookToBookRawValue(
    book: IBook | (Partial<NewBook> & BookFormDefaults)
  ): BookFormRawValue | PartialWithRequiredKeyOf<NewBookFormRawValue> {
    return {
      ...book,
      releaseDate: book.releaseDate ? book.releaseDate.format(DATE_TIME_FORMAT) : undefined,
      bookGenres: book.bookGenres ?? [],
      authors: book.authors ?? [],
    };
  }
}
