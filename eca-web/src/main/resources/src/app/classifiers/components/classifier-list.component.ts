import { Component, OnInit } from '@angular/core';
import { EvaluationLogDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";

@Component({
  selector: 'app-classifier-list',
  templateUrl: './classifier-list.component.html',
  styleUrls: ['./classifier-list.component.scss']
})
export class ClassifierListComponent implements OnInit {

  public evaluationLogs: EvaluationLogDto[] = [];

  public ngOnInit() {
  }

}
