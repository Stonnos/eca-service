import { Component, OnInit } from '@angular/core';
import { ExperimentTabUtils } from "../model/experiment-tab.utils";

@Component({
  selector: 'app-experiment-tabs',
  templateUrl: './experiment-tabs.component.html',
  styleUrls: ['./experiment-tabs.component.scss']
})
export class ExperimentTabsComponent implements OnInit {

  public activeTabIndex: number = 0;

  public ngOnInit() {
    this.updateActiveIndex();
  }

  public onChangeTab(event): void {
    localStorage.setItem(ExperimentTabUtils.EXPERIMENT_ACTIVE_TAB_KEY, event.index);
  }

  public updateActiveIndex(): void {
    const tabIndex = localStorage.getItem(ExperimentTabUtils.EXPERIMENT_ACTIVE_TAB_KEY);
    if (tabIndex) {
      this.activeTabIndex = parseInt(tabIndex);
    }
  }
}
