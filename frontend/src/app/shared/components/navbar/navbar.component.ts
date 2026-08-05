import { Component, inject, Input } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterLink, RouterLinkActive],
  template: `
    <nav class="bg-gray-800 border-b border-gray-700 shadow-lg">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex items-center justify-between h-16">
          <div class="flex items-center">
            <div class="flex-shrink-0 text-teal-400 font-bold text-xl tracking-wider">
              IoT Dashboard
            </div>
            <div class="hidden md:block">
              <div class="ml-10 flex items-baseline space-x-4">
                <a [routerLink]="systemType ? ['/dashboard', systemType] : ['/select']" routerLinkActive="bg-gray-900 text-white" 
                   class="text-gray-300 hover:bg-gray-700 hover:text-white px-3 py-2 rounded-md text-sm font-medium transition-colors cursor-pointer">
                  Dashboard
                </a>
                <a [routerLink]="systemType ? ['/history', systemType] : ['/select']" routerLinkActive="bg-gray-900 text-white" 
                   class="text-gray-300 hover:bg-gray-700 hover:text-white px-3 py-2 rounded-md text-sm font-medium transition-colors cursor-pointer">
                  Geçmiş Veriler
                </a>
              </div>
            </div>
          </div>
          <div class="hidden md:block">
            <button routerLink="/select" 
                    class="mr-4 border border-teal-500 text-teal-400 hover:bg-teal-500/10 font-medium py-2 px-4 rounded-md transition-colors shadow-sm">
              Sistem Değiştir
            </button>
            <button (click)="logout()" 
                    class="bg-red-600 hover:bg-red-700 text-white font-medium py-2 px-4 rounded-md transition-colors shadow-md">
              Çıkış Yap
            </button>
          </div>
        </div>
      </div>
    </nav>
  `
})
export class NavbarComponent {
  
  @Input() systemType: string = '';

  private authService = inject(AuthService);
  private router = inject(Router);

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
