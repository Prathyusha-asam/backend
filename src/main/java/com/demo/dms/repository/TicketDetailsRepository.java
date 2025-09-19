package com.demo.dms.repository;

import com.demo.dms.entity.TicketDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface TicketDetailsRepository extends JpaRepository<TicketDetails, Long> {
    Page<TicketDetails> findByAssignee(String assignee, Pageable pageable);

    Optional<TicketDetails> findByTicketNumberIgnoreCase(String ticketNumber);

    boolean existsByTicketNumberIgnoreCase(String ticketNumber);

    long count();

    long countByReturnedTrue();

    void deleteByTicketNumberIgnoreCase(String ticketNumber);

}
