import { Routes } from '@angular/router';
import { LoginPageComponente } from '../pages/login-page.componente/login-page.componente';
import { LoginComponente } from '../componentes/login.componente/login.componente';
import { PrincipalPageComponent } from '../pages/principal-page.component/principal-page.component';
import { PrincipalComponent } from '../componentes/principal.component/principal.component';

export const routes: Routes = [
  
    {

    path:'',
    component: PrincipalPageComponent, 
    children:[
        {
            path:'principal',
            component:PrincipalComponent,
        },

        {   path:'', redirectTo:'principal', pathMatch:'full'

        }
    ]

},

    {

    path:'login',
    component: LoginPageComponente, 
    children:[
        {
            path:'login-form', 
            component:LoginComponente
        }
    ]
}
]
