package de.dfki.lt.hfc;

import java.util.*;
import gnu.trove.*;
import de.dfki.lt.hfc.types.*;

/**
 * a wrapper class, hiding a table (a set of int arrays) and several _mappings_
 * 
 * @author (C) Hans-Ulrich Krieger
 * @since JDK 1.5
 * @version Thu Oct 29 15:26:15 CET 2015
 */
public class BindingTable {
	
	/**
	 *
	 */
	public Set<int[]> table;
	
	/**
	 * important to establish an order to guarantee proper set operations over binding tables
	 * whose column labels vary
	 */
	protected SortedMap<Integer, Integer> nameToPos;
	
	/**
	 * (internal) name (a negative int, e.g., -2) to external name (a string, e.g., "?y")
	 */
	protected Map<Integer, String> nameToExternalName;
	
	/**
	 * needed in case toString() is called in order to access the idToObject field and the
	 * proxyToUris field when equivalence class reduction is turned on as specified by Boolean
	 * field equivalenceClassReduction in class TupleStore
	 */
	protected TupleStore tupleStore;
	
	/**
	 * in case (addProxyInfo == true), the toString() methods add a further mapping table
	 * to the result string, representing the mapping from proxis to URIs
	 */
	protected boolean addProxyInfo = false;
	
	/**
	 * expansion through expandBindingTable() can only be called once
	 */
	protected boolean isExpanded = false;
	
	/**
	 * might be assigned a value by Calc.restrict(BindingTable bt, ArrayList<Predicate> predicate);
	 * is usually important for tests & actions involving (complex) relational variables
	 */
	public int[] arguments;
	
	/**
	 * might be assigned a value by Calc.restrict(BindingTable bt, ArrayList<Predicate> predicate);
	 * is usually important for tests & actions involving (complex) relational variables
	 */
	public HashMap<Integer, ArrayList<Integer>> relIdToFunIds;
	
	/**
	 * might be assigned a value by Calc.restrict(BindingTable bt, ArrayList<Predicate> predicate);
	 * is usually important for tests & actions involving (complex) relational variables
	 */
	protected HashMap<String, Integer> varToId;
	
	/**
	 * assigns the null value to all four public fields
	 */
	public BindingTable() {
		this.table = null;
		this.nameToPos = null;
		this.nameToExternalName = null;
		this.tupleStore = null;
	}
	
	/**
	 * assigns value of parameter table to this.table;
	 * three other fields are assigned the null value
	 */
	public BindingTable(Set<int[]> table) {
		this.table = table;
		this.nameToPos = null;
		this.nameToExternalName = null;
		this.tupleStore = null;
	}

	/**
	 * assigns null values to this.nameToPos and this.nameToExternalName;
	 * this.tupleStore is set to parameter tupleStore and this.table is
	 * assigned an empty THashSet<int[]> object (all positions are assumed
	 * to be relevant)
	 */
	public BindingTable(TupleStore tupleStore) {
		
		this.table = new THashSet<int[]>();
		this.nameToPos = null;
		this.nameToExternalName = null;
		this.tupleStore = tupleStore;
	}
	
	/**
	 * copy constructor for exclusive use in copy constructor of Cluster;
	 * only table value needs to be shallow copied
	 */
	protected BindingTable(BindingTable bt, TupleStore ts) {
		this.table = new THashSet<int[]>(bt.table);
		this.nameToPos = bt.nameToPos;
		this.nameToExternalName = bt.nameToExternalName;
		this.tupleStore = ts;
	}
	
	/**
	 * assigns non-null values to this.table and this.nameToPos
	 */
	public BindingTable(Set<int[]> table, SortedMap<Integer, Integer> nameToPos) {
		this.table = table;
		this.nameToPos = nameToPos;
		this.nameToExternalName = null;
		this.tupleStore = null;
	}
	
