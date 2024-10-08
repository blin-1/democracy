import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { OfficeFormService, OfficeFormGroup } from './office-form.service';
import { IOffice } from '../office.model';
import { OfficeService } from '../service/office.service';
import { State } from 'app/entities/enumerations/state.model';
import { YesNo } from 'app/entities/enumerations/yes-no.model';

@Component({
  standalone: true,
  selector: 'jhi-office-update',
  templateUrl: './office-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class OfficeUpdateComponent implements OnInit {
  isSaving = false;
  office: IOffice | null = null;
  stateValues = Object.keys(State);
  yesNoValues = Object.keys(YesNo);

  editForm: OfficeFormGroup = this.officeFormService.createOfficeFormGroup();

  constructor(
    protected officeService: OfficeService,
    protected officeFormService: OfficeFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ office }) => {
      this.office = office;
      if (office) {
        this.updateForm(office);
      }
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const office = this.officeFormService.getOffice(this.editForm);
    if (office.id !== null) {
      this.subscribeToSaveResponse(this.officeService.update(office));
    } else {
      this.subscribeToSaveResponse(this.officeService.create(office));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IOffice>>): void {
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

  protected updateForm(office: IOffice): void {
    this.office = office;
    this.officeFormService.resetForm(this.editForm, office);
  }
}
