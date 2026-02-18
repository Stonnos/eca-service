import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  AttributeDto,
  AttributeValueDto,
  ContingencyTableReportDto
} from '../../../../../../../target/generated-sources/typescript/eca-web-dto';
import { BaseDialog } from '../../common/dialog/base-dialog';
import { InstancesService } from '../../instances/services/instances.service';
import { finalize } from 'rxjs/internal/operators';
import { MessageService } from 'primeng/api';
import { ContingencyTableRequestModel } from '../model/contingency-table-request-model';
import { ColumnModel } from '../../common/model/column.model';

@Component({
  selector: 'app-contingency-table-dialog',
  templateUrl: './contingency-table-dialog.html',
  styleUrls: ['./contingency-table-dialog.scss']
})
export class ContingencyTableDialog implements BaseDialog, OnInit {

  private static readonly DEFAULT_ALPHA_VAL: number = 0.05;

  public alphaVal: number = ContingencyTableDialog.DEFAULT_ALPHA_VAL;
  public minAlphaVal: number = 0.001;
  public maxAlphaVal: number = 0.999;
  public useYates: boolean = false;

  public columns: any = [];

  public tableItems: any[] = [];

  public selectedXAttribute: AttributeDto;

  public selectedYAttribute: AttributeDto;

  public reportDto: ContingencyTableReportDto;

  public loading: boolean = false;

  @Input()
  public visible: boolean = false;
  @Input()
  public instancesId: number;
  @Input()
  public attributes: AttributeDto[] = [];

  @Output()
  public visibilityChange: EventEmitter<boolean> = new EventEmitter();

  public constructor(private instancesService: InstancesService,
                     private messageService: MessageService) {
  }

  public ngOnInit(): void {
  }

  public hide() {
    this.visibilityChange.emit(false);
  }

  public onHideOptions(): void {
   this.setDefaultAlphaIfNotValid();
  }

  public fetchReport(): void {
    this.setDefaultAlphaIfNotValid();
    const request = new ContingencyTableRequestModel(this.instancesId, this.selectedXAttribute.id, this.selectedYAttribute.id, this.alphaVal, this.useYates);
    this.loading = true;
    this.instancesService.getContingencyTableReport(request)
      .pipe(
        finalize(() => {
          this.loading = false;
        })
      )
      .subscribe({
        next: (reportDto: ContingencyTableReportDto) => {
          this.handleReport(reportDto);
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }

  private setDefaultAlphaIfNotValid(): void {
    if (!this.alphaVal || this.alphaVal < this.minAlphaVal || this.alphaVal > this.maxAlphaVal) {
      this.alphaVal = ContingencyTableDialog.DEFAULT_ALPHA_VAL;
    }
  }

  private handleReport(reportDto: ContingencyTableReportDto): void {
    let yAttrColValues = reportDto.yattribute.values.map((valueDto: AttributeValueDto) => new ColumnModel(valueDto.value, valueDto.value));
    let attrColName = `${reportDto.xattribute.name}/${reportDto.yattribute.name}`;
    this.columns = [new ColumnModel(attrColName, attrColName), ...yAttrColValues, new ColumnModel('Всего', 'Всего')];
    console.log(JSON.stringify(this.columns));
    this.tableItems = reportDto.tableData.map((row: number[], index: number) => {
      if (index < reportDto.xattribute.values.length) {
        return [reportDto.xattribute.values[index].value, ...row];
      } else {
        return ['Всего', ...row]
      }
    });
    this.reportDto = reportDto;
  }
}
