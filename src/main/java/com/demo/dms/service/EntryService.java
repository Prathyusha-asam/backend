package com.demo.dms.service;

import com.demo.dms.entity.ChildEntryDetails;
import com.demo.dms.entity.ParentTicketDetails;
import com.demo.dms.entity.TicketDetailsEntry;
import com.demo.dms.repository.ParentTicketDetailsRepository;
import com.demo.dms.repository.TicketDetailsEntryRepository;
import com.demo.dms.security.AuthUtils;
import com.demo.dms.web.dto.AdminAndEntryDto;
import com.demo.dms.web.dto.TicketStatus;
import com.demo.dms.web.dto.WeeklyTrendPoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.time.temporal.WeekFields;
import java.util.*;
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

    public List<WeeklyTrendPoint> getWeeklyTrends() {
        List<Object[]> returnedDataFromDb = repo.findWeeklyReturnCountsInCurrentMonthForAllDevTypes();

        Map<Integer, Integer> returnedResults = returnedDataFromDb.stream()
                .collect(Collectors.toMap(
                        row -> ((Number) row[0]).intValue(), // weekOfMonth
                        row -> ((Number) row[1]).intValue()  // returnCount
                ));

        // 2. Fetch data for TOTAL tickets.
        List<Object[]> totalDataFromDb = repo.findWeeklyTotalCountsInCurrentMonthForAllDevTypes();
        Map<Integer, Integer> totalResults = totalDataFromDb.stream()
                .collect(Collectors.toMap(
                        row -> ((Number) row[0]).intValue(), // weekOfMonth
                        row -> ((Number) row[1]).intValue()  // totalCount
                ));

        // 3. Prepare for iteration and formatting.
        List<WeeklyTrendPoint> completeWeeklyTrend = new ArrayList<>();
        YearMonth currentYearMonth = YearMonth.now();
        LocalDate firstDayOfMonth = currentYearMonth.atDay(1);
        LocalDate lastDayOfMonth = currentYearMonth.atEndOfMonth();

        LocalDate currentDay = firstDayOfMonth;

        // 4. Loop through each week of the month to build the labels and aggregate the data.
        while (currentDay.isBefore(lastDayOfMonth) || currentDay.isEqual(lastDayOfMonth)) {
            LocalDate weekStartDate = currentDay;
            LocalDate weekEndDate = currentDay.with(DayOfWeek.SUNDAY);

            if (weekEndDate.isAfter(lastDayOfMonth)) {
                weekEndDate = lastDayOfMonth;
            }

            int weekNumber = weekStartDate.get(WeekFields.of(Locale.US).weekOfMonth());
            int returnedCount = returnedResults.getOrDefault(weekNumber, 0);
            int totalCount = totalResults.getOrDefault(weekNumber, 0);

            String monthShortName = weekStartDate.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            String label = String.format("(%s %d-%d)",
                    monthShortName,
                    weekStartDate.getDayOfMonth(),
                    weekEndDate.getDayOfMonth());

            // 5. Create the DTO and add it to our final list.
            completeWeeklyTrend.add(new WeeklyTrendPoint(label, totalCount, returnedCount));

            currentDay = weekEndDate.plusDays(1);
        }

        return completeWeeklyTrend;
    }

    public List<TicketStatus> countStatus() {
        List<Object[]> totalCount = repo.countAllStatus();
        return totalCount.stream()
                .map(row -> new TicketStatus(
                        row[0] != null ? row[0].toString() : "UNKNOWN",
                        Math.toIntExact((Long) row[1])
                ))

                .collect(Collectors.toList());

    }

}
