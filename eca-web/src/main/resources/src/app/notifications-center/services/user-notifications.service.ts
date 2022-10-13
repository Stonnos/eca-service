import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  PageDto, ReadNotificationsDto, SimplePageRequestDto, UserNotificationDto, UserNotificationStatisticsDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from "rxjs/internal/Observable";
import { environment } from "../../../environments/environment";
import { Utils } from "../../common/util/utils";

@Injectable()
export class UserNotificationsService {

  private serviceUrl = environment.webPushUrl + '/notifications';

  public constructor(private http: HttpClient) {
  }

  public getNotifications(pageRequest: SimplePageRequestDto): Observable<PageDto<UserNotificationDto>> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.post<PageDto<UserNotificationDto>>(this.serviceUrl + '/list', pageRequest, { headers: headers });
  }

  public getNotificationsStatistics(): Observable<UserNotificationStatisticsDto> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.get<UserNotificationStatisticsDto>(this.serviceUrl + '/statistics', { headers: headers });
  }

  public readNotifications(readNotificationsDto: ReadNotificationsDto): Observable<any> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.post(this.serviceUrl + '/read', readNotificationsDto, { headers: headers });
  }
}
