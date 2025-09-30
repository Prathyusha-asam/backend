package com.demo.dms.repository;

import com.demo.dms.entity.TicketDetailsEntry;
import com.demo.dms.web.dto.WeeklyTrendPoint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("SELECT c FROM TicketDetailsEntry c WHERE c.devType = 'Frontend' AND c.returned = true")
    List<TicketDetailsEntry> returnDetailsForFrontend();

    @Query("SELECT c FROM TicketDetailsEntry c WHERE c.devType = 'Backend' AND c.returned = true")
    List<TicketDetailsEntry> returnDetailsForBackend();

    @Query(value = """
        SELECT
            w.wk AS week,
            COALESCE(a.total, 0) AS totalTickets,
            COALESCE(a.returned, 0) AS totalReturned
        FROM (
            SELECT 1 AS wk UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6
        ) w
        LEFT JOIN (
            SELECT
                (WEEK(t.Created_On, 1) - WEEK(DATE_FORMAT(t.Created_On, '%Y-%m-01'), 1) + 1) AS weekOfMonth,
                COUNT(*) AS total,
                SUM(CASE WHEN t.Returned = 1 THEN 1 ELSE 0 END) AS returned
            FROM Ticket_Details_Entry t
            WHERE MONTH(t.Created_On) = :month
              AND YEAR(t.Created_On) = :year
            GROUP BY weekOfMonth
        ) a ON a.weekOfMonth = w.wk
        ORDER BY w.wk
        """, nativeQuery = true)
    List<Object[]> findWeeklyTotalsAndReturns(@Param("year") int year, @Param("month") int month);

    @Query(value = "SELECT COUNT(c) FROM TicketDetailsEntry c WHERE lower(c.status) = :status")
    long countStatusByRequirement(@Param("status") String status);
}
