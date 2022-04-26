import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  FormTemplateDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from "rxjs/internal/Observable";
import { environment } from "../../../environments/environment";
import { Utils } from "../../common/util/utils";

@Injectable()
export class FormTemplatesService {

  private ecaServerUrl = environment.serverUrl;

  public constructor(private http: HttpClient) {
  }

  public getClassifiersFormTemplates(): Observable<FormTemplateDto[]> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8',
      'Authorization': Utils.getBearerTokenHeader()
    });
    return this.http.get<FormTemplateDto[]>(this.ecaServerUrl + '/classifiers/templates/list', { headers: headers });
  }
}
