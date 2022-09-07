import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../book-genre.test-samples';

import { BookGenreFormService } from './book-genre-form.service';

describe('BookGenre Form Service', () => {
  let service: BookGenreFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BookGenreFormService);
  });

  describe('Service methods', () => {
    describe('createBookGenreFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createBookGenreFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            books: expect.any(Object),
          })
        );
      });

      it('passing IBookGenre should create a new form with FormGroup', () => {
        const formGroup = service.createBookGenreFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
            books: expect.any(Object),
          })
        );
      });
    });

    describe('getBookGenre', () => {
      it('should return NewBookGenre for default BookGenre initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createBookGenreFormGroup(sampleWithNewData);

        const bookGenre = service.getBookGenre(formGroup) as any;

        expect(bookGenre).toMatchObject(sampleWithNewData);
      });

      it('should return NewBookGenre for empty BookGenre initial value', () => {
        const formGroup = service.createBookGenreFormGroup();

        const bookGenre = service.getBookGenre(formGroup) as any;

        expect(bookGenre).toMatchObject({});
      });

      it('should return IBookGenre', () => {
        const formGroup = service.createBookGenreFormGroup(sampleWithRequiredData);

        const bookGenre = service.getBookGenre(formGroup) as any;

        expect(bookGenre).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IBookGenre should not enable id FormControl', () => {
        const formGroup = service.createBookGenreFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewBookGenre should disable id FormControl', () => {
        const formGroup = service.createBookGenreFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
