package com.demo.dms.web.dto;

public class TicketViewDtos {

  public record ParentDTO(
      Long id, String ticketNumber, String ticketType, String devType,
      String createdBy, String createdOn, String updatedBy, String updatedOn
  ) {}

  public record ChildDTO(
      Long id, String ticketNumber, String status, Integer estimation,
      Boolean returned, Integer returnNumber, String assignee,
      String startDate, String endDate,
      String createdBy, String createdOn, String updatedBy, String updatedOn
  ) {}

  public record AggregateView(
      ParentDTO parent,
      ChildDTO backend,
      ChildDTO frontend,
      ChildDTO qa
  ) {}

  // create/update
  public record TicketCreateRequest(
      String ticketNumber, String ticketType, String devType
  ) {}

  public record ParentUpdateDTO(
      String ticketType, String devType, String completeDate
  ) {}

  public record ChildUpsertDTO(
      String status, Integer estimation, Boolean returned, Integer returnNumber,
      String assignee, String startDate, String endDate
  ) {}

  // admin composite update
  public record AdminCompositeUpdateDTO(
      ParentUpdateDTO parent,
      ChildUpsertDTO backend,
      ChildUpsertDTO frontend,
      ChildUpsertDTO qa
  ) {}
}
