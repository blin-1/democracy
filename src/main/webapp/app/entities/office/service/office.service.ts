import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { map } from 'rxjs/operators';

import dayjs from 'dayjs/esm';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IOffice, NewOffice } from '../office.model';

export type PartialUpdateOffice = Partial<IOffice> & Pick<IOffice, 'id'>;

type RestOf<T extends IOffice | NewOffice> = Omit<T, 'electionDate'> & {
  electionDate?: string | null;
};

export type RestOffice = RestOf<IOffice>;

export type NewRestOffice = RestOf<NewOffice>;

export type PartialUpdateRestOffice = RestOf<PartialUpdateOffice>;

export type EntityResponseType = HttpResponse<IOffice>;
export type EntityArrayResponseType = HttpResponse<IOffice[]>;

@Injectable({ providedIn: 'root' })
export class OfficeService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/offices');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(office: NewOffice): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(office);
    return this.http
      .post<RestOffice>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  update(office: IOffice): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(office);
    return this.http
      .put<RestOffice>(`${this.resourceUrl}/${this.getOfficeIdentifier(office)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  partialUpdate(office: PartialUpdateOffice): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(office);
    return this.http
      .patch<RestOffice>(`${this.resourceUrl}/${this.getOfficeIdentifier(office)}`, copy, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<RestOffice>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map(res => this.convertResponseFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<RestOffice[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map(res => this.convertResponseArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getOfficeIdentifier(office: Pick<IOffice, 'id'>): number {
    return office.id;
  }

  compareOffice(o1: Pick<IOffice, 'id'> | null, o2: Pick<IOffice, 'id'> | null): boolean {
    return o1 && o2 ? this.getOfficeIdentifier(o1) === this.getOfficeIdentifier(o2) : o1 === o2;
  }

  addOfficeToCollectionIfMissing<Type extends Pick<IOffice, 'id'>>(
    officeCollection: Type[],
    ...officesToCheck: (Type | null | undefined)[]
  ): Type[] {
    const offices: Type[] = officesToCheck.filter(isPresent);
    if (offices.length > 0) {
      const officeCollectionIdentifiers = officeCollection.map(officeItem => this.getOfficeIdentifier(officeItem)!);
      const officesToAdd = offices.filter(officeItem => {
        const officeIdentifier = this.getOfficeIdentifier(officeItem);
        if (officeCollectionIdentifiers.includes(officeIdentifier)) {
          return false;
        }
        officeCollectionIdentifiers.push(officeIdentifier);
        return true;
      });
      return [...officesToAdd, ...officeCollection];
    }
    return officeCollection;
  }

  protected convertDateFromClient<T extends IOffice | NewOffice | PartialUpdateOffice>(office: T): RestOf<T> {
    return {
      ...office,
      electionDate: office.electionDate?.toJSON() ?? null,
    };
  }

  protected convertDateFromServer(restOffice: RestOffice): IOffice {
    return {
      ...restOffice,
      electionDate: restOffice.electionDate ? dayjs(restOffice.electionDate) : undefined,
    };
  }

  protected convertResponseFromServer(res: HttpResponse<RestOffice>): HttpResponse<IOffice> {
    return res.clone({
      body: res.body ? this.convertDateFromServer(res.body) : null,
    });
  }

  protected convertResponseArrayFromServer(res: HttpResponse<RestOffice[]>): HttpResponse<IOffice[]> {
    return res.clone({
      body: res.body ? res.body.map(item => this.convertDateFromServer(item)) : null,
    });
  }
}
