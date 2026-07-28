import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from "../../../environments/environment";
import { Observable } from "rxjs/internal/Observable";
import { ForceSetPasswordRequest } from '../model/force-set-password.request';

@Injectable()
export class ForceSetPasswordService {

  private serviceUrl = environment.oauthUrl + '/force-set-password';

  public constructor(private http: HttpClient) {
  }

  public forceSetPassword(forceSetPasswordRequest: ForceSetPasswordRequest): Observable<any> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8'
    });
    return this.http.post(this.serviceUrl, forceSetPasswordRequest, { headers: headers })
  }
}
