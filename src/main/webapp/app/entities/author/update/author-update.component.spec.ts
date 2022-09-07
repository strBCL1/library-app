import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { AuthorFormService } from './author-form.service';
import { AuthorService } from '../service/author.service';
import { IAuthor } from '../author.model';

import { IUser } from 'app/entities/user/user.model';
import { UserService } from 'app/entities/user/user.service';
import { IBook } from 'app/entities/book/book.model';
import { BookService } from 'app/entities/book/service/book.service';

import { AuthorUpdateComponent } from './author-update.component';

describe('Author Management Update Component', () => {
  let comp: AuthorUpdateComponent;
  let fixture: ComponentFixture<AuthorUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let authorFormService: AuthorFormService;
  let authorService: AuthorService;
  let userService: UserService;
  let bookService: BookService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([])],
      declarations: [AuthorUpdateComponent],
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
      .overrideTemplate(AuthorUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(AuthorUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    authorFormService = TestBed.inject(AuthorFormService);
    authorService = TestBed.inject(AuthorService);
    userService = TestBed.inject(UserService);
    bookService = TestBed.inject(BookService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call User query and add missing value', () => {
      const author: IAuthor = { id: 456 };
      const user: IUser = { id: 60902 };
      author.user = user;

      const userCollection: IUser[] = [{ id: 35438 }];
      jest.spyOn(userService, 'query').mockReturnValue(of(new HttpResponse({ body: userCollection })));
      const additionalUsers = [user];
      const expectedCollection: IUser[] = [...additionalUsers, ...userCollection];
      jest.spyOn(userService, 'addUserToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ author });
      comp.ngOnInit();

      expect(userService.query).toHaveBeenCalled();
      expect(userService.addUserToCollectionIfMissing).toHaveBeenCalledWith(
        userCollection,
        ...additionalUsers.map(expect.objectContaining)
      );
      expect(comp.usersSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Book query and add missing value', () => {
      const author: IAuthor = { id: 456 };
      const books: IBook[] = [{ id: 34948 }];
      author.books = books;

      const bookCollection: IBook[] = [{ id: 48784 }];
      jest.spyOn(bookService, 'query').mockReturnValue(of(new HttpResponse({ body: bookCollection })));
      const additionalBooks = [...books];
      const expectedCollection: IBook[] = [...additionalBooks, ...bookCollection];
      jest.spyOn(bookService, 'addBookToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ author });
      comp.ngOnInit();

      expect(bookService.query).toHaveBeenCalled();
      expect(bookService.addBookToCollectionIfMissing).toHaveBeenCalledWith(
        bookCollection,
        ...additionalBooks.map(expect.objectContaining)
      );
      expect(comp.booksSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const author: IAuthor = { id: 456 };
      const user: IUser = { id: 9570 };
      author.user = user;
      const book: IBook = { id: 17994 };
      author.books = [book];

      activatedRoute.data = of({ author });
      comp.ngOnInit();

      expect(comp.usersSharedCollection).toContain(user);
      expect(comp.booksSharedCollection).toContain(book);
      expect(comp.author).toEqual(author);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAuthor>>();
      const author = { id: 123 };
      jest.spyOn(authorFormService, 'getAuthor').mockReturnValue(author);
      jest.spyOn(authorService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ author });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: author }));
      saveSubject.complete();

      // THEN
      expect(authorFormService.getAuthor).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(authorService.update).toHaveBeenCalledWith(expect.objectContaining(author));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAuthor>>();
      const author = { id: 123 };
      jest.spyOn(authorFormService, 'getAuthor').mockReturnValue({ id: null });
      jest.spyOn(authorService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ author: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: author }));
      saveSubject.complete();

      // THEN
      expect(authorFormService.getAuthor).toHaveBeenCalled();
      expect(authorService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IAuthor>>();
      const author = { id: 123 };
      jest.spyOn(authorService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ author });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(authorService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareUser', () => {
      it('Should forward to userService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(userService, 'compareUser');
        comp.compareUser(entity, entity2);
        expect(userService.compareUser).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareBook', () => {
      it('Should forward to bookService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(bookService, 'compareBook');
        comp.compareBook(entity, entity2);
        expect(bookService.compareBook).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
