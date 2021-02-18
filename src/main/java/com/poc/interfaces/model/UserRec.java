package com.poc.interfaces.model;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 * @author sravantatikonda
 */
@Data
@Entity
@Table(name = "USER_RECS", schema = "poc_schm")
public class UserRec implements Serializable {

  private static final long serialVersionUID = -3707819200740751550L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "USER_ID", nullable = false)
  private Integer userId;

  @Column(name = "FIRST_NAME", nullable = false)
  private String firstName;

  @Column(name = "LAST_NAME")
  private String lastName;

  @Column(name = "EMAIL", nullable = false)
  private String email;

  @Column(name = "city", nullable = false)
  private String city;

  @Column(name = "state", nullable = false)
  private String state;

  @Column(name = "country", nullable = false)
  private String country;

  @Column(name = "zipcode", nullable = false)
  private String zipCode;

  @Column(name = "date_of_birth", nullable = false)
  private Date dateOfBirth;
}
