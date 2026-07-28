import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from "../../../environments/environment";
import { Observable } from "rxjs/internal/Observable";
import { ChangeEmailRequestStatusDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { ChangeEmailRequest } from '../model/change-email.request';

@Injectable()
export class ChangeEmailService {

  private serviceUrl = environment.oauthUrl + '/email/change';

  public constructor(private http: HttpClient) {
  }

  public changeEmail(changeEmailRequest: ChangeEmailRequest): Observable<ChangeEmailRequestStatusDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8'
    });
    return this.http.post<ChangeEmailRequestStatusDto>(this.serviceUrl + '/request', changeEmailRequest, { headers: headers })
  }

  public confirmChangeEmailRequest(token: string, confirmationCode: string) {
    const formData = new FormData();
    formData.append('token', token);
    formData.append('confirmationCode', confirmationCode);
    return this.http.post(this.serviceUrl + '/confirm', formData);
  }

  public revokeChangeEmail(token: string) {
    const formData = new FormData();
    formData.append('revocationToken', token);
    return this.http.post(this.serviceUrl + '/revoke', formData);
  }

  public getChangeEmailRequestStatus(): Observable<ChangeEmailRequestStatusDto> {
    return this.http.get<ChangeEmailRequestStatusDto>(this.serviceUrl + '/request-status')
  }
}
