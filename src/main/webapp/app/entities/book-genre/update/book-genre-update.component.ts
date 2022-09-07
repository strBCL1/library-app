import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { BookGenreFormService, BookGenreFormGroup } from './book-genre-form.service';
import { IBookGenre } from '../book-genre.model';
import { BookGenreService } from '../service/book-genre.service';

@Component({
  selector: 'jhi-book-genre-update',
  templateUrl: './book-genre-update.component.html',
})
export class BookGenreUpdateComponent implements OnInit {
  isSaving = false;
  bookGenre: IBookGenre | null = null;

  editForm: BookGenreFormGroup = this.bookGenreFormService.createBookGenreFormGroup();

  constructor(
    protected bookGenreService: BookGenreService,
    protected bookGenreFormService: BookGenreFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ bookGenre }) => {
      this.bookGenre = bookGenre;
      if (bookGenre) {
        this.updateForm(bookGenre);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const bookGenre = this.bookGenreFormService.getBookGenre(this.editForm);
    if (bookGenre.id !== null) {
      this.subscribeToSaveResponse(this.bookGenreService.update(bookGenre));
    } else {
      this.subscribeToSaveResponse(this.bookGenreService.create(bookGenre));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IBookGenre>>): void {
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

  protected updateForm(bookGenre: IBookGenre): void {
    this.bookGenre = bookGenre;
    this.bookGenreFormService.resetForm(this.editForm, bookGenre);
  }
}
