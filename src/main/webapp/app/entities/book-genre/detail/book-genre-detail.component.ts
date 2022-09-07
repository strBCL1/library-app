import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { IBookGenre } from '../book-genre.model';

@Component({
  selector: 'jhi-book-genre-detail',
  templateUrl: './book-genre-detail.component.html',
})
export class BookGenreDetailComponent implements OnInit {
  bookGenre: IBookGenre | null = null;

  constructor(protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ bookGenre }) => {
      this.bookGenre = bookGenre;
    });
  }

  previousState(): void {
    window.history.back();
  }
}
