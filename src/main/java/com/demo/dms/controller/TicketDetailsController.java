package com.demo.dms.controller;

import com.demo.dms.entity.TicketDetails;
import com.demo.dms.service.TicketDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/dms") // ⚠️ If you also set server.servlet.context-path=/dms, remove this to avoid /dms/dms/...
@CrossOrigin("*")
public class TicketDetailsController {

    private final TicketDetailsService ticketDetailsService;

    @Autowired
    public TicketDetailsController(TicketDetailsService ticketDetailsService) {
        this.ticketDetailsService = ticketDetailsService;
    }

    // GET one by id
    @GetMapping(value = "/ticket-details/{id}", produces = "application/json")
    public ResponseEntity<TicketDetails> getTicketDetailsById(@PathVariable long id) {
        return ticketDetailsService.getTicketById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // LIST all or filter by assignee (use ?assignee=alice@company.com)
    @GetMapping(value = "/ticket-details", produces = "application/json")
    public ResponseEntity<List<TicketDetails>> list(@RequestParam(name = "email", required = false) String assignee) {
        List<TicketDetails> out = (assignee == null || assignee.isBlank())
                ? ticketDetailsService.getAllTicketDetails()
                : ticketDetailsService.getTicketByAssignee(assignee);
        return ResponseEntity.ok(out);
    }

    // CREATE (returns 201 + Location header)
    @PostMapping(value = "/ticket-details", consumes = "application/json", produces = "application/json")
    public ResponseEntity<TicketDetails> create(@RequestBody TicketDetails ticketDetails) {
        TicketDetails saved = ticketDetailsService.createTicketDetails(ticketDetails);
        return ResponseEntity
                .created(URI.create("/dms/ticket-details/" + saved.getTicketNumber()))
                .body(saved);
    }

    // (Optional) BULK DELETE: /dms/ticket-details?ids=1,2,3
    @DeleteMapping(value = "/ticket-details")
    public ResponseEntity<Void> deleteMany(@RequestParam("ids") List<Long> ids) {
        ticketDetailsService.deleteByIds(ids);
        return ResponseEntity.noContent().build();
    }
}
