package com.example.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name="picture")
public class Picture implements java.io.Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Column(name="data", nullable=false, columnDefinition="BYTEA")
    private byte[] data;

    public Picture() {}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] picture) {
        this.data = picture;
    }
}

