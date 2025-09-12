package com.demo.dms.service;

import com.demo.dms.entity.Log;
import com.demo.dms.repository.LogRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LogService {

    private final LogRepository logRepository;

    @Autowired
    public LogService(LogRepository logRepository) {
        this.logRepository = logRepository;
    }

    public List<Log> getHistory() {
        return logRepository.findAll();
    }

    @Transactional
    public Log createHistory(Log log) {
        return logRepository.save(log);
    }

    public Optional<Log> getHistoryById(long ticketNumber) {
        return Optional.ofNullable(logRepository.findByTicketNumber(ticketNumber));
    }
}
