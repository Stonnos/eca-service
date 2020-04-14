import { Component, OnInit } from '@angular/core';
import { BaseCreateDialogComponent } from "../../common/dialog/base-create-dialog.component";
import { ClassifiersConfigurationModel } from "../model/classifiers-configuration.model";

@Component({
  selector: 'app-create-classifiers-configuration',
  templateUrl: './create-classifiers-configuration.component.html',
  styleUrls: ['./create-classifiers-configuration.component.scss']
})
export class CreateClassifiersConfigurationComponent extends BaseCreateDialogComponent<ClassifiersConfigurationModel> implements OnInit {

  public ngOnInit(): void {
  }

}
