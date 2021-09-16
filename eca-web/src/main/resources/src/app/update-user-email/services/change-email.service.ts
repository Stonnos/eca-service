import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from "../../../environments/environment";
import { Utils } from "../../common/util/utils";

@Injectable()
export class ChangeEmailService {

  private serviceUrl = environment.oauthUrl + '/email/change';

  public constructor(private http: HttpClient) {
  }

  public changeEmail(newEmail: string) {
    const headers = new HttpHeaders({
      'Authorization': Utils.getBearerTokenHeader()
    });
    const formData = new FormData();
    formData.append('newEmail', newEmail);
    return this.http.post(this.serviceUrl + '/request', formData, { headers: headers })
  }

  public confirmChangeEmailRequest(token: string) {
    const formData = new FormData();
    formData.append('token', token);
    return this.http.post(this.serviceUrl + '/confirm', formData);
  }
}
