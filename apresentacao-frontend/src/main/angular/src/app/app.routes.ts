import { Routes } from '@angular/router';

import { Reservas } from './reservas/reservas';

export const routes: Routes = [
    { path: 'reservas', component: Reservas },
    { path: 'reservas/:usuarioId', component: Reservas },
    { path: '', redirectTo: 'reservas', pathMatch: 'full' },
];
