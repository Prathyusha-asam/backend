package com.demo.dms.service;

import com.demo.dms.entity.ChildEntryDetails;
import com.demo.dms.entity.ParentTicketDetails;
import com.demo.dms.entity.TicketDetailsEntry;
import com.demo.dms.repository.ParentTicketDetailsRepository;
import com.demo.dms.repository.TicketDetailsEntryRepository;
import com.demo.dms.security.AuthUtils;
import com.demo.dms.web.dto.AdminAndEntryDto;
import com.demo.dms.web.dto.WeeklyTrendPoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.WeekFields;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class EntryService {
    TicketDetailsEntryRepository repo;
    ParentTicketDetailsRepository parentRepo;
    EntryService(TicketDetailsEntryRepository repo, ParentTicketDetailsRepository parentRepo) {
        this.repo = repo;
        this.parentRepo = parentRepo;
    }

    public AdminAndEntryDto createTicket(AdminAndEntryDto req, String email) {
        if (req.getTicketNumber() == null || req.getTicketNumber().isBlank())
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "ticketNumber is required");

        if (parentRepo.existsByTicketNumberIgnoreCase(req.getTicketNumber()))
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.CONFLICT, "ticketNumber already exists");
        var parent = new ParentTicketDetails();
        parent.setTicketNumber(req.getTicketNumber());
        parent.setTicketType(req.getTicketType());
        parent.setDevType(req.getDevType());
        parent.setCreatedBy(email);
        parent.setCreatedOn(LocalDateTime.now());
        parent.setReturned(req.isReturned());
        parent.setReturnedNumber(req.getReturnedNumber());
        parentRepo.save(parent);

        List<ChildEntryDetails> entries = req.getEntries();
        for(ChildEntryDetails c : entries) {
            TicketDetailsEntry child = setTicketDetailsEntry(c, parent);
            child.setCreatedOn(LocalDateTime.now());
            child.setCreatedBy(email);
            child.setComments(c.getComments());
            repo.save(child);
        }

        return req;
    }

    public AdminAndEntryDto updateTicket(String ticketNumber, AdminAndEntryDto req) {
        var existing = parentRepo.findByTicketNumberIgnoreCase(ticketNumber)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(
                        org.springframework.http.HttpStatus.NOT_FOUND, "Ticket not found"));
        if (req.getTicketType() != null) existing.setTicketType(req.getTicketType());
        if (req.getDevType() != null) existing.setDevType(req.getDevType());
        if (req.getReturnedNumber() >0 ) existing.setReturnedNumber(req.getReturnedNumber());
        existing.setReturned(req.isReturned());
        existing.setUpdatedOn(LocalDateTime.now());
        existing.setUpdatedBy(AuthUtils.currentEmail());


        List<ChildEntryDetails> entries = req.getEntries();
        for(ChildEntryDetails c : entries) {
            TicketDetailsEntry child = repo.
                    findTopByTicket_TicketNumberIgnoreCaseAndDevTypeIgnoreCaseOrderByIdDesc
                            (req.getTicketNumber(), c.getDevType());
            child.setUpdatedOn(LocalDateTime.now());
            child.setUpdatedBy(AuthUtils.currentEmail());
            child.setTicket(existing);
            child.setDevType(c.getDevType());
            child.setAssignee(c.getAssignee());
            child.setEstimation(c.getEstimation());
            child.setStatus(c.getStatus());
            child.setStartDate(c.getStartDate());
            child.setEndDate(c.getEndDate());
            child.setUpdatedBy(AuthUtils.currentEmail());
            child.setUpdatedOn(LocalDateTime.now());
            child.setComments(c.getComments());
            repo.save(child);
        }
        parentRepo.save(existing);
        return req;
    }

    private static TicketDetailsEntry setTicketDetailsEntry(ChildEntryDetails c, ParentTicketDetails parent) {
        TicketDetailsEntry child = new TicketDetailsEntry();
        child.setTicket(parent);
        child.setDevType(c.getDevType());
        child.setAssignee(c.getAssignee());
        child.setEstimation(c.getEstimation());
        child.setStatus(c.getStatus());
        child.setStartDate(c.getStartDate());
        child.setEndDate(c.getEndDate());
        return child;
    }

    public List<WeeklyTrendPoint> getWeeklyTrends(int year, int month) {
        List<Object[]> rows = repo.findWeeklyTotalsAndReturns(year, month);

        return rows.stream()
                .map(r -> new WeeklyTrendPoint(
                        "Week " + ((Number) r[0]).intValue(),   // period as "Week 1", "Week 2", etc.
                        ((Number) r[1]).intValue(),            // total tickets
                        ((Number) r[2]).intValue()             // total returned
                ))
                .collect(Collectors.toList());
    }

    public long countStatus(String status) {
        return repo.countStatusByRequirement(status);
    }
}
