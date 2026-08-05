import { Component, OnInit, OnDestroy, inject, ViewChild } from '@angular/core';
import { CommonModule, DecimalPipe } from '@angular/common';
import { ActivatedRoute, RouterModule, Router } from '@angular/router';
import { NavbarComponent } from '../../shared/components/navbar/navbar.component';
import { IotService, NodeDetailResponseDto, IotData } from '../../core/services/iot.service';
import { BaseChartDirective } from 'ng2-charts';
import { ChartConfiguration, ChartOptions, ChartType } from 'chart.js';
import { environment } from '../../../environments/environment';

@Component({
  selector: 'app-node-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, NavbarComponent, BaseChartDirective, DecimalPipe],
  templateUrl: './node-detail.component.html',
  styleUrl: './node-detail.component.scss'
})
export class NodeDetailComponent implements OnInit, OnDestroy {

  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private iotService = inject(IotService);

  @ViewChild(BaseChartDirective) chart?: BaseChartDirective;

  systemType: string = 'vineyard';
  nodeId: string | null = null;
  nodeDetail: NodeDetailResponseDto | null = null;
  recentTableData: IotData[] = [];
  private pollingInterval: any;
  private eventSource?: EventSource;

  public lineChartData: ChartConfiguration<'line'>['data'] = {
    labels: [],
    datasets: []
  };
  public lineChartOptions: ChartOptions<'line'> = {
    responsive: true,
    maintainAspectRatio: false,
    elements: {
      line: { tension: 0.4, borderWidth: 3 },
      point: { radius: 4, hitRadius: 10, hoverRadius: 6 }
    },
    scales: {
      y: { type: 'linear', display: true, position: 'left', grid: { color: '#374151' }, ticks: { color: '#d1d5db' }, title: { display: true, text: '', color: '#d1d5db' } },
      y1: { type: 'linear', display: true, position: 'right', grid: { drawOnChartArea: false }, ticks: { color: '#d1d5db' }, title: { display: true, text: '', color: '#d1d5db' } },
      x: { grid: { color: '#374151' }, ticks: { color: '#d1d5db' } }
    },
    plugins: {
      legend: { labels: { color: '#ffffff' } },
      tooltip: { mode: 'index', intersect: false }
    }
  };

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.systemType = params.get('systemType') || 'vineyard';
      const idParam = params.get('id');
      if (idParam) {
        this.nodeId = idParam;
        this.fetchNodeData();

        if (this.systemType === 'live-data') {
          this.startLiveStream();
        } else {
          this.pollingInterval = setInterval(() => {
            this.fetchNodeData(true);
          }, 5000);
        }
      }
    });
  }

  ngOnDestroy(): void {
    if (this.pollingInterval) {
      clearInterval(this.pollingInterval);
    }
    if (this.eventSource) {
      this.eventSource.close();
    }
  }

  startLiveStream(): void {
    this.eventSource = new EventSource(`${environment.apiUrl}/live/stream`);
    this.eventSource.onmessage = (event) => {
      const newData: IotData = JSON.parse(event.data);
      if (String(newData.node_id) !== String(this.nodeId)) return;

      const updatedData = [newData, ...(this.nodeDetail?.recentData || [])].slice(0, 50);
      if (this.nodeDetail) {
        this.nodeDetail.recentData = updatedData;
      }
      this.recentTableData = updatedData.slice(0, 10);
      this.updateChartData(updatedData);
    };
  }

  fetchNodeData(isPolling: boolean = false): void {
    if (!this.nodeId) return;
    
    this.iotService.getNodeDetail(this.nodeId, this.systemType).subscribe({
      next: (data) => {
        this.nodeDetail = data;
        
        if (data.recentData && data.recentData.length > 0) {
          this.recentTableData = data.recentData.slice(0, 10);
          this.updateChartData(data.recentData);
        }
      },
      error: (err) => {
        if (!isPolling) console.error('Node verisi yüklenemedi', err);
      }
    });
  }

  updateChartData(data: any[]) {
      const chronologicalData = [...data].reverse();
      
      const labels = chronologicalData.map((d: any) => {
        const date = new Date(d.timestamp!);
        return `${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
      });

      let datasets: any[] = [];
      let yLabels = { y: '', y1: '' };
      
      switch (this.systemType) {
        case 'wind':
            datasets = [
                { data: chronologicalData.map((d: any) => d.wind_speed_mps || 0), label: 'Rüzgar Hızı (m/s)', yAxisID: 'y', borderColor: '#3b82f6', pointBackgroundColor: '#3b82f6', pointBorderColor: '#fff', fill: false },
                { data: chronologicalData.map((d: any) => d.pressure_hpa || 0), label: 'Basınç (hPa)', yAxisID: 'y1', borderColor: '#8b5cf6', pointBackgroundColor: '#8b5cf6', pointBorderColor: '#fff', fill: false }
            ];
            yLabels = { y: 'Rüzgar Hızı (m/s)', y1: 'Basınç (hPa)' };
            break;
        case 'flood':
            datasets = [
                { data: chronologicalData.map((d: any) => d.river_height_m || 0), label: 'Su Seviyesi (m)', yAxisID: 'y', borderColor: '#06b6d4', pointBackgroundColor: '#06b6d4', pointBorderColor: '#fff', fill: false },
                { data: chronologicalData.map((d: any) => d.flow_speed_mps || 0), label: 'Akıntı Hızı (m/s)', yAxisID: 'y1', borderColor: '#f97316', pointBackgroundColor: '#f97316', pointBorderColor: '#fff', fill: false }
            ];
            yLabels = { y: 'Su Seviyesi (m)', y1: 'Akıntı Hızı (m/s)' };
            break;
        case 'live-data':
            datasets = [
                { data: chronologicalData.map((d: any) => d.env_temp_c || 0), label: 'Ortam Sıcaklığı (°C)', yAxisID: 'y', borderColor: '#f43f5e', pointBackgroundColor: '#f43f5e', pointBorderColor: '#fff', fill: false },
                { data: chronologicalData.map((d: any) => d.humidity_rh || 0), label: 'Nem (%RH)', yAxisID: 'y1', borderColor: '#3b82f6', pointBackgroundColor: '#3b82f6', pointBorderColor: '#fff', fill: false }
            ];
            yLabels = { y: 'Ortam Sıcaklığı (°C)', y1: 'Nem (%RH)' };
            break;
        case 'vineyard':
        default:
            datasets = [
                { data: chronologicalData.map((d: any) => d.temperature || 0), label: 'Sıcaklık (°C)', yAxisID: 'y', borderColor: '#f97316', pointBackgroundColor: '#f97316', pointBorderColor: '#fff', fill: false },
                { data: chronologicalData.map((d: any) => d.humidity || 0), label: 'Nem (%)', yAxisID: 'y1', borderColor: '#3b82f6', pointBackgroundColor: '#3b82f6', pointBorderColor: '#fff', fill: false }
            ];
            yLabels = { y: 'Sıcaklık (°C)', y1: 'Nem (%)' };
            break;
      }

      this.lineChartData.labels = labels;
      this.lineChartData.datasets = datasets;
      
      if (this.lineChartOptions?.scales?.['y']) {
        (this.lineChartOptions.scales['y'] as any).title!.text = yLabels.y;
      }
      if (this.lineChartOptions?.scales?.['y1']) {
        (this.lineChartOptions.scales['y1'] as any).title!.text = yLabels.y1;
      }

      if (this.chart) {
        this.chart.update();
      }
  }
}
