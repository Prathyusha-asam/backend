package com.demo.dms.service;

import com.demo.dms.entity.TicketDetails;
import com.demo.dms.repository.TicketDetailsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class TicketDetailsService {

    private final TicketDetailsRepository repo;

    @Autowired
    public TicketDetailsService(TicketDetailsRepository repo) {
        this.repo = repo;
    }

    public List<TicketDetails> getAllTicketDetails() {
        return repo.findAll();
    }

    public Optional<TicketDetails> getTicketById(long id) {
        return repo.findById(id);
    }

    public List<TicketDetails> getTicketByAssignee(String assignee) {
        return repo.findByAssignee(assignee);
    }

    @Transactional
    public TicketDetails createTicketDetails(TicketDetails ticketDetails) {
        // optional: normalize inputs (trim, lower-case email, etc.)
        if (ticketDetails.getAssignee() != null) {
            ticketDetails.setAssignee(ticketDetails.getAssignee().trim());
        }
        return repo.save(ticketDetails);
    }

    @Transactional
    public void deleteByIds(List<Long> ids) {
        repo.deleteAllByIdInBatch(ids); // efficient bulk delete
    }
}
