import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'candidate',
        data: { pageTitle: 'democracyApp.candidate.home.title' },
        loadChildren: () => import('./candidate/candidate.routes'),
      },
      {
        path: 'address',
        data: { pageTitle: 'democracyApp.address.home.title' },
        loadChildren: () => import('./address/address.routes'),
      },
      {
        path: 'issue',
        data: { pageTitle: 'democracyApp.issue.home.title' },
        loadChildren: () => import('./issue/issue.routes'),
      },
      {
        path: 'position',
        data: { pageTitle: 'democracyApp.position.home.title' },
        loadChildren: () => import('./position/position.routes'),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
