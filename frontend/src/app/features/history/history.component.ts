import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';
import { IotService } from '../../core/services/iot.service';

@Component({
  selector: 'app-history',
  standalone: true,
  imports: [CommonModule, RouterModule, NavbarComponent],
  template: `
    <app-navbar [systemType]="systemType"></app-navbar>

    <div class="min-h-screen bg-gray-900 pt-10 pb-12 px-4 sm:px-6 lg:px-8">
      <div class="max-w-7xl mx-auto">
        <div class="flex justify-between items-center mb-8">
          <div>
            <h1 class="text-3xl font-bold text-white mb-2">Sensör Geçmişi</h1>
            <p class="text-gray-400">Tüm kaydedilmiş sensör verilerinin listesi.</p>
          </div>
          
          <button (click)="downloadCsv()" 
                  class="flex items-center gap-2 bg-teal-600 hover:bg-teal-500 active:scale-95 text-white py-2 px-4 rounded-lg font-medium transition-all duration-200 shadow-lg shadow-teal-900/20">
            <svg xmlns="http://www.w3.org/2000/svg" class="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-4l-4 4m0 0l-4-4m4 4V4" />
            </svg>
            CSV Olarak İndir
          </button>
        </div>

        <div class="flex flex-col">
          <div class="-my-2 overflow-x-auto sm:-mx-6 lg:-mx-8">
            <div class="py-2 align-middle inline-block min-w-full sm:px-6 lg:px-8">
              <div class="shadow overflow-hidden border-b border-gray-700 sm:rounded-lg">
                <table class="min-w-full divide-y divide-gray-700">
                  <thead class="bg-gray-800">
                    <tr>
                      <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-400 uppercase tracking-wider">Tarih</th>
                      <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-400 uppercase tracking-wider">Node / Cihaz</th>
                      @switch (systemType) {
                        @case ('vineyard') {
                          <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-400 uppercase tracking-wider">Sıcaklık (°C)</th>
                          <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-400 uppercase tracking-wider">Nem (%)</th>
                          <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-400 uppercase tracking-wider">Toprak Sıc. (°C)</th>
                          <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-400 uppercase tracking-wider">Işık (lx)</th>
                        }
                        @case ('wind') {
                          <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-400 uppercase tracking-wider">Rüzgar Hızı (m/s)</th>
                          <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-400 uppercase tracking-wider">Basınç (hPa)</th>
                          <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-400 uppercase tracking-wider">Sıcaklık (°C)</th>
                        }
                        @case ('flood') {
                          <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-400 uppercase tracking-wider">Su Seviyesi (m)</th>
                          <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-400 uppercase tracking-wider">Risk Skoru</th>
                          <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-400 uppercase tracking-wider">Akış Hızı (m/s)</th>
                        }
                        @case ('live-data') {
                          <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-400 uppercase tracking-wider">Ortam Sıc. (°C)</th>
                          <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-400 uppercase tracking-wider">Nem (%RH)</th>
                          <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-400 uppercase tracking-wider">Çip Sıc. (°C)</th>
                          <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-400 uppercase tracking-wider">Işık (lx)</th>
                          <th scope="col" class="px-6 py-3 text-left text-xs font-medium text-gray-400 uppercase tracking-wider">Pil (mV)</th>
                        }
                      }
                    </tr>
                  </thead>
                  <tbody class="bg-gray-800 divide-y divide-gray-700 h-96 overflow-y-auto">
                    
                    <tr *ngFor="let item of historyData" class="hover:bg-gray-700 transition-colors">
                      <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-300">
                        {{ item.timestamp | date:'dd/MM/yyyy HH:mm:ss' }}
                      </td>
                      <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-teal-400">
                        <a [routerLink]="['/node', systemType, item.nodeId || item.node_id]" class="hover:underline cursor-pointer">
                          {{ item.nodeId || item.node_id }}
                        </a>
                      </td>
                      
                      @switch (systemType) {
                        @case ('vineyard') {
                          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-300">
                            <span [ngClass]="{'text-red-400': item.temperature > 40}">
                              {{ item.temperature }}
                            </span>
                          </td>
                          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-300">{{ item.humidity }}</td>
                          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-300">{{ item.soil_temperature }}</td>
                          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-300">{{ item.light }}</td>
                        }
                        @case ('wind') {
                          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-300">{{ item.wind_speed_mps }}</td>
                          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-300">{{ item.pressure_hpa }}</td>
                          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-300">{{ item.temperature_c }}</td>
                        }
                        @case ('flood') {
                          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-300">{{ item.river_height_m }}</td>
                          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-300">{{ item.flood_risk_score }}</td>
                          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-300">{{ item.flow_speed_mps }}</td>
                        }
                        @case ('live-data') {
                          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-300">
                            <span [ngClass]="{'text-red-400': item.env_temp_c > 40}">
                              {{ item.env_temp_c }}
                            </span>
                          </td>
                          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-300">{{ item.humidity_rh }}</td>
                          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-300">{{ item.onchip_temp_c }}</td>
                          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-300">{{ item.light_lux }}</td>
                          <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-300">{{ item.battery_mv }}</td>
                        }
                      }
                    </tr>
                    
                    <tr *ngIf="historyData.length === 0">
                      <td colspan="7" class="px-6 py-8 text-center text-sm text-gray-500">
                        Gösterilecek geçmiş veri bulunmuyor.
                      </td>
                    </tr>

                  </tbody>
                </table>
              </div>
            </div>
          </div>
        </div>

      </div>
    </div>
  `
})
export class HistoryComponent implements OnInit {

  private iotService = inject(IotService);
  private route = inject(ActivatedRoute);
  
  historyData: any[] = [];
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
        this.historyData = data.dataList ? data.dataList.reverse() : [];
      },
      error: (err) => {
        console.error('Geçmiş veriler alınamadı', err);
      }
    });
  }

  downloadCsv(): void {
    this.iotService.downloadCsvData(this.systemType).subscribe({
      next: (blob: Blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `iot_sensor_data_${this.systemType}.csv`;
        a.click();
        window.URL.revokeObjectURL(url);
      },
      error: (err) => {
        console.error('CSV dosyası indirilirken hata oluştu', err);
      }
    });
  }
}
