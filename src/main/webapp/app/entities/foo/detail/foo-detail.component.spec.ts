import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { FooDetailComponent } from './foo-detail.component';

describe('Foo Management Detail Component', () => {
  let comp: FooDetailComponent;
  let fixture: ComponentFixture<FooDetailComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [FooDetailComponent],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: { data: of({ foo: { id: 123 } }) },
        },
      ],
    })
      .overrideTemplate(FooDetailComponent, '')
      .compileComponents();
    fixture = TestBed.createComponent(FooDetailComponent);
    comp = fixture.componentInstance;
  });

  describe('OnInit', () => {
    it('Should load foo on init', () => {
      // WHEN
      comp.ngOnInit();

      // THEN
      expect(comp.foo).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
