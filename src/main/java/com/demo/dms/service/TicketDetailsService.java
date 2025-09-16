package com.demo.dms.service;

import com.demo.dms.entity.TicketDetails;
import com.demo.dms.repository.TicketDetailsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TicketDetailsService {

    private final TicketDetailsRepository ticketDetailsRepository;

    @Autowired
    public TicketDetailsService(TicketDetailsRepository ticketDetailsRepository) {
        this.ticketDetailsRepository = ticketDetailsRepository;
    }

    public Page<TicketDetails> getAllTicketDetails(Pageable pageable) {
        return ticketDetailsRepository.findAll(pageable);
    }
    
    public Optional<TicketDetails> getDetailsByTicketNumber(String ticketNumber) {
        return ticketDetailsRepository.findByTicketNumberIgnoreCase(ticketNumber);
    }

    public Page<TicketDetails> getTicketByAssignee(String assignee, Pageable pageable) {
        return ticketDetailsRepository.findByAssignee(assignee, pageable);
    }

    @Transactional
    public TicketDetails createTicketDetails(TicketDetails td) {
        // normalize + validate
        String tn = td.getTicketNumber() == null ? "" : td.getTicketNumber().trim();
        if (tn.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ticketNumber is required");
        }

        if (ticketDetailsRepository.existsByTicketNumberIgnoreCase(tn)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "ticketNumber already exists");
        }

        td.setTicketNumber(tn);
        if (td.getAssignee() != null) {
            td.setAssignee(td.getAssignee().trim());
        }

        try {
            return ticketDetailsRepository.save(td);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "ticketNumber already exists");
        }
    }


    @Transactional
    public TicketDetails updateFull(String ticketNumber, TicketDetails body) {
        TicketDetails existing = ticketDetailsRepository.findByTicketNumberIgnoreCase(ticketNumber)
                .orElseThrow(() -> new EntityNotFoundException("Ticket " + ticketNumber + " not found"));

        existing.setTicketNumber(body.getTicketNumber());
        existing.setTicketType(body.getTicketType());
        existing.setDevType(body.getDevType());
        existing.setStatus(body.getStatus());
        existing.setEstimation(body.getEstimation());
        existing.setReturned(body.isReturned());
        existing.setReturnNumber(body.getReturnNumber());
        existing.setAssignee(body.getAssignee());
        existing.setStartDate(body.getStartDate());
        existing.setCompleteDate(body.getCompleteDate());
        existing.setCreatedBy(body.getCreatedBy());
        existing.setCreatedOn(body.getCreatedOn());
        existing.setUpdatedBy(body.getAssignee());
        existing.setUpdatedOn(String.valueOf(java.time.Instant.now()));

        return ticketDetailsRepository.save(existing);
    }

    @Transactional
    public void deleteByIds(List<Long> ids) {
        ticketDetailsRepository.deleteAllByIdInBatch(ids);
    }

}
