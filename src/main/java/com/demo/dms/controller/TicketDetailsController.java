package com.demo.dms.controller;

import com.demo.dms.entity.TicketDetails;
import com.demo.dms.service.TicketDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/dms")
@CrossOrigin("*")
public class TicketDetailsController {

    private final TicketDetailsService ticketDetailsService;

    @Autowired
    public TicketDetailsController(TicketDetailsService ticketDetailsService) {
        this.ticketDetailsService = ticketDetailsService;
    }

    @GetMapping(value = "/ticket-details/{ticketNumber}", produces = "application/json")
    public ResponseEntity<TicketDetails> getDetailsByTicketNumber(@PathVariable String ticketNumber) {
        return ticketDetailsService.getDetailsByTicketNumber(ticketNumber)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/ticket-details", produces = "application/json")
    public ResponseEntity<Page<TicketDetails>> list(@RequestParam(name = "email", required = false) String assignee, Pageable pageable) {
        Page<TicketDetails> out = (assignee == null || assignee.isBlank())
                ? ticketDetailsService.getAllTicketDetails(pageable)
                : ticketDetailsService.getTicketByAssignee(assignee, pageable);
        return ResponseEntity.ok(out);
    }

    @PostMapping(value = "/ticket-details", consumes = "application/json", produces = "application/json")
    public ResponseEntity<TicketDetails> create(@RequestBody TicketDetails ticketDetails) {
        TicketDetails saved = ticketDetailsService.createTicketDetails(ticketDetails);
        return ResponseEntity
                .created(URI.create("/dms/ticket-details/" + saved.getTicketNumber()))
                .body(saved);
    }

    @PutMapping(value = "/ticket-details/{ticketNumber}", consumes = "application/json", produces = "application/json")
    public ResponseEntity<TicketDetails> updateFull(@PathVariable String ticketNumber,
                                                    @RequestBody TicketDetails body) {
        TicketDetails updated = ticketDetailsService.updateFull(ticketNumber, body);
        return ResponseEntity.ok(updated);
    }

    // (Optional) BULK DELETE: /dms/ticket-details?ids=1,2,3
    @DeleteMapping(value = "/ticket-details")
    public ResponseEntity<Void> deleteMany(@RequestParam("ids") List<Long> ids) {
        ticketDetailsService.deleteByIds(ids);
        return ResponseEntity.noContent().build();
    }
}
