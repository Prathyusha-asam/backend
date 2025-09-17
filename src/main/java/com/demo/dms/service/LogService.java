package com.demo.dms.service;

import com.demo.dms.entity.Log;
import com.demo.dms.repository.LogRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LogService {

    private final LogRepository logRepository;

    @Autowired
    public LogService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public Page<Log> getHistory(Pageable pageable) {
        return logRepository.findAll(pageable);
    }

    @Transactional
    public Log createHistory(Log log) {
        log.setCreatedOn(String.valueOf(java.time.Instant.now()));
        return logRepository.save(log);
    }

    public Optional<Log> getHistoryById(String ticketNumber) {
        return Optional.ofNullable(logRepository.findByTicketNumber(ticketNumber));
    }
}
