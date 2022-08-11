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

  public changeEmail(newEmail: string) {
    const headers = new HttpHeaders({
      'Authorization': Utils.getBearerTokenHeader()
    });
    const formData = new FormData();
    formData.append('newEmail', newEmail);
    return this.http.post(this.serviceUrl + '/request', formData, { headers: headers })
  }

  public confirmChangeEmailRequest(token: string) {
    const headers = new HttpHeaders({
      'Authorization': Utils.getBearerTokenHeader()
    });
    const formData = new FormData();
    formData.append('token', token);
    return this.http.post(this.serviceUrl + '/confirm', formData, { headers: headers });
  }

  public getChangeEmailRequestStatus(): Observable<ChangeEmailRequestStatusDto> {
    const headers = new HttpHeaders({
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.get<ChangeEmailRequestStatusDto>(this.serviceUrl + '/request-status', { headers: headers })
  }
}
