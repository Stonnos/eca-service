import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  AuditLogDto,
  PageDto,
  PageRequestDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from "rxjs/internal/Observable";
import { PageRequestService } from "../../common/services/page-request.service";
import { environment } from "../../../environments/environment";
import { Utils } from "../../common/util/utils";

@Injectable()
export class AuditLogService {

  private serviceUrl = environment.auditLogUrl + '/audit-log';

  public constructor(private http: HttpClient, private pageRequestService: PageRequestService) {
  }

  public getAuditLogs(pageRequest: PageRequestDto): Observable<PageDto<AuditLogDto>> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': Utils.getBearerTokenHeader()
    });
    const params: HttpParams = this.pageRequestService.convertToHttpRequestParams(pageRequest);
    const options = { headers: headers, params: params };
    return this.http.get<PageDto<AuditLogDto>>(this.serviceUrl + '/list', options);
  }

  public getAuditLogsBaseReport(pageRequest: PageRequestDto): Observable<Blob> {
    const headers = new HttpHeaders({
      'Authorization': Utils.getBearerTokenHeader()
    });
    const params: HttpParams = this.pageRequestService.convertToHttpRequestParams(pageRequest);
    const options = { headers: headers, params: params, responseType: 'blob' as 'json' };
    return this.http.get<Blob>(this.serviceUrl + '/report/download', options);
  }
}
