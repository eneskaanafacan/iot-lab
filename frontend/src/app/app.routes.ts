import { Routes } from '@angular/router';
import { LoginComponent } from './features/login/login.component';
import { DashboardComponent } from './features/dashboard/dashboard.component';
import { HistoryComponent } from './features/history/history.component';
import { NodeDetailComponent } from './features/node-detail/node-detail.component';
import { AuthGuard } from './core/guards/auth.guard';
import { SystemSelectComponent } from './features/system-select/system-select.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { 
    path: 'select', 
    component: SystemSelectComponent, 
    canActivate: [AuthGuard] 
  },
  { 
    path: 'dashboard/:systemType', 
    component: DashboardComponent, 
    canActivate: [AuthGuard] 
  },
  { 
    path: 'node/:systemType/:id', 
    component: NodeDetailComponent, 
    canActivate: [AuthGuard] 
  },
  { 
    path: 'history/:systemType', 
    component: HistoryComponent, 
    canActivate: [AuthGuard] 
  },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: '**', redirectTo: '/login' }
];
