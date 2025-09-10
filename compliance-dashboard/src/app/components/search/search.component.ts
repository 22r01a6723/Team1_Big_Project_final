import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { MatSnackBar } from '@angular/material/snack-bar';
import { SearchService, Message, SearchRequest, Participant, Content, Context } from '../../services/search.service';

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css']
})
export class SearchComponent implements OnInit {
  @ViewChild(MatPaginator) paginator!: MatPaginator;

  searchForm: FormGroup;
  searchResults = new MatTableDataSource<Message>([]);
  displayedColumns: string[] = ['messageId', 'sender', 'network', 'subject', 'body', 'timestamp', 'channel', 'flagged'];

  tenantIds: string[] = [];
  totalResults = 0;
  pageSize = 20;
  currentPage = 0;
  loading = false;
  searchPerformed = false;
  showAdvancedFilters = false;
  currentSearchKeyword = '';

  tenantsLoading = false;
  connectionError = false;
  connectionErrorMessage = '';

  constructor(
    private fb: FormBuilder,
    private searchService: SearchService,
    private snackBar: MatSnackBar
  ) {
    this.searchForm = this.fb.group({
      tenantId: [''],
      keyword: [''],
      network: [''],
      flagged: [''],
      team: [''],
      channel: [''],
      subject: [''],
      participantId: [''],
      startDate: [''],
      endDate: ['']
    });
  }

  ngOnInit(): void {
    this.loadTenantIds();
  }

  loadTenantIds(): void {
    this.tenantsLoading = true;
    this.connectionError = false;

    this.searchService.getTenants().subscribe({
      next: (tenants: string[]) => {
        console.log('✅ Loaded tenants:', tenants);
        this.tenantIds = tenants;
        this.tenantsLoading = false;
      },
      error: (error: any) => {
        console.error('❌ Error loading tenants:', error);
        this.tenantsLoading = false;
        this.connectionError = true;
        this.connectionErrorMessage = this.getErrorMessage(error);
        this.showSnackBar('Error fetching tenants.');
      }
    });
  }

  onTenantChange(): void {
    this.searchResults.data = [];
    this.totalResults = 0;
    this.searchPerformed = false;
  }

  onSearch(): void {
    console.log('🔍 STARTING SEARCH');
    console.log('Selected tenant:', this.searchForm.get('tenantId')?.value);

    const formValue = this.searchForm.value;
    if (!formValue.tenantId) {
      this.showSnackBar('Please select a tenant first');
      return;
    }

    this.loading = true;
    this.searchPerformed = true;
    this.currentPage = 0;
    this.currentSearchKeyword = formValue.keyword || '';

    const searchRequest: SearchRequest = {
      tenantId: formValue.tenantId,
      keyword: formValue.keyword || undefined,
      network: formValue.network || undefined,
      flagged: formValue.flagged !== '' ? formValue.flagged : undefined,
      team: formValue.team || undefined,
      channel: formValue.channel || undefined,
      subject: formValue.subject || undefined,
      participantIds: formValue.participantId ? [formValue.participantId] : undefined,
      startTime: formValue.startDate ? new Date(formValue.startDate).toISOString() : undefined,
      endTime: formValue.endDate ? new Date(formValue.endDate).toISOString() : undefined,
      page: this.currentPage,
      size: this.pageSize
    };

    console.log('🔍 Search request:', searchRequest);

    this.searchService.searchMessages(searchRequest).subscribe({
      next: (response) => {
        console.log('🔍 FULL RESPONSE FROM ELASTICSEARCH:', JSON.stringify(response, null, 2));
        console.log('📊 Response content array:', response.content);
        console.log('📈 Total elements:', response.totalElements);

        this.searchResults.data = response.content;
        this.totalResults = response.totalElements;
        this.loading = false;

        if (response.content.length > 0) {
          console.log('✅ First message sample:', response.content[0]);
        }
      },
      error: (error) => {
        console.error('❌ Search error:', error);
        this.loading = false;
        this.connectionError = true;
        this.connectionErrorMessage = this.getErrorMessage(error);
        this.showSnackBar('Search error.');
      }
    });
  }

  onClear(): void {
    this.searchForm.reset();
    this.searchResults.data = [];
    this.totalResults = 0;
    this.searchPerformed = false;
    this.showAdvancedFilters = false;
    this.currentSearchKeyword = '';
    this.connectionError = false;
  }

  onPageChange(event: any): void {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.onSearch();
  }

  private getErrorMessage(error: any): string {
    if (error.status === 0) return 'Cannot connect to backend service at port 8080';
    // if (error.status === 0) return 'Cannot connect to backend service at port 8083';
    if (error.status === 404) return 'API endpoint not found - check if search service is running';
    if (error.status === 500) return 'Internal server error in search service';
    return `HTTP ${error.status}: ${error.message || error.statusText}`;
  }

  private showSnackBar(message: string): void {
    this.snackBar.open(message, 'Close', { duration: 4000 });
  }

  // Template methods
  getNetworkIcon(network: string): string {
    switch (network?.toLowerCase()) {
      case 'slack': return 'chat';
      case 'email': return 'email';
      case 'teams': return 'groups';
      default: return 'device_unknown';
    }
  }

  getSenderWithHighlight(participants: Participant[]): string {
    if (!participants || participants.length === 0) return 'N/A';

    // Find the sender participant
    const sender = participants.find(p => p.role === 'sender');
    if (sender) {
      const senderName = sender.displayName || sender.id || 'Unknown';
      return this.highlightKeyword(senderName);
    }

    // Fallback - take first participant
    const firstParticipant = participants[0];
    const name = firstParticipant?.displayName || firstParticipant?.id || 'Unknown';
    return this.highlightKeyword(name);
  }
  getSubjectWithHighlight(content: Content | undefined): string {
    const subject = content?.subject || 'No subject';
    return this.highlightKeyword(subject);
  }

  getBodyPreviewWithHighlight(content: Content | undefined): string {
    if (!content?.body) return 'No content';
    const preview = content.body.length > 100 ? content.body.substring(0, 100) + '...' : content.body;
    return this.highlightKeyword(preview);
  }

  formatDate(timestamp: string | number | Date | undefined): string {
    if (!timestamp) return '';
    const date = new Date(timestamp);
    return date.toLocaleString();
  }

  private highlightKeyword(text: string): string {
    if (!this.currentSearchKeyword) return text;
    const escapedKeyword = this.currentSearchKeyword.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
    const regex = new RegExp(escapedKeyword, 'gi');
    return text.replace(regex, (match) => `<mark>${match}</mark>`);
  }
}
