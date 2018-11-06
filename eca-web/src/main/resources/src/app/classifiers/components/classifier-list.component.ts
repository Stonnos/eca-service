import { Component } from '@angular/core';
import {
  EvaluationLogDto,
  PageDto,
  PageRequestDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { BaseListComponent } from "../../lists/base-list.component";
import { MessageService, SelectItem } from "primeng/api";
import { ClassifiersService } from "../services/classifiers.service";
import { Filter } from "../../filter/filter.model";
import { OverlayPanel } from "primeng/primeng";

@Component({
  selector: 'app-classifier-list',
  templateUrl: './classifier-list.component.html',
  styleUrls: ['./classifier-list.component.scss']
})
export class ClassifierListComponent extends BaseListComponent<EvaluationLogDto> {

  public instancesInfoColumn: string = "instancesInfo.relationName";

  public selectedEvaluationLog: EvaluationLogDto;
  public selectedColumn: string;

  public constructor(private classifiersService: ClassifiersService,
                     private messageService: MessageService) {
    super();
    this.defaultSortField = "creationDate";
    this.linkColumns = ["classifierName", "evaluationMethod", this.instancesInfoColumn];
    this.initColumns();
    this.initFilters();
  }

  public ngOnInit() {
  }

  public getNextPage(pageRequest: PageRequestDto) {
    this.classifiersService.getEvaluations(pageRequest).subscribe((pageDto: PageDto<EvaluationLogDto>) => {
      this.items = pageDto.content;
      this.total = pageDto.totalCount;
    }, (error) => {
      this.messageService.add({ severity: 'error', summary: 'Error', detail: error.message });
    });
  }

  onLink(column: string, item: EvaluationLogDto) {
  }

  public onSelect(event, evaluationLog: EvaluationLogDto, column: string, overlayPanel: OverlayPanel) {
    this.selectedEvaluationLog = evaluationLog;
    this.selectedColumn = column;
    overlayPanel.toggle(event);
  }

  public getColumnValue(column: string, item: EvaluationLogDto) {
    return column == this.instancesInfoColumn ? item.instancesInfo.relationName : item[column];
  }

  private initColumns() {
    this.columns = [
      { name: "requestId", label: "Request UUID" },
      { name: "classifierName", label: "Classifier name" },
      { name: this.instancesInfoColumn, label: "Training data" },
      { name: "evaluationMethod", label: "Evaluation method" },
      { name: "creationDate", label: "Creation date" },
      { name: "startDate", label: "Start date" },
      { name: "endDate", label: "End date" },
      { name: "evaluationStatus", label: "Status" }
    ];
  }

  private initFilters() {
    const evaluationMethods: SelectItem[] = [
      { label: "TRAINING_DATA", value: "TRAINING_DATA" },
      { label: "CROSS_VALIDATION", value: "CROSS_VALIDATION" }
    ];
    const statuses: SelectItem[] = [
      { label: "NEW", value: "NEW" },
      { label: "FINISHED", value: "FINISHED" },
      { label: "TIMEOUT", value: "TIMEOUT" },
      { label: "ERROR", value: "ERROR" }
    ];
    this.filters.push(new Filter("requestId", "Request UUID",
      "TEXT", "EQUALS", null));
    this.filters.push(new Filter("classifierName", "Classifier name",
      "TEXT", "LIKE", null));
    this.filters.push(new Filter("evaluationMethod", "Evaluation method", "REFERENCE",
      "EQUALS", null, evaluationMethods));
    this.filters.push(new Filter("evaluationStatus", "Evaluation status", "REFERENCE",
      "EQUALS", null, statuses));
    this.filters.push(new Filter("creationDate", "Creation date from",
      "DATE", "GTE", null));
    this.filters.push(new Filter("creationDate", "Creation date to",
      "DATE", "LTE", null));
  }

}
