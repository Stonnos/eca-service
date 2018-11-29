import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  EnumDto, ErsReportDto,
  ExperimentDto,
  PageDto,
  PageRequestDto, RequestStatusStatisticsDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from "rxjs/internal/Observable";
import { ConfigService } from "../../config.service";
import { CookieService } from "ngx-cookie-service";

@Injectable()
export class ExperimentsService {

  private serviceUrl = ConfigService.appConfig.apiUrl;

  public constructor(private http: HttpClient, private cookieService: CookieService) {
  }

  public getExperiments(pageRequest: PageRequestDto): Observable<PageDto<ExperimentDto>> {
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
    return this.http.get<PageDto<ExperimentDto>>(this.serviceUrl + '/experiments', options);
  }

  public getExperimentTypes(): Observable<EnumDto[]> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': 'Bearer ' + this.cookieService.get('access_token')
    });
    return this.http.get<EnumDto[]>(this.serviceUrl + '/experiment-types', { headers: headers });
  }

  public getRequestStatusesStatistics(): Observable<RequestStatusStatisticsDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': 'Bearer ' + this.cookieService.get('access_token')
    });
    return this.http.get<RequestStatusStatisticsDto>(this.serviceUrl + '/experiment/request-statuses-statistics', { headers: headers });
  }

  public getExperimentResultsFile(uuid: string): Observable<Blob> {
    return this.http.get<Blob>(this.serviceUrl + '/experiment/download/' + uuid, { responseType: 'blob' as 'json' });
  }

  public getExperimentTrainingDataFile(uuid: string): Observable<Blob> {
    const headers = new HttpHeaders({
      'Authorization': 'Bearer ' + this.cookieService.get('access_token')
    });
    const options = { headers: headers, responseType: 'blob' as 'json' };
    return this.http.get<Blob>(this.serviceUrl + '/experiment-training-data/' + uuid, options);
  }

  public getErsReport(uuid: string): Observable<ErsReportDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': 'Bearer ' + this.cookieService.get('access_token')
    });
    return this.http.get<ErsReportDto>(this.serviceUrl + '/experiment-ers-report/' + uuid, { headers: headers });
  }

  public sentEvaluationResults(uuid: string) {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': 'Bearer ' + this.cookieService.get('access_token')
    });
    let params = new HttpParams().set('uuid', uuid.toString());
    const options = { headers: headers, params: params };
    return this.http.post(this.serviceUrl + '/sent-experiment-evaluation-results', options);
  }
}
