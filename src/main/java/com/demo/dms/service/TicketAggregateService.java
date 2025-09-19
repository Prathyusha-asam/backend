// src/main/java/com/demo/dms/service/TicketAggregateService.java
package com.demo.dms.service;

import com.demo.dms.entity.*;
import com.demo.dms.repository.*;
import com.demo.dms.security.AuthUtils;
import com.demo.dms.web.dto.TicketViewDtos.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TicketAggregateService {

  private final TicketDetailsRepository tickets;
  private final BackendEntryRepository backRepo;
  private final FrontendEntryRepository frontRepo;
  private final QaEntryRepository qaRepo;

  public TicketAggregateService(TicketDetailsRepository tickets,
                                BackendEntryRepository backRepo,
                                FrontendEntryRepository frontRepo,
                                QaEntryRepository qaRepo) {
    this.tickets = tickets; this.backRepo = backRepo; this.frontRepo = frontRepo; this.qaRepo = qaRepo;
  }

  // ========== READ (role-aware) ==========

  public Page<AggregateView> list(Pageable pageable) {
    boolean admin = AuthUtils.hasRole("ROLE_ADMIN");
    boolean back  = AuthUtils.hasRole("ROLE_BACKEND");
    boolean front = AuthUtils.hasRole("ROLE_FRONTEND");
    boolean qa    = AuthUtils.hasRole("ROLE_QA");

    return tickets.findAll(pageable).map(t -> {
      ParentDTO p = parentDTO(t);
      var b = (admin || back)  ? backRepo.findByTicket_TicketNumberIgnoreCase(t.getTicketNumber()).map(this::childDTO).orElse(null) : null;
      var f = (admin || front) ? frontRepo.findByTicket_TicketNumberIgnoreCase(t.getTicketNumber()).map(this::childDTO).orElse(null) : null;
      var q = (admin || qa)    ? qaRepo.findByTicket_TicketNumberIgnoreCase(t.getTicketNumber()).map(this::childDTO).orElse(null) : null;
      return new AggregateView(p, b, f, q);
    });
  }

  public AggregateView one(String ticketNumber) {
    var t = tickets.findByTicketNumberIgnoreCase(ticketNumber).orElse(null);
    if (t == null) return null;

    boolean admin = AuthUtils.hasRole("ROLE_ADMIN");
    boolean back  = AuthUtils.hasRole("ROLE_BACKEND");
    boolean front = AuthUtils.hasRole("ROLE_FRONTEND");
    boolean qa    = AuthUtils.hasRole("ROLE_QA");

    ParentDTO p = parentDTO(t);
    var b = (admin || back)  ? backRepo.findByTicket_TicketNumberIgnoreCase(ticketNumber).map(this::childDTO).orElse(null) : null;
    var f = (admin || front) ? frontRepo.findByTicket_TicketNumberIgnoreCase(ticketNumber).map(this::childDTO).orElse(null) : null;
    var q = (admin || qa)    ? qaRepo.findByTicket_TicketNumberIgnoreCase(ticketNumber).map(this::childDTO).orElse(null) : null;
    return new AggregateView(p, b, f, q);
  }

  private ParentDTO parentDTO(TicketDetails t) {
    return new ParentDTO(t.getId(), t.getTicketNumber(), t.getTicketType(), t.getDevType(),
            t.getCreatedBy(), t.getCreatedOn(), t.getUpdatedBy(), t.getUpdatedOn());
  }

  private ChildDTO childDTO(AbstractLaneEntry e) {
    return new ChildDTO(
            e.getId(),
            e.getTicket().getTicketNumber(),
            e.getStatus(), e.getEstimation(), e.getReturned(), e.getReturnNumber(),
            e.getAssignee(), e.getStartDate(), e.getEndDate(),
            e.getCreatedBy(), e.getCreatedOn(), e.getUpdatedBy(), e.getUpdatedOn()
    );
  }

  // ========== CREATE ==========

  @Transactional
  public TicketDetails createTicket(TicketCreateRequest req, String creatorEmail) {
    if (req.ticketNumber() == null || req.ticketNumber().isBlank())
      throw new org.springframework.web.server.ResponseStatusException(
              org.springframework.http.HttpStatus.BAD_REQUEST, "ticketNumber is required");

    if (tickets.existsByTicketNumberIgnoreCase(req.ticketNumber()))
      throw new org.springframework.web.server.ResponseStatusException(
              org.springframework.http.HttpStatus.CONFLICT, "ticketNumber already exists");

    var t = new TicketDetails();
    t.setTicketNumber(req.ticketNumber().trim());
    t.setTicketType(req.ticketType());
    t.setDevType(req.devType());
    t.setReturned(Boolean.FALSE);
    t.setReturnNumber(0);
    t.setCreatedBy(creatorEmail);
    t.setCreatedOn(java.time.Instant.now().toString());
    return tickets.save(t);
  }

  // ========== ADMIN UPDATE (parent + any children) ==========

  @Transactional
  public AggregateView adminUpdate(String ticketNumber, AdminCompositeUpdateDTO body, String actor) {
    var t = tickets.findByTicketNumberIgnoreCase(ticketNumber).orElseThrow();

    if (body.parent() != null) {
      var p = body.parent();
      if (p.ticketType() != null) t.setTicketType(p.ticketType());
      if (p.devType() != null)    t.setDevType(p.devType());
      if (p.completeDate() != null) t.setCompleteDate(p.completeDate());
      t.setUpdatedBy(actor);
      t.setUpdatedOn(java.time.Instant.now().toString());
      tickets.save(t);
    }

    if (body.backend() != null) upsertLane(ticketNumber, body.backend(), actor, "BACKEND");
    if (body.frontend() != null) upsertLane(ticketNumber, body.frontend(), actor, "FRONTEND");
    if (body.qa() != null)       upsertLane(ticketNumber, body.qa(), actor, "QA");

    return one(ticketNumber);
  }

  // ========== LANE UPSERTS (role) ==========

  @Transactional
  public AggregateView upsertBackend(String ticketNumber, ChildUpsertDTO dto, String actor, boolean isAdmin) {
    upsertLane(ticketNumber, dto, actor, "BACKEND", isAdmin);
    return one(ticketNumber);
  }
  @Transactional
  public AggregateView upsertFrontend(String ticketNumber, ChildUpsertDTO dto, String actor, boolean isAdmin) {
    upsertLane(ticketNumber, dto, actor, "FRONTEND", isAdmin);
    return one(ticketNumber);
  }
  @Transactional
  public AggregateView upsertQa(String ticketNumber, ChildUpsertDTO dto, String actor, boolean isAdmin) {
    upsertLane(ticketNumber, dto, actor, "QA", isAdmin);
    return one(ticketNumber);
  }

  // helper
  private void upsertLane(String ticketNumber, ChildUpsertDTO dto, String actor, String lane) {
    upsertLane(ticketNumber, dto, actor, lane, true);
  }
  private void upsertLane(String ticketNumber, ChildUpsertDTO dto, String actor, String lane, boolean adminCanSetAssignee) {
    var t = tickets.findByTicketNumberIgnoreCase(ticketNumber).orElseThrow();

    switch (lane) {
      case "BACKEND" -> {
        var opt = backRepo.findByTicket_TicketNumberIgnoreCase(ticketNumber);
        var e = opt.orElseGet(BackendEntry::new);
        e.setTicket(t);
        fillLane(e, dto, actor, adminCanSetAssignee);
        backRepo.save(e);
      }
      case "FRONTEND" -> {
        var opt = frontRepo.findByTicket_TicketNumberIgnoreCase(ticketNumber);
        var e = opt.orElseGet(FrontendEntry::new);
        e.setTicket(t);
        fillLane(e, dto, actor, adminCanSetAssignee);
        frontRepo.save(e);
      }
      case "QA" -> {
        var opt = qaRepo.findByTicket_TicketNumberIgnoreCase(ticketNumber);
        var e = opt.orElseGet(QaEntry::new);
        e.setTicket(t);
        fillLane(e, dto, actor, adminCanSetAssignee);
        qaRepo.save(e);
      }
    }
  }

  private void fillLane(AbstractLaneEntry e, ChildUpsertDTO dto, String actor, boolean adminCanSetAssignee) {
    e.setStatus(dto.status());
    e.setEstimation(dto.estimation());
    e.setReturned(Boolean.TRUE.equals(dto.returned()));
    e.setReturnNumber(dto.returnNumber() == null ? 0 : dto.returnNumber());
    e.setStartDate(dto.startDate());
    e.setEndDate(dto.endDate());

    // assignee: admin can set; lane user defaults to self
    String who = actor;
    if (adminCanSetAssignee && dto.assignee() != null && !dto.assignee().isBlank())
      who = dto.assignee();
    e.setAssignee(who);

    if (e.getId() == null) {
      e.setCreatedBy(actor);
      e.setCreatedOn(java.time.Instant.now().toString());
    } else {
      e.setUpdatedBy(actor);
      e.setUpdatedOn(java.time.Instant.now().toString());
    }
  }

  // ========== DELETE (admin) ==========

  @Transactional
  public void deleteTicket(String ticketNumber) {
    // delete children first (if DB not set to cascade)
    backRepo.deleteByTicket_TicketNumberIgnoreCase(ticketNumber);
    frontRepo.deleteByTicket_TicketNumberIgnoreCase(ticketNumber);
    qaRepo.deleteByTicket_TicketNumberIgnoreCase(ticketNumber);
    tickets.deleteByTicketNumberIgnoreCase(ticketNumber);
  }
}
