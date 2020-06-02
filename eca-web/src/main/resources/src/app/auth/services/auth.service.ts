import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from "rxjs/internal/Observable";
import { UserModel } from "../model/user.model";
import { AuthenticationKeys } from "../model/auth.keys";
import { environment } from "../../../environments/environment";

@Injectable()
export class AuthService {

  private serviceUrl = environment.oauthUrl;
  private clientId = environment.clientId;
  private secret = environment.secret;

  constructor(private http: HttpClient) {
  }

  public obtainAccessToken(user: UserModel): Observable<any> {
    const params = new URLSearchParams();
    params.append('username', user.login);
    params.append('password', user.password);
    params.append('grant_type', 'password');
    return this.performTokenRequest(params);
  }

  public refreshToken(): Observable<any> {
    const params = new URLSearchParams();
    params.append('refresh_token', localStorage.getItem(AuthenticationKeys.REFRESH_TOKEN));
    params.append('grant_type', 'refresh_token');
    return this.performTokenRequest(params);
  }

  public saveToken(token): void {
    localStorage.setItem(AuthenticationKeys.ACCESS_TOKEN, token.access_token);
    localStorage.setItem(AuthenticationKeys.REFRESH_TOKEN, token.refresh_token);
  }

  private performTokenRequest(params: URLSearchParams): Observable<any> {
    const headers = new HttpHeaders({
      'Content-type': 'application/x-www-form-urlencoded; charset=utf-8',
      'Authorization': this.getHttpBasicAuthorizationHeader()
    });
    const options = { headers: headers };
    return this.http.post(this.serviceUrl + '/oauth/token', params.toString(), options);
  }

  private getHttpBasicAuthorizationHeader(): string {
    return 'Basic ' + btoa(this.clientId + ':' + this.secret);
  }
}
