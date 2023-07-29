package ru.ccfit.bozhko.models;

import javax.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "payment_schedule")
public class PaymentSchedule{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "credit_id")
    private Credit credit;

    @Column(name = "date")
    private Date date;

    @Column(name = "sum_loan")
    private double sumLoan;

    @Column(name = "sum_percent")
    private double sumPercent;
}
