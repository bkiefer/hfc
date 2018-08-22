package de.dfki.lt.hfc.indices.intervaltree_deprecated;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 * A binary tree that stores overlapping interval ranges. Implemented as an AKeyL tree (better searches).
 * Notes: Adapted from "Introduction to Algorithms", second edition,
 * by Thomas H. Cormen, Charles E. leiserson,
 * Ronald L. Rivest, Clifford Stein.
 * chapter 13.2
 *
 * @param <Key>
 * @author John Thomas McDole
 */
public class IntervalTree<Key extends Comparable> {

  private IntervalNode<Key> root;

  private int count;

  private Comparator<Interval<Key>> comparator;


  //public IntervalTree(Comparator<Interval<Key>> comparator) {
  //    this.comparator = comparator;
  //}

  public IntervalTree() { // TODO aus dem constructor rausziehen.
    this.comparator = (o1, o2) -> {
      int comp = o1.getLower().compareTo(o2.getLower());
      if (comp == 0) {
        comp = o1.getUpper().compareTo(o2.getUpper());
      }
      return comp;
    };
  }

  @Override
  public String toString() {
    return root.toString();
  }

  /**
   * Add the element to the tree.
   */
  public boolean add(Interval<Key> element) {
    IntervalNode<Key> x = root;

    IntervalNode<Key> node = new IntervalNode<Key>();
    Key max = element.getUpper();
    node.max = max; // Initial value is always ourself.
    node.object = element;

    if (root == null) {
      root = node;
      ++count;
      return true;
    }

    while (true) {
      // We only update if the new element is larger than the existing ones
      if (node.max.compareTo(x.max) > 0) {
        x.max = max;
      }

      int compare = comparator.compare(element, x.object);
      if (0 == compare) {
        return false;
      } else if (compare < 0) {
        if (x.left == null) {
          node.parent = x;
          x.left = node;
          x.balanceFactor -= 1;
          break;
        }
        x = x.left;
      } else {
        if (x.right == null) {
          node.parent = x;
          x.right = node;
          x.balanceFactor += 1;
          break;
        }
        x = x.right;
      }
    }
		/*
			AKeyL balancing act (for height balanced trees)
			Now that we've inserted, we've unbalanced some trees, we need
			to follow the tree back up to the root double checking that the tree
			is still balanced and _maybe_ perform a single or double rotation.
			Note: Left additions == -1, Right additions == +1
			Balanced Node = { -1, 0, 1 }, out of balance = { -2, 2 }
			Single rotation when Parent & Child share signed balance,
			Double rotation when sign differs!
		*/
    node = x;
    while (node.balanceFactor != 0 && node.parent != null) {
      // Find out which side of the parent we're on
      if (node.parent.left == node) {
        // Lefties are -1 since we hate lefties
        node.parent.balanceFactor -= 1;
      } else {
        node.parent.balanceFactor += 1;
      }
      node = node.parent;
      if (node.balanceFactor == 2) {
        // Heavy on the right side - Test for which rotation to perform
        if (node.right.balanceFactor == 1) {
          // Single (left) rotation; this will balance everything to zero
          rotateLeft(node);
          node.balanceFactor = node.parent.balanceFactor = 0;
          node = node.parent;
          // Update node's new max; but recalculate the children
          recalculateMax(node.left);
          recalculateMax(node.right);
          recalculateMax(node);
        } else {
          // Double (Right/Left) rotation
          // node will now be old node.right.left
          rotateRightLeft(node);
          node = node.parent; // Update to new parent (old grandchild)
          recalculateMax(node.left);
          recalculateMax(node.right);
          recalculateMax(node);
          if (node.balanceFactor == 1) {
            node.right.balanceFactor = 0;
            node.left.balanceFactor = -1;
          } else if (node.balanceFactor == 0) {
            node.right.balanceFactor = 0;
            node.left.balanceFactor = 0;
          } else {
            node.right.balanceFactor = 1;
            node.left.balanceFactor = 0;
          }
          node.balanceFactor = 0;
        }
        break; // out of loop, we're balanced
      } else if (node.balanceFactor == -2) {
        // Heavy on the left side - Test for which rotation to perform
        if (node.left.balanceFactor == -1) {
          rotateRight(node);
          node.balanceFactor = node.parent.balanceFactor = 0;
          node = node.parent;

          // Update node's new max; but recalculate the children
          recalculateMax(node.left);
          recalculateMax(node.right);
          recalculateMax(node);
        } else {
          // Double (Left/Right) rotation
          // node will now be old node.left.right
          rotateLeftRight(node);
          node = node.parent;
          recalculateMax(node.left);
          recalculateMax(node.right);
          recalculateMax(node);
          if (node.balanceFactor == -1) {
            node.right.balanceFactor = 1;
            node.left.balanceFactor = 0;
          } else if (node.balanceFactor == 0) {
            node.right.balanceFactor = 0;
            node.left.balanceFactor = 0;
          } else {
            node.right.balanceFactor = 0;
            node.left.balanceFactor = -1;
          }
          node.balanceFactor = 0;
        }
        break; // out of loop, we're balanced
      }
    } // end of while(balancing)
    count++;
    return true;
  }

