import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  ChartDataDto, CreateExperimentResultDto,
  ExperimentErsReportDto,
  ExperimentDto,
  PageDto,
  PageRequestDto,
  RequestStatusStatisticsDto, ExperimentResultsDetailsDto, SendingStatus
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from "rxjs/internal/Observable";
import { ConfigService } from "../../config.service";
import { AuthenticationKeys } from "../../auth/model/auth.keys";
import { ExperimentRequest } from "../../create-experiment/model/experiment-request.model";
import { PageRequestService } from "../../common/services/page-request.service";

@Injectable()
export class ExperimentsService {

  private serviceUrl = ConfigService.appConfig.apiUrl + '/experiment';

  public constructor(private http: HttpClient, private pageRequestService: PageRequestService) {
  }

  public getExperiments(pageRequest: PageRequestDto): Observable<PageDto<ExperimentDto>> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': 'Bearer ' + localStorage.getItem(AuthenticationKeys.ACCESS_TOKEN)
    });
    const params: HttpParams = this.pageRequestService.convertToHttpRequestParams(pageRequest);
    const options = { headers: headers, params: params };
    return this.http.get<PageDto<ExperimentDto>>(this.serviceUrl + '/list', options);
  }

  public getExperiment(requestId: string): Observable<ExperimentDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': 'Bearer ' + localStorage.getItem(AuthenticationKeys.ACCESS_TOKEN)
    });
    return this.http.get<ExperimentDto>(this.serviceUrl + '/details/' + requestId, { headers: headers });
  }

  public getExperimentResultsDetails(id: number): Observable<ExperimentResultsDetailsDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': 'Bearer ' + localStorage.getItem(AuthenticationKeys.ACCESS_TOKEN)
    });
    return this.http.get<ExperimentResultsDetailsDto>(this.serviceUrl + '/results/details/' + id, { headers: headers });
  }

  public getRequestStatusesStatistics(): Observable<RequestStatusStatisticsDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': 'Bearer ' + localStorage.getItem(AuthenticationKeys.ACCESS_TOKEN)
    });
    return this.http.get<RequestStatusStatisticsDto>(this.serviceUrl + '/request-statuses-statistics', { headers: headers });
  }

  public getExperimentResultsFile(requestId: string): Observable<Blob> {
    const headers = new HttpHeaders({
      'Authorization': 'Bearer ' + localStorage.getItem(AuthenticationKeys.ACCESS_TOKEN)
    });
    const options = { headers: headers, responseType: 'blob' as 'json' };
    return this.http.get<Blob>(this.serviceUrl + '/results/' + requestId, options);
  }

  public getExperimentTrainingDataFile(requestId: string): Observable<Blob> {
    const headers = new HttpHeaders({
      'Authorization': 'Bearer ' + localStorage.getItem(AuthenticationKeys.ACCESS_TOKEN)
    });
    const options = { headers: headers, responseType: 'blob' as 'json' };
    return this.http.get<Blob>(this.serviceUrl + '/training-data/' + requestId, options);
  }

  public getExperimentErsReport(requestId: string): Observable<ExperimentErsReportDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': 'Bearer ' + localStorage.getItem(AuthenticationKeys.ACCESS_TOKEN)
    });
    return this.http.get<ExperimentErsReportDto>(this.serviceUrl + '/ers-report/' + requestId, { headers: headers });
  }

  public checkExperimentResultsSendingStatus(requestId: string): Observable<SendingStatus> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': 'Bearer ' + localStorage.getItem(AuthenticationKeys.ACCESS_TOKEN)
    });
    return this.http.get<SendingStatus>(this.serviceUrl + '/ers-report/sending-status/' + requestId, { headers: headers });
  }

  public sentEvaluationResults(requestId: string) {
    const headers = new HttpHeaders({
      'Content-type': 'application/json',
      'Authorization': 'Bearer ' + localStorage.getItem(AuthenticationKeys.ACCESS_TOKEN)
    });
    return this.http.post(this.serviceUrl + '/sent-evaluation-results', requestId, { headers: headers });
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

  public createExperiment(experimentRequest: ExperimentRequest): Observable<CreateExperimentResultDto> {
    const headers = new HttpHeaders({
      'Authorization': 'Bearer ' + localStorage.getItem(AuthenticationKeys.ACCESS_TOKEN)
    });
    const formData = new FormData();
    formData.append('trainingData', experimentRequest.trainingDataFile, experimentRequest.trainingDataFile.name);
    formData.append('experimentType', experimentRequest.experimentType.value);
    formData.append('evaluationMethod', experimentRequest.evaluationMethod.value);
    return this.http.post<CreateExperimentResultDto>(this.serviceUrl + '/create', formData, { headers: headers });
  }
}
