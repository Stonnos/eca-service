import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  ChartDto, CreateEvaluationResponseDto,
  EvaluationLogDetailsDto,
  EvaluationLogDto,
  PageDto,
  RocCurveDataDto,
  PageRequestDto, RequestStatusStatisticsDto, S3ContentResponseDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from "rxjs/internal/Observable";
import { environment } from "../../../environments/environment";
import { catchError, finalize, switchMap } from "rxjs/internal/operators";
import { EMPTY } from "rxjs/internal/observable/empty";
import { saveAs } from 'file-saver/dist/FileSaver';
import { CreateEvaluationRequestDto } from "../../create-classifier/model/create-evaluation-request.model";
import {
  CreateOptimalEvaluationRequestDto
} from "../../create-optimal-classifier/model/create-optimal-evaluation-request.model";
import { RocCurveService } from '../../common/services/roc-curve.service';

@Injectable()
export class ClassifiersService implements RocCurveService {

  private serviceUrl = environment.serverUrl + '/evaluation';

  public constructor(private http: HttpClient) {
  }

  public createEvaluationRequest(evaluationRequestDto: CreateEvaluationRequestDto): Observable<CreateEvaluationResponseDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8'
    });
    return this.http.post<CreateEvaluationResponseDto>(this.serviceUrl + '/create', evaluationRequestDto, { headers: headers });
  }

  public createOptimalEvaluationRequest(evaluationRequestDto: CreateOptimalEvaluationRequestDto): Observable<CreateEvaluationResponseDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8'
    });
    return this.http.post<CreateEvaluationResponseDto>(this.serviceUrl + '/create-optimal', evaluationRequestDto, { headers: headers });
  }

  public getEvaluations(pageRequest: PageRequestDto): Observable<PageDto<EvaluationLogDto>> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8'
    });
    return this.http.post<PageDto<EvaluationLogDto>>(this.serviceUrl + '/list', pageRequest, { headers: headers });
  }

  public getRequestStatusesStatistics(): Observable<RequestStatusStatisticsDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8'
    });
    return this.http.get<RequestStatusStatisticsDto>(this.serviceUrl + '/request-statuses-statistics', { headers: headers });
  }

  public getEvaluationLogDetails(id: number): Observable<EvaluationLogDetailsDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8'
    });
    return this.http.get<EvaluationLogDetailsDto>(this.serviceUrl + '/details/' + id, { headers: headers });
  }

  public getClassifiersStatistics(createdDateFrom: string, createdDateTo: string): Observable<ChartDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/x-www-form-urlencoded; charset=utf-8'
    });
    let params = new HttpParams().set('createdDateFrom', createdDateFrom).set('createdDateTo', createdDateTo);
    const options = { headers: headers, params: params };
    return this.http.get<ChartDto>(this.serviceUrl + '/classifiers-statistics', options);
  }

  public getModelContentUrl(id: number): Observable<S3ContentResponseDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8'
    });
    return this.http.get<S3ContentResponseDto>(this.serviceUrl + '/model/' + id, { headers: headers });
  }

  public getRocCurveData(evaluationLogId: number, classValueIndex: number): Observable<RocCurveDataDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8'
    });
    let params = new HttpParams()
      .set('evaluationLogId', evaluationLogId.toString())
      .set('classValueIndex', classValueIndex.toString());
    const options = { headers: headers, params: params };
    return this.http.get<RocCurveDataDto>(this.serviceUrl + '/roc-curve', options);
  }

  public downloadContent(url: string): Observable<Blob> {
    const options = { responseType: 'blob' as 'json' };
    return this.http.get<Blob>(url, options);
  }

  public downloadModel(evaluationLogDto: EvaluationLogDto, onSuccessCallback: () => void, onErrorCallback: (error: any) => void): void {
    this.getModelContentUrl(evaluationLogDto.id)
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
          saveAs(blob, evaluationLogDto.modelPath);
        },
        error: (error) => {
          onErrorCallback(error);
        }
      });
  }
}
