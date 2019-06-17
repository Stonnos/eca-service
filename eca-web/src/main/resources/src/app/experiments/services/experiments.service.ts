import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  ChartDataDto,
  ErsReportDto,
  ExperimentDto,
  PageDto,
  PageRequestDto, RequestStatusStatisticsDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from "rxjs/internal/Observable";
import { ConfigService } from "../../config.service";
import { AuthenticationKeys } from "../../auth/model/auth.keys";

@Injectable()
export class ExperimentsService {

  private serviceUrl = ConfigService.appConfig.apiUrl + '/experiment';

  public constructor(private http: HttpClient) {
  }

  public getExperiments(pageRequest: PageRequestDto): Observable<PageDto<ExperimentDto>> {
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
    return this.http.get<PageDto<ExperimentDto>>(this.serviceUrl + '/list', options);
  }

  public getRequestStatusesStatistics(): Observable<RequestStatusStatisticsDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': 'Bearer ' + localStorage.getItem(AuthenticationKeys.ACCESS_TOKEN)
    });
    return this.http.get<RequestStatusStatisticsDto>(this.serviceUrl + '/request-statuses-statistics', { headers: headers });
  }

  public getExperimentResultsFile(uuid: string): Observable<Blob> {
    return this.http.get<Blob>(this.serviceUrl + '/download/' + uuid, { responseType: 'blob' as 'json' });
  }

  public getExperimentTrainingDataFile(uuid: string): Observable<Blob> {
    const headers = new HttpHeaders({
      'Authorization': 'Bearer ' + localStorage.getItem(AuthenticationKeys.ACCESS_TOKEN)
    });
    const options = { headers: headers, responseType: 'blob' as 'json' };
    return this.http.get<Blob>(this.serviceUrl + '/training-data/' + uuid, options);
  }

  public getErsReport(uuid: string): Observable<ErsReportDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': 'Bearer ' + localStorage.getItem(AuthenticationKeys.ACCESS_TOKEN)
    });
    return this.http.get<ErsReportDto>(this.serviceUrl + '/ers-report/' + uuid, { headers: headers });
  }

  public sentEvaluationResults(uuid: string) {
    const headers = new HttpHeaders({
      'Content-type': 'application/json',
      'Authorization': 'Bearer ' + localStorage.getItem(AuthenticationKeys.ACCESS_TOKEN)
    });
    return this.http.post(this.serviceUrl + '/sent-evaluation-results', uuid, { headers: headers });
  }

  public getExperimentTypesStatistics(createdDateFrom: string, createdDateTo: string): Observable<ChartDataDto[]> {
    const headers = new HttpHeaders({
      'Content-type': 'application/x-www-form-urlencoded; charset=utf-8',
      'Authorization': 'Bearer ' + localStorage.getItem(AuthenticationKeys.ACCESS_TOKEN)
    });
    let params = new HttpParams().set('createdDateFrom', createdDateFrom).set('createdDateTo', createdDateTo);
    const options = { headers: headers, params: params };
    return this.http.get<ChartDataDto[]>(this.serviceUrl + '/statistics', options);
  }
}
