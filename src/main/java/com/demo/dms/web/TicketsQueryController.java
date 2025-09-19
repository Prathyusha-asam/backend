package com.demo.dms.web;

import com.demo.dms.security.AuthUtils;
import com.demo.dms.service.TicketAggregateService;
import com.demo.dms.web.dto.TicketViewDtos.AggregateView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dms/tickets")
@CrossOrigin("*")
public class TicketsQueryController {

  private final TicketAggregateService svc;

  public TicketsQueryController(TicketAggregateService svc) { this.svc = svc; }

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
}
