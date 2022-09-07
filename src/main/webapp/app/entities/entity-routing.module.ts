import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'book',
        data: { pageTitle: 'Books' },
        loadChildren: () => import('./book/book.module').then(m => m.BookModule),
      },
      {
        path: 'author',
        data: { pageTitle: 'Authors' },
        loadChildren: () => import('./author/author.module').then(m => m.AuthorModule),
      },
      {
        path: 'publisher',
        data: { pageTitle: 'Publishers' },
        loadChildren: () => import('./publisher/publisher.module').then(m => m.PublisherModule),
      },
      {
        path: 'book-genre',
        data: { pageTitle: 'BookGenres' },
        loadChildren: () => import('./book-genre/book-genre.module').then(m => m.BookGenreModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