  /**
   * Test to see if an element is stored in the tree
   */
  public boolean contains(Object object) {
    @SuppressWarnings("unchecked")
    Interval<Key> element = (Interval<Key>) object;
    IntervalNode<Key> x = getIntervalNode(element);
    return x != null;
  }

  /**
   * Test to see if an element is stored in the tree
   */
  private IntervalNode<Key> getIntervalNode(Interval<Key> element) {
    if (element == null) return null;
    IntervalNode<Key> x = root;
    while (x != null) {
      int compare = comparator.compare(element, x.object);
      if (0 == compare) {
        // This only means our interval matches; we need to search for the exact element
        // We could have been glutons and used a hashmap to back.
        return x; // comparator.compare should check for further restrictions.
      } else if (compare < 0) {
        x = x.left;
      } else {
        x = x.right;
      }
    }
    return null;
  }

  /**
   * Make sure the node has the right maximum of the subtree
   * node.max = MAX( node.high, node.left.max, node.right.max );
   */
  private void recalculateMax(IntervalNode<Key> node) {
    Key max;
    if (node == null) return;
    if (node.left == node.right && node.right == null) {
      node.max = node.object.getUpper();
      return;
    } else if (node.left == null) {
      max = node.right.max;
    } else if (node.right == null) {
      max = node.left.max;
    } else {
      /* Get the best of the children */
      max = node.left.max.compareTo(node.right.max) > 0 ? node.left.max : node.right.max;
    }

    /* And pit that against our interval */
    node.max = node.object.getUpper().compareTo(max) > 0 ? node.object.getUpper() : max;
  }

  /**
   * This function will right rotate/pivot N with its left child, placing
   * it on the right of its left child.
   * <p>
   * N                      Y
   * / \                    / \
   * Y   A                  Z   N
   * / \          ==>       / \ / \
   * Z   B                  D  CB   A
   * / \
   * D   C
   * <p>
   * Assertion: must have a left element!
   */
  private void rotateRight(IntervalNode<Key> node) {
    IntervalNode<Key> y = node.left;
    assert y != null;

    /* turn Y's right subtree(B) into N's left subtree. */
    node.left = y.right;
    if (node.left != null) {
      node.left.parent = node;
    }
    y.parent = node.parent;
    if (y.parent == null) {
      root = y;
    } else {
      if (node.parent.left == node) {
        node.parent.left = y;
      } else {
        node.parent.right = y;
      }
    }
    y.right = node;
    node.parent = y;
  }

  /**
   * This function will left rotate/pivot N with its right child, placing
   * it on the left of its right child.
   * <p>
   * N                      Y
   * / \                    / \
   * A   Y                  N   Z
   * / \      ==>       / \ / \
   * B   Z              A  BC   D
   * / \
   * C   D
   * <p>
   * Assertion: must have a right element!
   */
  private void rotateLeft(IntervalNode<Key> node) {
    IntervalNode<Key> y = node.right;
    assert y != null;

    /* turn Y's left subtree(B) into N's right subtree. */
    node.right = y.left;
    if (node.right != null) {
      node.right.parent = node;
    }
    y.parent = node.parent;
    if (y.parent == null) {
      root = y;
    } else {
      if (node.parent.left == node) {
        y.parent.left = y;
      } else {
        y.parent.right = y;
      }
    }
    y.left = node;
    node.parent = y;
  }

  /**
   * This function will double rotate node with right/left operations.
   * node is S.
   * <p>
   * S                      G
   * / \                    / \
   * A   C                  S   C
   * / \      ==>       / \ / \
   * G   B              A  DC   B
   * / \
   * D   C
   */
  private void rotateRightLeft(IntervalNode<Key> node) {
    rotateRight(node.right);
    rotateLeft(node);
  }

