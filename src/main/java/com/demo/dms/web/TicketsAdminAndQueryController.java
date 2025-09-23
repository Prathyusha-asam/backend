package com.demo.dms.web;

import com.demo.dms.entity.ParentTicketDetails;
import com.demo.dms.entity.TicketDetailsEntry;
import com.demo.dms.repository.ParentTicketDetailsRepository;
import com.demo.dms.repository.TicketDetailsEntryRepository;
import com.demo.dms.security.AuthUtils;
import com.demo.dms.service.EntryService;
import com.demo.dms.service.ParentTicketService;
import com.demo.dms.web.dto.AdminAndEntryDto;
import com.demo.dms.web.dto.TicketStats;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dms/tickets")
@CrossOrigin("*")
public class TicketsAdminAndQueryController {

  private final ParentTicketDetailsRepository parentRepo;
  private final TicketDetailsEntryRepository entryRepo;
  private final ParentTicketService parentTicketService;

  private final EntryService entryService;


  public TicketsAdminAndQueryController(ParentTicketDetailsRepository parentRepo,
                                        TicketDetailsEntryRepository entryRepo,
                                        ParentTicketService parentTicketService, EntryService entryService) {
    this.parentRepo = parentRepo;
    this.entryRepo = entryRepo;
    this.parentTicketService = parentTicketService;

      this.entryService = entryService;
  }

  // ------------------------------------------------------------
  // 1) ADMIN: Fetch ALL ticket details (parent + all children)
  // GET /dms/tickets/admin (paged)
  // ------------------------------------------------------------
  @GetMapping("/admin")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Page<AdminAggregateDTO>> allForAdmin(Pageable pageable) {
    Page<ParentTicketDetails> parents = parentRepo.findAll(pageable);
    List<AdminAggregateDTO> rows = parents.getContent().stream()
        .map(p -> new AdminAggregateDTO(p, entryRepo.findByTicket_TicketNumberIgnoreCase(p.getTicketNumber())))
        .toList();

    return ResponseEntity.ok(new PageImpl<>(
        rows, pageable, parents.getTotalElements()));
  }

  // ------------------------------------------------------------
  // 2) ADMIN: Fetch one ticket (parent + all children) by ticketNumber
  // GET /dms/tickets/{ticketNumber}
  // ------------------------------------------------------------
  @GetMapping("/{ticketNumber}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<AdminAggregateDTO> oneForAdmin(@PathVariable String ticketNumber) {
    var p = parentRepo.findByTicketNumberIgnoreCase(ticketNumber);
    if (p.isEmpty()) return ResponseEntity.notFound().build();
    var children = entryRepo.findByTicket_TicketNumberIgnoreCase(ticketNumber);
    return ResponseEntity.ok(new AdminAggregateDTO(p.get(), children));
  }

  // ------------------------------------------------------------
  // 3) Fetch ticket details by {ticketNumber}/{devType}
  // GET /dms/tickets/{ticketNumber}/{devType}
  // (Admin sees all, others can call this to get lane-specific details)
  // ------------------------------------------------------------
  @GetMapping("/{ticketNumber}/{devType}")
  @PreAuthorize("hasAnyRole('ADMIN','BACKEND','FRONTEND','QA')")
  public ResponseEntity<LaneAggregateDTO> byTicketAndLane(@PathVariable String ticketNumber,
                                                          @PathVariable String devType) {
    var p = parentRepo.findByTicketNumberIgnoreCase(ticketNumber);
    if (p.isEmpty()) return ResponseEntity.notFound().build();
    var lane = normalizeLane(devType);
    var entries = entryRepo.findByTicket_TicketNumberIgnoreCaseAndDevTypeIgnoreCase(ticketNumber, lane);
    return ResponseEntity.ok(new LaneAggregateDTO(p.get(), lane, entries));
  }

