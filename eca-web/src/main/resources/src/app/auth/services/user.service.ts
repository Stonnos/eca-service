import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from "rxjs/internal/Observable";
import { CookieService } from "ngx-cookie-service";
import { ConfigService } from "../../config.service";
import {
  UserDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";

@Injectable()
export class UserService {

  private serviceUrl = ConfigService.appConfig.apiUrl;

  constructor(private http: HttpClient, private cookieService: CookieService) {
  }

  public getCurrentUser(): Observable<UserDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': 'Bearer ' + this.cookieService.get('access_token')
    });
    return this.http.get<UserDto>(this.serviceUrl + '/current-user', { headers: headers });
  }
}
