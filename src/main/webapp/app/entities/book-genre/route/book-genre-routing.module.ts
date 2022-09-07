import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { BookGenreComponent } from '../list/book-genre.component';
import { BookGenreDetailComponent } from '../detail/book-genre-detail.component';
import { BookGenreUpdateComponent } from '../update/book-genre-update.component';
import { BookGenreRoutingResolveService } from './book-genre-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const bookGenreRoute: Routes = [
  {
    path: '',
    component: BookGenreComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: BookGenreDetailComponent,
    resolve: {
      bookGenre: BookGenreRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: BookGenreUpdateComponent,
    resolve: {
      bookGenre: BookGenreRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: BookGenreUpdateComponent,
    resolve: {
      bookGenre: BookGenreRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(bookGenreRoute)],
  exports: [RouterModule],
})
export class BookGenreRoutingModule {}
