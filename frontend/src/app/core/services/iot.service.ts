import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface IotData {
  id?: string;
  is_energest?: number;
  node_id?: number | string;
  light?: number;
  temperature?: number;
  humidity?: number;
  soil_temperature?: number;
  clock_drift?: number;
  timestamp?: string;
  harbour_id?: string;
  river_id?: string;
  wind_speed_mps?: number;
  wind_gust_mps?: number;
  wind_direction_deg?: number;
  pressure_hpa?: number;
  river_height_m?: number;
  flow_speed_mps?: number;
  rainfall_mm_h?: number;
  soil_moisture_pct?: number;
  alert_level?: string;
  flood_risk_score?: number;
  time_s?: number;
  onchip_temp_c?: number;
  battery_mv?: number;
  env_temp_c?: number;
  humidity_rh?: number;
  light_lux?: number;
}

export interface DashboardData {
  dataList: any[];
  avgLight?: number;
  avgTemperature?: number;
  avgHumidity?: number;
  avgSoilTemperature?: number;

  avgWindSpeed?: number;
  avgWindGust?: number;
  avgPressure?: number;

  avgRiverHeight?: number;
  avgFlowSpeed?: number;
  avgRainfall?: number;
  avgSoilMoisture?: number;
  avgFloodRiskScore?: number;

  avgEnvTemp?: number;
  avgOnChipTemp?: number;
  avgBattery?: number;
}

@Injectable({
  providedIn: 'root'
})
export class IotService {
  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getDashboardData(systemType: string): Observable<DashboardData> {
    const endpointMap: Record<string, string> = {
      'vineyard': 'iot',
      'wind': 'wind',
      'flood': 'flood',
      'live-data': 'live'
    };
    const path = endpointMap[systemType] || 'iot';
    return this.http.get<DashboardData>(`${this.apiUrl}/${path}/dashboard`);
  }

  downloadCsvData(systemType: string = 'vineyard'): Observable<Blob> {
    const endpointMap: Record<string, string> = { 'vineyard': 'iot', 'wind': 'wind', 'flood': 'flood', 'live-data': 'live' };
    const path = endpointMap[systemType] || 'iot';
    return this.http.get(`${this.apiUrl}/${path}/export/csv`, { responseType: 'blob' });
  }

  getNodeDetail(nodeId: string | number, systemType: string = 'vineyard'): Observable<NodeDetailResponseDto> {
    const endpointMap: Record<string, string> = { 'vineyard': 'iot', 'wind': 'wind', 'flood': 'flood', 'live-data': 'live' };
    const path = endpointMap[systemType] || 'iot';
    return this.http.get<NodeDetailResponseDto>(`${this.apiUrl}/${path}/node/${nodeId}`);
  }
}

export interface NodeStatistics {
  avgTemperature: number;
  avgHumidity: number;
  avgLight: number;
  avgSoilTemperature: number;
  maxTemperature: number;
  minTemperature: number;
}

export interface NodeDetailResponseDto {
  nodeId: string | number;
  recentData: IotData[];
  statistics: NodeStatistics;
}
