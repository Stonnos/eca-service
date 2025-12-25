import { Component, OnInit } from '@angular/core';
import {
  RoutePathDto
} from "../../../../../../../target/generated-sources/typescript/eca-web-dto";
import { EvaluationResultsHistoryService } from '../../evaluation-results-history/services/evaluation-results-history.service';
import { ActivatedRoute, Router } from '@angular/router';
import { MessageService } from 'primeng/api';

@Component({
  selector: 'app-evaluation-results-request-path',
  templateUrl: './evaluation-results-request-path.component.html',
  styleUrls: ['./evaluation-results-request-path.component.scss']
})
export class EvaluationResultsRequestPathComponent implements OnInit {

  private readonly id: string;

  public constructor(private evaluationResultsHistoryService: EvaluationResultsHistoryService,
                     private route: ActivatedRoute,
                     private router: Router,
                     private messageService: MessageService,) {
    this.id = this.route.snapshot.params.id;
  }

  public ngOnInit(): void {
    this.handleRequestPath();
  }

  public handleRequestPath(): void {
    this.evaluationResultsHistoryService.getEvaluationResultsRequestPath(this.id)
      .subscribe({
        next: (routePathDto: RoutePathDto) => {
          this.router.navigate([routePathDto.path]);
        },
        error: (error) => {
          this.messageService.add({ severity: 'error', summary: 'Ошибка', detail: error.message });
        }
      });
  }
}
