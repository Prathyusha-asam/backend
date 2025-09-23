package com.demo.dms.controller;

import com.demo.dms.entity.Log;
import com.demo.dms.repository.LogRepository;
import com.demo.dms.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/dms/logs") // unified base
@CrossOrigin("*")
public class LogController {

    private final LogService logService;
    private final LogRepository logRepository;

    @Autowired
    public LogController(LogService logService, LogRepository logRepository) {
        this.logService = logService;
        this.logRepository = logRepository;
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<Page<Log>> findAll(Pageable pageable) {
        return ResponseEntity.ok(logService.getHistory(pageable));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN','BACKEND','FRONTEND','QA')")
    public ResponseEntity<Page<Log>> search(@RequestParam(required = false, name = "email") String createdBy,
                                            @RequestParam(required = false) String ticketNumber,
                                            Pageable pageable) {
        boolean hasAssignee = StringUtils.hasText(createdBy);
        boolean hasTicket = StringUtils.hasText(ticketNumber);

        Page<Log> page;

        if (hasAssignee && hasTicket) {
            page = logRepository.findByCreatedByStartingWithAndTicketNumberStartingWith(
                    createdBy.trim(), ticketNumber.trim(), pageable);
        } else if (hasAssignee) {
            page = logRepository.findByCreatedByIgnoreCase(createdBy.trim(), pageable);
        } else if (hasTicket) {
            page = logRepository.findByTicketNumberStartingWithIgnoreCase(ticketNumber.trim(), pageable);
        } else {
            // all child rows
            page = logRepository.findAll(pageable);
        }
        return ResponseEntity.ok(page);
    }

    //create request
    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<Log> create(@RequestBody Log log) {
        Log saved = logService.createHistory(log);
        return ResponseEntity
                .created(URI.create("/dms/logs/" + saved.getLogId()))
                .body(saved);
    }
}
