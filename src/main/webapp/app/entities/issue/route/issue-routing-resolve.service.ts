import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IIssue } from '../issue.model';
import { IssueService } from '../service/issue.service';

export const issueResolve = (route: ActivatedRouteSnapshot): Observable<null | IIssue> => {
  const id = route.params['id'];
  if (id) {
    return inject(IssueService)
      .find(id)
      .pipe(
        mergeMap((issue: HttpResponse<IIssue>) => {
          if (issue.body) {
            return of(issue.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        })
      );
  }
  return of(null);
};

export default issueResolve;
