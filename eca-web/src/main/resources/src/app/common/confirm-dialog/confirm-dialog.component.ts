import { Component, Input, OnInit, EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-confirm-dialog',
  templateUrl: './confirm-dialog.component.html',
  styleUrls: ['./confirm-dialog.component.scss']
})
export class ConfirmDialogComponent implements OnInit {

  @Input()
  public visible: boolean = true;
  @Input()
  public header: string;
  @Input()
  public dialogWidth: string = '600px';
  @Input()
  public closable: boolean = true;

  @Output()
  public closedEvent: EventEmitter<void> = new EventEmitter<void>();

  public ngOnInit() {
  }

  public close(): void {
    this.closedEvent.emit();
  }
}
