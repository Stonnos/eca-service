import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from "rxjs/internal/Observable";
import { MenuItemDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { environment } from "../../../environments/environment";
import { Utils } from "../util/utils";

@Injectable()
export class WebAppService {

  private serviceUrl = environment.webAppUrl + '/api/web';

  public constructor(private http: HttpClient) {
  }

  public getMenuItems(): Observable<MenuItemDto[]> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.get<MenuItemDto[]>(this.serviceUrl + '/menu-bar', { headers: headers });
  }
}
