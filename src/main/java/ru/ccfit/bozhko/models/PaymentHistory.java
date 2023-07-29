package ru.ccfit.bozhko.models;

import javax.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "payment_history")
public class PaymentHistory{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "credit_id")
    private Credit credit;

    @ManyToOne
    @JoinColumn(name = "payment_type_id")
    private PaymentType paymentType;

    @Column(name = "date")
    private Date date;

    @Column(name = "sum_loan")
    private double sumLoan;

    @Column(name = "sum_percent")
    private double sumPercent;
}
