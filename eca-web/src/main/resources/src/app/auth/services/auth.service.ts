import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from "rxjs/internal/Observable";
import { ConfigService } from "../../config.service";
import { UserModel } from "../components/user.model";
import { AuthenticationKeys } from "../model/auth.keys";

@Injectable()
export class AuthService {

  private serviceUrl = ConfigService.appConfig.oauthUrl;
  private clientId = ConfigService.appConfig.clientId;
  private secret = ConfigService.appConfig.secret;

  constructor(private http: HttpClient) {
  }

  public obtainAccessToken(user: UserModel): Observable<any> {
    const params = new URLSearchParams();
    params.append('username', user.login);
    params.append('password', user.password);
    params.append('grant_type', 'password');
    const headers = new HttpHeaders({
      'Content-type': 'application/x-www-form-urlencoded; charset=utf-8',
      'Authorization': 'Basic ' + btoa(this.clientId + ':' + this.secret)
    });
    const options = { headers: headers };
    return this.http.post(this.serviceUrl, params.toString(), options);
  }

  public refreshToken(): Observable<any> {
    const params = new URLSearchParams();
    params.append('refresh_token', localStorage.getItem(AuthenticationKeys.REFRESH_TOKEN));
    params.append('grant_type', 'refresh_token');
    const headers = new HttpHeaders({
      'Content-type': 'application/x-www-form-urlencoded; charset=utf-8',
      'Authorization': 'Basic ' + btoa(this.clientId + ':' + this.secret)
    });
    const options = { headers: headers };
    return this.http.post(this.serviceUrl, params.toString(), options);
  }

  public saveToken(token): void {
    const expireDate = new Date().getTime() + 1000 * token.expires_in;
    localStorage.setItem(AuthenticationKeys.ACCESS_TOKEN, token.access_token);
    localStorage.setItem(AuthenticationKeys.REFRESH_TOKEN, token.refresh_token);
    localStorage.setItem(AuthenticationKeys.EXPIRE_DATE, expireDate.toString());
  }
}
