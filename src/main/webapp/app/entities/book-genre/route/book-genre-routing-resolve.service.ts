import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IBookGenre } from '../book-genre.model';
import { BookGenreService } from '../service/book-genre.service';

@Injectable({ providedIn: 'root' })
export class BookGenreRoutingResolveService implements Resolve<IBookGenre | null> {
  constructor(protected service: BookGenreService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IBookGenre | null | never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((bookGenre: HttpResponse<IBookGenre>) => {
          if (bookGenre.body) {
            return of(bookGenre.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(null);
  }
}
