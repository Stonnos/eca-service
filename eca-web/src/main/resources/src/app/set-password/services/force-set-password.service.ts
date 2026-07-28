import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from "../../../environments/environment";
import { Observable } from "rxjs/internal/Observable";
import { ForceSetPasswordRequest } from '../model/force-set-password.request';

@Injectable()
export class ForceSetPasswordService {

  private serviceUrl = environment.oauthUrl + '/force-set-password';

  private token;

  public constructor(private http: HttpClient) {
  }

  public setToken(token: string): void {
    this.token = token;
  }

  public getToken(): void {
    return this.token;
  }

  public forceSetPassword(forceSetPasswordRequest: ForceSetPasswordRequest): Observable<any> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8'
    });
    return this.http.post(this.serviceUrl, forceSetPasswordRequest, { headers: headers })
  }

  public verifyToken(token: string): Observable<boolean> {
    const formData = new FormData();
    formData.append('token', token);
    return this.http.post<boolean>(this.serviceUrl + '/verify-token', formData);
  }
}
