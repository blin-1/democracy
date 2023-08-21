import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { PositionFormService, PositionFormGroup } from './position-form.service';
import { IPosition } from '../position.model';
import { PositionService } from '../service/position.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IIssue } from 'app/entities/issue/issue.model';
import { IssueService } from 'app/entities/issue/service/issue.service';
import { ICandidate } from 'app/entities/candidate/candidate.model';
import { CandidateService } from 'app/entities/candidate/service/candidate.service';

@Component({
  standalone: true,
  selector: 'jhi-position-update',
  templateUrl: './position-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class PositionUpdateComponent implements OnInit {
  isSaving = false;
  position: IPosition | null = null;

  issuesSharedCollection: IIssue[] = [];
  candidatesSharedCollection: ICandidate[] = [];

  editForm: PositionFormGroup = this.positionFormService.createPositionFormGroup();

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected positionService: PositionService,
    protected positionFormService: PositionFormService,
    protected issueService: IssueService,
    protected candidateService: CandidateService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareIssue = (o1: IIssue | null, o2: IIssue | null): boolean => this.issueService.compareIssue(o1, o2);

  compareCandidate = (o1: ICandidate | null, o2: ICandidate | null): boolean => this.candidateService.compareCandidate(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ position }) => {
      this.position = position;
      if (position) {
        this.updateForm(position);
      }

      this.loadRelationshipsOptions();
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
    const position = this.positionFormService.getPosition(this.editForm);
    if (position.id !== null) {
      this.subscribeToSaveResponse(this.positionService.update(position));
    } else {
      this.subscribeToSaveResponse(this.positionService.create(position));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IPosition>>): void {
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

  protected updateForm(position: IPosition): void {
    this.position = position;
    this.positionFormService.resetForm(this.editForm, position);

    this.issuesSharedCollection = this.issueService.addIssueToCollectionIfMissing<IIssue>(this.issuesSharedCollection, position.issue);
    this.candidatesSharedCollection = this.candidateService.addCandidateToCollectionIfMissing<ICandidate>(
      this.candidatesSharedCollection,
      position.candidate
    );
  }

  protected loadRelationshipsOptions(): void {
    this.issueService
      .query()
      .pipe(map((res: HttpResponse<IIssue[]>) => res.body ?? []))
      .pipe(map((issues: IIssue[]) => this.issueService.addIssueToCollectionIfMissing<IIssue>(issues, this.position?.issue)))
      .subscribe((issues: IIssue[]) => (this.issuesSharedCollection = issues));

    this.candidateService
      .query()
      .pipe(map((res: HttpResponse<ICandidate[]>) => res.body ?? []))
      .pipe(
        map((candidates: ICandidate[]) =>
          this.candidateService.addCandidateToCollectionIfMissing<ICandidate>(candidates, this.position?.candidate)
        )
      )
      .subscribe((candidates: ICandidate[]) => (this.candidatesSharedCollection = candidates));
  }
}
