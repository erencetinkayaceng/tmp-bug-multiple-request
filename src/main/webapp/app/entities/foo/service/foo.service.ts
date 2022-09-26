import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IFoo, NewFoo } from '../foo.model';

export type PartialUpdateFoo = Partial<IFoo> & Pick<IFoo, 'id'>;

export type EntityResponseType = HttpResponse<IFoo>;
export type EntityArrayResponseType = HttpResponse<IFoo[]>;

@Injectable({ providedIn: 'root' })
export class FooService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/foos');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(foo: NewFoo): Observable<EntityResponseType> {
    return this.http.post<IFoo>(this.resourceUrl, foo, { observe: 'response' });
  }

  update(foo: IFoo): Observable<EntityResponseType> {
    return this.http.put<IFoo>(`${this.resourceUrl}/${this.getFooIdentifier(foo)}`, foo, { observe: 'response' });
  }

  partialUpdate(foo: PartialUpdateFoo): Observable<EntityResponseType> {
    return this.http.patch<IFoo>(`${this.resourceUrl}/${this.getFooIdentifier(foo)}`, foo, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IFoo>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IFoo[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getFooIdentifier(foo: Pick<IFoo, 'id'>): number {
    return foo.id;
  }

  compareFoo(o1: Pick<IFoo, 'id'> | null, o2: Pick<IFoo, 'id'> | null): boolean {
    return o1 && o2 ? this.getFooIdentifier(o1) === this.getFooIdentifier(o2) : o1 === o2;
  }

  addFooToCollectionIfMissing<Type extends Pick<IFoo, 'id'>>(fooCollection: Type[], ...foosToCheck: (Type | null | undefined)[]): Type[] {
    const foos: Type[] = foosToCheck.filter(isPresent);
    if (foos.length > 0) {
      const fooCollectionIdentifiers = fooCollection.map(fooItem => this.getFooIdentifier(fooItem)!);
      const foosToAdd = foos.filter(fooItem => {
        const fooIdentifier = this.getFooIdentifier(fooItem);
        if (fooCollectionIdentifiers.includes(fooIdentifier)) {
          return false;
        }
        fooCollectionIdentifiers.push(fooIdentifier);
        return true;
      });
      return [...foosToAdd, ...fooCollection];
    }
    return fooCollection;
  }
}
