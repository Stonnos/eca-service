import { Component, Input, OnInit } from '@angular/core';
import { ClassifierInfoDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { JsonPipe } from "@angular/common";
import { saveAs } from 'file-saver/dist/FileSaver';

declare var Prism: any;

@Component({
  selector: 'app-classifier-options-info',
  templateUrl: './classifier-options-info.html',
  styleUrls: ['./classifier-options-info.scss']
})
export class ClassifierOptionsInfo implements OnInit {

  @Input()
  public classifierInfo: ClassifierInfoDto;

  public ngOnInit(): void {
  }

  public getFormattedJsonConfig(): string {
    if (this.classifierInfo && this.classifierInfo.classifierOptionsJson) {
      const configObj = JSON.parse(this.classifierInfo.classifierOptionsJson);
      const json = new JsonPipe().transform(configObj);
      return Prism.highlight(json, Prism.languages['json']);
    }
    return null;
  }

  public saveClassifierOptions() {
    if (this.classifierInfo.classifierOptionsJson) {
      const classifierConfig = this.classifierInfo.classifierOptionsJson;
      if (classifierConfig) {
        let blob: Blob = new Blob([classifierConfig], {type: 'application/json'});
        saveAs(blob, `${this.classifierInfo.classifierName}.json`);
      }
    }
  }
}
