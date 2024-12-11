import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from "rxjs/internal/Observable";
import { PageRequestDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { environment } from "../../../environments/environment";
import { ReportType } from "../model/report-type.enum";

@Injectable()
export class ReportsService {

  private serviceUrl = environment.serverUrl + '/reports';

  public constructor(private http: HttpClient) {
  }

  public getBaseReport(pageRequest: PageRequestDto, reportType: ReportType): Observable<Blob> {
    const params: HttpParams = new HttpParams()
        .set('reportType', reportType);
    const options = { params: params, responseType: 'blob' as 'json' };
    return this.http.post<Blob>(this.serviceUrl + '/download', pageRequest, options);
  }
}
