import { Routes } from '@angular/router';


export const routes: Routes = [
  { path: '', redirectTo: 'my-bank.com.br', pathMatch: 'full' },
  { path: 'my-bank.com.br',
    loadChildren:() => import('./router/router-m/router-m-routing.module').then(m => m.RouterMRoutingModule)
  }
];
