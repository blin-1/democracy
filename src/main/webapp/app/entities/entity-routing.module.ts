import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'candidate',
        data: { pageTitle: 'Candidates' },
        loadChildren: () => import('./candidate/candidate.routes'),
      },
      {
        path: 'office',
        data: { pageTitle: 'Offices' },
        loadChildren: () => import('./office/office.routes'),
      },
      {
        path: 'issue',
        data: { pageTitle: 'Issues' },
        loadChildren: () => import('./issue/issue.routes'),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
