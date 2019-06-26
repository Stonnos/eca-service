import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { ExperimentRequest } from "../model/experiment-request.model";

@Component({
  selector: 'app-create-experiment',
  templateUrl: './create-experiment.component.html',
  styleUrls: ['./create-experiment.component.scss']
})
export class CreateExperimentComponent implements OnInit {

  public experimentRequest: ExperimentRequest = new ExperimentRequest();

  @Input()
  public visible: boolean = false;

  @Output()
  public visibilityChange: EventEmitter<boolean> = new EventEmitter();

  @Output()
  public createEvent: EventEmitter<ExperimentRequest> = new EventEmitter();

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
}
