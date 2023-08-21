import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { IssueFormService, IssueFormGroup } from './issue-form.service';
import { IIssue } from '../issue.model';
import { IssueService } from '../service/issue.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';

@Component({
  standalone: true,
  selector: 'jhi-issue-update',
  templateUrl: './issue-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class IssueUpdateComponent implements OnInit {
  isSaving = false;
  issue: IIssue | null = null;

  editForm: IssueFormGroup = this.issueFormService.createIssueFormGroup();

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected issueService: IssueService,
    protected issueFormService: IssueFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ issue }) => {
      this.issue = issue;
      if (issue) {
        this.updateForm(issue);
      }
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('democracyApp.error', { message: err.message })),
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const issue = this.issueFormService.getIssue(this.editForm);
    if (issue.id !== null) {
      this.subscribeToSaveResponse(this.issueService.update(issue));
    } else {
      this.subscribeToSaveResponse(this.issueService.create(issue));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IIssue>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(issue: IIssue): void {
    this.issue = issue;
    this.issueFormService.resetForm(this.editForm, issue);
  }
}