  /**
   * This function will double rotate node with left/right operations.
   * node is S.
   * <p>
   * S                      G
   * / \                    / \
   * C   A                  C   S
   * / \          ==>       / \ / \
   * B   G                  B  CD   A
   * / \
   * C   D
   */
  private void rotateLeftRight(IntervalNode<Key> node) {
    rotateLeft(node.left);
    rotateRight(node);
  }

  /**
   * Return the minimum node for the subtree
   */
  IntervalNode<Key> minimumNode(IntervalNode<Key> node) {
    while (node.left != null) {
      node = node.left;
    }
    return node;
  }

  /**
   * Return the maximum node for the subtree
   */
  IntervalNode<Key> maxiumNode(IntervalNode<Key> node) {
    while (node.right != null) {
      node = node.right;
    }
    return node;
  }

  /**
   * Return the next greatest element (or null)
   */
  IntervalNode<Key> successor(IntervalNode<Key> node) {
    if (node.right != null) {
      return minimumNode(node.right);
    }
    while (node.parent != null && node == node.parent.right) {
      node = node.parent;
    }
    return node.parent;
  }

  /**
   * Return the next smaller element (or null)
   */
  IntervalNode<Key> predecessor(IntervalNode<Key> node) {
    if (node.left != null) {
      return maxiumNode(node.left);
    }
    while (node.parent != null && node.parent.left == node) {
      node = node.parent;
    }
    return node.parent;
  }

  public Set<Interval> searchIntervalsIncluding(Interval interval) {
    Set<Interval> found = new HashSet<>();
    searchIntervalsIncludingRecursive(interval, root, found);
    return found;
  }

  private void searchIntervalsIncludingRecursive(Interval interval, IntervalNode<Key> node, Set<Interval> storage) {
    if (node == null)
      return;

    // If the node's max interval is less than the low interval, no children will match
    if (node.max.compareTo(interval.getLower()) < 0) {
      return;
    }

    // left children
    searchIntervalsIncludingRecursive(interval, node.left, storage);

    // Do we ??? s.low >= n.low && s.hing <= n.high; where s = interval, n = node
    if (node.object.getLower().compareTo(interval.getLower()) <= 0
            && interval.getUpper().compareTo(node.object.getUpper()) <= 0) {
      storage.add(node.object);
    }

    // if interval.high is to the left of the start of Node's interval, then no children to the
    // right will match (short cut)
    if (interval.getUpper().compareTo(node.object.getLower()) < 0) {
      return;
    }

    // else, search the right nodes as well
    searchIntervalsIncludingRecursive(interval, node.right, storage);
  }

  public void clear() {
    count = 0;
    root = null;
  }

  public boolean containsAll(Collection<?> arg0) {
    for (Object ele : arg0) {
      if (!contains(ele)) return false;
    }
    return true;
  }

  public boolean isEmpty() {
    return count == 0;
  }

  public Set<int[]> remove(Interval interval) {
    IntervalNode<Key> x = getIntervalNode(interval);
    Set<int[]> result = new HashSet<>();
    if (x != null) {
      result.addAll(x.object.getValue());
      remove(x);
      return result;
    }
    return result;
  }

