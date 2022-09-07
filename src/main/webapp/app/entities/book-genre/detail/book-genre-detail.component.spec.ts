import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { BookGenreDetailComponent } from './book-genre-detail.component';

describe('BookGenre Management Detail Component', () => {
  let comp: BookGenreDetailComponent;
  let fixture: ComponentFixture<BookGenreDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [BookGenreDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ bookGenre: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(BookGenreDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(BookGenreDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load bookGenre on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.bookGenre).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
