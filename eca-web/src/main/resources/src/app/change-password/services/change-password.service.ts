import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from "../../../environments/environment";
import { ChangePasswordRequest } from "../model/change-password.request";
import { Utils } from "../../common/util/utils";

@Injectable()
export class ChangePasswordService {

  private serviceUrl = environment.oauthUrl + '/password/change';

  public constructor(private http: HttpClient) {
  }

  public changePassword(changePasswordRequest: ChangePasswordRequest) {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.post(this.serviceUrl + '/request', changePasswordRequest, { headers: headers })
  }

  public confirmChangePasswordRequest(token: string) {
    const headers = new HttpHeaders({
      'Authorization': Utils.getBearerTokenHeader()
    });
    const formData = new FormData();
    formData.append('token', token);
    return this.http.post(this.serviceUrl + '/confirm', formData, { headers: headers });
  }
}
