package com.demo.dms.repository;

import com.demo.dms.entity.ParentTicketDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParentTicketDetailsRepository extends JpaRepository<ParentTicketDetails, Long> {
    Optional<ParentTicketDetails> findByTicketNumberIgnoreCase(String ticketNumber);
    boolean existsByTicketNumberIgnoreCase(String ticketNumber);
    void deleteByTicketNumberIgnoreCase(String ticketNumber);
    Page<ParentTicketDetails> findAll(Pageable pageable);
    ParentTicketDetails save(ParentTicketDetails parentTicketDetails);

    long countByReturnedTrue();
}
