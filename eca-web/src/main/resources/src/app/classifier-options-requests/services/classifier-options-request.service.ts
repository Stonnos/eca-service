import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  ClassifierOptionsRequestDto,
  PageDto,
  PageRequestDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from "rxjs/internal/Observable";
import { ConfigService } from "../../config.service";
import { AuthenticationKeys } from "../../auth/model/auth.keys";

@Injectable()
export class ClassifierOptionsRequestService {

  private serviceUrl = ConfigService.appConfig.apiUrl;

  public constructor(private http: HttpClient) {
  }

  public getClassifiersOptionsRequests(pageRequest: PageRequestDto): Observable<PageDto<ClassifierOptionsRequestDto>> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': 'Bearer ' + localStorage.getItem(AuthenticationKeys.ACCESS_TOKEN)
    });
    let params = new HttpParams().set('page', pageRequest.page.toString())
      .set('size', pageRequest.size.toString())
      .set('sortField', pageRequest.sortField)
      .set('ascending', pageRequest.ascending.toString())
      .set('searchQuery', pageRequest.searchQuery);
    pageRequest.filters.map((filter, index) => {
      params = params.set(`filters['${index}'].name`, filter.name);
      params = params.set(`filters['${index}'].value`, filter.value);
      params = params.set(`filters['${index}'].filterType`, filter.filterType);
      params = params.set(`filters['${index}'].matchMode`, filter.matchMode);
    });
    const options = { headers: headers, params: params };
    return this.http.get<PageDto<ClassifierOptionsRequestDto>>(this.serviceUrl + '/classifiers-options-requests', options);
  }
}
