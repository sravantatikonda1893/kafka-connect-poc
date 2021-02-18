package com.poc.interfaces.util.input;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * @author sravantatikonda
 */
@Data
@JsonIgnoreProperties
public class AddressUnit {

  private String address1;
  private String address2;
  private String city;
  private String state;
  private String postalCode;
  private Coordinates coordinates;

}
