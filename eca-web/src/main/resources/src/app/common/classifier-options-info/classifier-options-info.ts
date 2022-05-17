import { Component, Input, OnInit } from '@angular/core';
import { ClassifierInfoDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";

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
}
