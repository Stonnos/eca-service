import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from "../../../environments/environment";
import { ForgotPasswordRequest } from "../model/forgot-password.request";
import { ResetPasswordRequest } from "../model/reset-password.request";

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
}
