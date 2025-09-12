package com.demo.dms.repository;

import com.demo.dms.entity.TicketDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TicketDetailsRepository extends JpaRepository<TicketDetails, Long> {
    List<TicketDetails> findByAssignee(String assignee);
}
