import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from "../../../environments/environment";
import { ForgotPasswordRequest } from "../model/forgot-password.request";
import { ResetPasswordRequest } from "../model/reset-password.request";
import { Observable } from "rxjs/internal/Observable";

@Injectable()
export class ResetPasswordService {

  private serviceUrl = environment.oauthUrl + '/password';

  public constructor(private http: HttpClient) {
  }

  public forgotPassword(forgotPasswordRequest: ForgotPasswordRequest) {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8'
    });
    return this.http.post(this.serviceUrl + '/forgot', forgotPasswordRequest, { headers: headers })
  }

  public resetPassword(resetPasswordRequest: ResetPasswordRequest) {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8'
    });
    return this.http.post(this.serviceUrl + '/reset', resetPasswordRequest, { headers: headers })
  }

  public verifyToken(token: string): Observable<boolean> {
    const formData = new FormData();
    formData.append('token', token);
    return this.http.post<boolean>(this.serviceUrl + '/verify-token', formData);
  }
}
