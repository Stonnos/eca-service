import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  EvaluationLogDetailsDto,
  EvaluationLogDto,
  PageDto,
  PageRequestDto, RequestStatusStatisticsDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from "rxjs/internal/Observable";
import { environment } from "../../../environments/environment";
import { Utils } from "../../common/util/utils";

@Injectable()
export class ClassifiersService {

  private serviceUrl = environment.serverUrl + '/evaluation';

  public constructor(private http: HttpClient) {
  }

  public getEvaluations(pageRequest: PageRequestDto): Observable<PageDto<EvaluationLogDto>> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.post<PageDto<EvaluationLogDto>>(this.serviceUrl + '/list', pageRequest, { headers: headers });
  }

  public getRequestStatusesStatistics(): Observable<RequestStatusStatisticsDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.get<RequestStatusStatisticsDto>(this.serviceUrl + '/request-statuses-statistics', { headers: headers });
  }

  public getEvaluationLogDetails(requestId: string): Observable<EvaluationLogDetailsDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.get<EvaluationLogDetailsDto>(this.serviceUrl + '/details/' + requestId, { headers: headers });
  }
}
