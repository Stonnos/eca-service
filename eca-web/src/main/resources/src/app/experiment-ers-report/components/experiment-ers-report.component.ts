import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {
  ExperimentErsReportDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";

@Component({
  selector: 'app-experiment-ers-report',
  templateUrl: './experiment-ers-report.component.html',
  styleUrls: ['./experiment-ers-report.component.scss']
})
export class ExperimentErsReportComponent implements OnInit {

  @Input()
  public visible: boolean = false;

  @Input()
  public experimentErsReport: ExperimentErsReportDto;

  @Output()
  public visibilityChange: EventEmitter<boolean> = new EventEmitter();

  @Output()
  public sent: EventEmitter<string> = new EventEmitter();

  public ngOnInit(): void {
  }

  public canSent(): boolean {
    return this.experimentErsReport && this.experimentErsReport.ersReportStatus.value == "NEED_SENT";
  }

  public hide(): void {
    this.visibilityChange.emit(false);
  }

  public sentEvaluationResults(): void {
    this.sent.emit(this.experimentErsReport.experimentUuid);
    this.hide();
  }
}
