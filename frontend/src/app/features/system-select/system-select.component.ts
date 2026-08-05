import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-system-select',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="min-h-screen bg-gray-900 flex flex-col items-center py-16 px-4">
      <div class="text-center mb-12">
        <h1 class="text-4xl font-extrabold text-white tracking-tight mb-2">IoT Seçim Portalı</h1>
        <p class="text-gray-400 text-lg">İzlemek istediğiniz sistemi seçiniz</p>
      </div>

      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8 max-w-7xl w-full">
        <div (click)="selectSystem('vineyard')" class="bg-gray-800 rounded-2xl shadow-xl border border-gray-700 hover:border-green-500 overflow-hidden cursor-pointer transition-all hover:scale-105 group p-8 flex flex-col items-center">
          <div class="h-24 w-24 bg-green-500/10 rounded-full flex items-center justify-center mb-6 group-hover:bg-green-500/20 transition-colors">
            <span class="text-5xl text-green-400">🌱</span>
          </div>
          <h2 class="text-2xl font-bold text-white mb-2 flex items-center gap-2">Tarım & Bağ</h2>
          <p class="text-center text-gray-400 text-sm">Toprak sıcaklığı, nem, ışık ve hava sıcaklığı ölçümleri. Akıllı tarım izleme.</p>
        </div>

        <div (click)="selectSystem('wind')" class="bg-gray-800 rounded-2xl shadow-xl border border-gray-700 hover:border-blue-500 overflow-hidden cursor-pointer transition-all hover:scale-105 group p-8 flex flex-col items-center">
          <div class="h-24 w-24 bg-blue-500/10 rounded-full flex items-center justify-center mb-6 group-hover:bg-blue-500/20 transition-colors">
            <span class="text-5xl text-blue-400">💨</span>
          </div>
          <h2 class="text-2xl font-bold text-white mb-2">Rüzgar & Liman</h2>
          <p class="text-center text-gray-400 text-sm">Rüzgar hızı, basınç, yön ve hava sıcaklığı ölçümleri. Liman ve santral izleme.</p>
        </div>

        <div (click)="selectSystem('flood')" class="bg-gray-800 rounded-2xl shadow-xl border border-gray-700 hover:border-cyan-500 overflow-hidden cursor-pointer transition-all hover:scale-105 group p-8 flex flex-col items-center">
          <div class="h-24 w-24 bg-cyan-500/10 rounded-full flex items-center justify-center mb-6 group-hover:bg-cyan-500/20 transition-colors">
            <span class="text-5xl text-cyan-400">🌊</span>
          </div>
          <h2 class="text-2xl font-bold text-white mb-2">Akarsu & Sel</h2>
          <p class="text-center text-gray-400 text-sm">Su seviyesi, debi, yağış miktarı ve risk skoru analizleri. Erken uyarı sistemleri.</p>
        </div>

        <div (click)="selectSystem('live-data')" class="bg-gray-800 rounded-2xl shadow-xl border border-gray-700 hover:border-rose-500 overflow-hidden cursor-pointer transition-all hover:scale-105 group p-8 flex flex-col items-center">
          <div class="h-24 w-24 bg-rose-500/10 rounded-full flex items-center justify-center mb-6 group-hover:bg-rose-500/20 transition-colors">
            <span class="text-5xl text-rose-400">📡</span>
          </div>
          <h2 class="text-2xl font-bold text-white mb-2">Raspberry Pi (Canlı)</h2>
          <p class="text-center text-gray-400 text-sm">Gerçek zamanlı Raspberry Pi sunucusundan gelen canlı sensör verileri. Çip sıcaklığı, ortam sıcaklığı, nem, ışık ve pil durumu izleme.</p>
        </div>
      </div>
    </div>
  `
})
export class SystemSelectComponent {
  private router = inject(Router);

  selectSystem(type: string): void {
    this.router.navigate(['/dashboard', type]);
  }
}
