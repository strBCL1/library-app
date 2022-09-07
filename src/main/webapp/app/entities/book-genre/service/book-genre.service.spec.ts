import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IBookGenre } from '../book-genre.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../book-genre.test-samples';

import { BookGenreService } from './book-genre.service';

const requireRestSample: IBookGenre = {
  ...sampleWithRequiredData,
};

describe('BookGenre Service', () => {
  let service: BookGenreService;
  let httpMock: HttpTestingController;
  let expectedResult: IBookGenre | IBookGenre[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(BookGenreService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a BookGenre', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const bookGenre = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(bookGenre).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a BookGenre', () => {
      const bookGenre = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(bookGenre).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a BookGenre', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of BookGenre', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a BookGenre', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addBookGenreToCollectionIfMissing', () => {
      it('should add a BookGenre to an empty array', () => {
        const bookGenre: IBookGenre = sampleWithRequiredData;
        expectedResult = service.addBookGenreToCollectionIfMissing([], bookGenre);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(bookGenre);
      });

      it('should not add a BookGenre to an array that contains it', () => {
        const bookGenre: IBookGenre = sampleWithRequiredData;
        const bookGenreCollection: IBookGenre[] = [
          {
            ...bookGenre,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addBookGenreToCollectionIfMissing(bookGenreCollection, bookGenre);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a BookGenre to an array that doesn't contain it", () => {
        const bookGenre: IBookGenre = sampleWithRequiredData;
        const bookGenreCollection: IBookGenre[] = [sampleWithPartialData];
        expectedResult = service.addBookGenreToCollectionIfMissing(bookGenreCollection, bookGenre);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(bookGenre);
      });

      it('should add only unique BookGenre to an array', () => {
        const bookGenreArray: IBookGenre[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const bookGenreCollection: IBookGenre[] = [sampleWithRequiredData];
        expectedResult = service.addBookGenreToCollectionIfMissing(bookGenreCollection, ...bookGenreArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const bookGenre: IBookGenre = sampleWithRequiredData;
        const bookGenre2: IBookGenre = sampleWithPartialData;
        expectedResult = service.addBookGenreToCollectionIfMissing([], bookGenre, bookGenre2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(bookGenre);
        expect(expectedResult).toContain(bookGenre2);
      });

      it('should accept null and undefined values', () => {
        const bookGenre: IBookGenre = sampleWithRequiredData;
        expectedResult = service.addBookGenreToCollectionIfMissing([], null, bookGenre, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(bookGenre);
      });

      it('should return initial array if no BookGenre is added', () => {
        const bookGenreCollection: IBookGenre[] = [sampleWithRequiredData];
        expectedResult = service.addBookGenreToCollectionIfMissing(bookGenreCollection, undefined, null);
        expect(expectedResult).toEqual(bookGenreCollection);
      });
    });

    describe('compareBookGenre', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareBookGenre(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareBookGenre(entity1, entity2);
        const compareResult2 = service.compareBookGenre(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareBookGenre(entity1, entity2);
        const compareResult2 = service.compareBookGenre(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareBookGenre(entity1, entity2);
        const compareResult2 = service.compareBookGenre(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
