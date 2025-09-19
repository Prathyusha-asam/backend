package com.demo.dms.web;

import com.demo.dms.entity.TicketDetails;
import com.demo.dms.security.AuthUtils;
import com.demo.dms.service.TicketAggregateService;
import com.demo.dms.service.TicketDetailsService;
import com.demo.dms.web.dto.TicketStats;
import com.demo.dms.web.dto.TicketViewDtos.AggregateView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/*@RestController
@RequestMapping("/dms/tickets")
@CrossOrigin("*")*/
public class TicketsQueryController {

  private final TicketAggregateService svc;
  private final TicketDetailsService ticketDetailsService;

  public TicketsQueryController(TicketAggregateService svc, TicketDetailsService ticketDetailsService) {
    this.svc = svc;
    this.ticketDetailsService = ticketDetailsService;
  }

  // LIST (role-aware)
  @GetMapping
  @PreAuthorize("hasAnyRole('ADMIN','BACKEND','FRONTEND','QA')")
  public ResponseEntity<Page<AggregateView>> list(Pageable pageable) {
    return ResponseEntity.ok(svc.list(pageable));
  }

  // GET ONE (role-aware)
  @GetMapping("/{ticketNumber}")
  @PreAuthorize("hasAnyRole('ADMIN','BACKEND','FRONTEND','QA')")
  public ResponseEntity<AggregateView> one(@PathVariable String ticketNumber) {
    var out = svc.one(ticketNumber);
    return out == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(out);
  }

  @GetMapping(value = "/stats", produces = "application/json")
  public ResponseEntity<TicketStats> stats() {
    return ResponseEntity.ok(ticketDetailsService.getStats());
  }

  // GET /dms/tickets/suggest?q=<optional>&assignee=<optional>&limit=8
  @GetMapping("/suggest")
  @PreAuthorize("hasAnyRole('ADMIN','BACKEND','FRONTEND','QA')")
  public ResponseEntity<java.util.List<TicketDetails>> suggest(
          @RequestParam(name = "q", required = false) String prefix,
          @RequestParam(name = "email", required = false) String assignee,
          @RequestParam(defaultValue = "8") int limit) {

    String normPrefix   = (prefix   != null && !prefix.trim().isEmpty()) ? prefix.trim()   : null;
    String normAssignee = (assignee != null && !assignee.trim().isEmpty()) ? assignee.trim() : null;

    int capped = Math.min(Math.max(limit, 1), 20);
    var page = org.springframework.data.domain.PageRequest.of(0, capped);

    var list = ticketDetailsService.suggest(normPrefix, normAssignee, page);
    return ResponseEntity.ok(list);
  }
}
