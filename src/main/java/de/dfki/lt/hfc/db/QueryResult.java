package de.dfki.lt.hfc.db;

import static de.dfki.lt.hfc.LiteralManager.isAtom;
import static de.dfki.lt.hfc.LiteralManager.splitUriNsName;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.dfki.lt.hfc.WrongFormatException;
import de.dfki.lt.hfc.types.AnyType;
import de.dfki.lt.hfc.types.XsdAnySimpleType;
import de.dfki.lt.loot.digraph.DiGraph;
import de.dfki.lt.loot.digraph.VertexListPropertyMap;
import de.dfki.lt.loot.digraph.VertexPropertyMap;
import de.dfki.lt.loot.digraph.io.GraphPrinterFactory;

public class QueryResult {
  private static final Logger logger = LoggerFactory.getLogger(QueryResult.class);

  public List<String> variables; // required
  public Table table; // required

  public QueryResult(List<String> v, List<List<String>> t) {
    variables = v;
    table = new Table(t);
  }

  public Table getTable() {
    return table;
  }

  public List<String> getVariables() {
    return variables;
  }

  public int getVariablesSize() {
    return variables.size();
  }

  public void saveToFile(Path filepath) throws IOException {
    try (Writer fw = new FileWriter(filepath.toFile())) {
      fw.append("# ");
      for (String var: variables) {
        fw.append(var).append(' ');
      }
      fw.append(System.lineSeparator());
      for (List<String> row : table.rows) {
        for (String val: row) {
          fw.append(val).append(' ');
        }
        fw.append(".").append(System.lineSeparator());
      }
    }
  }

    /** Turn a URI string into an object.
   *  A "normal" URI or blank node is turned into an RDF object, an XSD is
   *  converted into a POD object.
   *
   * @param value
   * @return
   */
  public static String getName(String value) {
    AnyType o = null;
    switch (value.charAt(0)) {
      case '<': // URI
        String[] nameNs = splitUriNsName(value);
        if (nameNs[0].charAt(nameNs[0].length() - 1) != '#') {
          // short namespaces have no colon at the end
          nameNs[0] += ':';
        }
        return nameNs[0] + nameNs[1];
      case '_': // blank node
        return value;
      case '"': // simple type
        try {
          o = XsdAnySimpleType.getXsdObject(value);
          return o.toName();
        } catch (WrongFormatException e) {
          throw new RuntimeException(e);
        }
      default:
        // Error: don't know what this is.
        logger.warn("What's this: {}", value);
        break;
    }
    return "ERR";
  }

  public void saveToGraph(Path filepath, boolean uniqueAtoms) throws IOException {
    if (variables.size() != 3) {
      logger.error("saveToGraph only handles triples");
      return;
    }
    DiGraph<String> g = new DiGraph<>();
    VertexPropertyMap<String> nodeName = new VertexListPropertyMap<String>(g);
    Map<String, Integer> node2vertex = new HashMap<>();
    // 'names' is the default map for the graph printer
    g.register("names", nodeName);
    for (List<String> row : table.rows) {
      String sourcename = getName(row.get(0));
      int source = -1;
      if (node2vertex.containsKey(sourcename)) {
        source = node2vertex.get(sourcename);
      } else {
        source = g.newVertex();
        nodeName.put(source, sourcename);
        node2vertex.put(sourcename, source);
      }
      String targetname = getName(row.get(2));
      int target = -1;
      if (uniqueAtoms && isAtom(row.get(2))) {
        target = g.newVertex();
        nodeName.put(target, targetname);
      } else if (! node2vertex.containsKey(targetname)) {
        target = g.newVertex();
        nodeName.put(target, targetname);
        node2vertex.put(targetname, target);
      } else {
        target = node2vertex.get(targetname);
      }
      g.newEdge(getName(row.get(1)), source, target);
    }
    GraphPrinterFactory.print(g, filepath);
  }
}
