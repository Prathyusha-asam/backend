package com.demo.dms.controller;

import com.demo.dms.entity.Log;
import com.demo.dms.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/dms/logs") // unified base
@CrossOrigin("*")
public class LogController {

    private final LogService logService;

    @Autowired
    public LogController(LogService logService) {
        this.logService = logService;
    }

    @GetMapping(produces = "application/json")
    public ResponseEntity<Page<Log>> findAll(Pageable pageable) {
        return ResponseEntity.ok(logService.getHistory(pageable));
    }

    @GetMapping(value = "/{ticketNumber}", produces = "application/json")
    public ResponseEntity<Log> getOne(@PathVariable long ticketNumber) {
        return logService.getHistoryById(ticketNumber)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
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
