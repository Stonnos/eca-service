import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from "rxjs/internal/Observable";
import { UiPermissionsDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { environment } from "../../../environments/environment";

@Injectable()
export class WebAppService {

  public constructor(private http: HttpClient) {
  }

  public getUiPermissions(): Observable<UiPermissionsDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8'
    });
    return this.http.get<UiPermissionsDto>(environment.oauthUrl + '/ui-permissions', { headers: headers });
  }
}
