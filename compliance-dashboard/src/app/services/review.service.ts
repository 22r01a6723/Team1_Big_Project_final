// review.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Flag {
  flagId: string;
  flagDescription: string;
  ruleId: string;
  messageId: string;
  tenantId: string;
  network: string;
  createdAt: number;
  reviewStatus?: 'PENDING' | 'REVIEWED';
}

@Injectable({
  providedIn: 'root'
})
export class ReviewService {
  private reviewUrl = 'http://localhost:8084/api/flags';

  constructor(private http: HttpClient) {}

  // Get all flags for tenant and network
  getAllFlags(tenantId: string, network: string): Observable<Flag[]> {
    const params = new HttpParams()
      .set('tenantId', tenantId)
      .set('network', network);

    return this.http.get<Flag[]>(this.reviewUrl, { params });
  }

  // Get flags by flag ID
  getFlagsByFlagId(flagId: string, tenantId: string, network: string): Observable<Flag[]> {
    const params = new HttpParams()
      .set('tenantId', tenantId)
      .set('network', network);

    return this.http.get<Flag[]>(`${this.reviewUrl}/${flagId}`, { params });
  }

  // Get flags by message
  getFlagsByMessage(tenantId: string, network: string): Observable<Flag[]> {
    const params = new HttpParams()
      .set('tenantId', tenantId)
      .set('network', network);

    return this.http.get<Flag[]>(`${this.reviewUrl}/message`, { params });
  }

  // Get flags by rule ID
  getFlagsByRuleId(ruleId: string, tenantId: string, network: string): Observable<Flag[]> {
    const params = new HttpParams()
      .set('tenantId', tenantId)
      .set('network', network);

    return this.http.get<Flag[]>(`${this.reviewUrl}/rule/${ruleId}`, { params });
  }

  // Search flags by description
  searchFlagsByDescription(description: string, tenantId: string, network: string): Observable<Flag[]> {
    const params = new HttpParams()
      .set('description', description)
      .set('tenantId', tenantId)
      .set('network', network);

    return this.http.get<Flag[]>(`${this.reviewUrl}/search`, { params });
  }

  // Get flags by date range
  getFlagsByDateRange(start: number, end: number, tenantId: string, network: string): Observable<Flag[]> {
    const params = new HttpParams()
      .set('start', start.toString())
      .set('end', end.toString())
      .set('tenantId', tenantId)
      .set('network', network);

    return this.http.get<Flag[]>(`${this.reviewUrl}/date-range`, { params });
  }

  // Update review status (you'll need to add this endpoint to your backend)
  updateReviewStatus(flagId: string, status: 'PENDING' | 'REVIEWED'): Observable<Flag> {
    return this.http.put<Flag>(`${this.reviewUrl}/${flagId}/status`, { reviewStatus: status });
  }
}
