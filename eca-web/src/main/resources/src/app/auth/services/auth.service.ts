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

  public verifyTfaCode(token: string, code: string): Observable<any> {
    const params = new URLSearchParams();
    params.append('token', token);
    params.append('tfa_code', code);
    params.append('grant_type', 'tfa_code');
    return this.performTokenRequest(params);
  }

  public refreshToken(): Observable<any> {
    const params = new URLSearchParams();
    params.append('grant_type', 'refresh_token');
    return this.performTokenRequest(params);
  }

  public saveLoggedInData(): void {
    localStorage.setItem(AuthenticationKeys.LOGGED_IN, String(true));
  }

  public logoutRequest(): Observable<any> {
    return this.http.post(this.serviceUrl + '/users/logout', null);
  }

  private performTokenRequest(params: URLSearchParams): Observable<any> {
    const headers = new HttpHeaders({
      'Content-type': 'application/x-www-form-urlencoded; charset=utf-8',
      'Authorization': this.getHttpBasicAuthorizationHeader()
    });
    const options = { headers: headers };
    return this.http.post(this.serviceUrl + '/oauth2/token', params.toString(), options);
  }

  private getHttpBasicAuthorizationHeader(): string {
    return 'Basic ' + btoa(this.clientId + ':' + this.secret);
  }
}
