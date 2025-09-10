// compliance.service.ts
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CompliancePolicy {
  id: string;
  network: string;
  name: string;
  description: string;
  regex: string;
  severity: string;
  enabled: boolean;
}

export interface FlaggedMessage {
  id: number;
  messageId: string;
  network: string;
  content: string;
  violatedPolicy: string;
  severity: string;
  timestamp: string;
}

@Injectable({
  providedIn: 'root'
})
export class ComplianceService {
  private complianceUrl = 'http://localhost:8080/api/compliance';
  //private complianceUrl = 'http://localhost:8082/api/compliance';

  constructor(private http: HttpClient) {}

  // Get all flagged messages
  getAllFlagged(): Observable<FlaggedMessage[]> {
    return this.http.get<FlaggedMessage[]>(`${this.complianceUrl}/flagged`);
  }

  // Evaluate a single message
  evaluateMessage(request: any): Observable<FlaggedMessage[]> {
    return this.http.post<FlaggedMessage[]>(`${this.complianceUrl}/evaluate`, request);
  }

  // Scan network messages
  scanNetwork(network: string): Observable<FlaggedMessage[]> {
    const params = new HttpParams().set('network', network);
    return this.http.post<FlaggedMessage[]>(`${this.complianceUrl}/scan`, {}, { params });
  }

  // Get compliance policies (You'll need to add this endpoint to your backend)
  getCompliancePolicies(): Observable<CompliancePolicy[]> {
    // This endpoint needs to be added to your ComplianceController
    return this.http.get<CompliancePolicy[]>(`${this.complianceUrl}/policies`);
  }

  // Get policies by network
  getPoliciesByNetwork(network: string): Observable<CompliancePolicy[]> {
    const params = new HttpParams().set('network', network);
    return this.http.get<CompliancePolicy[]>(`${this.complianceUrl}/policies/network`, { params });
  }

  // Get flagged messages by policy
  getFlaggedByPolicy(policyName: string): Observable<FlaggedMessage[]> {
    const params = new HttpParams().set('policy', policyName);
    return this.http.get<FlaggedMessage[]>(`${this.complianceUrl}/flagged/policy`, { params });
  }
}
