package ru.ccfit.bozhko.models;

import javax.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tariffs")
public class Tariff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "sum")
    private double sum;

    @Column(name = "rate")
    private int rate;

    @Column(name = "period")
    private int period;
}
