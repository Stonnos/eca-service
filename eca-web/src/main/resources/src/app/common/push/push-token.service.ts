import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  PushTokenDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from "rxjs/internal/Observable";
import { environment } from "../../../environments/environment";
import { Utils } from "../util/utils";

@Injectable()
export class PushTokenService {

  private serviceUrl = environment.webPushUrl + '/push/token';

  public constructor(private http: HttpClient) {
  }

  public obtainPushToken(): Observable<PushTokenDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.post<PushTokenDto>(this.serviceUrl, null,{ headers: headers });
  }
}
