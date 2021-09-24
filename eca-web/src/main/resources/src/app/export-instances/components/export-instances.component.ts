import { Component, Input } from '@angular/core';
import { BaseCreateDialogComponent } from "../../common/dialog/base-create-dialog.component";
import { ExportInstancesModel } from "../model/export-instances.model";
import { InstancesReportInfoDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";

@Component({
  selector: 'app-export-instances',
  templateUrl: './export-instances.component.html',
  styleUrls: ['./export-instances.component.scss']
})
export class ExportInstancesComponent extends BaseCreateDialogComponent<ExportInstancesModel> {

  @Input()
  public reportTypes: InstancesReportInfoDto[] = [];

  public loading: boolean = false;

  public ngOnInit(): void {
  }
}
