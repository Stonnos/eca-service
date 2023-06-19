import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  CreateExperimentResultDto,
  ExperimentErsReportDto,
  ExperimentDto,
  PageDto,
  PageRequestDto,
  RequestStatusStatisticsDto, ExperimentResultsDetailsDto, ExperimentProgressDto, S3ContentResponseDto, ChartDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { saveAs } from 'file-saver/dist/FileSaver';
import { Observable } from "rxjs/internal/Observable";
import { environment } from "../../../environments/environment";
import { Utils } from "../../common/util/utils";
import { catchError, finalize, switchMap } from "rxjs/internal/operators";
import { EMPTY } from "rxjs/internal/observable/empty";
import { CreateExperimentRequestDto } from "../../create-experiment/model/create-experiment-request.model";

@Injectable()
export class ExperimentsService {

  private serviceUrl = environment.serverUrl + '/experiment';

  public constructor(private http: HttpClient) {
  }

  public getExperiments(pageRequest: PageRequestDto): Observable<PageDto<ExperimentDto>> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.post<PageDto<ExperimentDto>>(this.serviceUrl + '/list', pageRequest, { headers: headers });
  }

  public getExperiment(id: number): Observable<ExperimentDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.get<ExperimentDto>(this.serviceUrl + '/details/' + id, { headers: headers });
  }

  public getExperimentResultsDetails(id: number): Observable<ExperimentResultsDetailsDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.get<ExperimentResultsDetailsDto>(this.serviceUrl + '/results/details/' + id, { headers: headers });
  }

  public getRequestStatusesStatistics(): Observable<RequestStatusStatisticsDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.get<RequestStatusStatisticsDto>(this.serviceUrl + '/request-statuses-statistics', { headers: headers });
  }

  public downloadExperimentResults(experiment: ExperimentDto, onSuccessCallback: () => void, onErrorCallback: (error: any) => void): void {
    this.getExperimentResultsContentUrl(experiment.id)
      .pipe(
        switchMap((s3ContentResponseDto: S3ContentResponseDto) => {
          return this.downloadContent(s3ContentResponseDto.contentUrl);
        }),
        catchError(error => {
          onErrorCallback(error);
          return EMPTY;
        }),
        finalize(() => {
          onSuccessCallback();
        })
      )
      .subscribe({
        next: (blob: Blob) => {
          saveAs(blob, experiment.modelPath);
        },
        error: (error) => {
          onErrorCallback(error);
        }
      });
  }

  public downloadContent(url: string): Observable<Blob> {
    const options = { responseType: 'blob' as 'json' };
    return this.http.get<Blob>(url, options);
  }

  public getExperimentResultsContentUrl(id: number): Observable<S3ContentResponseDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.get<S3ContentResponseDto>(this.serviceUrl + '/results-content/' + id, { headers: headers });
  }

  public getExperimentErsReport(id: number): Observable<ExperimentErsReportDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.get<ExperimentErsReportDto>(this.serviceUrl + '/ers-report/' + id, { headers: headers });
  }

  public getExperimentsStatistics(createdDateFrom: string, createdDateTo: string): Observable<ChartDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/x-www-form-urlencoded; charset=utf-8',
      'Authorization': Utils.getBearerTokenHeader()
    });
    let params = new HttpParams().set('createdDateFrom', createdDateFrom).set('createdDateTo', createdDateTo);
    const options = { headers: headers, params: params };
    return this.http.get<ChartDto>(this.serviceUrl + '/statistics', options);
  }

  public createExperiment(experimentRequest: CreateExperimentRequestDto): Observable<CreateExperimentResultDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.post<CreateExperimentResultDto>(this.serviceUrl + '/create', experimentRequest, { headers: headers });
  }

  public getExperimentProgress(id: number): Observable<ExperimentProgressDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.get<ExperimentProgressDto>(this.serviceUrl + '/progress/' + id, { headers: headers });
  }
}
