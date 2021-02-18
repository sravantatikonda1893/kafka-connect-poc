package com.poc.interfaces.controller;

import com.poc.interfaces.util.XmlRecordsGenerator;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author sravantatikonda
 */
@RestController
@Slf4j
public class GenController {

  @Autowired
  private XmlRecordsGenerator xmlRecordsGenerator;

  /**
   * An API to generate user records in DB and order records in XML files
   *
   * @param usersCount
   * @param filesCount
   * @param recordCount
   * @return
   * @throws Exception
   */
  @ApiOperation(value = "load", notes = "")
  @GetMapping(value = "/loader/{usersCount}/{filesCount}/{recordCount}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Boolean> load(@PathVariable Integer usersCount,
      @PathVariable Integer filesCount, @PathVariable Integer recordCount) throws Exception {
    xmlRecordsGenerator.loadCSVValues(
        "/Users/sravantatikonda/POC/kafka-connect/src/main/resources/loaderinput/");
    xmlRecordsGenerator.genXmlFiles(filesCount, recordCount);
    xmlRecordsGenerator.loadUserTable(usersCount);
    log.info("Successfully created");
    return new ResponseEntity<>(true, HttpStatus.OK);
  }
}
