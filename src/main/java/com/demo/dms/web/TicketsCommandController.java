package com.demo.dms.web;

import com.demo.dms.security.AuthUtils;
import com.demo.dms.service.TicketAggregateService;
import com.demo.dms.web.dto.TicketViewDtos.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dms/tickets")
@CrossOrigin("*")
public class TicketsCommandController {

  private final TicketAggregateService svc;

  public TicketsCommandController(TicketAggregateService svc) { this.svc = svc; }

  // CREATE parent
  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN','BACKEND','FRONTEND','QA')")
  public ResponseEntity<?> create(@RequestBody TicketCreateRequest req) {
    var t = svc.createTicket(req, AuthUtils.currentEmail());
    return ResponseEntity.ok(
        new ParentDTO(t.getId(), t.getTicketNumber(), t.getTicketType(), t.getDevType(),
            t.getCreatedBy(), t.getCreatedOn(), t.getUpdatedBy(), t.getUpdatedOn()));
  }

  // ADMIN composite update (parent + any lanes)
  @PutMapping("/{ticketNumber}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<AggregateView> adminUpdate(@PathVariable String ticketNumber,
                                                   @RequestBody AdminCompositeUpdateDTO body) {
    var out = svc.adminUpdate(ticketNumber, body, AuthUtils.currentEmail());
    return ResponseEntity.ok(out);
  }

  // Lane upserts (each ensures single child row per ticket)
  @PutMapping("/{ticketNumber}/backend")
  @PreAuthorize("hasAnyRole('ADMIN','BACKEND')")
  public ResponseEntity<AggregateView> backendUpsert(@PathVariable String ticketNumber,
                                                     @RequestBody ChildUpsertDTO body) {
    boolean admin = AuthUtils.hasRole("ROLE_ADMIN");
    return ResponseEntity.ok(svc.upsertBackend(ticketNumber, body, AuthUtils.currentEmail(), admin));
  }

  @PutMapping("/{ticketNumber}/frontend")
  @PreAuthorize("hasAnyRole('ADMIN','FRONTEND')")
  public ResponseEntity<AggregateView> frontendUpsert(@PathVariable String ticketNumber,
                                                      @RequestBody ChildUpsertDTO body) {
    boolean admin = AuthUtils.hasRole("ROLE_ADMIN");
    return ResponseEntity.ok(svc.upsertFrontend(ticketNumber, body, AuthUtils.currentEmail(), admin));
  }

  @PutMapping("/{ticketNumber}/qa")
  @PreAuthorize("hasAnyRole('ADMIN','QA')")
  public ResponseEntity<AggregateView> qaUpsert(@PathVariable String ticketNumber,
                                                @RequestBody ChildUpsertDTO body) {
    boolean admin = AuthUtils.hasRole("ROLE_ADMIN");
    return ResponseEntity.ok(svc.upsertQa(ticketNumber, body, AuthUtils.currentEmail(), admin));
  }

  // DELETE (admin)
  @DeleteMapping("/{ticketNumber}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> delete(@PathVariable String ticketNumber) {
    svc.deleteTicket(ticketNumber);
    return ResponseEntity.noContent().build();
  }
}
