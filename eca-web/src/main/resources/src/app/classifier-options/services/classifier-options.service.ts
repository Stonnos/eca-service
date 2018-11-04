import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  ClassifierOptionsDto,
  PageDto,
  PageRequestDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from "rxjs/internal/Observable";

@Injectable()
export class ClassifierOptionsService {

  private serviceUrl = "http://localhost:8085/eca-server";

  public constructor(private http: HttpClient) {
  }

  public getClassifiersOptions(pageRequest: PageRequestDto): Observable<PageDto<ClassifierOptionsDto>> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
    });
    let params = new HttpParams().set('page', pageRequest.page.toString())
      .set('size', pageRequest.size.toString())
      .set('sortField', pageRequest.sortField)
      .set('ascending', pageRequest.ascending.toString());
    const options = { headers: headers, params: params };
    return this.http.get<PageDto<ClassifierOptionsDto>>(this.serviceUrl + '/experiment/classifiers-config/configs-page', options);
  }
}
