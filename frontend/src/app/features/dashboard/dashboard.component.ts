import { Component, OnInit, inject } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';
import { DashboardData, IotService } from '../../core/services/iot.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, NavbarComponent, DecimalPipe],
  template: `
    <app-navbar [systemType]="systemType"></app-navbar>
    
    <div class="min-h-screen bg-gray-900 pt-10 pb-12 px-4 sm:px-6 lg:px-8">
      <div class="max-w-7xl mx-auto">
        <h1 class="text-3xl font-bold text-white mb-2">
          @if (systemType === 'wind') { Rüzgar & Liman Sistemi }
          @else if (systemType === 'flood') { Akarsu & Sel Sistemi }
          @else if (systemType === 'live-data') { Raspberry Pi Canlı Sistemi }
          @else { Tarım & Bağ Sistemi }
        </h1>
        <p class="text-gray-400 mb-8">Son 3 saatin sensör ortalamaları ve anlık akış</p>

        <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-10" *ngIf="dashboardData">
          
          @switch (systemType) {
            @case ('vineyard') {
              <div class="bg-gray-800 rounded-xl shadow border border-gray-700 p-6 flex items-center justify-between">
                <div><p class="text-sm font-medium text-gray-400">Ortalama Sıcaklık</p><p class="text-3xl font-extrabold text-white mt-1">{{ dashboardData.avgTemperature | number:'1.1-1' }} °C</p></div>
                <div class="p-3 bg-red-500/10 rounded-full"><span class="text-red-400 text-2xl">🌡️</span></div>
              </div>
              <div class="bg-gray-800 rounded-xl shadow border border-gray-700 p-6 flex items-center justify-between">
                <div><p class="text-sm font-medium text-gray-400">Ortalama Nem</p><p class="text-3xl font-extrabold text-white mt-1">{{ dashboardData.avgHumidity | number:'1.1-1' }} %</p></div>
                <div class="p-3 bg-blue-500/10 rounded-full"><span class="text-blue-400 text-2xl">💧</span></div>
              </div>
              <div class="bg-gray-800 rounded-xl shadow border border-gray-700 p-6 flex items-center justify-between">
                <div><p class="text-sm font-medium text-gray-400">Ortalama Işık</p><p class="text-3xl font-extrabold text-white mt-1">{{ dashboardData.avgLight | number:'1.1-1' }} lx</p></div>
                <div class="p-3 bg-yellow-500/10 rounded-full"><span class="text-yellow-400 text-2xl">☀️</span></div>
              </div>
              <div class="bg-gray-800 rounded-xl shadow border border-gray-700 p-6 flex items-center justify-between">
                <div><p class="text-sm font-medium text-gray-400">Ortalama Toprak Sıc.</p><p class="text-3xl font-extrabold text-white mt-1">{{ dashboardData.avgSoilTemperature | number:'1.1-1' }} °C</p></div>
                <div class="p-3 bg-green-500/10 rounded-full"><span class="text-green-400 text-2xl">🌱</span></div>
              </div>
            }
            @case ('wind') {
              <div class="bg-gray-800 rounded-xl shadow border border-gray-700 p-6 flex items-center justify-between">
                <div><p class="text-sm font-medium text-gray-400">Rüzgar Hızı (Ort)</p><p class="text-3xl font-extrabold text-white mt-1">{{ dashboardData.avgWindSpeed | number:'1.1-1' }} m/s</p></div>
                <div class="p-3 bg-blue-500/10 rounded-full"><span class="text-blue-400 text-2xl">💨</span></div>
              </div>
              <div class="bg-gray-800 rounded-xl shadow border border-gray-700 p-6 flex items-center justify-between">
                <div><p class="text-sm font-medium text-gray-400">Basınç (Ort)</p><p class="text-3xl font-extrabold text-white mt-1">{{ dashboardData.avgPressure | number:'1.1-1' }} hPa</p></div>
                <div class="p-3 bg-indigo-500/10 rounded-full"><span class="text-indigo-400 text-2xl">🧭</span></div>
              </div>
              <div class="bg-gray-800 rounded-xl shadow border border-gray-700 p-6 flex items-center justify-between">
                <div><p class="text-sm font-medium text-gray-400">Hamle (Gust)</p><p class="text-3xl font-extrabold text-white mt-1">{{ dashboardData.avgWindGust | number:'1.1-1' }} m/s</p></div>
                <div class="p-3 bg-gray-500/10 rounded-full"><span class="text-gray-400 text-2xl">🌪️</span></div>
              </div>
            }
            @case ('flood') {
              <div class="bg-gray-800 rounded-xl shadow border border-gray-700 p-6 flex items-center justify-between">
                <div><p class="text-sm font-medium text-gray-400">Su Seviyesi (Ort)</p><p class="text-3xl font-extrabold text-white mt-1">{{ dashboardData.avgRiverHeight | number:'1.1-2' }} m</p></div>
                <div class="p-3 bg-cyan-500/10 rounded-full"><span class="text-cyan-400 text-2xl">🌊</span></div>
              </div>
              <div class="bg-gray-800 rounded-xl shadow border border-gray-700 p-6 flex items-center justify-between">
                <div><p class="text-sm font-medium text-gray-400">Risk Skoru (Ort)</p><p class="text-3xl font-extrabold text-white mt-1">{{ dashboardData.avgFloodRiskScore | number:'1.0-0' }} / 100</p></div>
                <div class="p-3 bg-orange-500/10 rounded-full"><span class="text-orange-400 text-2xl">⚠️</span></div>
              </div>
              <div class="bg-gray-800 rounded-xl shadow border border-gray-700 p-6 flex items-center justify-between">
                <div><p class="text-sm font-medium text-gray-400">Akış Hızı (Ort)</p><p class="text-3xl font-extrabold text-white mt-1">{{ dashboardData.avgFlowSpeed | number:'1.1-1' }} m/s</p></div>
                <div class="p-3 bg-blue-500/10 rounded-full"><span class="text-blue-400 text-2xl">⏩</span></div>
              </div>
            }
            @case ('live-data') {
              <div class="bg-gray-800 rounded-xl shadow border border-gray-700 p-6 flex items-center justify-between">
                <div><p class="text-sm font-medium text-gray-400">Ortalama Ortam Sıc.</p><p class="text-3xl font-extrabold text-white mt-1">{{ dashboardData.avgEnvTemp | number:'1.1-1' }} °C</p></div>
                <div class="p-3 bg-red-500/10 rounded-full"><span class="text-red-400 text-2xl">🌡️</span></div>
              </div>
              <div class="bg-gray-800 rounded-xl shadow border border-gray-700 p-6 flex items-center justify-between">
                <div><p class="text-sm font-medium text-gray-400">Ortalama Nem</p><p class="text-3xl font-extrabold text-white mt-1">{{ dashboardData.avgHumidity | number:'1.1-1' }} %</p></div>
                <div class="p-3 bg-blue-500/10 rounded-full"><span class="text-blue-400 text-2xl">💧</span></div>
              </div>
              <div class="bg-gray-800 rounded-xl shadow border border-gray-700 p-6 flex items-center justify-between">
                <div><p class="text-sm font-medium text-gray-400">Ortalama Işık</p><p class="text-3xl font-extrabold text-white mt-1">{{ dashboardData.avgLight | number:'1.1-1' }} lx</p></div>
                <div class="p-3 bg-yellow-500/10 rounded-full"><span class="text-yellow-400 text-2xl">☀️</span></div>
              </div>
              <div class="bg-gray-800 rounded-xl shadow border border-gray-700 p-6 flex items-center justify-between">
                <div><p class="text-sm font-medium text-gray-400">Ortalama Pil Seviyesi</p><p class="text-3xl font-extrabold text-white mt-1">{{ dashboardData.avgBattery | number:'1.0-0' }} mV</p></div>
                <div class="p-3 bg-green-500/10 rounded-full"><span class="text-green-400 text-2xl">🔋</span></div>
              </div>
            }
          }

        </div>

        <h2 class="text-xl font-bold text-white mb-4">Son Gelen Sensör Verileri</h2>
        <div class="bg-gray-800 rounded-xl shadow overflow-hidden border border-gray-700">
          <ul class="divide-y divide-gray-700">
            
            <li *ngFor="let item of (dashboardData?.dataList?.slice()?.reverse() | slice:0:10)" class="p-4 hover:bg-gray-750 flex items-center justify-between transition-colors">
              <div class="flex items-center space-x-4">
                <div class="flex-shrink-0 w-12 h-12 bg-gray-700 rounded-full flex items-center justify-center text-teal-400 font-bold hover:bg-gray-600 transition-colors cursor-pointer" [routerLink]="['/node', systemType, item.nodeId || item.node_id]">
                  {{ item.nodeId || item.node_id }}
                </div>
                <div>
                  <p class="text-sm font-medium text-white">Node / Cihaz <a [routerLink]="['/node', systemType, item.nodeId || item.node_id]" class="text-teal-400 hover:underline">#{{ item.nodeId || item.node_id }}</a></p>
                  <p class="text-xs text-gray-400">{{ item.timestamp | date:'dd/MM/yyyy HH:mm:ss' }}</p>
                </div>
              </div>
              <div class="flex space-x-6 text-sm">
                @switch (systemType) {
                  @case ('vineyard') {
                    <div class="text-gray-300"><span class="text-gray-500 mr-1">Sıcaklık:</span>{{ item.temperature }}°C</div>
                    <div class="text-gray-300"><span class="text-gray-500 mr-1">Nem:</span>{{ item.humidity }}%</div>
                  }
                  @case ('wind') {
                    <div class="text-gray-300"><span class="text-gray-500 mr-1">Hız:</span>{{ item.wind_speed_mps }} m/s</div>
                    <div class="text-gray-300"><span class="text-gray-500 mr-1">Basınç:</span>{{ item.pressure_hpa }} hPa</div>
                  }
                  @case ('flood') {
                    <div class="text-gray-300"><span class="text-gray-500 mr-1">Seviye:</span>{{ item.river_height_m }} m</div>
                    <div class="text-gray-300"><span class="text-gray-500 mr-1">Risk:</span>{{ item.flood_risk_score }}</div>
                  }
                  @case ('live-data') {
                    <div class="text-gray-300"><span class="text-gray-500 mr-1">Ortam Sıc:</span>{{ item.env_temp_c }}°C</div>
                    <div class="text-gray-300"><span class="text-gray-500 mr-1">Nem:</span>{{ item.humidity_rh }}%</div>
                    <div class="text-gray-300"><span class="text-gray-500 mr-1">Işık:</span>{{ item.light_lux }} lx</div>
                    <div class="text-gray-300"><span class="text-gray-500 mr-1">Pil:</span>{{ item.battery_mv }} mV</div>
                  }
                }
              </div>
            </li>
            
            <li *ngIf="!dashboardData?.dataList || dashboardData?.dataList?.length === 0" class="p-6 text-center text-gray-400">
              Henüz data bulunmuyor...
            </li>

          </ul>
        </div>

      </div>
    </div>
  `
})
export class DashboardComponent implements OnInit {
  
  private iotService = inject(IotService);
  private route = inject(ActivatedRoute);
  
  dashboardData: DashboardData | null = null;
  systemType: string = 'vineyard';

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.systemType = params.get('systemType') || 'vineyard';
      this.fetchData();
    });
  }

  fetchData(): void {
    this.iotService.getDashboardData(this.systemType).subscribe({
      next: (data) => {
        this.dashboardData = data;
      },
      error: (err) => {
        console.error('Data yüklenemedi', err);
      }
    });
  }
}