  // ------------------------------------------------------------
  // 4) ADMIN: Update parent (by ticketNumber)
  // PUT /dms/tickets/{ticketNumber}
  // Body: ParentTicketDetails (fields to overwrite if non-null)
  // ------------------------------------------------------------
 /* @PutMapping("/{ticketNumber}")
  @PreAuthorize("hasRole('ADMIN')")
  @Transactional
  public ResponseEntity<ParentTicketDetails> updateParent(@PathVariable String ticketNumber,
                                                                    @RequestBody ParentTicketDetails patch) {
    var p = parentRepo.findByTicketNumberIgnoreCase(ticketNumber)
        .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
            org.springframework.http.HttpStatus.NOT_FOUND, "Ticket not found"));

    // update only non-null fields (simple merge)
    if (patch.getTicketType() != null) p.setTicketType(patch.getTicketType());
    if (patch.isReturned()) p.setReturned(patch.isReturned());
    if (patch.getReturnedNumber() < 0) p.setReturnedNumber(patch.getReturnedNumber());
    if (patch.getCreatedBy() != null) p.setCreatedBy(patch.getCreatedBy());
    if (patch.getUpdatedBy() != null) p.setUpdatedBy(patch.getUpdatedBy());
    p.setUpdatedOn(LocalDateTime.now());
    p.setUpdatedBy(AuthUtils.currentEmail());

    return ResponseEntity.ok(parentRepo.save(p));
  }*/

  // ------------------------------------------------------------
  // 5) Update child (upsert) at /dms/tickets/{ticketNumber}/{devType}
  // PUT body: TicketDetailsEntry (we’ll upsert the *latest* entry per devType)
  // ------------------------------------------------------------
 /* @PutMapping("/{ticketNumber}/{devType}")
  @PreAuthorize("hasAnyRole('ADMIN','BACKEND','FRONTEND','QA')")
  @Transactional
  public ResponseEntity<TicketDetailsEntry> upsertChild(@PathVariable String ticketNumber,
                                                        @PathVariable String devType,
                                                        @RequestBody TicketDetailsEntry body) {
    var parent = parentRepo.findByTicketNumberIgnoreCase(ticketNumber)
        .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
            org.springframework.http.HttpStatus.NOT_FOUND, "Ticket not found"));

    String lane = normalizeLane(devType);

    var existingOpt = entryRepo.findTopByTicket_TicketNumberIgnoreCaseAndDevTypeIgnoreCaseOrderByIdDesc(ticketNumber, lane);
    TicketDetailsEntry e = existingOpt.orElseGet(TicketDetailsEntry::new);

    e.setTicket(parent);             // ensure FK link
    e.setDevType(lane);              // force lane from path
    if (body.getAssignee() != null) e.setAssignee(body.getAssignee());
    if (body.getEstimation() > 0) e.setEstimation(body.getEstimation());
    if (body.getStatus() != null) e.setStatus(body.getStatus());
    if (body.getStartDate() != null) e.setStartDate(body.getStartDate());
    if (body.getEndDate() != null) e.setEndDate(body.getEndDate());
    if (body.getCreatedBy() != null) e.setCreatedBy(body.getCreatedBy());
    e.setAssignee(AuthUtils.currentEmail());
    e.setUpdatedBy(AuthUtils.currentEmail());
    e.setCreatedBy(AuthUtils.currentEmail());
   // e.setUpdatedOn(LocalDateTime.parse(Instant.now().toString()));

    return ResponseEntity.ok(entryRepo.save(e));
  }*/

  // ------------------------------------------------------------
  // 6) Delete ticket (parent) → cascades to child entries
  // DELETE /dms/tickets/{ticketNumber}
  // ------------------------------------------------------------
  @DeleteMapping("/{ticketNumber}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<Void> deleteTicket(@PathVariable String ticketNumber) {
    var exists = parentRepo.findByTicketNumberIgnoreCase(ticketNumber).isPresent();
    if (!exists) return ResponseEntity.notFound().build();
    parentRepo.deleteByTicketNumberIgnoreCase(ticketNumber);
    return ResponseEntity.noContent().build();
  }


