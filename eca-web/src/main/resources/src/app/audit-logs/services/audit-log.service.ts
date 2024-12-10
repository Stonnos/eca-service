import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  AuditLogDto,
  PageDto,
  PageRequestDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from "rxjs/internal/Observable";
import { environment } from "../../../environments/environment";

@Injectable()
export class AuditLogService {

  private serviceUrl = environment.auditLogUrl + '/audit-log';

  public constructor(private http: HttpClient) {
  }

  public getAuditLogs(pageRequest: PageRequestDto): Observable<PageDto<AuditLogDto>> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8'
    });
    return this.http.post<PageDto<AuditLogDto>>(this.serviceUrl + '/list', pageRequest, { headers: headers });
  }

  public getAuditLogsBaseReport(pageRequest: PageRequestDto): Observable<Blob> {
    const options = { responseType: 'blob' as 'json' };
    return this.http.post<Blob>(this.serviceUrl + '/report/download', pageRequest, options);
  }
}
