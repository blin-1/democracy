import { Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { IssueComponent } from './list/issue.component';
import { IssueDetailComponent } from './detail/issue-detail.component';
import { IssueUpdateComponent } from './update/issue-update.component';
import IssueResolve from './route/issue-routing-resolve.service';
import { ASC } from 'app/config/navigation.constants';

const issueRoute: Routes = [
  {
    path: '',
    component: IssueComponent,
    data: {
      defaultSort: 'id,' + ASC,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: IssueDetailComponent,
    resolve: {
      issue: IssueResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: IssueUpdateComponent,
    resolve: {
      issue: IssueResolve,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: IssueUpdateComponent,
    resolve: {
      issue: IssueResolve,
    },
    canActivate: [UserRouteAccessService],
  },
];

export default issueRoute;
