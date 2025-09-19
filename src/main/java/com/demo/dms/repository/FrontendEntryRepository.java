package com.demo.dms.repository;

import com.demo.dms.entity.FrontendEntry;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FrontendEntryRepository extends JpaRepository<FrontendEntry, Long> {
    Optional<FrontendEntry> findByTicket_TicketNumberIgnoreCase(String ticketNumber);
    Optional<FrontendEntry> findByIdAndTicket_TicketNumberIgnoreCase(Long id, String ticketNumber);
    Optional<FrontendEntry> findTopByTicket_TicketNumberIgnoreCaseOrderByIdDesc(String ticketNumber);

    Page<FrontendEntry> findByAssigneeIgnoreCase(String assignee, Pageable pageable);
    void deleteByTicket_TicketNumberIgnoreCase(String ticketNumber);
}
