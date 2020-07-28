import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from "rxjs/internal/Observable";
import { PageRequestDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { PageRequestService } from "./page-request.service";
import { environment } from "../../../environments/environment";
import { Utils } from "../util/utils";

@Injectable()
export class ReportsService {

  private serviceUrl = environment.serverUrl + '/reports';

  public constructor(private http: HttpClient, private pageRequestService: PageRequestService) {
  }

  public getExperimentsBaseReport(pageRequest: PageRequestDto): Observable<Blob> {
    const headers = new HttpHeaders({
      'Authorization': Utils.getBearerTokenHeader()
    });
    const params: HttpParams = this.pageRequestService.convertToHttpRequestParams(pageRequest);
    const options = { headers: headers, params: params, responseType: 'blob' as 'json' };
    return this.http.get<Blob>(this.serviceUrl + '/experiments', options);
  }

  public getEvaluationLogsBaseReport(pageRequest: PageRequestDto): Observable<Blob> {
    const headers = new HttpHeaders({
      'Authorization': Utils.getBearerTokenHeader()
    });
    const params: HttpParams = this.pageRequestService.convertToHttpRequestParams(pageRequest);
    const options = { headers: headers, params: params, responseType: 'blob' as 'json' };
    return this.http.get<Blob>(this.serviceUrl + '/evaluations', options);
  }
}
