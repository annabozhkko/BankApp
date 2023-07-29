package ru.ccfit.bozhko.models;

import javax.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "scoring")
public class Scoring{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "tariff_id")
    private Tariff tariff;

    @Column(name = "approval_status")
    private boolean approvalStatus;
}

