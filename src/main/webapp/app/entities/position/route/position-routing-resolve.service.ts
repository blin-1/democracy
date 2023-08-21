import { inject } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPosition } from '../position.model';
import { PositionService } from '../service/position.service';

export const positionResolve = (route: ActivatedRouteSnapshot): Observable<null | IPosition> => {
  const id = route.params['id'];
  if (id) {
    return inject(PositionService)
      .find(id)
      .pipe(
        mergeMap((position: HttpResponse<IPosition>) => {
          if (position.body) {
            return of(position.body);
          } else {
            inject(Router).navigate(['404']);
            return EMPTY;
          }
        })
      );
  }
  return of(null);
};

export default positionResolve;