	/**
	 * assigns non-null values to this.table, this.nameToPos, and this.nameToExternalName
	 */
	public BindingTable(Set<int[]> table,
											SortedMap<Integer, Integer> nameToPos,
											Map<Integer, String> nameToExternalName) {
		this.table = table;
		this.nameToPos = nameToPos;
		this.nameToExternalName = nameToExternalName;
		this.tupleStore = null;
	}
	
	/**
	 * assigns non-null values to all four public fields
	 */
	public BindingTable(Set<int[]> table,
											SortedMap<Integer, Integer> nameToPos,
											Map<Integer, String> nameToExternalName,
											TupleStore tupleStore) {
		this.table = table;
		this.nameToPos = nameToPos;
		this.nameToExternalName = nameToExternalName;
		this.tupleStore = tupleStore;
	}
	
	/**
	 * a further argument varToId originating from the RuleStore when reading in rules
	 * (individual map for each rule)
	 */
	public BindingTable(Set<int[]> table,
											SortedMap<Integer, Integer> nameToPos,
											Map<Integer, String> nameToExternalName,
											TupleStore tupleStore,
											int[] arguments,
											HashMap<Integer, ArrayList<Integer>> relIdToFunIds,
											HashMap<String, Integer> varToId) {
		this.table = table;
		this.nameToPos = nameToPos;
		this.nameToExternalName = nameToExternalName;
		this.tupleStore = tupleStore;
		this.arguments = arguments;
		this.relIdToFunIds = relIdToFunIds;
		this.varToId = varToId;
	}

	/**
	 * assigns non-null values to this.nameToPos, this.nameToExternalName, and this.tupleStore;
	 * this.table is assigned an empty THashSet<int[]> object (all positions are assumed to be
	 * relevant)
	 */
	public BindingTable(SortedMap<Integer, Integer> nameToPos,
											Map<Integer, String> nameToExternalName,
											TupleStore tupleStore) {
		this.table = new THashSet<int[]>();  // (no strategy object)
		this.nameToPos = nameToPos;
		this.nameToExternalName = nameToExternalName;
		this.tupleStore = tupleStore;
	}
  
  /**
   * given an external name, this method returns the position (an int)
   * of the column which is headed by externalName;
   * in case externalName is not a valid table heading, -1 is returned!
   */
  public int obtainPosition(String externalName) {
    Set<Map.Entry<Integer, String>> iname2ename = this.nameToExternalName.entrySet();
    int iname = 0;
    for (Map.Entry<Integer, String> elem : iname2ename) {
      if (elem.getValue().equals(externalName)) {
        iname = elem.getKey();  // a _negative_ int
        break;
      }
    }
    if (iname == 0)
      return -1;
    else
      return this.nameToPos.get(iname);
  }
	
	/**
	 * uses the full URI/XSD name
	 * note: in order to properly behave, toString() needs properly set fields, viz.,
	 * table, nameToPos, nameToExternalName, and tupleStore;
	 * note: the content of the table is NOT expanded when this method is called directly
	 */
	public String toString() {		
		int maxLength = -1;
		int size = this.nameToPos.keySet().size();
		Integer[] nameArray = new Integer[size];
		for (Integer name : this.nameToPos.keySet())
			nameArray[--size] = name;
		String element;
		for (int[] tuple : this.table) {
			for (Integer name : nameArray) {
				element = this.tupleStore.idToObject.get(tuple[this.nameToPos.get(name)]);
				if (element.length() > maxLength)
					maxLength = element.length();
			}
		}
		// call unary toString() with the maximal length
		return toString(maxLength);
	}
	
	/**
	 * depending on the argument, the content is destructively expanded and
	 * returned as a string
	 */
	public String toString(boolean expand) {
		this.addProxyInfo = !expand;
		if (expand)
			expandBindingTable();
		return toString();
	}
	
	public String toString(int maxLength, boolean expand) {
		this.addProxyInfo = !expand;
		if (expand)
			expandBindingTable();
		return toString(maxLength);
	}
	
