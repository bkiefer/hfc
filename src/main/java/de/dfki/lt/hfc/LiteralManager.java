package de.dfki.lt.hfc;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.dfki.lt.hfc.types.AnyType;
import de.dfki.lt.hfc.types.BlankNode;
import de.dfki.lt.hfc.types.Uri;
import de.dfki.lt.hfc.types.XsdAnySimpleType;
import de.dfki.lt.hfc.types.XsdString;

public class LiteralManager {
  private final NamespaceManager nsm;

  public LiteralManager(NamespaceManager n) {
    nsm = n;
  }

  /** number of namespaces */
  public int noOfNamespaces() {
    return nsm.longToNs.size();
  }

  private static String getXSDNamespace(StringTokenizer st) {
    StringBuilder stb = new StringBuilder();
    String token;
    while(st.hasMoreTokens()){
      token = st.nextToken();
      if (!token.equals(">")){
        stb.append(token);
      } else {
        stb.append(token);
        break;
      }
    }
    String namespace = stb.toString();
    if (namespace.endsWith(">")){
      return namespace;
    } else {
      throw new IllegalArgumentException("Illegal or unknown namespace: " + namespace);
    }
  }


  /** Split uri literal into namespace and name string */
  public static String[] splitUriNsName(String literal) {
    String namespace;
    int pos = literal.lastIndexOf("#");
    if (pos != -1) {
      // uri must be in long form: get also rid of <>
      namespace = literal.substring(1, pos + 1);
      literal = literal.substring(pos + 1, literal.length() - 1);
    } else {
      // uri should be in short form or have no namespace at all.
      pos = literal.indexOf(":"); // this may also match the "method" http:
      // check for empty namespace
      if (pos < 0) {
        pos = 0;
        namespace = "";
        // get rid of <>
        literal = literal.substring(1, literal.length() - 1);
      } else {
        namespace = literal.substring(1, pos);
        if (namespace.equals("http")) {
          // this is a long namespace with empty name, the colon is that after
          // http:....
          namespace = literal.substring(1, literal.length() -1) + "#";
          literal = ""; // name is empty
        } else {
          literal = literal.substring(pos + 1, literal.length() - 1);
        }
      }
    }
    String[] res = { namespace, literal };
    return res;
  }


  /** return the namespace part of the string representation of the URI */
  public static String getNamespace(String uri) {
    int i = uri.lastIndexOf('#');
    if (i < 0) {
      i = uri.lastIndexOf(':');
    }
    return uri.substring(0, i+1);
  }


  public static String toUnicode(String in) {
    StringBuilder out = new StringBuilder();
    for (int i = 0; i < in.length(); i++) {
      final char ch = in.charAt(i);
      if (ch <= 127)
        out.append(ch);
      else
        out.append("\\u").append(String.format("%04x", (int) ch));
    }
    return out.toString();
  }


  /**
  * given a string representation of a literal (an argument of a tuple),
  * isUri() returns true iff literal is a URI; false, otherwise
  */
  public static boolean isUri(String literal) {
    return literal.startsWith("<");
  }

  /**
  * given a string representation of a literal (an argument of a tuple),
  * isBlankNode() returns true iff literal is a blank node; false, otherwise
  */
  public static boolean isBlankNode(String literal) {
    return literal.startsWith("_");
  }

  /**
  * given a string representation of a literal (an argument of a tuple),
  * isAtom() returns true iff literal is an XSD atom; false, otherwise
  */
  public static boolean isAtom(String literal) {
    return literal.startsWith("\"");
  }


  /**
   * a URI starts with a '<' and ends with a '>';
   */
  public static String parseURI(StringTokenizer st, ArrayList<String> tuple) {
    // the leading '<' char has already been consumed, so consume tokens until
    // we
    // find the closing '>'
    // note: no blanks are allowed inside a URI, but a URI might clearly contain
    // '_' and '\' chars, so reading only the next two tokens would lead to
    // wrong
    // results in general
    StringBuilder sb = new StringBuilder("<");
    String token;
    while (st.hasMoreTokens()) {
      token = st.nextToken();
      if (token.equals(">"))
        break;
      else
        sb.append(token);
    }
    token = sb.append(">").toString();
    tuple.add(token);
    return token;
  }


