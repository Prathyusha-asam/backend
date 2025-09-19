package com.demo.dms.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "QA_ENTRY", uniqueConstraints = {
    @UniqueConstraint(name = "UK_QA_TN", columnNames = "TICKET_NUMBER")
})
@Getter @Setter
public class QaEntry extends AbstractLaneEntry {
  public QaEntry() {

  }
}