  public void remove(IntervalNode<Key> node) {
    IntervalNode<Key> y, w;
    --count;
    /*
     * JTM - if you read wikipedia, it states remove the node if its a leaf,
     * otherwise, replace it with its predecessor or successor. we've not
     * done that here; though we probably should!
     */
    if (node.right == null || node.right.left == null) {
      // simple solutions
      if (node.right != null) {
        y = node.right;
        y.parent = node.parent;
        y.balanceFactor = node.balanceFactor - 1;
        y.left = node.left;
        if (y.left != null) {
          y.left.parent = y;
        }
      } else if (node.left != null) {
        y = node.left;
        y.parent = node.parent;
        y.balanceFactor = node.balanceFactor + 1;
      } else {
        y = null;
      }
      if (root == node) {
        root = y;
      } else if (node.parent.left == node) {
        node.parent.left = y;
        if (y == null) {
          // account for leaf deletions changing the balance
          node.parent.balanceFactor += 1;
          y = node.parent; // start searching from here;
        }
      } else {
        node.parent.right = y;
        if (y == null) {
          node.parent.balanceFactor -= 1;
          y = node.parent;
        }
      }
      w = y;
    } else {
      /*
       * This node is not a leaf; we should find the successor node, swap
       * it with this* and then update the balance factors.
       */
      y = successor(node);
      y.left = node.left;
      if (y.left != null) {
        y.left.parent = y;
      }

      w = y.parent;
      w.left = y.right;
      if (w.left != null) {
        w.left.parent = w;
      }
      // known: we're removing from the left
      w.balanceFactor += 1;

      // known due to test for n->r->l above
      y.right = node.right;
      y.right.parent = y;
      y.balanceFactor = node.balanceFactor;

      y.parent = node.parent;
      if (root == node) {
        root = y;
      } else if (node.parent.left == node) {
        node.parent.left = y;
      } else {
        node.parent.right = y;
      }
    }

    // Safe to kill node now; its free to go.
    node.balanceFactor = 0;
    node.left = node.right = node.parent = null;
    node.object = null;

    // Recalculate max values all the way to the top.
    node = w;
    while (node != null) {
      recalculateMax(node);
      node = node.parent;
    }

    // Re-balance to the top, ending early if OK
    node = w;
    while (node != null) {
      if (node.balanceFactor == -1 || node.balanceFactor == 1) {
        // The height of node hasn't changed; done!
        break;
      }
      if (node.balanceFactor == 2) {
        // Heavy on the right side; figure out which rotation to perform
        if (node.right.balanceFactor == -1) {
          rotateRightLeft(node);
          node = node.parent; // old grand-child!
          recalculateMax(node.left);
          recalculateMax(node.right);
          recalculateMax(node);
          if (node.balanceFactor == 1) {
            node.right.balanceFactor = 0;
            node.left.balanceFactor = -1;
          } else if (node.balanceFactor == 0) {
            node.right.balanceFactor = 0;
            node.left.balanceFactor = 0;
          } else {
            node.right.balanceFactor = 1;
            node.left.balanceFactor = 0;
          }
          node.balanceFactor = 0;
        } else {
          // single left-rotation
          rotateLeft(node);
          recalculateMax(node.left);
          recalculateMax(node.right);
          recalculateMax(node);
          if (node.parent.balanceFactor == 0) {
            node.parent.balanceFactor = -1;
            node.balanceFactor = 1;
            break;
          } else {
            node.parent.balanceFactor = 0;
            node.balanceFactor = 0;
            node = node.parent;
            continue;
          }
        }
      } else if (node.balanceFactor == -2) {
        // Heavy on the left
        if (node.left.balanceFactor == 1) {
          rotateLeftRight(node);
          node = node.parent; // old grand-child!
          recalculateMax(node.left);
          recalculateMax(node.right);
          recalculateMax(node);
          if (node.balanceFactor == -1) {
            node.right.balanceFactor = 1;
            node.left.balanceFactor = 0;
          } else if (node.balanceFactor == 0) {
            node.right.balanceFactor = 0;
            node.left.balanceFactor = 0;
          } else {
            node.right.balanceFactor = 0;
            node.left.balanceFactor = -1;
          }
          node.balanceFactor = 0;
        } else {
          rotateRight(node);
          recalculateMax(node.left);
          recalculateMax(node.right);
          recalculateMax(node);
          if (node.parent.balanceFactor == 0) {
            node.parent.balanceFactor = 1;
            node.balanceFactor = -1;
            break;
          } else {
            node.parent.balanceFactor = 0;
            node.balanceFactor = 0;
            node = node.parent;
            continue;
          }
        }
      }
      // continue up the tree for testing
      if (node.parent != null) {
        /*
         * The concept of balance here is reverse from addition; since
         * we are taking away weight from one side or the other (thus
         * the balance changes in favor of the other side)
         */
        if (node.parent.left == node) {
          node.parent.balanceFactor += 1;
        } else {
          node.parent.balanceFactor -= 1;
        }
      }
      node = node.parent;
    }
  }

  public int size() {
    return count;
  }

  /**
   * Keyerify every node of the tree has the correct balance factor between right and left nodes
   *
   * @param node to test
   * @return height of this node
   */
  private int verifyHeight(IntervalNode<Key> node) {
    if (node == null)
      return 0;
    int left = verifyHeight(node.left);
    int right = verifyHeight(node.right);
    int calcBalanc = right - left;
    if (node.balanceFactor != calcBalanc) {
      throw new IllegalStateException("Balance off; is:" + node.balanceFactor + " should:"
              + calcBalanc);
    }
    return Math.max(left, right) + 1;
  }

  /**
   * Keyerify every node of the tree has the correct height
   *
   * @return height of this node
   */
  public int verifyHeight() {
    return verifyHeight(root);
  }

