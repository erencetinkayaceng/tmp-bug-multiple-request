import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import { FooFormService, FooFormGroup } from './foo-form.service';
import { IFoo } from '../foo.model';
import { FooService } from '../service/foo.service';

@Component({
  selector: 'jhi-foo-update',
  templateUrl: './foo-update.component.html',
})
export class FooUpdateComponent implements OnInit {
  isSaving = false;
  foo: IFoo | null = null;

  editForm: FooFormGroup = this.fooFormService.createFooFormGroup();

  constructor(protected fooService: FooService, protected fooFormService: FooFormService, protected activatedRoute: ActivatedRoute) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ foo }) => {
      this.foo = foo;
      if (foo) {
        this.updateForm(foo);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const foo = this.fooFormService.getFoo(this.editForm);
    if (foo.id !== null) {
      this.subscribeToSaveResponse(this.fooService.update(foo));
    } else {
      this.subscribeToSaveResponse(this.fooService.create(foo));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IFoo>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(foo: IFoo): void {
    this.foo = foo;
    this.fooFormService.resetForm(this.editForm, foo);
  }
}
