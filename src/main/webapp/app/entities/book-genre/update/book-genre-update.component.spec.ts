import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { BookGenreFormService } from './book-genre-form.service';
import { BookGenreService } from '../service/book-genre.service';
import { IBookGenre } from '../book-genre.model';

import { BookGenreUpdateComponent } from './book-genre-update.component';

describe('BookGenre Management Update Component', () => {
  let comp: BookGenreUpdateComponent;
  let fixture: ComponentFixture<BookGenreUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let bookGenreFormService: BookGenreFormService;
  let bookGenreService: BookGenreService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [BookGenreUpdateComponent],
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
      .overrideTemplate(BookGenreUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(BookGenreUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    bookGenreFormService = TestBed.inject(BookGenreFormService);
    bookGenreService = TestBed.inject(BookGenreService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const bookGenre: IBookGenre = { id: 456 };

      activatedRoute.data = of({ bookGenre });
      comp.ngOnInit();

      expect(comp.bookGenre).toEqual(bookGenre);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBookGenre>>();
      const bookGenre = { id: 123 };
      jest.spyOn(bookGenreFormService, 'getBookGenre').mockReturnValue(bookGenre);
      jest.spyOn(bookGenreService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ bookGenre });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: bookGenre }));
      saveSubject.complete();

      // THEN
      expect(bookGenreFormService.getBookGenre).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(bookGenreService.update).toHaveBeenCalledWith(expect.objectContaining(bookGenre));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBookGenre>>();
      const bookGenre = { id: 123 };
      jest.spyOn(bookGenreFormService, 'getBookGenre').mockReturnValue({ id: null });
      jest.spyOn(bookGenreService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ bookGenre: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: bookGenre }));
      saveSubject.complete();

      // THEN
      expect(bookGenreFormService.getBookGenre).toHaveBeenCalled();
      expect(bookGenreService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IBookGenre>>();
      const bookGenre = { id: 123 };
      jest.spyOn(bookGenreService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ bookGenre });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(bookGenreService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
