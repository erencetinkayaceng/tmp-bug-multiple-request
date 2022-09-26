import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { FooComponent } from './list/foo.component';
import { FooDetailComponent } from './detail/foo-detail.component';
import { FooUpdateComponent } from './update/foo-update.component';
import { FooDeleteDialogComponent } from './delete/foo-delete-dialog.component';
import { FooRoutingModule } from './route/foo-routing.module';

@NgModule({
  imports: [SharedModule, FooRoutingModule],
  declarations: [FooComponent, FooDetailComponent, FooUpdateComponent, FooDeleteDialogComponent],
})
export class FooModule {}