	/**
	 * maxLength indicates when entries need to be truncated;
	 * note: in order to properly behave, toString() needs properly set fields, viz.,
	 * table, nameToPos, nameToExternalName, and tupleStore;
	 * note: the content of the table is NOT expanded when this method is called directly
	 */
	public String toString(int maxLength) {
		StringBuilder sb = new StringBuilder();
		// nameToPos' key set is sorted (negative ints), but need inverse order
		int size = this.nameToPos.keySet().size();
		Integer[] nameArray = new Integer[size];
		for (Integer name : this.nameToPos.keySet())
			nameArray[--size] = name;
		// print header
		for (int i = 0; i < nameArray.length * (maxLength + 3) + 1; i++)
			sb.append("=");
		sb.append("\n");
		String element;
		int difference;
		for (int i = 0; i < nameArray.length; i++) {
			sb.append("| ");
			if (this.nameToExternalName == null)
				element = "?" + nameArray[i];
			else
				element = this.nameToExternalName.get(nameArray[i]);
			sb.append(element);  // always append full variable name
			difference = maxLength - element.length();
			if (difference > 0)
				for (int j = 0; j < difference; j++)
					sb.append(" ");
			sb.append(" ");
		}
		sb.append("|\n");
		for (int i = 0; i < nameArray.length * (maxLength + 3) + 1; i++)
			sb.append("=");
		sb.append("\n");
		// print body
		for (int[] tuple : this.table) {
			for (Integer name : nameArray) {
				sb.append("| ");
				element = this.tupleStore.idToObject.get(tuple[this.nameToPos.get(name)]);
				difference = maxLength - element.length();
				if (difference > 0) {
					sb.append(element);
					for (int j = 0; j < difference; j++)
						sb.append(" ");
				}
				else {
					sb.append(element.substring(0, maxLength));
				}
				sb.append(" ");
			}
			sb.append("|\n");
		}
		// table finished
		for (int i = 0; i < nameArray.length * (maxLength + 3) + 1; i++)
			sb.append("-");
		sb.append("\n" + this.table.size());
		sb.append("\n");
		// now, is there a need to add the proxy-to-URIs mapping
		if (this.addProxyInfo) {
			final TIntObjectHashMap<TIntArrayList> proxyToUris = this.tupleStore.proxyToUris;
			final ArrayList<String> idToObject = this.tupleStore.idToObject;
			TIntArrayList uris;
			for (int i : proxyToUris.keys()) {
				sb.append(idToObject.get(i));
				sb.append(" -> ");
				uris = proxyToUris.get(i);
				for (int j = 0; j < uris.size(); j++) {
					sb.append(idToObject.get(uris.get(j)));
					sb.append(" ");
				}
				sb.append("\n");
			}
			sb.append("\n");
		}
		return sb.toString();
	}
	
	/**
	 * expands this binding table by multiplying out proxies by their equivalent
	 * URIs;
	 * the method does not check whether equivalence class reduction is turned
	 * on or not; in case it is turned off, calling this method does not have an
	 * effect
	 */
	protected void expandBindingTable() {
		// if already expanded, no need to call method again, esp., since SELECT
		// statements without the DISTINCT keyword use java.util.Hashset objects to
		// represent duplicate elements (int arrays); otherwise (w/ DISTINCT), a
		// Trove THashSet object is used to represent the internal table
		if (this.isExpanded)
			return;
		this.isExpanded = true;
		// compute maximal tuple length to guarantee termination
		int max = 0;
		for (int[] tuple : this.table)
			if (tuple.length > max)
				max = tuple.length;
		Set<int[]> newTuples = new THashSet<int[]>();
		final TIntObjectHashMap<TIntArrayList> proxyToUris = this.tupleStore.proxyToUris;
		// move over each tuple position in every tuple
		for (int pos = 0; pos < max; pos++) {
			// need this sequence of for loops, since I'm not allowed to enlarge the set during iteration
			for (int[] tuple : this.table) {
				// tuples might be of different length
				if (pos < tuple.length)
					if (proxyToUris.containsKey(tuple[pos]))
						expandTuple(tuple, proxyToUris, newTuples, pos);
			}
			this.table.addAll(newTuples);
			newTuples.clear();
		}
	}

