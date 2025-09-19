package com.demo.dms.service;

import com.demo.dms.entity.TicketDetails;
import com.demo.dms.repository.TicketDetailsRepository;
import com.demo.dms.web.dto.TicketStats;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
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
            td.setCreatedOn(String.valueOf(java.time.Instant.now()));
            return ticketDetailsRepository.save(td);
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "ticketNumber already exists");
        }
    }


    @Transactional
    public TicketDetails updateFull(String ticketNumber, TicketDetails body) {
        TicketDetails existing = ticketDetailsRepository.findByTicketNumberIgnoreCase(ticketNumber)
                .orElseThrow(() -> new EntityNotFoundException("Ticket " + ticketNumber + " not found"));
        boolean isReturned = false;
        if("Ready-to-Dev".equalsIgnoreCase(body.getStatus()) && "Testing".equalsIgnoreCase(existing.getStatus())) {
            isReturned = true;
        }
        existing.setId(body.getId());
        existing.setTicketNumber(body.getTicketNumber());
        existing.setTicketType(body.getTicketType());
        existing.setDevType(body.getDevType());
        existing.setStatus(body.getStatus());
        existing.setEstimation(body.getEstimation());

        if(existing.isReturned()) {
            existing.setReturnNumber(body.getReturnNumber()+1);
        } else {
            existing.setReturnNumber(body.getReturnNumber());
        }

        existing.setReturned(isReturned);
        existing.setAssignee(body.getAssignee());
        existing.setStartDate(body.getStartDate());
        existing.setCompleteDate(body.getCompleteDate());
        existing.setCreatedBy(body.getCreatedBy());
        existing.setCreatedOn(body.getCreatedOn());
        existing.setUpdatedBy(body.getAssignee());
        existing.setUpdatedOn(String.valueOf(java.time.Instant.now()));

        return ticketDetailsRepository.save(existing);
    }

    @Transactional(readOnly = true)
    public TicketStats getStats() {
        long total = ticketDetailsRepository.count();
        long returned = ticketDetailsRepository.countByReturnedTrue(); // or countByReturnedTrue() / custom query
        return new TicketStats(total, returned);
    }

    @Transactional
    public void deleteByIds(List<Long> ids) {
        ticketDetailsRepository.deleteAllByIdInBatch(ids);
    }

    public void delete(TicketDetails existing) {
        ticketDetailsRepository.delete(existing);
    }

    public Page<TicketDetails> search(String assignee, String ticketNumber, Pageable pageable) {

        boolean hasAssignee = StringUtils.hasText(assignee);
        boolean hasTicket = StringUtils.hasText(ticketNumber);

        Page<TicketDetails> page;

        if (hasAssignee && hasTicket) {
            page = ticketDetailsRepository.findByAssigneeIgnoreCaseAndTicketNumberStartingWithIgnoreCase(
                    assignee.trim(), ticketNumber.trim(), pageable);
        } else if (hasAssignee) {
            page = ticketDetailsRepository.findByAssigneeIgnoreCase(assignee.trim(), pageable);
        } else if (hasTicket) {
            page = ticketDetailsRepository.findByTicketNumberStartingWithIgnoreCase(ticketNumber.trim(), pageable);
        } else {
            page = ticketDetailsRepository.findAll(pageable);
        }

        return page;
    }

    public List<TicketDetails> suggest( String normPrefix, String normAssignee, Pageable page) {
        return ticketDetailsRepository.suggestTickets(normPrefix, normAssignee, page);
    }
}
