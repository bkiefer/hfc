package de.dfki.lt.hfc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.yaml.snakeyaml.Yaml;

public class TestConfig extends Config {

  private TestConfig(Map<String, Object> configs) {
    super(configs, null);
  }

  private TestConfig(Map<String, Object> configs, File f) {
    super(configs, f);
  }

  @SuppressWarnings("unchecked")
  public static TestConfig getInstance(InputStream in) {
    Yaml yaml = new Yaml();
    return new TestConfig((Map<String, Object>) yaml.load(in));
  }

  @SuppressWarnings("unchecked")
  public static TestConfig getInstance(String fileName) throws FileNotFoundException {
    return new TestConfig(
        (Map<String, Object>) new Yaml().load(new FileInputStream(fileName)),
        new File(fileName).getParentFile());
  }

  /**
   * the DEFAULT settings basically address the RDF triple case without equivalence class reduction
   *
   * @return an instance of Config containing the default settings
   */
  public static TestConfig getDefaultConfig() throws IOException {
    InputStream in = Config.class.getResourceAsStream("/DefaultConfig.yml");
    return getInstance(in);
  }

  /**
   * these DEFAULT settings address the RDF triple case with equivalence class
   * reduction
   *
   * @return an instance of Config containing the default settings
   */
  public static TestConfig getDefaultEqRedConfig() throws IOException {
    InputStream in = Config.class.getResourceAsStream("/DefaultEqRedConfig.yml");
    return getInstance(in);
  }

  public static TestConfig getPalDomConfig() throws IOException {
    TestConfig c = getDefaultConfig();
    c.addNamespace("pal", "http://www.lt-world.org/pal.owl#");
    c.addNamespace("dom", "http://www.lt-world.org/dom.owl#");
    return c;
  }

  @SuppressWarnings("unchecked")
  public void addNamespace(String shortNs, String longNs) {
    HashMap<String, String> shortToLong =
        (HashMap<String, String>) configs.get(NAMESPACES);
    shortToLong.put(shortNs, longNs);
  }

  public void put(String key, Object value) {
    configs.put(key, value);
  }
}
