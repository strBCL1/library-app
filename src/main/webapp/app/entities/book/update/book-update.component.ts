import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import { BookFormService, BookFormGroup } from './book-form.service';
import { IBook } from '../book.model';
import { BookService } from '../service/book.service';
import { IBookGenre } from 'app/entities/book-genre/book-genre.model';
import { BookGenreService } from 'app/entities/book-genre/service/book-genre.service';
import { IPublisher } from 'app/entities/publisher/publisher.model';
import { PublisherService } from 'app/entities/publisher/service/publisher.service';

@Component({
  selector: 'jhi-book-update',
  templateUrl: './book-update.component.html',
})
export class BookUpdateComponent implements OnInit {
  isSaving = false;
  book: IBook | null = null;

  bookGenresSharedCollection: IBookGenre[] = [];
  publishersSharedCollection: IPublisher[] = [];

  editForm: BookFormGroup = this.bookFormService.createBookFormGroup();

  constructor(
    protected bookService: BookService,
    protected bookFormService: BookFormService,
    protected bookGenreService: BookGenreService,
    protected publisherService: PublisherService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareBookGenre = (o1: IBookGenre | null, o2: IBookGenre | null): boolean => this.bookGenreService.compareBookGenre(o1, o2);

  comparePublisher = (o1: IPublisher | null, o2: IPublisher | null): boolean => this.publisherService.comparePublisher(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ book }) => {
      this.book = book;
      if (book) {
        this.updateForm(book);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const book = this.bookFormService.getBook(this.editForm);
    if (book.id !== null) {
      this.subscribeToSaveResponse(this.bookService.update(book));
    } else {
      this.subscribeToSaveResponse(this.bookService.create(book));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBook>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(book: IBook): void {
    this.book = book;
    this.bookFormService.resetForm(this.editForm, book);

    this.bookGenresSharedCollection = this.bookGenreService.addBookGenreToCollectionIfMissing<IBookGenre>(
      this.bookGenresSharedCollection,
      ...(book.bookGenres ?? [])
    );
    this.publishersSharedCollection = this.publisherService.addPublisherToCollectionIfMissing<IPublisher>(
      this.publishersSharedCollection,
      book.publisher
    );
  }

  protected loadRelationshipsOptions(): void {
    this.bookGenreService
      .query()
      .pipe(map((res: HttpResponse<IBookGenre[]>) => res.body ?? []))
      .pipe(
        map((bookGenres: IBookGenre[]) =>
          this.bookGenreService.addBookGenreToCollectionIfMissing<IBookGenre>(bookGenres, ...(this.book?.bookGenres ?? []))
        )
      )
      .subscribe((bookGenres: IBookGenre[]) => (this.bookGenresSharedCollection = bookGenres));

    this.publisherService
      .query()
      .pipe(map((res: HttpResponse<IPublisher[]>) => res.body ?? []))
      .pipe(
        map((publishers: IPublisher[]) =>
          this.publisherService.addPublisherToCollectionIfMissing<IPublisher>(publishers, this.book?.publisher)
        )
      )
      .subscribe((publishers: IPublisher[]) => (this.publishersSharedCollection = publishers));
  }
}
