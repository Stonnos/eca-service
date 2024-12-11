import { HttpClient, HttpHeaders, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import {
  FormTemplateGroupDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { Observable } from "rxjs/internal/Observable";
import { environment } from "../../../environments/environment";
import { ClassifierGroupTemplatesType } from "../model/classifier-group-templates.type";

@Injectable()
export class FormTemplatesService {

  private ecaServerUrl = environment.serverUrl;

  public constructor(private http: HttpClient) {
  }

  public getClassifiersFormTemplates(classifierGroupTemplatesType: ClassifierGroupTemplatesType): Observable<FormTemplateGroupDto[]> {
    const headers = new HttpHeaders({
      'Content-type': 'application/json; charset=utf-8'
    });
    const params: HttpParams = new HttpParams()
      .set('classifierGroupTemplatesType', classifierGroupTemplatesType);
    const options = { headers: headers, params: params };
    return this.http.get<FormTemplateGroupDto[]>(this.ecaServerUrl + '/classifiers/templates/list', options);
  }
}