  /**
   * Search the set for any elements in the interval
   * <p>
   * TODO(jtmcdole): should be a set backed by the tree?
   *
   * @param interval
   * @return
   */
  public Set<Interval<Key>> searchInterval(Interval<Key> interval) {
    Set<Interval<Key>> found = new HashSet<Interval<Key>>();
    searchIntervalRecursive(interval, root, found);
    return found;
  }

  /**
   * Search each node, recursively matching against the search interval
   */
  private void searchIntervalRecursive(Interval<Key> interval, IntervalNode<Key> node,
                                       Set<Interval<Key>> storage) {
    if (node == null)
      return;

    // If the node's max interval is less than the low interval, no children will match
    if (node.max.compareTo(interval.getLower()) < 0)
      return;

    // left children
    searchIntervalRecursive(interval, node.left, storage);

    // Do we overlap? s.low <= n.high && n.low <= s.high; where s = interval, n = node
    if (interval.getLower().compareTo(node.object.getUpper()) <= 0
            && node.object.getLower().compareTo(interval.getUpper()) <= 0) {
      storage.add(node.object);
    }

    // if interval.high is to the left of the start of Node's interval, then no children to the
    // right will match (short cut)
    if (interval.getUpper().compareTo(node.object.getLower()) < 0) {
      return;
    }

    // else, search the right nodes as well
    searchIntervalRecursive(interval, node.right, storage);
  }

  /**
   * Search the tree for the matching element, or the 'nearest' one.
   */
  public Interval<Key> searchNearestElement(Interval<Key> element) {
    return searchNearestElement(element, SearchNearest.SEARCH_NEAREST_ABSOLUTE);
  }

  /**
   * Search the tree for the matching element, or the 'nearest' one.
   */
  public Interval<Key> searchNearestElement(Interval<Key> element, SearchNearest nearestOption) {
    IntervalNode<Key> found = searchNearest(element, nearestOption);
    if (found != null)
      return found.object;
    return null;
  }

  /**
   * Search the tree for the matching element, or the 'nearest' node.
   */
  protected IntervalNode<Key> searchNearest(Interval<Key> element, SearchNearest option) {
    if (element == null)
      return null;
    IntervalNode<Key> x = root;
    if (x == null)
      return null;
    IntervalNode<Key> previous = x;
    int compare = 0;
    while (x != null) {
      previous = x;
      compare = comparator.compare(element, x.object);
      if (0 == compare) {
        return x; // comparator.compare should check for further
        // restrictions.
      } else if (compare < 0) {
        x = x.left;
      } else {
        x = x.right;
      }
    }

    if (option == SearchNearest.SEARCH_NEAREST_ROUNDED_UP) {
      return (compare < 0) ? previous : successor(previous);
    } else if (option == SearchNearest.SEARCH_NEAREST_ROUNDED_DOWN) {
      return (compare < 0) ? predecessor(previous) : previous;
    }
    // Default: nearest absolute value
    // Fell off the tree looking for the exact match; now we need
    // to find the nearest element.
    x = (compare < 0) ? predecessor(previous) : successor(previous);
    if (x == null)
      return previous;
    int otherCompare = comparator.compare(element, x.object);
    if (compare < 0) {
      return Math.abs(compare) < otherCompare ? previous : x;
    }
    return Math.abs(otherCompare) < compare ? x : previous;
  }

  /**
   * Controls the results for searchNearest()
   */
  public static enum SearchNearest {
    SEARCH_NEAREST_ROUNDED_DOWN, // If result not found, always chose the lower element
    SEARCH_NEAREST_ABSOLUTE,     // If result not found, chose the nearest based on comparison
    SEARCH_NEAREST_ROUNDED_UP    // If result not found, always chose the higher element
  }

  /**
   * A node in the interval tree
   */
  static class IntervalNode<Key extends Comparable> {
    IntervalNode<Key> parent;
    IntervalNode<Key> left;
    IntervalNode<Key> right;
    int balanceFactor;
    Interval<Key> object;
    Key max;

    @Override
    public String toString() {
      boolean leftSet = left != null;
      boolean rightSet = right != null;
      StringBuilder strb = new StringBuilder("(b:" + balanceFactor + " o:" + object + " l:" + leftSet + " r:" + rightSet
              + " max:" + max + ")");
      strb.append("\n");
      strb.append("Left: " + (leftSet ? left.toString() : "/") + "~~~~~Right: " + (rightSet ? right.toString() : "/"));
      return strb.toString();
    }
  }
}