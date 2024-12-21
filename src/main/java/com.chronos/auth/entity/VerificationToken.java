package com.chronos.auth.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;


@Entity
@Getter
@Setter
@NoArgsConstructor
public class VerificationToken {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long tokenId;

  private String token;

  private Date expirationTime;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(nullable = false, name = "userId")
  private User user;

  public VerificationToken(String token, User user) {
    this.token = token;
    this.user = user;
    this.expirationTime = calculateExpirationTime();
  }

  public VerificationToken(String token) {
    this.token = token;
    this.expirationTime = calculateExpirationTime();
  }

  public Date calculateExpirationTime() {
    long expirationTimeInMinutes = 10;
    long expirationTimeInMilliseconds = expirationTimeInMinutes * 60 * 1000;

    // Check if expirationTimeInMilliseconds is calculated correctly
    System.out.println("Expiration time in milliseconds: " + expirationTimeInMilliseconds);

    Date expirationDate = new Date(System.currentTimeMillis() + expirationTimeInMilliseconds);

    // Check if expirationDate is being calculated correctly
    System.out.println("Expiration date: " + expirationDate);
    return expirationDate;
  }

}
