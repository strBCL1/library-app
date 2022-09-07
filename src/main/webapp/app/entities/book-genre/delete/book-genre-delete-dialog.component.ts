import { Component } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

import { IBookGenre } from '../book-genre.model';
import { BookGenreService } from '../service/book-genre.service';
import { ITEM_DELETED_EVENT } from 'app/config/navigation.constants';

@Component({
  templateUrl: './book-genre-delete-dialog.component.html',
})
export class BookGenreDeleteDialogComponent {
  bookGenre?: IBookGenre;

  constructor(protected bookGenreService: BookGenreService, protected activeModal: NgbActiveModal) {}

  cancel(): void {
    this.activeModal.dismiss();
  }

  confirmDelete(id: number): void {
    this.bookGenreService.delete(id).subscribe(() => {
      this.activeModal.close(ITEM_DELETED_EVENT);
    });
  }
}
