import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-info-panel',
  templateUrl: './info-panel.component.html',
  styleUrls: ['./info-panel.component.scss']
})
export class InfoPanelComponent implements OnInit {

  @Input()
  public loading: boolean = false;

  @Input()
  public header: string;

  @Input()
  public loadingMessage: string = 'Пожалуйста, подождите...';

  @Input()
  public message: string;

  @Input()
  public buttonLink: string = '/login';

  public ngOnInit() {
  }
}
