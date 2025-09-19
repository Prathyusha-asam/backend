package com.demo.dms.entity;

import jakarta.persistence.*;
import lombok.*;

@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractLaneEntry {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "ID")
  private Long id;

  // Join by TICKET_NUMBER string (not ID)
  @ManyToOne(optional = false)
  @JoinColumn(
      name = "TICKET_NUMBER",
      referencedColumnName = "TICKET_NUMBER",
      foreignKey = @ForeignKey(name = "FK_TN_TO_PARENT")
  )
  private TicketDetails ticket;

  @Column(name = "STATUS")
  private String status;

  @Column(name = "ESTIMATION")
  private Integer estimation;

  @Column(name = "IS_RETURNED")
  private Boolean returned;

  @Column(name = "RETURN_NUMBER")
  private Integer returnNumber;

  @Column(name = "ASSIGNEE")
  private String assignee;

  @Column(name = "START_DATE")
  private String startDate;

  @Column(name = "END_DATE")
  private String endDate;

  @Column(name = "CREATED_BY")
  private String createdBy;

  @Column(name = "CREATED_ON")
  private String createdOn;

  @Column(name = "UPDATED_BY")
  private String updatedBy;

  @Column(name = "UPDATED_ON")
  private String updatedOn;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public TicketDetails getTicket() {
    return ticket;
  }

  public void setTicket(TicketDetails ticket) {
    this.ticket = ticket;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public Integer getEstimation() {
    return estimation;
  }

  public void setEstimation(Integer estimation) {
    this.estimation = estimation;
  }

  public Boolean getReturned() {
    return returned;
  }

  public void setReturned(Boolean returned) {
    this.returned = returned;
  }

  public Integer getReturnNumber() {
    return returnNumber;
  }

  public void setReturnNumber(Integer returnNumber) {
    this.returnNumber = returnNumber;
  }

  public String getAssignee() {
    return assignee;
  }

  public void setAssignee(String assignee) {
    this.assignee = assignee;
  }

  public String getStartDate() {
    return startDate;
  }

  public void setStartDate(String startDate) {
    this.startDate = startDate;
  }

  public String getEndDate() {
    return endDate;
  }

  public void setEndDate(String endDate) {
    this.endDate = endDate;
  }

  public String getCreatedBy() {
    return createdBy;
  }

  public void setCreatedBy(String createdBy) {
    this.createdBy = createdBy;
  }

  public String getCreatedOn() {
    return createdOn;
  }

  public void setCreatedOn(String createdOn) {
    this.createdOn = createdOn;
  }

  public String getUpdatedBy() {
    return updatedBy;
  }

  public void setUpdatedBy(String updatedBy) {
    this.updatedBy = updatedBy;
  }

  public String getUpdatedOn() {
    return updatedOn;
  }

  public void setUpdatedOn(String updatedOn) {
    this.updatedOn = updatedOn;
  }
}
