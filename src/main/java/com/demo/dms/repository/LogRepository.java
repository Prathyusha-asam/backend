package com.demo.dms.repository;

import com.demo.dms.entity.Log;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {
    Log findByTicketNumber(String ticketNumber);
    Page<Log> findByCreatedByStartingWithAndTicketNumberStartingWith
            (String createdBy, String ticketNumber, Pageable pageable);
    Page<Log> findByCreatedByIgnoreCase(String createdBy, Pageable pageable);
    Page<Log> findByTicketNumberStartingWithIgnoreCase(String ticketNumber, Pageable pageable);
}
