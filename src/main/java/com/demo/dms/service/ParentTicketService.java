package com.demo.dms.service;

import com.demo.dms.entity.ParentTicketDetails;
import com.demo.dms.repository.ParentTicketDetailsRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class ParentTicketService {

    private final ParentTicketDetailsRepository repo;

    public ParentTicketService(ParentTicketDetailsRepository repo) {
        this.repo = repo;
    }

    public Page<ParentTicketDetails> list(Pageable pageable) {
        return repo.findAll(pageable);
    }

    public Optional<ParentTicketDetails> getByTicketNumber(String ticketNumber) {
        return repo.findByTicketNumberIgnoreCase(ticketNumber);
    }

    public ParentTicketDetails createTicket(ParentTicketDetails req, String creatorEmail) {
        if (req.getTicketNumber() == null || req.getTicketNumber().isBlank())
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "ticketNumber is required");

        if (repo.existsByTicketNumberIgnoreCase(req.getTicketNumber()))
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.CONFLICT, "ticketNumber already exists");

        var t = new ParentTicketDetails();
        t.setTicketNumber(req.getTicketNumber().trim());
        t.setTicketType(req.getTicketType());
        t.setDevType(req.getDevType());
        t.setReturned(Boolean.FALSE);
        t.setReturnedNumber(0);
        t.setCreatedBy(creatorEmail);
        t.setCreatedOn(LocalDateTime.now());
        t.setUpdatedOn(LocalDateTime.now());
        return repo.save(t);
    }


    public ParentTicketDetails update(String ticketNumber, ParentTicketDetails patch) {
        var existing = repo.findByTicketNumberIgnoreCase(ticketNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ticket not found"));

        // Only update allowed fields (don’t let someone change ticketNumber here)
        if (patch.getTicketType() != null) existing.setTicketType(patch.getTicketType());
        existing.setReturned(patch.isReturned());
        existing.setReturnedNumber(patch.getReturnedNumber());
        existing.setUpdatedOn(LocalDateTime.now());
        if (patch.getUpdatedBy() != null) existing.setUpdatedBy(patch.getUpdatedBy());

        return repo.save(existing);
    }

    public void delete(String ticketNumber) {
        if (!repo.existsByTicketNumberIgnoreCase(ticketNumber)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ticket not found");
        }
        repo.deleteByTicketNumberIgnoreCase(ticketNumber); // cascades to children via FK
    }
}
