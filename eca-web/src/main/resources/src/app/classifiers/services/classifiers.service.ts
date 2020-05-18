import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  EvaluationLogDetailsDto,
  EvaluationLogDto,
  PageDto,
  PageRequestDto, RequestStatusStatisticsDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from "rxjs/internal/Observable";
import { AuthenticationKeys } from "../../auth/model/auth.keys";
import { PageRequestService } from "../../common/services/page-request.service";
import { environment } from "../../../environments/environment";

@Injectable()
export class ClassifiersService {

  private serviceUrl = environment.serverUrl + '/evaluation';

  public constructor(private http: HttpClient, private pageRequestService: PageRequestService) {
  }

  public getEvaluations(pageRequest: PageRequestDto): Observable<PageDto<EvaluationLogDto>> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': 'Bearer ' + localStorage.getItem(AuthenticationKeys.ACCESS_TOKEN)
    });
    const params: HttpParams = this.pageRequestService.convertToHttpRequestParams(pageRequest);
    const options = { headers: headers, params: params };
    return this.http.get<PageDto<EvaluationLogDto>>(this.serviceUrl + '/list', options);
  }

  public getRequestStatusesStatistics(): Observable<RequestStatusStatisticsDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': 'Bearer ' + localStorage.getItem(AuthenticationKeys.ACCESS_TOKEN)
    });
    return this.http.get<RequestStatusStatisticsDto>(this.serviceUrl + '/request-statuses-statistics', { headers: headers });
  }

  public getEvaluationLogDetails(requestId: string): Observable<EvaluationLogDetailsDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': 'Bearer ' + localStorage.getItem(AuthenticationKeys.ACCESS_TOKEN)
    });
    return this.http.get<EvaluationLogDetailsDto>(this.serviceUrl + '/details/' + requestId, { headers: headers });
  }
}
