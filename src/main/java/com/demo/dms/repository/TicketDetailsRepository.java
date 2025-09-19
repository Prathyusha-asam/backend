package com.demo.dms.repository;

import com.demo.dms.entity.TicketDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;


public interface TicketDetailsRepository extends JpaRepository<TicketDetails, Long> {
    Page<TicketDetails> findByAssignee(String assignee, Pageable pageable);

    Optional<TicketDetails> findByTicketNumberIgnoreCase(String ticketNumber);

    boolean existsByTicketNumberIgnoreCase(String ticketNumber);

    long count();

    long countByReturnedTrue();

    void deleteByTicketNumberIgnoreCase(String ticketNumber);

    Page<TicketDetails> findByAssigneeIgnoreCase(String assignee, Pageable pageable);
    Page<TicketDetails> findByTicketNumberStartingWithIgnoreCase(String ticketNumberPrefix, Pageable pageable);
    Page<TicketDetails> findByAssigneeIgnoreCaseAndTicketNumberStartingWithIgnoreCase(
            String assignee, String ticketNumberPrefix, Pageable pageable);

    @Query("""
  select t
  from TicketDetails t
  where (:assignee is null or lower(t.assignee) = lower(:assignee))
    and (:prefix   is null or upper(t.ticketNumber) like upper(concat(:prefix, '%')))
  order by t.ticketNumber asc
""")
    java.util.List<TicketDetails> suggestTickets(
            @Param("prefix") String prefix,         // pass null when no q
            @Param("assignee") String assignee,     // pass null when no assignee
            Pageable pageable);

}
