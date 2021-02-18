package com.poc.interfaces.util.input;

import java.io.Serializable;
import lombok.Data;

/**
 * @author sravantatikonda
 */
@Data
public class Coordinates implements Serializable {

  private static final long serialVersionUID = 1L;

  private String lat;
  private String lng;
}
