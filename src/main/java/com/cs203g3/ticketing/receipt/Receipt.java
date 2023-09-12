package com.cs203g3.ticketing.receipt;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.cs203g3.ticketing.ticket.Ticket;
import com.cs203g3.ticketing.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Entity
public class Receipt {
    @Id 
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID uuid;

    @NotNull
    @ManyToOne
    @JoinColumn(name="userId")
    private User user;

    @NotNull
    @DecimalMin(value="0.0", inclusive=true)
    @Digits(integer=6,fraction=2)
    private BigDecimal amountPaid;

    @JsonIgnore
    @OneToMany(mappedBy="receipt")
    private List<Ticket> tickets;
}
