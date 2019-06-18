import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { ErsReportDto } from "../../../../../../../target/generated-sources/typescript/eca-web-dto";

@Component({
  selector: 'app-ers-report',
  templateUrl: './ers-report.component.html',
  styleUrls: ['./ers-report.component.scss']
})
export class ErsReportComponent implements OnInit {

  @Input()
  public visible: boolean = false;

  @Input()
  public ersReport: ErsReportDto;

  @Output()
  public visibilityChange: EventEmitter<boolean> = new EventEmitter();

  @Output()
  public sent: EventEmitter<string> = new EventEmitter();

  public ngOnInit(): void {
  }

  public canSent(): boolean {
    return this.ersReport && this.ersReport.ersReportStatus.value == "NEED_SENT";
  }

  public hide(): void {
    this.visibilityChange.emit(false);
  }

  public sentEvaluationResults(): void {
    this.sent.emit(this.ersReport.experimentUuid);
    this.hide();
  }
}
