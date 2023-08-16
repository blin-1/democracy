import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { PositionFormService } from './position-form.service';
import { PositionService } from '../service/position.service';
import { IPosition } from '../position.model';
import { ICandidate } from 'app/entities/candidate/candidate.model';
import { CandidateService } from 'app/entities/candidate/service/candidate.service';
import { IIssue } from 'app/entities/issue/issue.model';
import { IssueService } from 'app/entities/issue/service/issue.service';

import { PositionUpdateComponent } from './position-update.component';

describe('Position Management Update Component', () => {
  let comp: PositionUpdateComponent;
  let fixture: ComponentFixture<PositionUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let positionFormService: PositionFormService;
  let positionService: PositionService;
  let candidateService: CandidateService;
  let issueService: IssueService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), PositionUpdateComponent],
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
      .overrideTemplate(PositionUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(PositionUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    positionFormService = TestBed.inject(PositionFormService);
    positionService = TestBed.inject(PositionService);
    candidateService = TestBed.inject(CandidateService);
    issueService = TestBed.inject(IssueService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Candidate query and add missing value', () => {
      const position: IPosition = { id: 456 };
      const candidate: ICandidate = { id: 24166 };
      position.candidate = candidate;

      const candidateCollection: ICandidate[] = [{ id: 31104 }];
      jest.spyOn(candidateService, 'query').mockReturnValue(of(new HttpResponse({ body: candidateCollection })));
      const additionalCandidates = [candidate];
      const expectedCollection: ICandidate[] = [...additionalCandidates, ...candidateCollection];
      jest.spyOn(candidateService, 'addCandidateToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ position });
      comp.ngOnInit();

      expect(candidateService.query).toHaveBeenCalled();
      expect(candidateService.addCandidateToCollectionIfMissing).toHaveBeenCalledWith(
        candidateCollection,
        ...additionalCandidates.map(expect.objectContaining)
      );
      expect(comp.candidatesSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Issue query and add missing value', () => {
      const position: IPosition = { id: 456 };
      const issue: IIssue = { id: 31617 };
      position.issue = issue;

      const issueCollection: IIssue[] = [{ id: 23588 }];
      jest.spyOn(issueService, 'query').mockReturnValue(of(new HttpResponse({ body: issueCollection })));
      const additionalIssues = [issue];
      const expectedCollection: IIssue[] = [...additionalIssues, ...issueCollection];
      jest.spyOn(issueService, 'addIssueToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ position });
      comp.ngOnInit();

      expect(issueService.query).toHaveBeenCalled();
      expect(issueService.addIssueToCollectionIfMissing).toHaveBeenCalledWith(
        issueCollection,
        ...additionalIssues.map(expect.objectContaining)
      );
      expect(comp.issuesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const position: IPosition = { id: 456 };
      const candidate: ICandidate = { id: 18399 };
      position.candidate = candidate;
      const issue: IIssue = { id: 14335 };
      position.issue = issue;

      activatedRoute.data = of({ position });
      comp.ngOnInit();

      expect(comp.candidatesSharedCollection).toContain(candidate);
      expect(comp.issuesSharedCollection).toContain(issue);
      expect(comp.position).toEqual(position);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPosition>>();
      const position = { id: 123 };
      jest.spyOn(positionFormService, 'getPosition').mockReturnValue(position);
      jest.spyOn(positionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ position });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: position }));
      saveSubject.complete();

      // THEN
      expect(positionFormService.getPosition).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(positionService.update).toHaveBeenCalledWith(expect.objectContaining(position));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPosition>>();
      const position = { id: 123 };
      jest.spyOn(positionFormService, 'getPosition').mockReturnValue({ id: null });
      jest.spyOn(positionService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ position: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: position }));
      saveSubject.complete();

      // THEN
      expect(positionFormService.getPosition).toHaveBeenCalled();
      expect(positionService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IPosition>>();
      const position = { id: 123 };
      jest.spyOn(positionService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ position });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(positionService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareCandidate', () => {
      it('Should forward to candidateService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(candidateService, 'compareCandidate');
        comp.compareCandidate(entity, entity2);
        expect(candidateService.compareCandidate).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareIssue', () => {
      it('Should forward to issueService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(issueService, 'compareIssue');
        comp.compareIssue(entity, entity2);
        expect(issueService.compareIssue).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
