package com.demo.dms.repository;

import com.demo.dms.entity.Log;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogRepository extends JpaRepository<Log, Long> {
    Log findByTicketNumber(String ticketNumber);
}
