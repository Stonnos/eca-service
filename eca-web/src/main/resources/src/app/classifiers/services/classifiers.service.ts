import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  EvaluationLogDetailsDto,
  EvaluationLogDto,
  PageDto,
  PageRequestDto, RequestStatusStatisticsDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from "rxjs/internal/Observable";
import { ConfigService } from "../../config.service";
import { CookieService } from "ngx-cookie-service";

@Injectable()
export class ClassifiersService {

  private serviceUrl = ConfigService.appConfig.apiUrl + '/evaluation';

  public constructor(private http: HttpClient, private cookieService: CookieService) {
  }

  public getEvaluations(pageRequest: PageRequestDto): Observable<PageDto<EvaluationLogDto>> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': 'Bearer ' + this.cookieService.get('access_token')
    });
    let params = new HttpParams().set('page', pageRequest.page.toString())
      .set('size', pageRequest.size.toString())
      .set('sortField', pageRequest.sortField)
      .set('ascending', pageRequest.ascending.toString());
    pageRequest.filters.map((filter, index) => {
      params = params.set(`filters['${index}'].name`, filter.name);
      params = params.set(`filters['${index}'].value`, filter.value);
      params = params.set(`filters['${index}'].filterType`, filter.filterType);
      params = params.set(`filters['${index}'].matchMode`, filter.matchMode);
    });
    const options = { headers: headers, params: params };
    return this.http.get<PageDto<EvaluationLogDto>>(this.serviceUrl + '/list', options);
  }

  public getRequestStatusesStatistics(): Observable<RequestStatusStatisticsDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': 'Bearer ' + this.cookieService.get('access_token')
    });
    return this.http.get<RequestStatusStatisticsDto>(this.serviceUrl + '/evaluation/request-statuses-statistics', { headers: headers });
  }

  public getEvaluationLogDetailsDto(requestId: string): Observable<EvaluationLogDetailsDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': 'Bearer ' + this.cookieService.get('access_token')
    });
    return this.http.get<EvaluationLogDetailsDto>(this.serviceUrl + '/details/' + requestId, { headers: headers });
  }
}
