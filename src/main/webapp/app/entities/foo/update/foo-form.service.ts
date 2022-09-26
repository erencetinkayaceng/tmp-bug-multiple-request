import { Injectable } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';

import { IFoo, NewFoo } from '../foo.model';

/**
 * A partial Type with required key is used as form input.
 */
type PartialWithRequiredKeyOf<T extends { id: unknown }> = Partial<Omit<T, 'id'>> & { id: T['id'] };

/**
 * Type for createFormGroup and resetForm argument.
 * It accepts IFoo for edit and NewFooFormGroupInput for create.
 */
type FooFormGroupInput = IFoo | PartialWithRequiredKeyOf<NewFoo>;

type FooFormDefaults = Pick<NewFoo, 'id'>;

type FooFormGroupContent = {
  id: FormControl<IFoo['id'] | NewFoo['id']>;
  name: FormControl<IFoo['name']>;
};

export type FooFormGroup = FormGroup<FooFormGroupContent>;

@Injectable({ providedIn: 'root' })
export class FooFormService {
  createFooFormGroup(foo: FooFormGroupInput = { id: null }): FooFormGroup {
    const fooRawValue = {
      ...this.getFormDefaults(),
      ...foo,
    };
    return new FormGroup<FooFormGroupContent>({
      id: new FormControl(
        { value: fooRawValue.id, disabled: true },
        {
          nonNullable: true,
          validators: [Validators.required],
        }
      ),
      name: new FormControl(fooRawValue.name, {
        validators: [Validators.required, Validators.maxLength(50)],
      }),
    });
  }

  getFoo(form: FooFormGroup): IFoo | NewFoo {
    return form.getRawValue() as IFoo | NewFoo;
  }

  resetForm(form: FooFormGroup, foo: FooFormGroupInput): void {
    const fooRawValue = { ...this.getFormDefaults(), ...foo };
    form.reset(
      {
        ...fooRawValue,
        id: { value: fooRawValue.id, disabled: true },
      } as any /* cast to workaround https://github.com/angular/angular/issues/46458 */
    );
  }

  private getFormDefaults(): FooFormDefaults {
    return {
      id: null,
    };
  }
}
