import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IFoo } from '../foo.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../foo.test-samples';

import { FooService } from './foo.service';

const requireRestSample: IFoo = {
  ...sampleWithRequiredData,
};

describe('Foo Service', () => {
  let service: FooService;
  let httpMock: HttpTestingController;
  let expectedResult: IFoo | IFoo[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(FooService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a Foo', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const foo = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(foo).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Foo', () => {
      const foo = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(foo).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Foo', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Foo', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Foo', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addFooToCollectionIfMissing', () => {
      it('should add a Foo to an empty array', () => {
        const foo: IFoo = sampleWithRequiredData;
        expectedResult = service.addFooToCollectionIfMissing([], foo);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(foo);
      });

      it('should not add a Foo to an array that contains it', () => {
        const foo: IFoo = sampleWithRequiredData;
        const fooCollection: IFoo[] = [
          {
            ...foo,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addFooToCollectionIfMissing(fooCollection, foo);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Foo to an array that doesn't contain it", () => {
        const foo: IFoo = sampleWithRequiredData;
        const fooCollection: IFoo[] = [sampleWithPartialData];
        expectedResult = service.addFooToCollectionIfMissing(fooCollection, foo);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(foo);
      });

      it('should add only unique Foo to an array', () => {
        const fooArray: IFoo[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const fooCollection: IFoo[] = [sampleWithRequiredData];
        expectedResult = service.addFooToCollectionIfMissing(fooCollection, ...fooArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const foo: IFoo = sampleWithRequiredData;
        const foo2: IFoo = sampleWithPartialData;
        expectedResult = service.addFooToCollectionIfMissing([], foo, foo2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(foo);
        expect(expectedResult).toContain(foo2);
      });

      it('should accept null and undefined values', () => {
        const foo: IFoo = sampleWithRequiredData;
        expectedResult = service.addFooToCollectionIfMissing([], null, foo, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(foo);
      });

      it('should return initial array if no Foo is added', () => {
        const fooCollection: IFoo[] = [sampleWithRequiredData];
        expectedResult = service.addFooToCollectionIfMissing(fooCollection, undefined, null);
        expect(expectedResult).toEqual(fooCollection);
      });
    });

    describe('compareFoo', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareFoo(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareFoo(entity1, entity2);
        const compareResult2 = service.compareFoo(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareFoo(entity1, entity2);
        const compareResult2 = service.compareFoo(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareFoo(entity1, entity2);
        const compareResult2 = service.compareFoo(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
