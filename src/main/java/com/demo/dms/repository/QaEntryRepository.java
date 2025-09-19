package com.demo.dms.repository;

import com.demo.dms.entity.QaEntry;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QaEntryRepository extends JpaRepository<QaEntry, Long> {
    Optional<QaEntry> findByTicket_TicketNumberIgnoreCase(String ticketNumber);
    Optional<QaEntry> findByIdAndTicket_TicketNumberIgnoreCase(Long id, String ticketNumber);
    Optional<QaEntry> findTopByTicket_TicketNumberIgnoreCaseOrderByIdDesc(String ticketNumber);

    Page<QaEntry> findByAssigneeIgnoreCase(String assignee, Pageable pageable);

    void deleteByTicket_TicketNumberIgnoreCase(String ticketNumber);

}
