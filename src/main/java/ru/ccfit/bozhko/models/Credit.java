package ru.ccfit.bozhko.models;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "issued_credits")
public class Credit{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @ManyToOne
    @JoinColumn(name = "tariff_id")
    private Tariff tariff;

    @Column(name = "date")
    private Date date;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "maturity_date")
    private Date maturityDate;
}
