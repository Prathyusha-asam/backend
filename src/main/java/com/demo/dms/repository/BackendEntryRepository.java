package com.demo.dms.repository;

import com.demo.dms.entity.BackendEntry;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BackendEntryRepository extends JpaRepository<BackendEntry, Long> {
    Optional<BackendEntry> findByTicket_TicketNumberIgnoreCase(String ticketNumber);
    Optional<BackendEntry> findByIdAndTicket_TicketNumberIgnoreCase(Long id, String ticketNumber);
    Optional<BackendEntry> findTopByTicket_TicketNumberIgnoreCaseOrderByIdDesc(String ticketNumber);


    Page<BackendEntry> findByAssigneeIgnoreCase(String assignee, Pageable pageable);

    void deleteByTicket_TicketNumberIgnoreCase(String ticketNumber);

}
