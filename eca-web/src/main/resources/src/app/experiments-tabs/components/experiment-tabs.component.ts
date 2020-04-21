import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-experiment-tabs',
  templateUrl: './experiment-tabs.component.html',
  styleUrls: ['./experiment-tabs.component.scss']
})
export class ExperimentTabsComponent implements OnInit {

  private static readonly EXPERIMENT_ACTIVE_TAB_KEY: string = 'experimentTabActiveIndex';

  public activeTabIndex: number = 0;

  public ngOnInit() {
    this.updateActiveIndex();
  }

  public onChangeTab(event): void {
    localStorage.setItem(ExperimentTabsComponent.EXPERIMENT_ACTIVE_TAB_KEY, event.index);
  }

  public updateActiveIndex(): void {
    const tabIndex = localStorage.getItem(ExperimentTabsComponent.EXPERIMENT_ACTIVE_TAB_KEY);
    if (tabIndex) {
      this.activeTabIndex = parseInt(tabIndex);
    }
  }
}
