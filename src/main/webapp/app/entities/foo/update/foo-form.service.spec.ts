import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../foo.test-samples';

import { FooFormService } from './foo-form.service';

describe('Foo Form Service', () => {
  let service: FooFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FooFormService);
  });

  describe('Service methods', () => {
    describe('createFooFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createFooFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
          })
        );
      });

      it('passing IFoo should create a new form with FormGroup', () => {
        const formGroup = service.createFooFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            name: expect.any(Object),
          })
        );
      });
    });

    describe('getFoo', () => {
      it('should return NewFoo for default Foo initial value', () => {
        // eslint-disable-next-line @typescript-eslint/no-unused-vars
        const formGroup = service.createFooFormGroup(sampleWithNewData);

        const foo = service.getFoo(formGroup) as any;

        expect(foo).toMatchObject(sampleWithNewData);
      });

      it('should return NewFoo for empty Foo initial value', () => {
        const formGroup = service.createFooFormGroup();

        const foo = service.getFoo(formGroup) as any;

        expect(foo).toMatchObject({});
      });

      it('should return IFoo', () => {
        const formGroup = service.createFooFormGroup(sampleWithRequiredData);

        const foo = service.getFoo(formGroup) as any;

        expect(foo).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing IFoo should not enable id FormControl', () => {
        const formGroup = service.createFooFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewFoo should disable id FormControl', () => {
        const formGroup = service.createFooFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