  // ------------------------------------------------------------
  // 7b) Instant “typeahead” suggestions that ALSO returns the same SearchRowDTOs
  // GET /dms/tickets/suggest?q=<prefix>&assignee=<optional>&limit=8
  // ------------------------------------------------------------
 /* @GetMapping("/suggest")
  @PreAuthorize("hasAnyRole('ADMIN','BACKEND','FRONTEND','QA')")
  public ResponseEntity<List<SearchRowDTO>> suggest(
      @RequestParam(name = "q") String prefix,
      @RequestParam(required = false) String assignee,
      @RequestParam(defaultValue = "8") int limit) {

    if (!StringUtils.hasText(prefix)) return ResponseEntity.ok(List.of());

    int capped = Math.min(Math.max(limit, 1), 20);
    var pr = PageRequest.of(0, capped);

    Page<TicketDetailsEntry> page;
    if (StringUtils.hasText(assignee)) {
      // If you don’t have this repo method, call
      // findByTicket_TicketNumberStartingWithIgnoreCase(prefix, pr)
      // and then filter in memory by assignee (less efficient)
      page = entryRepo.findByAssigneeIgnoreCaseAndTicket_TicketNumberStartingWithIgnoreCase(
          assignee.trim(), prefix.trim(), pr);
    } else {
      page = entryRepo.findByTicket_TicketNumberStartingWithIgnoreCase(prefix.trim(), pr);
    }

    return ResponseEntity.ok(page.getContent().stream().map(this::toSearchRow).toList());
  }*/

  // CREATE parent
  /*@PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ParentTicketDetails> create(@RequestBody ParentTicketDetails parentTicketDetails) {
    ParentTicketDetails saved = parentTicketService.createTicket(parentTicketDetails, AuthUtils.currentEmail());
    return ResponseEntity
            .created(URI.create("/dms/tickets/" + saved.getTicketNumber()))
            .body(saved);
  }*/

  @PostMapping
  @PreAuthorize("hasAnyRole('ADMIN','BACKEND','FRONTEND','QA')")
  public ResponseEntity<AdminAndEntryDto> createTicket(@RequestBody AdminAndEntryDto req) {
      AdminAndEntryDto saved = entryService.createTicket(req, AuthUtils.currentEmail());
      return ResponseEntity.ok(saved);
  }

  @PutMapping("/{ticketNumber}")
  @PreAuthorize("hasAnyRole('ADMIN','BACKEND','FRONTEND','QA')")
  public ResponseEntity<AdminAndEntryDto> updateTicket(@PathVariable String ticketNumber,
                                                       @RequestBody AdminAndEntryDto req) {
    return ResponseEntity.ok(entryService.updateTicket(ticketNumber, req));
  }

  // ------------------------------------------------------------
  // 7) Search API (paged)
  // GET /dms/tickets/search?assignee=&ticketNumber=
  //
  // Returns rows shaped as:
  //  TicketNumber, DevType, ReturnNumber, Returned, Estimation, Assignee, StartDate, EndDate
  //
  // Rules:
  //  - If assignee provided → filter by assignee (case-insensitive exact)
  //  - If ticketNumber typing → starts-with on ticketNumber (instant-friendly)
  //  - If both provided → apply both filters
  //  - If neither → all child rows
  // ------------------------------------------------------------
  @GetMapping("/search")
  @PreAuthorize("hasAnyRole('ADMIN','BACKEND','FRONTEND','QA')")
  public ResponseEntity<Page<SearchRowDTO>> search(
          @RequestParam(required = false) String assignee,
          @RequestParam(required = false) String ticketNumber,
          Pageable pageable) {

    boolean hasAssignee = StringUtils.hasText(assignee);
    boolean hasTicket = StringUtils.hasText(ticketNumber);

    Page<TicketDetailsEntry> page;

    if (hasAssignee && hasTicket) {
      page = entryRepo.findByAssigneeIgnoreCaseAndTicket_TicketNumberStartingWithIgnoreCase(
              assignee.trim(), ticketNumber.trim(), pageable);
    } else if (hasAssignee) {
      page = entryRepo.findByAssigneeIgnoreCase(assignee.trim(), pageable);
    } else if (hasTicket) {
      page = entryRepo.findByTicket_TicketNumberStartingWithIgnoreCase(ticketNumber.trim(), pageable);
    } else {
      // all child rows
      page = entryRepo.findAll(pageable);
    }

    List<SearchRowDTO> rows = page.getContent().stream().map(this::toSearchRow).toList();
    return ResponseEntity.ok(new PageImpl<>(rows, pageable, page.getTotalElements()));
  }

