package com.demo.dms.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "BACKEND_ENTRY", uniqueConstraints = {
    @UniqueConstraint(name = "UK_BACKEND_TN", columnNames = "TICKET_NUMBER")
})
@Getter @Setter
public class BackendEntry extends AbstractLaneEntry {
    public BackendEntry() {

    }
}
