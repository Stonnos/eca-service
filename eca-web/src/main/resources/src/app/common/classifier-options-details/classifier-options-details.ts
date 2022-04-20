import { Component, Input, OnInit } from '@angular/core';
import { JsonPipe } from "@angular/common";
import { InputOptionDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";

declare var Prism: any;

@Component({
  selector: 'app-classifier-options-details',
  templateUrl: './classifier-options-details.html',
  styleUrls: ['./classifier-options-details.scss']
})
export class ClassifierOptionsDetails implements OnInit {

  @Input()
  public jsonOptions: string;

  @Input()
  public inputOptions: InputOptionDto[] = [];

  public ngOnInit() {
  }

  public getFormattedJsonConfig(): string {
    if (this.jsonOptions) {
      const configObj = JSON.parse(this.jsonOptions);
      const json = new JsonPipe().transform(configObj);
      return Prism.highlight(json, Prism.languages['json']);
    }
    return null;
  }
}
