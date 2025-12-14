import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RegisterComponent } from '../../page1/register/register.component';
import { ConfirmComponent } from '../../page2/confirm/confirm.component';
import { LoginComponent } from '../../page3/login/login.component';
import { ClientComponent } from '../../page4/client/client.component';
import { authGuard } from '../../service/guards/auth.guard';
import { ViewUsersComponent } from '../../page5/view-users/view-users.component';
import { ViewDocumentsComponent } from '../../page5/view-documents/view-documents.component';
import { ViewReportsComponent } from '../../page5/view-reports/view-reports.component';
import { ConfigurationComponent } from '../../page4/configuration/configuration.component';
import { ViewCreditDocumentsComponent } from '../../page5/view-credit-documents/view-credit-documents.component';
import { PaymentComponent } from '../../page4/payment/payment.component';

const routes: Routes = [
  {path:'', component: RegisterComponent},
  {path:'confirm-email', component: ConfirmComponent},
  {path:'login', component: LoginComponent},
  {path:'client', component: ClientComponent},
  {path:'payment', component: PaymentComponent, canActivate: [authGuard]},
  {path:'configuration', component: ConfigurationComponent, canActivate: [authGuard]},
  {path:'adm-works-view-documents', component: ViewDocumentsComponent, canActivate: [authGuard]},
  {path:'adm-works-view-credit-documents', component: ViewCreditDocumentsComponent, canActivate: [authGuard]},
  {path:'adm-works-view-users', component: ViewUsersComponent, canActivate: [authGuard]},
  {path:'adm-works-view-reports', component: ViewReportsComponent, canActivate: [authGuard]},

  {path:'**', component: RegisterComponent}
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class RouterMRoutingModule { }
