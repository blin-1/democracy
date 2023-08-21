import { Component, OnInit, ElementRef } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { CandidateFormService, CandidateFormGroup } from './candidate-form.service';
import { ICandidate } from '../candidate.model';
import { CandidateService } from '../service/candidate.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';
import { IOffice } from 'app/entities/office/office.model';
import { OfficeService } from 'app/entities/office/service/office.service';
import { Party } from 'app/entities/enumerations/party.model';

@Component({
  standalone: true,
  selector: 'jhi-candidate-update',
  templateUrl: './candidate-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class CandidateUpdateComponent implements OnInit {
  isSaving = false;
  candidate: ICandidate | null = null;
  partyValues = Object.keys(Party);

  officesSharedCollection: IOffice[] = [];

  editForm: CandidateFormGroup = this.candidateFormService.createCandidateFormGroup();

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected candidateService: CandidateService,
    protected candidateFormService: CandidateFormService,
    protected officeService: OfficeService,
    protected elementRef: ElementRef,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareOffice = (o1: IOffice | null, o2: IOffice | null): boolean => this.officeService.compareOffice(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ candidate }) => {
      this.candidate = candidate;
      if (candidate) {
        this.updateForm(candidate);
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

  clearInputImage(field: string, fieldContentType: string, idInput: string): void {
    this.editForm.patchValue({
      [field]: null,
      [fieldContentType]: null,
    });
    if (idInput && this.elementRef.nativeElement.querySelector('#' + idInput)) {
      this.elementRef.nativeElement.querySelector('#' + idInput).value = null;
    }
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const candidate = this.candidateFormService.getCandidate(this.editForm);
    if (candidate.id !== null) {
      this.subscribeToSaveResponse(this.candidateService.update(candidate));
    } else {
      this.subscribeToSaveResponse(this.candidateService.create(candidate));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<ICandidate>>): void {
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

  protected updateForm(candidate: ICandidate): void {
    this.candidate = candidate;
    this.candidateFormService.resetForm(this.editForm, candidate);

    this.officesSharedCollection = this.officeService.addOfficeToCollectionIfMissing<IOffice>(
      this.officesSharedCollection,
      candidate.office
    );
  }

  protected loadRelationshipsOptions(): void {
    this.officeService
      .query()
      .pipe(map((res: HttpResponse<IOffice[]>) => res.body ?? []))
      .pipe(map((offices: IOffice[]) => this.officeService.addOfficeToCollectionIfMissing<IOffice>(offices, this.candidate?.office)))
      .subscribe((offices: IOffice[]) => (this.officesSharedCollection = offices));
  }
}
