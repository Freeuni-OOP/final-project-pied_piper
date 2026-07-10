package com.lecturboxd.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/**
 * EN: Pending signup OTP — holds email code plus staged name/password until verified.
 * KA: მოლოდინში მყოფი რეგისტრაციის OTP — ინახავს ელფოსტის კოდს და დროებით სახელს/პაროლს ვერიფიკაციამდე.
 */
@Entity
@Table(name = "verification_codes")
public class VerificationCode {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // EN: Email the OTP was sent to | KA: ელფოსტა, რომელზეც OTP გაიგზავნა
    @Column(nullable = false)
    private String email;

    // EN: Six-digit one-time code | KA: ექვსნიშნა ერთჯერადი კოდი
    @Column(nullable = false, length = 6)
    private String code;

    // EN: Instant after which the code is invalid | KA: მომენტი, რის შემდეგაც კოდი არასწორია
    @Column(nullable = false)
    private Instant expiresAt;

    // EN: Whether this code was already consumed | KA: უკვე გამოყენებულია თუ არა ეს კოდი
    @Column(nullable = false)
    private boolean used = false;

    // EN: Staged display name applied on successful verify | KA: დროებითი საჩვენებელი სახელი, რომელიც წარმატებულ ვერიფიკაციაზე გამოიყენება
    @Column(nullable = false)
    private String name;

    // EN: Staged password hash applied on successful verify | KA: დროებითი პაროლის ჰეში, რომელიც წარმატებულ ვერიფიკაციაზე გამოიყენება
    @Column(nullable = false)
    private String passwordHash;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(Instant expiresAt) {
        this.expiresAt = expiresAt;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
}
