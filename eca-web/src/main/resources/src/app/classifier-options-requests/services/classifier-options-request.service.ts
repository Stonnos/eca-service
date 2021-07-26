import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  ClassifierOptionsRequestDto,
  PageDto,
  PageRequestDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from "rxjs/internal/Observable";
import { environment } from "../../../environments/environment";
import { Utils } from "../../common/util/utils";

@Injectable()
export class ClassifierOptionsRequestService {

  private serviceUrl = environment.serverUrl;

  public constructor(private http: HttpClient) {
  }

  public getClassifiersOptionsRequests(pageRequest: PageRequestDto): Observable<PageDto<ClassifierOptionsRequestDto>> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.post<PageDto<ClassifierOptionsRequestDto>>(this.serviceUrl + '/classifiers-options-requests',
      pageRequest, { headers: headers });
  }
}
