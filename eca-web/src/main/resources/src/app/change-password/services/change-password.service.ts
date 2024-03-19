import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from "../../../environments/environment";
import { ChangePasswordRequest } from "../model/change-password.request";
import { Utils } from "../../common/util/utils";
import { Observable } from "rxjs/internal/Observable";
import {
  ChangePasswordRequestStatusDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";

@Injectable()
export class ChangePasswordService {

  private serviceUrl = environment.oauthUrl + '/password/change';

  public constructor(private http: HttpClient) {
  }

  public changePassword(changePasswordRequest: ChangePasswordRequest): Observable<ChangePasswordRequestStatusDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.post<ChangePasswordRequestStatusDto>(this.serviceUrl + '/request', changePasswordRequest, { headers: headers })
  }

  public confirmChangePasswordRequest(token: string, confirmationCode: string) {
    const headers = new HttpHeaders({
      'Authorization': Utils.getBearerTokenHeader()
    });
    const formData = new FormData();
    formData.append('token', token);
    formData.append('confirmationCode', confirmationCode);
    return this.http.post(this.serviceUrl + '/confirm', formData, { headers: headers });
  }

  public getChangePasswordRequestStatus(): Observable<ChangePasswordRequestStatusDto> {
    const headers = new HttpHeaders({
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.get<ChangePasswordRequestStatusDto>(this.serviceUrl + '/request-status', { headers: headers })
  }
}
