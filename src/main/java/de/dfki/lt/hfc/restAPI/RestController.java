package de.dfki.lt.hfc.restAPI;

import java.util.concurrent.atomic.AtomicLong;

import de.dfki.lt.hfc.BindingTable;
import de.dfki.lt.hfc.Hfc;
import de.dfki.lt.hfc.QueryParseException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

@org.springframework.web.bind.annotation.RestController
public class RestController {

  private final Hfc hfc;
  private final AtomicLong counter = new AtomicLong();

  public RestController(Hfc hfc) {
    this.hfc = hfc;
  }

  @RequestMapping("/query")
  public BindingTable query(@RequestParam(value="query") String queryString) {
    try {
      return hfc.executeQuery(queryString);
    } catch (QueryParseException e) {
      e.printStackTrace();
      throw new ResponseStatusException(
              HttpStatus.NOT_FOUND, "InvalidQuery", e);
    }
  }

  @RequestMapping("/shutdown")
  public void shoutdown(@RequestParam(value="exit", defaultValue = "true") boolean exit){
    if (exit)
      hfc.shutdown();
    else
      hfc.shutdownNoExit();
  }

  @RequestMapping("/status")
  public String status(){
    return hfc.status();
  }


}
