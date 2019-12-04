import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from "rxjs/internal/Observable";
import { ConfigService } from "../../config.service";
import { AuthenticationKeys } from "../../auth/model/auth.keys";

@Injectable()
export class ReportsService {

  private serviceUrl = ConfigService.appConfig.apiUrl + '/reports';

  public constructor(private http: HttpClient) {
  }

  public getExperimentsBaseReport(): Observable<Blob> {
    const headers = new HttpHeaders({
      'Authorization': 'Bearer ' + localStorage.getItem(AuthenticationKeys.ACCESS_TOKEN)
    });
    const options = { headers: headers, responseType: 'blob' as 'json' };
    return this.http.get<Blob>(this.serviceUrl + '/experiments', options);
  }

  public getEvaluationLogsBaseReport(): Observable<Blob> {
    const headers = new HttpHeaders({
      'Authorization': 'Bearer ' + localStorage.getItem(AuthenticationKeys.ACCESS_TOKEN)
    });
    const options = { headers: headers, responseType: 'blob' as 'json' };
    return this.http.get<Blob>(this.serviceUrl + '/evaluations', options);
  }
}
