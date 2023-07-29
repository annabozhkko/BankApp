package ru.ccfit.bozhko.models;

import javax.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "payment_types")
public class PaymentType{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "commission")
    private int commission;
}
