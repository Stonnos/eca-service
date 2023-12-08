import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  EvaluationResultsHistoryDto, InstancesInfoDto,
  PageDto,
  PageRequestDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from "rxjs/internal/Observable";
import { environment } from "../../../environments/environment";
import { Utils } from "../../common/util/utils";

@Injectable()
export class EvaluationResultsHistoryService {

  private serviceUrl = environment.ersUrl + '/evaluation-results';

  public constructor(private http: HttpClient) {
  }

  public getEvaluationResultsHistory(pageRequest: PageRequestDto): Observable<PageDto<EvaluationResultsHistoryDto>> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.post<PageDto<EvaluationResultsHistoryDto>>(this.serviceUrl + '/history', pageRequest, { headers: headers });
  }

  public getInstancesInfoHistoryPage(pageRequest: PageRequestDto): Observable<PageDto<InstancesInfoDto>> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.post<PageDto<InstancesInfoDto>>(this.serviceUrl + '/instances/history', pageRequest, { headers: headers });
  }
}
