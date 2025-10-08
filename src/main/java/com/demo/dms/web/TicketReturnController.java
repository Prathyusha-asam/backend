package com.demo.dms.web;

import com.demo.dms.entity.TicketReturn;
import com.demo.dms.service.TicketReturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dms/returnDetails") // unified base
@CrossOrigin("*")
public class TicketReturnController {

    private final TicketReturnService ticketReturnService;

    @Autowired
    public TicketReturnController(TicketReturnService ticketReturnService) {
        this.ticketReturnService = ticketReturnService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','BACKEND','FRONTEND','QA')")
    public ResponseEntity<TicketReturn> create(@RequestBody TicketReturn req) {
        System.out.println("inside creating return details====================$$$$$$$$$$$$$$$$");
        return ResponseEntity.ok(ticketReturnService.save(req));
    }

    @GetMapping("/{ticketEntryId}")
    @PreAuthorize("hasAnyRole('ADMIN','BACKEND','FRONTEND','QA')")
    public Page<TicketReturn> findByTicketEntryId(@PathVariable long ticketEntryId, Pageable page) {
        return ticketReturnService.findByTicketEntryId(ticketEntryId, page);
    }
}
