import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from "rxjs/internal/Observable";

@Injectable()
export class AuthService {

  private serviceUrl = "http://localhost:8085/eca-oauth/oauth/token";
  private secret = 'eca:web_secret';

  constructor(private router: Router, private http: HttpClient) {
  }

  obtainAccessToken(username: string, password: string): Observable<any> {
    const params = new URLSearchParams();
    params.append('username', username);
    params.append('password', password);
    params.append('grant_type', 'password');
    params.append('client_id', 'eca');
    const headers = new HttpHeaders({
      'Content-type': 'application/x-www-form-urlencoded; charset=utf-8',
      'Authorization': 'Basic ' + btoa(this.secret)
    });
    const options = { headers: headers };
    return this.http.post(this.serviceUrl, params.toString(), options);
  }
}
