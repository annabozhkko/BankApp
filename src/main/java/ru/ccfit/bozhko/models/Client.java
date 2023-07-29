package ru.ccfit.bozhko.models;

import javax.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "clients")
public class Client{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "full_name")
    private String fullname;

    @Column(name = "passport_details")
    private String passportDetails;
}
