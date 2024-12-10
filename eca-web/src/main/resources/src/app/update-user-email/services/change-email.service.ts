import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from "../../../environments/environment";
import { Utils } from "../../common/util/utils";
import { Observable } from "rxjs/internal/Observable";
import { ChangeEmailRequestStatusDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";

@Injectable()
export class ChangeEmailService {

  private serviceUrl = environment.oauthUrl + '/email/change';

  public constructor(private http: HttpClient) {
  }

  public changeEmail(newEmail: string): Observable<ChangeEmailRequestStatusDto> {
    const formData = new FormData();
    formData.append('newEmail', newEmail);
    return this.http.post<ChangeEmailRequestStatusDto>(this.serviceUrl + '/request', formData)
  }

  public confirmChangeEmailRequest(token: string, confirmationCode: string) {
    const formData = new FormData();
    formData.append('token', token);
    formData.append('confirmationCode', confirmationCode);
    return this.http.post(this.serviceUrl + '/confirm', formData);
  }

  public getChangeEmailRequestStatus(): Observable<ChangeEmailRequestStatusDto> {
    return this.http.get<ChangeEmailRequestStatusDto>(this.serviceUrl + '/request-status')
  }
}
