package com.example.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "reservation")
public class Reservation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "slot_id", nullable = false)
    private AvailableSlot slot;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public AvailableSlot getSlot() {
        return slot;
    }

    public void setSlot(AvailableSlot slot) {
        this.slot = slot;
    }
}
