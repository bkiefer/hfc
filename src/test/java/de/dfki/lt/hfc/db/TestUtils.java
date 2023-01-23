package de.dfki.lt.hfc.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

import de.dfki.lt.hfc.Config;
import de.dfki.lt.hfc.Hfc;
import de.dfki.lt.hfc.db.rdfProxy.RdfProxy;
import de.dfki.lt.hfc.types.XsdAnySimpleType;

public class TestUtils {

  private static final String RESOURCE_DIR = "src/test/data/pal/";

  public static RdfProxy setupProxy(HfcDbHandler h) {
    RdfProxy _proxy = new RdfProxy(h);
    return _proxy;
  }

  public static void readConfig(Hfc hfc, String configFile) throws IOException {
    Yaml yaml = new Yaml();
    Map<String, Object> confs = yaml.load(new FileInputStream(new File(configFile)));

    Config c = Config.getInstance(configFile);
    @SuppressWarnings("unchecked")
    List<String> tuples = (List<String>)confs.get("tupleFiles");
    long now = System.currentTimeMillis();
    String nowxsd = XsdAnySimpleType.javaToXsd(now).toString();
    if (tuples != null) {
      for (String fileName : tuples) {
        hfc.uploadTuples(c, c.resolvePath(fileName).getPath(), null, nowxsd);
      }
    }
  }

  public static HfcDbHandler setupLocalHandler() {
    HfcDbHandler h = new HfcDbHandler(RESOURCE_DIR + "test.yml");
    try {
      readConfig(h._hfc, RESOURCE_DIR + "rifca.yml");
    } catch (IOException ex) {
      throw new RuntimeException(ex);
    }
    return h;
  }

}
