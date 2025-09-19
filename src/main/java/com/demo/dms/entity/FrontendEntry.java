package com.demo.dms.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "FRONTEND_ENTRY", uniqueConstraints = {
    @UniqueConstraint(name = "UK_FRONTEND_TN", columnNames = "TICKET_NUMBER")
})
@Getter @Setter
public class FrontendEntry extends AbstractLaneEntry {
  public FrontendEntry() {

  }
}
