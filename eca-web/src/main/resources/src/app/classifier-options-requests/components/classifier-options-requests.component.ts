import { Component, OnInit } from '@angular/core';
import {
  ClassifierOptionsRequestDto, PageDto,
  PageRequestDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import {MessageService, SelectItem} from "primeng/api";
import { BaseListComponent } from "../../lists/base-list.component";
import { OverlayPanel} from "primeng/primeng";
import { JsonPipe } from "@angular/common";
import { ClassifierOptionsRequestService } from "../services/classifier-options-request.service";
import {Filter} from "../../filter/filter.model";

declare var Prism: any;

@Component({
  selector: 'app-classifier-options-requests',
  templateUrl: './classifier-options-requests.component.html',
  styleUrls: ['./classifier-options-requests.component.scss']
})
export class ClassifierOptionsRequestsComponent extends BaseListComponent<ClassifierOptionsRequestDto> implements OnInit {

  public selectedRequest: ClassifierOptionsRequestDto;
  public selectedColumn: string;

  public constructor(private classifierOptionsService: ClassifierOptionsRequestService,
                     private messageService: MessageService) {
    super();
    this.defaultSortField = "requestDate";
    this.linkColumns = ["classifierName", "evaluationMethod"];
    this.notSortableColumns = ["classifierName"];
    this.initColumns();
    this.initFilters();
  }

  public ngOnInit() {
  }

  public getNextPage(pageRequest: PageRequestDto) {
    this.classifierOptionsService.getClassifiersOptionsRequests(pageRequest)
      .subscribe((pageDto: PageDto<ClassifierOptionsRequestDto>) => {
      this.setPage(pageDto);
    }, (error) => {
      this.messageService.add({ severity: 'error', summary: 'Error', detail: error.message });
    });
  }

  public getColumnValue(column: string, item: ClassifierOptionsRequestDto) {
    if (column == "classifierName" && this.hasClassifierOptionsResponse(item)) {
      return item.classifierOptionsResponseModels[0].classifierName;
    } else {
      return item[column];
    }
  }

  public hasClassifierOptionsResponse(item: ClassifierOptionsRequestDto): boolean {
    return !!item && !!item.classifierOptionsResponseModels && item.classifierOptionsResponseModels.length > 0;
  }

  public onSelect(event, classifierOptionsRequestDto: ClassifierOptionsRequestDto, column: string, overlayPanel: OverlayPanel) {
    this.selectedRequest = classifierOptionsRequestDto;
    this.selectedColumn = column;
    overlayPanel.toggle(event);
  }

  public getFormattedJsonConfig(): string {
    if (this.hasClassifierOptionsResponse(this.selectedRequest)) {
      const classifierConfig = this.selectedRequest.classifierOptionsResponseModels[0].options;
      if (!!classifierConfig) {
        const configObj = JSON.parse(classifierConfig);
        const json = new JsonPipe().transform(configObj);
        return Prism.highlight(json, Prism.languages['json']);
      }
    }
    return null;
  }

  private initColumns() {
    this.columns = [
      { name: "requestId", label: "Request UUID" },
      { name: "relationName", label: "Relation name" },
      { name: "classifierName", label: "Classifier name" },
      { name: "evaluationMethod", label: "Evaluation method" },
      { name: "requestDate", label: "Request date" },
      { name: "responseStatus", label: "Response status" }
    ];
  }

  private initFilters() {
    const evaluationMethods: SelectItem[] = [
      { label: "All", value: null },
      { label: "TRAINING_DATA", value: "TRAINING_DATA" },
      { label: "CROSS_VALIDATION", value: "CROSS_VALIDATION" }
    ];
    const statuses: SelectItem[] = [
      { label: "All", value: null },
      { label: "SUCCESS", value: "SUCCESS" },
      { label: "INVALID_REQUEST_PARAMS", value: "INVALID_REQUEST_PARAMS" },
      { label: "DATA_NOT_FOUND", value: "DATA_NOT_FOUND" },
      { label: "RESULTS_NOT_FOUND", value: "RESULTS_NOT_FOUND" },
      { label: "ERROR", value: "ERROR" }
    ];
    this.filters.push(new Filter("requestId", "Request UUID",
      "TEXT", "EQUALS", null));
    this.filters.push(new Filter("relationName", "Training data",
      "TEXT", "LIKE", null));
    this.filters.push(new Filter("evaluationMethod", "Evaluation method", "REFERENCE",
      "EQUALS", null, evaluationMethods));
    this.filters.push(new Filter("responseStatus", "Response status", "REFERENCE",
      "EQUALS", null, statuses));
    this.filters.push(new Filter("requestDate", "Request date from",
      "DATE", "GTE", null));
    this.filters.push(new Filter("requestDate", "Request date to",
      "DATE", "LTE", null));
  }
}
