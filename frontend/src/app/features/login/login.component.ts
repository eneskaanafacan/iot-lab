import { Component, inject } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [ReactiveFormsModule, CommonModule],
  template: `
    <div class="min-h-screen flex items-center justify-center bg-gray-900 px-4 sm:px-6 lg:px-8">
      <div class="max-w-md w-full space-y-8 bg-gray-800 p-10 rounded-xl shadow-2xl border border-gray-700">
        
        <div class="text-center">
          <h2 class="mt-6 text-3xl font-extrabold text-white tracking-tight">IoT Dashboard</h2>
          <p class="mt-2 text-sm text-gray-400">
            Sisteme giriş yapmak için bilgilerinizi giriniz.
          </p>
        </div>
        
        <form class="mt-8 space-y-6" [formGroup]="loginForm" (ngSubmit)="onSubmit()">
          
          <div class="rounded-md shadow-sm space-y-4">
            <div>
              <label for="username" class="sr-only">Kullanıcı Adı</label>
              <input id="username" type="text" formControlName="username" required
                class="appearance-none rounded-lg relative block w-full px-4 py-3 border border-gray-600 bg-gray-700 placeholder-gray-400 text-white focus:outline-none focus:ring-teal-500 focus:border-teal-500 focus:z-10 sm:text-sm transition-colors"
                placeholder="Kullanıcı Adı">
            </div>
            
            <div>
              <label for="password" class="sr-only">Şifre</label>
              <input id="password" type="password" formControlName="password" required
                class="appearance-none rounded-lg relative block w-full px-4 py-3 border border-gray-600 bg-gray-700 placeholder-gray-400 text-white focus:outline-none focus:ring-teal-500 focus:border-teal-500 focus:z-10 sm:text-sm transition-colors"
                placeholder="Şifre">
            </div>
          </div>

          <div *ngIf="errorMessage" class="text-red-400 text-sm text-center">
            {{ errorMessage }}
          </div>

          <div>
            <button type="submit" [disabled]="loginForm.invalid || isLoading"
              class="group relative w-full flex justify-center py-3 px-4 border border-transparent text-sm font-medium rounded-lg text-white bg-teal-600 hover:bg-teal-700 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-teal-500 focus:ring-offset-gray-900 disabled:opacity-50 transition-colors shadow-lg shadow-teal-500/30">
              
              <span *ngIf="isLoading" class="absolute left-0 inset-y-0 flex items-center pl-3">
                <svg class="animate-spin h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                  <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                  <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                </svg>
              </span>

              {{ isLoading ? 'Giriş Yapılıyor...' : 'Giriş Yap' }}
            </button>
          </div>
          
        </form>
      </div>
    </div>
  `
})
export class LoginComponent {
  
  private authService = inject(AuthService);
  private router = inject(Router);
  private fb = inject(FormBuilder);

  loginForm: FormGroup = this.fb.group({
    username: ['', Validators.required],
    password: ['', Validators.required]
  });

  isLoading = false;
  errorMessage = '';

  onSubmit(): void {
    if (this.loginForm.valid) {
      this.isLoading = true;
      this.errorMessage = '';
      
      const { username, password } = this.loginForm.value;

      this.authService.login(username, password).subscribe({
        next: () => {
          this.router.navigate(['/select']);
        },
        error: (err) => {
          this.isLoading = false;
          this.errorMessage = 'Giriş başarısız. Lütfen bilgilerinizi kontrol ediniz.';
        }
      });
    }
  }
}
