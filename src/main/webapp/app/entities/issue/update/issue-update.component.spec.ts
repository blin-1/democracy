import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { IssueFormService } from './issue-form.service';
import { IssueService } from '../service/issue.service';
import { IIssue } from '../issue.model';

import { IssueUpdateComponent } from './issue-update.component';

describe('Issue Management Update Component', () => {
  let comp: IssueUpdateComponent;
  let fixture: ComponentFixture<IssueUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let issueFormService: IssueFormService;
  let issueService: IssueService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), IssueUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(IssueUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(IssueUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    issueFormService = TestBed.inject(IssueFormService);
    issueService = TestBed.inject(IssueService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const issue: IIssue = { id: 456 };

      activatedRoute.data = of({ issue });
      comp.ngOnInit();

      expect(comp.issue).toEqual(issue);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IIssue>>();
      const issue = { id: 123 };
      jest.spyOn(issueFormService, 'getIssue').mockReturnValue(issue);
      jest.spyOn(issueService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ issue });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: issue }));
      saveSubject.complete();

      // THEN
      expect(issueFormService.getIssue).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(issueService.update).toHaveBeenCalledWith(expect.objectContaining(issue));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IIssue>>();
      const issue = { id: 123 };
      jest.spyOn(issueFormService, 'getIssue').mockReturnValue({ id: null });
      jest.spyOn(issueService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ issue: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: issue }));
      saveSubject.complete();

      // THEN
      expect(issueFormService.getIssue).toHaveBeenCalled();
      expect(issueService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IIssue>>();
      const issue = { id: 123 };
      jest.spyOn(issueService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ issue });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(issueService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
