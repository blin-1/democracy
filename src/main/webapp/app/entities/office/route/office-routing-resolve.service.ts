import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IOffice } from '../office.model';
import { OfficeService } from '../service/office.service';

export const officeResolve = (route: ActivatedRouteSnapshot): Observable<null | IOffice> => {
  const id = route.params['id'];
  if (id) {
    return inject(OfficeService)
      .find(id)
      .pipe(
        mergeMap((office: HttpResponse<IOffice>) => {
          if (office.body) {
            return of(office.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        })
      );
  }
  return of(null);
};

export default officeResolve;
