import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Participant {
  role: string;        // sender, recipient, cc, bcc
  id: string;          // email, username, etc.
  displayName?: string; // optional
}

export interface Content {
  subject: string;
  body: string;
}

export interface Context {
  team: string;
  channel: string;
  rawReference: string;
}

export interface FlagInfo {
  flagDescription: string;
  timestamp: string;
}

export interface Message {
  messageId: string;
  tenantId: string;
  network: string;
  timestamp: string;
  participants: Participant[];  // Array of participant objects
  content: Content;             // Nested content object
  context: Context;             // Nested context object
  flagged: boolean;
  flagInfo?: FlagInfo;
}

export interface SearchRequest {
  keyword?: string;
  tenantId: string;
  messageId?: string;
  subject?: string;
  body?: string;
  network?: string;
  flagged?: boolean;
  team?: string;
  channel?: string;
  rawReference?: string;
  participantIds?: string[];
  participantRoles?: string[];
  flagDescription?: string;
  ruleId?: string;
  startTime?: string;
  endTime?: string;
  startFlagTime?: string;
  endFlagTime?: string;
  page?: number;
  size?: number;
  exactMatch?: boolean;
  partialMatch?: boolean;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
}

@Injectable({
  providedIn: 'root'
})
export class SearchService {
  private baseUrl = 'http://localhost:8080/api/messages';
  private tenantUrl = 'http://localhost:8080/api/messages/tenants';
  // private baseUrl = 'http://localhost:8083/api/messages';
  // private tenantUrl = 'http://localhost:8083/api/messages/tenants';


  constructor(private http: HttpClient) {}

  getTenants(): Observable<string[]> {
    return this.http.get<string[]>(this.tenantUrl);
  }

  searchMessages(request: SearchRequest): Observable<PageResponse<Message>> {
    return this.http.post<PageResponse<Message>>(`${this.baseUrl}/search`, request);
  }

  keywordSearch(tenantId: string, keyword: string, page = 0, size = 20): Observable<PageResponse<Message>> {
    return this.http.get<PageResponse<Message>>(
      `${this.baseUrl}/keyword/${tenantId}?keyword=${encodeURIComponent(keyword)}&page=${page}&size=${size}`
    );
  }

  searchByNetwork(tenantId: string, network: string, page = 0, size = 20): Observable<PageResponse<Message>> {
    return this.http.get<PageResponse<Message>>(
      `${this.baseUrl}/network/${tenantId}/${network}?page=${page}&size=${size}`
    );
  }

  searchByFlagged(tenantId: string, flagged: boolean, page = 0, size = 20): Observable<PageResponse<Message>> {
    return this.http.get<PageResponse<Message>>(
      `${this.baseUrl}/flagged/${tenantId}/${flagged}?page=${page}&size=${size}`
    );
  }
}
