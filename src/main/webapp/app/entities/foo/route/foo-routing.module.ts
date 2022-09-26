import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { FooComponent } from '../list/foo.component';
import { FooDetailComponent } from '../detail/foo-detail.component';
import { FooUpdateComponent } from '../update/foo-update.component';
import { FooRoutingResolveService } from './foo-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const fooRoute: Routes = [
  {
    path: '',
    component: FooComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: FooDetailComponent,
    resolve: {
      foo: FooRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: FooUpdateComponent,
    resolve: {
      foo: FooRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: FooUpdateComponent,
    resolve: {
      foo: FooRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(fooRoute)],
  exports: [RouterModule],
})
export class FooRoutingModule {}
