import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IBookGenre, NewBookGenre } from '../book-genre.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IBookGenre for edit and NewBookGenreFormGroupInput for create.
 */
type BookGenreFormGroupInput = IBookGenre | PartialWithRequiredKeyOf<NewBookGenre>;

type BookGenreFormDefaults = Pick<NewBookGenre, 'id' | 'books'>;

type BookGenreFormGroupContent = {
  id: FormControl<IBookGenre['id'] | NewBookGenre['id']>;
  name: FormControl<IBookGenre['name']>;
  books: FormControl<IBookGenre['books']>;
};

export type BookGenreFormGroup = FormGroup<BookGenreFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class BookGenreFormService {
  createBookGenreFormGroup(bookGenre: BookGenreFormGroupInput = { id: null }): BookGenreFormGroup {
    const bookGenreRawValue = {
      ...this.getFormDefaults(),
      ...bookGenre,
    };
    return new FormGroup<BookGenreFormGroupContent>({
      id: new FormControl(
        { value: bookGenreRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(bookGenreRawValue.name, {
        validators: [Validators.maxLength(50)],
      }),
      books: new FormControl(bookGenreRawValue.books ?? []),
    });
  }

  getBookGenre(form: BookGenreFormGroup): IBookGenre | NewBookGenre {
    return form.getRawValue() as IBookGenre | NewBookGenre;
  }

  resetForm(form: BookGenreFormGroup, bookGenre: BookGenreFormGroupInput): void {
    const bookGenreRawValue = { ...this.getFormDefaults(), ...bookGenre };
    form.reset(
      {
        ...bookGenreRawValue,
        id: { value: bookGenreRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): BookGenreFormDefaults {
    return {
      id: null,
      books: [],
    };
  }
}
