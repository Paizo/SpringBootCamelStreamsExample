package com.paizo.balance.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Entity
@Table(name = "persons")
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Person {

    @Id
    @Column(name = "id")
    @GeneratedValue
    private UUID id;

    @Version
    private Long version;

    @Column(name = "firstname", length = 50)
    private String firstName;

    @Column(name = "lastname", length = 50)
    private String lastName;

    @Column(name = "address", length = 50)
    private String address;

    @Column(name = "postcode", length = 10)
    private String postcode;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "balance")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private BigDecimal balance;

    @Column(name = "birthday")
    private LocalDate birthday;

    public String toString() {
        return "Person(id=" + this.getId() + ", version=" + this.getVersion() + ", firstName=" + this.getFirstName() + ", lastName=" + this.getLastName() + ", address=" + this.getAddress() + ", postcode=" + this.getPostcode() + ", phone=" + this.getPhone() + ", birthday=" + this.getBirthday() + ", balance=XXX)";
    }

}