import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IBookGenre, NewBookGenre } from '../book-genre.model';

export type PartialUpdateBookGenre = Partial<IBookGenre> & Pick<IBookGenre, 'id'>;

export type EntityResponseType = HttpResponse<IBookGenre>;
export type EntityArrayResponseType = HttpResponse<IBookGenre[]>;

@Injectable({ providedIn: 'root' })
export class BookGenreService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/book-genres');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(bookGenre: NewBookGenre): Observable<EntityResponseType> {
    return this.http.post<IBookGenre>(this.resourceUrl, bookGenre, { observe: 'response' });
  }

  update(bookGenre: IBookGenre): Observable<EntityResponseType> {
    return this.http.put<IBookGenre>(`${this.resourceUrl}/${this.getBookGenreIdentifier(bookGenre)}`, bookGenre, { observe: 'response' });
  }

  partialUpdate(bookGenre: PartialUpdateBookGenre): Observable<EntityResponseType> {
    return this.http.patch<IBookGenre>(`${this.resourceUrl}/${this.getBookGenreIdentifier(bookGenre)}`, bookGenre, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IBookGenre>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IBookGenre[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getBookGenreIdentifier(bookGenre: Pick<IBookGenre, 'id'>): number {
    return bookGenre.id;
  }

  compareBookGenre(o1: Pick<IBookGenre, 'id'> | null, o2: Pick<IBookGenre, 'id'> | null): boolean {
    return o1 && o2 ? this.getBookGenreIdentifier(o1) === this.getBookGenreIdentifier(o2) : o1 === o2;
  }

  addBookGenreToCollectionIfMissing<Type extends Pick<IBookGenre, 'id'>>(
    bookGenreCollection: Type[],
    ...bookGenresToCheck: (Type | null | undefined)[]
  ): Type[] {
    const bookGenres: Type[] = bookGenresToCheck.filter(isPresent);
    if (bookGenres.length > 0) {
      const bookGenreCollectionIdentifiers = bookGenreCollection.map(bookGenreItem => this.getBookGenreIdentifier(bookGenreItem)!);
      const bookGenresToAdd = bookGenres.filter(bookGenreItem => {
        const bookGenreIdentifier = this.getBookGenreIdentifier(bookGenreItem);
        if (bookGenreCollectionIdentifiers.includes(bookGenreIdentifier)) {
          return false;
        }
        bookGenreCollectionIdentifiers.push(bookGenreIdentifier);
        return true;
      });
      return [...bookGenresToAdd, ...bookGenreCollection];
    }
    return bookGenreCollection;
  }
}