	/**
	 * given a tuple that contains a proxy at position pos, generate copies of
	 * this tuple that only differ by replacing the proxy through its equivalences
	 */
	private void expandTuple(int[] tuple,
													 TIntObjectHashMap<TIntArrayList> proxyToUris,
													 Set<int[]> newTuples,
													 int pos) {
		int[] newTuple;
		TIntArrayList uris = proxyToUris.get(tuple[pos]);
		// I can start with the second element, first is always the proxy!
		for (int i = 1; i < uris.size(); i++) {
			newTuple = new int[tuple.length];
			for (int j = 0; j < tuple.length; j++) {
				if (j == pos)
					newTuple[j] = uris.get(i);
				else
					newTuple[j] = tuple[j];
			}
			newTuples.add(newTuple);
		}
	}
  
  /**
   * as the constructors of the inner class are private, I do provide two methods
   * which return a BindingTableIterator object;
   * this implementation will keep the sequence of elements in a tuple for the
   * next(), nextAsString(), nextAsXsdType(), and nextAsObject() methods
   */
  public BindingTableIterator iterator() {
    return this.new BindingTableIterator();
  }
  
  /**
   * this implementation of iterator() might reorder the sequence of elements in a
   * tuple, depending on the sequence of variable names, given by parameter vars;
   * in case vars.length < tuple.length, the nextXXX() method also implement a kind
   * of table projection, however, _without_ removing potential duplicate elements!
   */
  public BindingTableIterator iterator(String ... vars) throws BindingTableIteratorException {
    // check whether vars refers to _legal_ variables, i.e., check whether each
    // variable from vars refers to one of the columns from the binding table
    // and keep the sequence of variables from vars in the tuple that is returned
    final Collection<String> allVars = this.nameToExternalName.values();
    for (String var : vars)
      if (! allVars.contains(var))
        throw new BindingTableIteratorException("wrongly-specified vars: there is no table column named " + var);
    return this.new BindingTableIterator(vars);
  }
  
  /**
   * an implementation of TupleIterator for BindingTable objects
   */
  class BindingTableIterator implements TupleIterator {
    
    /**
     * make the tuple store from BindingTable directly accessable in this local class
     */
    private TupleStore tupleStore = BindingTable.this.tupleStore;
    
    /**
     * the number of tuples of the binding table
     */
    private int size = BindingTable.this.table.size();
    
    /**
     * if null, next(), nextAsXsdType(), nextAsString(), and nextAsObject() keep the
     * original sequence of the elements of the individual tuples;
     * otherwise, the elements of the returned tuples are reordered according to the
     * sequence given by vars
     */
    private String[] vars;
    
    /**
     * is called by iterator()
     */
    private BindingTableIterator() {
      super();
      this.vars = null;
    }
    
    /**
     * is called by iterator(String ... vars) and the sequence of vars are recorded
     */
    private BindingTableIterator(String[] vars) {
      super();
      this.vars = vars;
    }

    /**
     *
     */
    public int hasSize() {
      return this.size;
    }
    
    /**
     *
     */
    public boolean hasNext() {
      return true;
    }
    
    /**
     *
     */
    public int[] next() {
      return null;
    }
    
    /**
     *
     */
    public AnyType[] nextAsXsdType() {
      return null;
    }
    
    /**
     *
     */
    public String[] nextAsString() {
      return null;
    }
    
    /**
     *
     */
    public Object[] nextAsObject() {
      return null;
    }
    
  }
  
  
  
  
  
  
  
  

}
