package com.demo.dms.controller;

import com.demo.dms.entity.Log;
import com.demo.dms.service.LogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

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
    public ResponseEntity<List<Log>> findAll() {
        return ResponseEntity.ok(logService.getHistory());
    }

    @GetMapping(value = "/{ticketNumber}", produces = "application/json")
    public ResponseEntity<Log> getOne(@PathVariable long ticketNumber) {
        return logService.getHistoryById(ticketNumber)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<Log> create(@RequestBody Log log) {
        Log saved = logService.createHistory(log);
        return ResponseEntity
                .created(URI.create("/dms/logs/" + saved.getLogId()))
                .body(saved);
    }
}
