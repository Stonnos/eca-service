import { Component, Input, OnInit } from '@angular/core';
import { JsonPipe } from "@angular/common";
import { ClassifierOptionsDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { saveAs } from 'file-saver/dist/FileSaver';

declare var Prism: any;

@Component({
  selector: 'app-classifier-options-details',
  templateUrl: './classifier-options-details.html',
  styleUrls: ['./classifier-options-details.scss']
})
export class ClassifierOptionsDetails implements OnInit {

  @Input()
  public classifierOptions: ClassifierOptionsDto;

  public ngOnInit() {
  }

  public getFormattedJsonConfig(): string {
    if (this.classifierOptions) {
      const configObj = JSON.parse(this.classifierOptions.config);
      const json = new JsonPipe().transform(configObj);
      return Prism.highlight(json, Prism.languages['json']);
    }
    return null;
  }

  public saveClassifierOptions() {
    let blob: Blob = new Blob([this.classifierOptions.config], {type: 'application/json'});
    saveAs(blob, `${this.classifierOptions.optionsName}.json`);
  }
}