  /**
   * XSD atoms are strings optionally followed by either a type identifier (^^type)
   * or a language tag (@lang);
   * within the preceding string, further strings are allowed, surrounded by "\"",
   * as well as spaces, "\\", etc.
   */
  public static String parseAtom(StringTokenizer st, ArrayList<String> tuple) {
    StringBuilder sb = new StringBuilder("\"");
    boolean backquote = false;
    String token;
    while (st.hasMoreTokens()) {
      token = st.nextToken();
      if (!backquote && token.equals("\""))
        // string fully parsed
        break;
      if (token.equals("\\"))
        backquote = true;
      else
        backquote = false;
      sb.append(token);
    }
    sb.append("\"");
    // now gather potential additional information (XSD Type or language tag);
    // the first whitespace char terminates XSD atom recognition
    //
    // type checking of XSD atoms should be implemented HERE -- use a second
    // string buffer to separate the bare atom from its type
    boolean bareAtom = true;
    while (st.hasMoreTokens()) {
      token = st.nextToken();
      if (token.equals(" "))
        break;
      else {
        bareAtom = false;
        sb.append("^^").append(getXSDNamespace(st));
        if (!(token.equals("^^") || token.equals("<") || token.equals(">")))
          sb.append(token);
      }
    }
    if (bareAtom) {
      // complete type in order to recognize duplicates (perhaps output a
      // message?)
      sb.append("^^").append(XsdString.SHORT_NAME);
    }
    token = sb.toString();
    Pattern p = Pattern.compile("\\\\u(\\p{XDigit}{4})");
    Matcher m = p.matcher(token);
    StringBuffer buf = new StringBuffer(token.length());
    while (m.find()) {
      String ch = String.valueOf((char) Integer.parseInt(m.group(1), 16));
      m.appendReplacement(buf, Matcher.quoteReplacement(ch));
    }
    m.appendTail(buf);
    tuple.add(buf.toString());
    return token;
  }


  public AnyType makeAnyType(String literal) throws WrongFormatException {
    AnyType anyType;
    if (isUri(literal)) {
      String[] nsAndName = splitUriNsName(literal);
      anyType = new Uri(nsAndName[1], nsm.getNamespaceObject(nsAndName[0]));
    } else if (isBlankNode(literal)) {
      anyType = new BlankNode(literal);
    } else {
      int idx = literal.lastIndexOf('^');
      if (idx == -1) {
        // note: parseAtom() completes a bare string by adding "^^<xsd:string>",
        // but if the string has a language tag, nothing is appended, thus
        // '^' is missing (as is required by the specification)
        anyType = new XsdString(literal);
      }
      // now do the `clever' dispatch through mapping the type names to Java
      // class constructors: @see
      // de.dfki.lt.hfc.NamespaceManager.readNamespaces()
      else {
        anyType = XsdAnySimpleType.getXsdObject(literal);
      }
    }
    return anyType;
  }


  /**
   * this method borrows code from normalizeNamespace() above and always tries to fully
   * expand the namespace prefix of an URI, even if shortIsDefault == true
   */
  private String expandUri(String uri) {
    int pos = uri.indexOf("://");
    if (pos != -1)
      // a fully-expanded URI
      return uri;
    pos = uri.indexOf(":");
    if (pos == -1)
      // a URI with an _empty_ namespace
      return uri;
    // URI _not_ expanded, otherwise
    String prefix = uri.substring(1, pos);  // skip '<'
    String suffix = uri.substring(pos + 1);
    String expansion = nsm.getLongForm(prefix);
    // namespace maping specified?
    if (expansion == null)
      return uri;
    else
      return "<" + expansion + suffix;
  }


  public String toExpandedString(String literal) {
    // distinguish between URIs vs. XSD atoms or blank nodes
    return (isAtom(literal) || isBlankNode(literal))
        ? toUnicode(literal.replace("xsd:", this.nsm.getLongForm("xsd")))
          : toUnicode(expandUri(literal));   // a URI
  }
}
