import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { OfficeDetailComponent } from './office-detail.component';

describe('Office Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OfficeDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: OfficeDetailComponent,
              resolve: { office: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding()
        ),
      ],
    })
      .overrideTemplate(OfficeDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load office on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', OfficeDetailComponent);

      // THEN
      expect(instance.office).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
