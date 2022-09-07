import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { BookFormService } from './book-form.service';
import { BookService } from '../service/book.service';
import { IBook } from '../book.model';
import { IBookGenre } from 'app/entities/book-genre/book-genre.model';
import { BookGenreService } from 'app/entities/book-genre/service/book-genre.service';
import { IPublisher } from 'app/entities/publisher/publisher.model';
import { PublisherService } from 'app/entities/publisher/service/publisher.service';

import { BookUpdateComponent } from './book-update.component';

describe('Book Management Update Component', () => {
  let comp: BookUpdateComponent;
  let fixture: ComponentFixture<BookUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let bookFormService: BookFormService;
  let bookService: BookService;
  let bookGenreService: BookGenreService;
  let publisherService: PublisherService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [BookUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(BookUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(BookUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    bookFormService = TestBed.inject(BookFormService);
    bookService = TestBed.inject(BookService);
    bookGenreService = TestBed.inject(BookGenreService);
    publisherService = TestBed.inject(PublisherService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call BookGenre query and add missing value', () => {
      const book: IBook = { id: 456 };
      const bookGenres: IBookGenre[] = [{ id: 99876 }];
      book.bookGenres = bookGenres;

      const bookGenreCollection: IBookGenre[] = [{ id: 86263 }];
      jest.spyOn(bookGenreService, 'query').mockReturnValue(of(new HttpResponse({ body: bookGenreCollection })));
      const additionalBookGenres = [...bookGenres];
      const expectedCollection: IBookGenre[] = [...additionalBookGenres, ...bookGenreCollection];
      jest.spyOn(bookGenreService, 'addBookGenreToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ book });
      comp.ngOnInit();

      expect(bookGenreService.query).toHaveBeenCalled();
      expect(bookGenreService.addBookGenreToCollectionIfMissing).toHaveBeenCalledWith(
        bookGenreCollection,
        ...additionalBookGenres.map(expect.objectContaining)
      );
      expect(comp.bookGenresSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Publisher query and add missing value', () => {
      const book: IBook = { id: 456 };
      const publisher: IPublisher = { id: 65411 };
      book.publisher = publisher;

      const publisherCollection: IPublisher[] = [{ id: 41410 }];
      jest.spyOn(publisherService, 'query').mockReturnValue(of(new HttpResponse({ body: publisherCollection })));
      const additionalPublishers = [publisher];
      const expectedCollection: IPublisher[] = [...additionalPublishers, ...publisherCollection];
      jest.spyOn(publisherService, 'addPublisherToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ book });
      comp.ngOnInit();

      expect(publisherService.query).toHaveBeenCalled();
      expect(publisherService.addPublisherToCollectionIfMissing).toHaveBeenCalledWith(
        publisherCollection,
        ...additionalPublishers.map(expect.objectContaining)
      );
      expect(comp.publishersSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const book: IBook = { id: 456 };
      const bookGenre: IBookGenre = { id: 38039 };
      book.bookGenres = [bookGenre];
      const publisher: IPublisher = { id: 56473 };
      book.publisher = publisher;

      activatedRoute.data = of({ book });
      comp.ngOnInit();

      expect(comp.bookGenresSharedCollection).toContain(bookGenre);
      expect(comp.publishersSharedCollection).toContain(publisher);
      expect(comp.book).toEqual(book);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBook>>();
      const book = { id: 123 };
      jest.spyOn(bookFormService, 'getBook').mockReturnValue(book);
      jest.spyOn(bookService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ book });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: book }));
      saveSubject.complete();

      // THEN
      expect(bookFormService.getBook).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(bookService.update).toHaveBeenCalledWith(expect.objectContaining(book));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBook>>();
      const book = { id: 123 };
      jest.spyOn(bookFormService, 'getBook').mockReturnValue({ id: null });
      jest.spyOn(bookService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ book: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: book }));
      saveSubject.complete();

      // THEN
      expect(bookFormService.getBook).toHaveBeenCalled();
      expect(bookService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBook>>();
      const book = { id: 123 };
      jest.spyOn(bookService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ book });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(bookService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareBookGenre', () => {
      it('Should forward to bookGenreService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(bookGenreService, 'compareBookGenre');
        comp.compareBookGenre(entity, entity2);
        expect(bookGenreService.compareBookGenre).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('comparePublisher', () => {
      it('Should forward to publisherService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(publisherService, 'comparePublisher');
        comp.comparePublisher(entity, entity2);
        expect(publisherService.comparePublisher).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