  @GetMapping(value = "/stats", produces = "application/json")
  @PreAuthorize("hasAnyRole('ADMIN','BACKEND','FRONTEND','QA')")
  public ResponseEntity<TicketStats> stats() {
    return ResponseEntity.ok(parentTicketService.getStats());
  }

  // ---------------- helpers / DTO mapping ----------------

  private String normalizeLane(String devType) {
    if (devType == null) return "";
    var d = devType.trim().toLowerCase();
    if (d.startsWith("back")) return "Backend";
    if (d.startsWith("front")) return "Frontend";
    if (d.equals("qa") || d.startsWith("qa")) return "QA";
    return devType.trim();
  }

  private SearchRowDTO toSearchRow(TicketDetailsEntry e) {
    var p = e.getTicket(); // requires @ManyToOne mapping on TICKET_NUMBER
    return new SearchRowDTO(
        p != null ? p.getTicketNumber() : null,
        p != null ? p.getDevType() : null,
        p != null ? p.getTicketType() : null,
        e.getDevType(),
        e.getStatus(),
        p != null ? p.getReturnedNumber() : null,
        p != null ? p.isReturned() : null,
        e.getEstimation(),
        e.getAssignee(),
        e.getStartDate(),
        e.getEndDate()
    );
  }

  // ======================= DTOs ==========================
  public static record AdminAggregateDTO(
      Parent parent,
      Map<String, List<Child>> childrenByTicketNumber
  ) {
    public AdminAggregateDTO(ParentTicketDetails p, List<TicketDetailsEntry> children) {
      this(
          new Parent(p.getId(), p.getTicketNumber(), p.getTicketType(), p.getDevType(),
              p.getCreatedBy(), String.valueOf(p.getCreatedOn()), p.getUpdatedBy(), String.valueOf(p.getUpdatedOn()),
              p.isReturned(), p.getReturnedNumber()),
          children.stream()
              .map(Child::of)
              .collect(Collectors.groupingBy(Child::ticketNumber, LinkedHashMap::new, Collectors.toList()))
      );
    }
  }

  public static record LaneAggregateDTO(
      Parent parent,
      String devType,
      List<Child> entries
  ) {
    public LaneAggregateDTO(ParentTicketDetails p, String devType, List<TicketDetailsEntry> list) {
      this(new Parent(p.getId(), p.getTicketNumber(), p.getTicketType(), p.getDevType(),
              p.getCreatedBy(), String.valueOf(p.getCreatedOn()), p.getUpdatedBy(), String.valueOf(p.getUpdatedOn()),
              p.isReturned(), p.getReturnedNumber()),
          devType,
          list.stream().map(Child::of).toList());
    }
  }

  public static record Parent(
      Long id,
      String ticketNumber,
      String ticketType,
      String devType,
      String createdBy,
      String createdOn,
      String updatedBy,
      String updatedOn,
      Boolean returned,
      Integer returnNumber
  ) {}

  public static record Child(
          Long id,
          String ticketNumber,
          String devType,
          String status,
          String assignee,
          long estimation,
          String startDate,
          String endDate,
          String createdBy,
          String createdOn,
          String updatedBy,
          String updatedOn
  ) {
    public static Child of(TicketDetailsEntry e) {
      return new Child(
          e.getId(),
          e.getTicketNumber(),
          e.getDevType(),
          e.getStatus(),
          e.getAssignee(),
          e.getEstimation(),
          e.getStartDate(),
          e.getEndDate(),
          e.getCreatedBy(),
          String.valueOf(e.getCreatedOn()),
          e.getUpdatedBy(),
          String.valueOf(e.getUpdatedOn())
      );
    }
  }

  // Search row shape you requested
  public static record SearchRowDTO(
          String ticketNumber,
          String parentDevType,
          String ticketType,
          String childDevType,
          String childStatus,
          Integer returnNumber,
          Boolean returned,
          long estimation,
          String assignee,
          String startDate,
          String endDate
  ) {}
}
