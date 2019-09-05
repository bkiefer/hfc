package de.dfki.lt.hfc.restAPI;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import de.dfki.lt.hfc.BindingTable;
import de.dfki.lt.hfc.Hfc;
import de.dfki.lt.hfc.QueryParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@org.springframework.web.bind.annotation.RestController
public class RestController {

  protected final Hfc hfc;
  private final AtomicLong counter = new AtomicLong();

  public RestController(Hfc hfc) {
    this.hfc = hfc;
  }

  @RequestMapping(value = "/query", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BindingTable> query(@RequestParam(value="query") String queryString) {
    try {
      return new ResponseEntity<BindingTable>(hfc.executeQuery(queryString), HttpStatus.OK);
    } catch (QueryParseException e) {
      e.printStackTrace();
      throw new ResponseStatusException(
              HttpStatus.NOT_FOUND, "InvalidQuery", e);
    }
  }

  @RequestMapping(value = "/shutdown", produces = MediaType.APPLICATION_JSON_VALUE)
  public void shoutdown(@RequestParam(value="exit", defaultValue = "true") boolean exit){
    if (exit)
      hfc.shutdown();
    else
      hfc.shutdownNoExit();
  }

  @RequestMapping(value = "/status", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<String> status(){
    return new ResponseEntity<>(hfc.status(), HttpStatus.OK);
  }

  @PostMapping(value = "/table", consumes = MediaType.APPLICATION_JSON_VALUE,  produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<Integer> addTuples(@RequestBody List<List<String>> tuples){
    return new ResponseEntity<Integer>(hfc.addTuples(tuples, null,null), HttpStatus.OK);
  }

}
