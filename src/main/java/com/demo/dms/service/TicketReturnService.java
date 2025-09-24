package com.demo.dms.service;

import com.demo.dms.entity.TicketReturn;
import com.demo.dms.repository.TicketDetailsEntryRepository;
import com.demo.dms.repository.TicketReturnRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Transactional
public class TicketReturnService {

    private final TicketReturnRepository ticketReturnRepository;
    private final TicketDetailsEntryRepository ticketDetailsEntryRepository;

    @Autowired
    public TicketReturnService(TicketReturnRepository ticketReturnRepository, TicketDetailsEntryRepository ticketDetailsEntryRepository) {
        this.ticketReturnRepository = ticketReturnRepository;
        this.ticketDetailsEntryRepository = ticketDetailsEntryRepository;
    }

    public TicketReturn save(TicketReturn req) {
        req.setCreatedOn(LocalDateTime.now());
        var existing = ticketDetailsEntryRepository.findById(req.getTicketEntryId())
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Entry not found for " + req.getTicketEntryId()));
        existing.setReturned(true);
        int count = existing.getReturnedNumber();
        existing.setReturnedNumber(++count);
        ticketDetailsEntryRepository.save(existing);
        return ticketReturnRepository.save(req);
    }

    public Page<TicketReturn> findByTicketEntryId(long id, Pageable page) {
        return ticketReturnRepository.findByTicketEntryId(id, page);
    }
}
