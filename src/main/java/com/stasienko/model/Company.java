package com.stasienko.model;

import jakarta.persistence.*;
import org.hibernate.annotations.GenericGenerator;
import java.util.UUID;
@Entity
@Table(name="company", schema="public")
public class Company  implements java.io.Serializable {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID")
    @Column(updatable = false, nullable = false)
    private UUID id;
    private String name;
    private String description;
    public Company() {
    }
    public Company(UUID id, String name) {
        this.id = id;
        this.name = name;
    }
    public Company(UUID id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
    @Id
    @Column(name="id", unique=true, nullable=false)
    public UUID getId() {
        return this.id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    @Column(name="name", nullable=false, length=255)
    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    @Column(name="description")
    public String getDescription() {
        return this.description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}
