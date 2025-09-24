package com.demo.dms.repository;

import com.demo.dms.entity.TicketReturn;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketReturnRepository extends JpaRepository<TicketReturn, Long> {
    Page<TicketReturn> findByTicketEntryId(long id, Pageable page);
}
