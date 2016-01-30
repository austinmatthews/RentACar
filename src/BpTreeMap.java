
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
	private static final boolean DEBUG = true;

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
	 * Construct an empty B+Tree map.
	 * @param _classK  the class for keys (K)
	 * @param _classV  the class for values (V)
	 */
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
	public Comparator <? super K> comparator () 
	{
		return null;
	} // comparator

	/********************************************************************************
	 * Return a set containing all the entries as pairs of keys and values.
	 * @return  the set view of the map
	 */
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
	public SortedMap <K,V> headMap (K toKey)
	{
		return subMap(firstKey(), toKey);
	} // headMap

	/********************************************************************************
	 * Return the portion of the B+Tree map where fromKey <= key.
	 * @return  the submap with keys in the range [fromKey, lastKey]
	 */
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
	private void print (Node n, int level)
	{
		out.println ("BpTreeMap");
		out.println ("-------------------------------------------");

		for (int j = 0; j < level; j++) out.print ("\t");
		out.print ("[ . ");
		for (int i = 0; i < n.nKeys; i++) out.print (n.key [i] + " . ");
		out.println ("]");
		if ( ! n.isLeaf) {
			for (int i = 0; i <= n.nKeys; i++) print ((Node) n.ref [i], level + 1);
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
				} else {
					return find (key, (Node) n.ref [i]);
				} // if
			} // if
		} // for
		return (n.isLeaf) ? null : find (key, (Node) n.ref [n.nKeys]);
	} // find

	/********************************************************************************
	 * Recursive helper function for inserting a key in B+trees.
	 * @param key  the key to insert
	 * @param tupleRef  the value/node to insert
	 * @param n    the current node
	 * @return  the node inserted into (may wish to return more information)
	 */
	private Node insert (K key, V tupleRef, Node n)
	{
		Node returnNode = n;
		boolean inserted = false;
		if (n.isLeaf) {                  // handle leaf node

			if (n.nKeys < ORDER - 1) {      //handle leaf if it does not need to split
				for (int i = 0; i < n.nKeys; i++) {
					K k_i = n.key [i];
					if (key.compareTo (k_i) < 0) {
						wedgeL (key, tupleRef, n, i);
						inserted = true;
						break;
					}
					else if (key.equals (k_i)) {
						out.println ("BpTreeMap.insert: attempt to insert duplicate key = " + key);
						inserted = true;
						break;
					} // if
				} // for
				if (! inserted) {

					wedgeL (key, tupleRef, n, n.nKeys);
				}
			}
			else {      //handle leaf if it needs to SPLIT!

				Node sib = splitL (key, tupleRef, n);
				returnNode = sib;

				//Create new root if and only if n == root.
				//This is the base case of splitting and will be reached the only the first time a node is split.
				//Is this syntax correct? Should it be n.equals(root)?
				if (n == root) {
					Node newRoot = new Node(false);
					root = newRoot;
				}
			} // if
		}
		else {                                         // handle internal node

			//Begin ECH and KAH Code

			Node newNode = null;
			boolean foundChild = false;
			int childIndex = 0;
			//Need to find which child node the key belongs in
			//Iterate through Node n's keys in order to determine which child the new key will be inserted into.
			//Then call insert on that child node
			for (int i = 0; i < nKeys; i++) {
				K k_n = n.key[i];
				if (key.compareTo(k_n) <= 0) {
					//go to child in ref[i]
					newNode = insert(key, tupleRef, n.ref[i]);
					childIndex = i;
					foundChild = true;
					break;
				}
			}
			if (!foundChild) {




				newNode = insert(key, tupleRef, n.ref[nKeys]);
				childIndex = nKeys;
				foundChild = true;
			}

			//if newNode is original child, parent does not change
			//if newNode is right sibling, parent must wedge newNode.key[0]
			//then possibility of parent splitting (divorce) must be addressed.
			//Is this syntax correct? Should it be !(newNode.equals(n.ref[childIndex])?
			if (newNode != n.ref[childIndex]) {
				inserted = false;
				if (n.nKeys < ORDER - 1) {                  
					for (int i = 0; i < n.nKeys; i++) {
						K k_i = n.key [i]; 
						if (key.compareTo (k_i) < 0) { 
							Node rtChild = wedgeI(newNode.key[0], tupleRef, n, i);
							returnNode = rtChild;

							inserted = true;
							break;
						}
						else if (key.equals (k_i)) { 
							out.println ("BpTreeMap.insert: attempt to insert duplicate key = " + key);
							inserted = true;
							break;
						} // if
					} // for
					//If key has not been wedged in at this point, it needs to go into the last available spot of the node.
					if (! inserted) {
						Node rtChild = wedgeI(newNode.key[0], tupleRef, n, n.nKeys);
						returnNode = rtChild;
					}
				}
				//If the parent was full, then we need to split it.
				else {
					Node sib = splitI (key, tupleRef, n);
					returnNode = sib;
				}

			}



			//Still need to consider root and what happens when it splits.
			//Handle in splitI by checking if node to be split is the root.




			//Keep in mind that tuple is ONLY stored in leaf




			//End ECH and KAH Code
			//  T O   B E   I M P L E M E N T E D

		} // if

		if (DEBUG) print (root, 0);
		return returnNode;                                     // FIX: return useful information

	} // insert

	/********************************************************************************
	 * Wedge the key-ref pair into leaf node n.
	 * @param key  the key to insert
	 * @param tupleRef  the value/node to insert
	 * @param n    the current node
	 * @param i    the insertion position within node n
	 */
	private void wedgeL (K key, V tupleRef, Node n, int i) 
	{
		for (int j = n.nKeys; j > i; j--) { 
			n.key [j] = n.key [j-1]; 
			n.ref [j] = n.ref [j-1]; 
		} // for
		n.key [i] = key; 
		n.ref [i] = tupleRef; 
		n.nKeys++; 
	} // wedgeL


	/********************************************************************************
	 * Wedge the key-ref pair into internal node n.
	 * @param key  the key to insert
	 * @param tupleRef  the value/node to insert
	 * @param n    the current node
	 * @param i    the insertion position within node n
	 */
	private void wedgeI (K key, V tupleRef, Node n, int i)
	{
		out.println ("wedgeI not implemented yet");

		//  T O   B E   I M P L E M E N T E D

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
		//Per Jinze Li ignore changes to parent node.

		//out.println ("splitL not implemented yet");
		Node rt = new Node (true);

		//Begin ECH and KAH code

		//Psuedo-psuedo code
		//split based on mid point.
		//for i < MID; move n.ref[] values to rt.ref[]
		//move n.ref[] values to beginning so that MID value is in ref[1]
		//determine which node (key, value) belong in
		//insert them into that node using wedgeL
		//The End???

		int j = MID;
		//This loop will split our key array into two separate arrays at MID. We don't need to "delete" keys from n.key because the insert method only looks at the number of keys in the node (not the array size) i.e. we have some trash left over but it doesn't hurt us.
		for (int i = 0; i < MID; i++) {
			rt.key[i] = n.key[j];
			j++;
		}

		//Setting nKeys to MID on both nodes to indicate which positions of the array hold meaningful values.
		rt.nKeys = MID;
		n.nKeys = MID;

		//The next two loops will determine which node the key will be inserted into.
		boolean foundHome = false;
		//Check if the key belongs to the left node, n.
		for (int i = 0; i < MID; i++) {
			K k_n = n.key [i];
			if (key.compareTo(k_n) <= 0) {
				wedgeL( key, ref, n, i);
				foundHome = true;
				break;
			}
		}

		//Check if the key belongs to the right node, rt.
		if (!foundHome){
			for (int i = 0; i < MID; i++) {
				K k_rt = rt.key [i];
				if (key.compareTo(k_rt) <= 0) {
					wedgeL( key, ref, rt, i);
					foundHome = true;
					break;
				}
			}
			//If foundHome is still false, key is the greatest value in either node and needs to be inserted at the end of node rt.
			if(!foundHome) {
				wedgeL( key, ref, rt, MID);
			}
		}
		
		//Setting the last element of Node n's ref array to point to the right sibling node.
		n.ref[MID] = rt;
		//End ECH and KAH code.

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

		//  T O   B E   I M P L E M E N T E D

		return rt;
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
		} else {
			for (int i = 1; i <= totalKeys; i += 2) bpt.put (i, i * i);
		} // if

		bpt.print (bpt.root, 0);
		for (int i = 0; i <= totalKeys; i++) {
			out.println ("key = " + i + " value = " + bpt.get (i));
		} // for
		out.println ("-------------------------------------------");
		out.println ("Average number of nodes accessed = " + bpt.count / (double) totalKeys);
	} // main

} // BpTreeMap class