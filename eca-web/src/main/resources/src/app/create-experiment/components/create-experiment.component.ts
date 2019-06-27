import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { ExperimentRequest } from "../model/experiment-request.model";
import { FilterService } from "../../filter/services/filter.service";
import { MessageService, SelectItem } from "primeng/api";
import {
  FilterDictionaryDto,
  FilterDictionaryValueDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";

@Component({
  selector: 'app-create-experiment',
  templateUrl: './create-experiment.component.html',
  styleUrls: ['./create-experiment.component.scss']
})
export class CreateExperimentComponent implements OnInit {

  public experimentRequest: ExperimentRequest = new ExperimentRequest();

  public experimentTypes: SelectItem[] = [];
  public evaluationMethods: SelectItem[] = [];

  @Input()
  public visible: boolean = false;

  @Output()
  public visibilityChange: EventEmitter<boolean> = new EventEmitter();

  @Output()
  public createEvent: EventEmitter<ExperimentRequest> = new EventEmitter();

  public constructor(private filterService: FilterService,
                     private messageService: MessageService) {
  }

  public ngOnInit(): void {
  }
  
  public hide(): void {
    this.visibilityChange.emit(false);
  }

  public onCreate(): void {
    this.createEvent.emit(this.experimentRequest);
    this.hide();
  }

  public onUpload(event: any, fileUpload: any): void {
    console.log('On upload');
    this.experimentRequest.trainingDataFile = event.files[0];
    fileUpload.clear();
  }

  public getExperimentTypes(): void {
    this.filterService.getExperimentTypeDictionary().subscribe((filterDictionary: FilterDictionaryDto) => {
      this.experimentTypes = filterDictionary.values.filter(value => !!value).map((filterValue: FilterDictionaryValueDto) => {
        return { label: filterValue.label, value: filterValue.value };
      });
    }, (error) => {
      this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
    });
  }

  public getEvaluationMethods(): void {
    this.filterService.getEvaluationMethodDictionary().subscribe((filterDictionary: FilterDictionaryDto) => {
      this.evaluationMethods = filterDictionary.values.filter(value => !!value).map((filterValue: FilterDictionaryValueDto) => {
        return { label: filterValue.label, value: filterValue.value };
      });
    }, (error) => {
      this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
    });
  }
}
