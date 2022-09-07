import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { BookGenreComponent } from './list/book-genre.component';
import { BookGenreDetailComponent } from './detail/book-genre-detail.component';
import { BookGenreUpdateComponent } from './update/book-genre-update.component';
import { BookGenreDeleteDialogComponent } from './delete/book-genre-delete-dialog.component';
import { BookGenreRoutingModule } from './route/book-genre-routing.module';

@NgModule({
  imports: [SharedModule, BookGenreRoutingModule],
  declarations: [BookGenreComponent, BookGenreDetailComponent, BookGenreUpdateComponent, BookGenreDeleteDialogComponent],
})
export class BookGenreModule {}
