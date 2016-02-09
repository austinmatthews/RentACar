
/************************************************************************************
 * @file BpTreeMap.java
 *
 * @author  John Miller
 */

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

import static java.lang.System.out;

/************************************************************************************
 * This class provides B+Tree maps.  B+Trees are used as multi-level index structures
 * that provide efficient access for both point queries and range queries.
 * All keys will be at the leaf level with leaf nodes linked by references.
 * Internal nodes will contain divider keys such that divKey corresponds to the
 * largest key in its left subtree.
 */
public class BpTreeMap <K extends Comparable <K>, V>
extends AbstractMap <K, V>
implements Serializable, Cloneable, SortedMap <K, V>
{
	/** The maximum fanout (number of children) for a B+Tree node.
	 */
	private static final int ORDER = 5;

	/** The floor of half the ORDER.
	 */
	private static final int MID = ORDER / 2;

	/** The debug flag
	 */
	private static final boolean DEBUG = false;

	/** The class for type K.
	 */
	private final Class <K> classK;

	/** The class for type V.
	 */
	private final Class <V> classV;

	/********************************************************************************
	 * This inner class defines nodes that are stored in the B+tree map.
	 */
	private class Node
	{
		boolean   isLeaf;
		int       nKeys;
		K []      key;
		Object [] ref;

		@SuppressWarnings("unchecked")
		Node (boolean _isLeaf)
		{
			isLeaf = _isLeaf;
			nKeys  = 0;
			key    = (K []) Array.newInstance (classK, ORDER - 1);
			if (isLeaf) {
				//ref = (V []) Array.newInstance (classV, ORDER);
				ref = new Object [ORDER];
			} else {
				ref = (Node []) Array.newInstance (Node.class, ORDER);
			} // if
		} // constructor
	} // Node inner class

	/** The root of the B+Tree
	 */
	private Node root;

	/** The counter for the number nodes accessed (for performance testing).
	 */
	private int count = 0;
	
	/********************************************************************************
	 * A getter method to retrieve root of tree for testing purposes.
	 * @return the root of the BpTreeMap
	 */
	public Node getRoot() {
		return this.root;
	}

	/********************************************************************************
	 * Construct an empty B+Tree map.
	 * @param _classK  the class for keys (K)
	 * @param _classV  the class for values (V)
	 */
	@SuppressWarnings("unchecked")
	public BpTreeMap (Class <K> _classK, Class <V> _classV)
	{
		classK = _classK;
		classV = _classV;
		root   = new Node (true);
	} // constructor

	/********************************************************************************
	 * Return null to use the natural order based on the key type.  This requires the
	 * key type to implement Comparable.
	 */
	@SuppressWarnings("unchecked")
	public Comparator <? super K> comparator () 
	{
		return null;
	} // comparator

	/********************************************************************************
	 * Return a set containing all the entries as pairs of keys and values.
	 * @return  the set view of the map
	 */
	@SuppressWarnings("unchecked")
	public Set <Map.Entry <K, V>> entrySet ()
	{
		Set <Map.Entry <K, V>> enSet = new HashSet <> ();

		//  T O   B E   I M P L E M E N T E D

		return enSet;
	} // entrySet

	/********************************************************************************
	 * Given the key, look up the value in the B+Tree map.
	 * @param key  the key used for look up
	 * @return  the value associated with the key or null if not found
	 */
	@SuppressWarnings("unchecked")
	public V get (Object key)
	{
		return find ((K) key, root);
	} // get

	/********************************************************************************
	 * Put the key-value pair in the B+Tree map.
	 * @param key    the key to insert
	 * @param value  the value to insert
	 * @return  null, not the previous value for this key
	 */
	@SuppressWarnings("unchecked")
	public V put (K key, V value)
	{
		insert (key, value, root);
		return null;
	} // put

	/********************************************************************************
	 * Return the first (smallest) key in the B+Tree map.
	 * @return  the first key in the B+Tree map.
	 */
	@SuppressWarnings("unchecked")
	public K firstKey () 
	{
		Node temp = root;
		//traverse down the left side of the tree
		while (!temp.isLeaf){
			temp = (Node) temp.ref[0];
		}
		return temp.key[0];

	} // firstKey

	/********************************************************************************
	 * Return the last (largest) key in the B+Tree map.
	 * @return  the last key in the B+Tree map.
	 */
	@SuppressWarnings("unchecked")
	public K lastKey () 
	{
		Node temp = root;
		//traverse down the right side of the tree
		while (!temp.isLeaf){
			temp = (Node) temp.ref[temp.nKeys];
		}
		return temp.key[temp.nKeys];

	} // lastKey

	/********************************************************************************
	 * Return the portion of the B+Tree map where key < toKey.
	 * @return  the submap with keys in the range [firstKey, toKey)
	 */
	@SuppressWarnings("unchecked")
	public SortedMap <K,V> headMap (K toKey)
	{
		return subMap(firstKey(), toKey);
	} // headMap

	/********************************************************************************
	 * Return the portion of the B+Tree map where fromKey <= key.
	 * @return  the submap with keys in the range [fromKey, lastKey]
	 */
	@SuppressWarnings("unchecked")
	public SortedMap <K,V> tailMap (K fromKey)
	{
		SortedMap<K,V> tail = subMap(fromKey, lastKey());
		tail.put(lastKey(),get(lastKey()));

		return tail;
	} // tailMap

	/********************************************************************************
	 * Return the portion of the B+Tree map whose keys are between fromKey and toKey,
	 * i.e., fromKey <= key < toKey.
	 * @return  the submap with keys in the range [fromKey, toKey)
	 */
	@SuppressWarnings("unchecked")
	public SortedMap <K,V> subMap (K fromKey, K toKey)
	{
		SortedMap<K,V> sub = new TreeMap<>();
		Node temp = root;
		//traverse down the left side of the tree
		//can make this more efficient by searching instead for the correct node to start at
		//and stopping when we get to toKey instead of iterating through every leaf node.
		//I don't think this really matters though, I think it just has to be implemented to 
		//extend SortedMap
		while (!temp.isLeaf){
			temp = (Node) temp.ref[0];
		}
		do {
			int i=0;
			while (i<temp.nKeys){
				if (temp.key[i]!=null && fromKey.compareTo(temp.key[i]) > 0 && temp.key[i].compareTo(toKey) < 0) {
					sub.put(temp.key[i], (V) temp.ref[i]);
				}
				i++;
			}
			temp = (Node) temp.ref[i];
		}
		while (temp.ref[temp.nKeys]!= null);
		return sub;
	} // subMap

	/********************************************************************************
	 * Return the size (number of keys) in the B+Tree.
	 * @return  the size of the B+Tree
	 */
	@SuppressWarnings("unchecked")
	public int size ()
	{
		int sum = 0;

		Node temp = root;
		//traverse down the left side of the tree
		while (!temp.isLeaf){
			temp = (Node) temp.ref[0];
		}

		//then count keys in each node
		while(temp.ref[temp.nKeys]!= null){
			sum += temp.nKeys;
			temp = (Node) temp.ref[temp.nKeys];
		}

		sum += temp.nKeys;
		return sum;

	} // size

	/********************************************************************************
	 * Print the B+Tree using a pre-order traveral and indenting each level.
	 * @param n      the current node to print
	 * @param level  the current level of the B+Tree
	 */
	@SuppressWarnings("unchecked")
	public void print (Node n, int level)
	{
		out.println ("BpTreeMap");
		out.println ("-------------------------------------------");

		for (int j = 0; j < level; j++) out.print ("\t");
		out.print ("[ . ");
		for (int i = 0; i < n.nKeys; i++) out.print (n.key [i] + " . ");
		out.println ("]");
		if ( ! n.isLeaf) {
			for (int i = 0; i <= n.nKeys; i++) {
//				out.println ("this node reference #" + i);
				print ((Node) n.ref [i], level + 1);
			}
		} // if

		out.println ("-------------------------------------------");
	} // print

	/********************************************************************************
	 * Recursive helper function for finding a key in B+trees.
	 * @param key  the key to find
	 * @param ney  the current node
	 */
	@SuppressWarnings("unchecked")
	private V find (K key, Node n)
	{
		count++;
		for (int i = 0; i < n.nKeys; i++) {
			K k_i = n.key [i];
			if (key.compareTo (k_i) <= 0) {
				if (n.isLeaf) {
					return (key.equals (k_i)) ? (V) n.ref [i] : null;
				} 
				else {
					return find (key, (Node) n.ref [i]);
				} // if
			} // if
		} // for
		return (n.isLeaf) ? null : find (key, (Node) n.ref [n.nKeys]);
	} // find
    /********************************************************************************
     * Recursive helper function for inserting a key in B+trees.
     * @param key  the key to insert
     * @param ref  the value/node to insert
     * @param n    the current node
     * @return  the node inserted into (may wish to return more information)
     */
    @SuppressWarnings("unchecked")
    private Node insert (K key, V ref, Node n)
    {
        Node returnNode = null;
        boolean inserted = false;
        if (n.isLeaf) {                                  // handle leaf node

            if (n.nKeys < ORDER - 1) {
                for (int i = 0; i < n.nKeys; i++) {
                    K k_i = n.key [i];
                    if (key.compareTo (k_i) < 0) {
                        wedgeL (key, ref, n, i);
                        inserted = true;
                        count++;
                        break;
                    } else if (key.equals (k_i)) {
                        out.println ("BpTreeMap.insert: attempt to insert duplicate key = " + key);
                        inserted = true;
                        break;
                    } // if
                } // for
                if (! inserted){
                    wedgeL (key, ref, n, n.nKeys);
                    count++;
                }
                return null;
            } else {

                for (int i = 0; i < n.nKeys; i++) {
                    K k_i = n.key [i];
                    if (key.equals (k_i)) {
                        out.println ("BpTreeMap.insert: attempt to insert duplicate key = " + key);
                        inserted = true;
                        break;
                    } // if
                } // for
                if(!inserted){
                    Node sib = splitL (key, ref, n);
                    n.ref[ORDER-1] = sib;
                    if(n == root){
                        root = new Node(false);
                        root.key[0] = n.key[n.nKeys-1];
                        root.ref[0] = n;
                        root.ref[1] = sib;
                        root.nKeys++;
                    } else {
                        returnNode = new Node(false);
                        returnNode.key[0] = n.key[n.nKeys-1];
                        returnNode.ref[0] = sib;
                        returnNode.nKeys++;
                        return returnNode;
                    }
                }

            } // if

        } else {                                         // handle internal node

            for(int i = 0; i < n.nKeys; i++){
                K k_i = n.key[i];
                if(key.compareTo(k_i) < 0){
                    returnNode = insert(key, ref, (Node)n.ref[i]);
                    if(returnNode != null){
                        k_i = returnNode.key[0];
                        if(n.nKeys < ORDER-1) wedgeI(k_i, (V)returnNode.ref[0], n, i);
                        else{
                            Node sib = splitI(key, ref, n);
                            if(sib.key[sib.nKeys] != null){
                                Node up = new Node(false);
                                up.key[0] = sib.key[sib.nKeys];
                                sib.key[sib.nKeys] = null;
                                up.ref[0] = sib.ref[sib.nKeys+1];
                                sib.ref[sib.nKeys+1] = null;
                                up.ref[0] = sib.ref[sib.nKeys+2];
                                sib.ref[sib.nKeys+2] = null;
                                up.nKeys++;
                                findPos(n, sib, k_i, (V)returnNode.ref[0], up.key[0]);
                                return up;
                            } else {
                                findPos(n, sib, k_i, (V)returnNode.ref[0], root.key[0]);
                            }
                        }
                    }
                    break;
                } else if(key.compareTo(k_i) == 0){
                    out.println ("BpTreeMap.insert2: attempt to insert duplicate key = " + key);
                    break;
                }
                if(i == n.nKeys-1 && key.compareTo(k_i) > 0){
                    returnNode = insert(key, ref, (Node)n.ref[n.nKeys]);
                    if(returnNode != null){
                        k_i = returnNode.key[0];
                        if(n.nKeys< ORDER-1) wedgeI(k_i, (V)returnNode.ref[0], n, n.nKeys);
                        else{
                            Node sib = splitI(key, ref, n);
                            if(sib.key[sib.nKeys] != null){
                                Node up = new Node(false);
                                up.key[0] = sib.key[sib.nKeys];
                                sib.key[sib.nKeys] = null;
                                up.ref[0] = sib.ref[sib.nKeys+1];
                                sib.ref[sib.nKeys+1] = null;
                                up.ref[0] = sib.ref[sib.nKeys+2];
                                sib.ref[sib.nKeys+2] = null;
                                up.nKeys++;
                                findPos(n, sib, k_i, (V)returnNode.ref[0], up.key[0]);
                                return up;
                            } else {
                                findPos(n, sib, k_i, (V)returnNode.ref[0], root.key[0]);
                            }                      
                        }
                        break;
                    }
                }
            }
            

        } // if

        if (DEBUG) print (root, 0);
        return null;                                     // FIX: return useful information
    } // insert

    /**
    * @param n      the current node
    * @param sib    the sibling node
    * @param key    the key to insert
    * @param ref    the node to insert
    */
    private void findPos(Node n, Node sib, K key, V ref, K parentKey){
            out.println("in findPOs");

            if(key.compareTo(parentKey) > 0){
                for(int j = 0; j < sib.nKeys; j++){
                    if(key.compareTo(sib.key[j]) < 0){
                        wedgeI(key, ref, sib, j);
                        break;
                    }
                    if(j == sib.nKeys-1 && key.compareTo(sib.key[j]) > 0){
                        wedgeI(key, ref, sib, sib.nKeys);
                        break;
                    }
                }
            } else {
                for(int j = 0; j < n.nKeys; j++){
                    if(key.compareTo(n.key[j]) < 0){
                        wedgeI(key, ref, n, j);
                        break;
                    }
                    if(j == n.nKeys-1 && key.compareTo(n.key[j]) > 0){
                        wedgeI(key, ref, n, n.nKeys);
                        break;
                    }
                }
            }
    }


    /********************************************************************************
     * Wedge the key-ref pair into leaf node n.
     * @param key  the key to insert
     * @param ref  the value/node to insert
     * @param n    the current node
     * @param i    the insertion position within node n
     */
    private void wedgeL (K key, V ref, Node n, int i)
    {
        for (int j = n.nKeys; j > i; j--) {
            n.key [j] = n.key [j-1];
            n.ref [j] = n.ref [j-1];
        } // for
        n.key [i] = key;
        n.ref [i] = ref;
        n.nKeys++;
    } // wedgeL

    /********************************************************************************
     * Wedge the key-ref pair into internal node n.
     * @param key  the key to insert
     * @param ref  the value/node to insert
     * @param n    the current node
     * @param i    the insertion position within node n
     */
    private void wedgeI (K key, V ref, Node n, int i)
    {
        out.println ("wedgeI not implemented yet");

        for(int j = n.nKeys; j > i; j--){
            n.key[j]   = n.key[j-1];
            n.ref[j+1] = n.ref[j];
        }
        n.key[i]   = key;
        n.ref[i+1] = ref;
        n.nKeys++;

    } // wedgeI

    /********************************************************************************
     * Split leaf node n and return the newly created right sibling node rt.
     * Split first (MID keys for both node n and node rt), then add the new key and ref.
     * @param key  the new key to insert
     * @param ref  the new value/node to insert
     * @param n    the current node
     * @return  the right sibling node (may wish to provide more information)
     */
    private Node splitL (K key, V ref, Node n)
    {
        out.println ("splitL not implemented yet");
        Node rt = new Node (true);
        int numKeys = n.nKeys;

        for(int i = MID; i < numKeys; i++){
            rt.key[i - MID] = n.key[i];
            rt.ref[i - MID] = n.ref[i];
            n.key[i] = null;
            n.ref[i] = null;
            n.nKeys--;
            rt.nKeys++;
        }

        if(key.compareTo(n.key[n.nKeys-1]) > 0) insert(key, ref, rt);
        else insert(key, ref, n);

        //  T O   B E   I M P L E M E N T E D

        return rt;
    } // splitL

    /********************************************************************************
     * Split internal node n and return the newly created right sibling node rt.
     * Split first (MID keys for node n and MID-1 for node rt), then add the new key and ref.
     * @param key  the new key to insert
     * @param ref  the new value/node to insert
     * @param n    the current node
     * @return  the right sibling node (may wish to provide more information)
     */
    private Node splitI (K key, V ref, Node n)
    {
        out.println ("splitI not implemented yet");
        Node rt = new Node (false);

        int numKeys = n.nKeys;

        for(int i = MID; i < numKeys; i++){
            rt.key[i - MID] = n.key[i];
            rt.ref[i - MID] = n.ref[i];
            n.key[i] = null;
            n.ref[i] = null;
            n.nKeys--;
            rt.nKeys++;
        }
        rt.ref[rt.nKeys] = n.ref[ORDER-1];
        n.ref[ORDER-1] = null;

        if(n == root){
            root = new Node(false);
            root.key[0] = n.key[MID-1];
            n.key[MID-1] = null;
            n.nKeys--;
            root.ref[0] = n;
            root.ref[1] = rt;
            root.nKeys++;
            return rt; //meaning it is the root
        } else {
            rt.key[rt.nKeys] = n.key[MID-1];
            n.key[MID-1] = null;
            rt.ref[rt.nKeys+1] = n;
            rt.ref[rt.nKeys+2] = rt;
            n.nKeys--;
            return rt;
        }

    } // splitI

	/********************************************************************************
	 * The main method used for testing.
	 * @param  the command-line arguments (args [0] gives number of keys to insert)
	 */
	public static void main (String [] args)
	{
		int totalKeys    = 9;
		boolean RANDOMLY = false;

		BpTreeMap <Integer, Integer> bpt = new BpTreeMap <> (Integer.class, Integer.class);
		if (args.length == 1) totalKeys = Integer.valueOf (args [0]);

		if (RANDOMLY) {
			Random rng = new Random ();
			for (int i = 1; i <= totalKeys; i += 2) bpt.put (rng.nextInt (2 * totalKeys), i * i);
		} 
		else {
			for (int i = 1; i <= totalKeys; i += 2) bpt.put (i, i * i);
		} // if

		bpt.print (bpt.root, 0);
		for (int i = 1; i <= totalKeys; i+=2) {
			out.println ("key = " + i + " value = " + bpt.get (i));
		} // for
		out.println ("-------------------------------------------");
		out.println ("Average number of nodes accessed = " + bpt.count / (double) totalKeys);
	} // main

} // BpTreeMap class