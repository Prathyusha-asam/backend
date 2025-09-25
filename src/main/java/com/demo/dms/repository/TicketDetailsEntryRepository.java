package com.demo.dms.repository;

import com.demo.dms.entity.TicketDetailsEntry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TicketDetailsEntryRepository extends JpaRepository<TicketDetailsEntry, Long> {

    // by parent ticket number
    List<TicketDetailsEntry> findByTicket_TicketNumberIgnoreCase(String ticketNumber);

    // by parent ticket number + dev type
    List<TicketDetailsEntry> findByTicket_TicketNumberIgnoreCaseAndDevTypeIgnoreCase(String ticketNumber, String devType);
    TicketDetailsEntry findTopByTicket_TicketNumberIgnoreCaseAndDevTypeIgnoreCaseOrderByIdDesc(String ticketNumber, String devType);

    // helpful for search screens
    Page<TicketDetailsEntry> findByAssigneeIgnoreCase(String assignee, Pageable pageable);
    Page<TicketDetailsEntry> findByTicket_TicketNumberStartingWithIgnoreCase(String prefix, Pageable pageable);
    Page<TicketDetailsEntry> findByAssigneeIgnoreCaseAndTicket_TicketNumberStartingWithIgnoreCase(String assignee, String prefix, Pageable pageable);
    Page<TicketDetailsEntry> findByTicket_TicketNumberStartingWithIgnoreCaseAndAssigneeIgnoreCase(String prefix, String assignee, Pageable pageable);
    Optional<TicketDetailsEntry> findById(long id);

    @Query("SELECT COUNT(c) FROM TicketDetailsEntry c WHERE c.devType = 'Frontend' AND c.returned = true")
    long countFrontendReturns();

    @Query("SELECT COUNT(c) FROM TicketDetailsEntry c WHERE c.devType = 'Backend' AND c.returned = true")
    long countBackendReturns();
}
