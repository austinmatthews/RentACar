
/************************************************************************************
 * @file LinHashMap.java    
 *
 * @author  John Miller
 */

import java.io.*;
import java.lang.reflect.Array;
import static java.lang.System.out;
import java.util.*;

/************************************************************************************
 * This class provides hash maps that use the Linear Hashing algorithm.
 * A hash table is created that is an array of buckets.
 */
public class LinHashMap <K, V>
       extends AbstractMap <K, V>
       implements Serializable, Cloneable, Map <K, V>
{
    /** The number of slots (for key-value pairs) per bucket.
     */
    private static final int SLOTS = 4;

    /** The class for type K.
     */
    private final Class <K> classK;

    /** The class for type V.
     */
    private final Class <V> classV;

    /********************************************************************************
     * This inner class defines buckets that are stored in the hash table.
     */
    private class Bucket
    {
        int    nKeys;
        K []   key;
        V []   value;
        Bucket next;

        @SuppressWarnings("unchecked")
        Bucket (Bucket n)
        {
            nKeys = 0;
            key   = (K []) Array.newInstance (classK, SLOTS);
            value = (V []) Array.newInstance (classV, SLOTS);
            next  = n;
        } // constructor
    } // Bucket inner class

    /** The list of buckets making up the hash table.
     */
    private final List <Bucket> hTable;

    /** The modulus for low resolution hashing
     */
    private int mod1;

    /** The modulus for high resolution hashing
     */
    private int mod2;

    /** Counter for the number buckets accessed (for performance testing).
     */
    private int count = 0;

    /** The index of the next bucket to split.
     */
    private int split = 0;

    /********************************************************************************
     * Construct a hash table that uses Linear Hashing.
     * @param classK    the class for keys (K)
     * @param classV    the class for keys (V)
     * @param initSize  the initial number of home buckets (a power of 2, e.g., 4)
     */
    public LinHashMap (Class <K> _classK, Class <V> _classV)    // , int initSize)
    {
        classK = _classK;
        classV = _classV;
        hTable = new ArrayList <> ();
        mod1   = 4;                        // initSize;
        mod2   = 2 * mod1;
    } // constructor

    /********************************************************************************
     * Return a set containing all the entries as pairs of keys and values.
     * @return  the set view of the map
     */
    public Set <Map.Entry <K, V>> entrySet ()
    {
        Set <Map.Entry <K, V>> enSet = new HashSet <> ();

        Map <K, V> map = new HashMap<K, V>();

        for(int i = 0; i < hTable.size(); i++){
            Bucket b = hTable.get(i);
            while(b != null){
                for(int j = 0; j < b.nKeys; j++){
                    map.put(b.key[j], b.value[j]);
                }
                b = b.next;
            }
        }
            
        enSet = map.entrySet();
        System.out.println(enSet);

        return enSet;
    } // entrySet

    /********************************************************************************
     * Given the key, look up the value in the hash table.
     * @param key  the key used for look up
     * @return  the value associated with the key
     */
    public V get (Object key)
    {
        int i = h (key);

        //  T O   B E   I M P L E M E N T E D

        return null;
    } // get

    /********************************************************************************
     * Put the key-value pair in the hash table.
     * @param key    the key to insert
     * @param value  the value to insert
     * @return  null (not the previous value)
     */
    public V put (K key, V value)
    {
        //init
        if(hTable.size() == 0){
            for(int i = 0; i < 4; i++){
                hTable.add(new Bucket(null));
            }
        }

        int mod = h(key);
        if(mod < split) mod = h2(key);
        Bucket bucket = hTable.get(mod);

        out.println ("LinearHashMap.put: key = " + key + ", h() = " + mod + ", value = " + value);

        int num  = hTable.get(mod).nKeys;
        boolean overflow = true;

        if(num < 4){
            bucket.key[num]   = key;
            bucket.value[num] = value;
            overflow = false;
            bucket.nKeys++;
        }

        if(overflow){
            insertOverflow(hTable.get(mod), key, value);
            handleOverFlow();
            removeExtra(bucket);
        }

        // isEndRound()
        isEndRound();

        return null;
    } // put

    /**
    * Checks to see if it is the end of the round table size =  mod2
    */
    public void isEndRound(){
        if(hTable.size() % mod2 == 0){
            split = 0;
            mod1 = mod2;
            mod2 = mod2 * 2;
            System.out.println("Mod1: " + mod1);
            System.out.println("Mod2: " + mod2);
        }
    }

    /**
    * Removes the extra buckets after a overflow insert
    * @param remove - the bucket at mod to check if there are empty overflow buckets
    */
    public void removeExtra(Bucket remove){
        while(remove.next != null){
            if(remove.next.nKeys == 0){
                remove.next = null;
                break;
            }
            remove = remove.next;
        }
    }

    /**
    * Insert an ley-value that will result in overflow
    * @param row - the current bucket to insert to
    * @param key - the key to insert
    * @param value - the value to insert
    */
    public void insertOverflow(Bucket row, K key, V value){
        if(row.next == null){
            row.next = new Bucket(null);
            row.next.key[0]   = key;
            row.next.value[0] = value;
            row.next.nKeys++;
        } else {
            if(row.next.nKeys < 4){
                row.next.key[row.next.nKeys] = key;
                row.next.value[row.next.nKeys] = value;
                row.next.nKeys++;
            } else {
                insertOverflow(row.next, key, value);
            }

        }
    }

    /**
    * Handles the rehashing of all the values in the bucket as well as in it's overflow bucket
    */
    public void handleOverFlow(){
        hTable.add(new Bucket(null));
        Bucket temp = hTable.get(split);
        ArrayList<K> tempKeys = new ArrayList<K>();
        ArrayList<V> tempVals = new ArrayList<V>();
        while(temp != null){
            for(int k = 0; k < SLOTS; k++){
                if(temp.key[k] != null){
                    tempKeys.add(temp.key[k]);   temp.key[k]   = null; temp.nKeys--;
                    tempVals.add(temp.value[k]); temp.value[k] = null; 
                }
            }
            temp = temp.next;
        }
        int index1 = 0; int index2 = 0;
        Bucket tempMod1 = hTable.get(split);
        Bucket tempMod2 = hTable.get(split + mod1);
        if(!tempKeys.isEmpty()){
            for(int m = 0; m < tempKeys.size(); m++){
                if(h2(tempKeys.get(m)) == split){
                    for(int i = 0; i < Math.floor(index1/SLOTS); i++){
                        tempMod1 = tempMod1.next;
                    }
                    tempMod1.key[index1 % SLOTS] = tempKeys.get(m);
                    tempMod1.value[index1 % SLOTS] = tempVals.get(m);
                    tempMod1.nKeys++;
                    index1++;
                } else {
                    for(int i = 0; i < Math.floor(index2/SLOTS); i++){
                        tempMod2 = tempMod2.next;
                    }
                    tempMod2.key[index2 % SLOTS]  = tempKeys.get(m);
                    tempMod2.value[index2 % SLOTS] = tempVals.get(m);
                    tempMod2.nKeys++;
                    index2++;
                }
            }
        }
        split++;
    }

    /********************************************************************************
     * Return the size (SLOTS * number of home buckets) of the hash table. 
     * @return  the size of the hash table
     */
    public int size ()
    {
        return SLOTS * (mod1 + split);
    } // size


    /********************************************************************************
     * Print the hash table.
     */
    private void print ()
    {
        out.println ("Hash Table (Linear Hashing)");
        out.println ("-------------------------------------------");
        
        //Loop through each index of the hTable
        for(int i = 0; i < hTable.size(); i++){
        	//Print each index number
        	out.print(i + ": ");
        	//Get a bucket at index i
        	Bucket temp = hTable.get(i);
        	while(temp.next != null){
        		out.print("[ ");
        		//Loop through each index of the current bucket
        		for(int h = 0; h < SLOTS; h++){
        			//Print out the key at index h of the bucket
        			out.print(temp.key[h] + ",");
        		}
        		out.print("] ----> ");
        		temp = temp.next;
        	}
        	//There is now only one bucket left
        	out.print("[ ");
    		//Loop through each index of the current bucket
    		for(int h = 0; h < SLOTS; h++){
    			//Print out the key at index h of the bucket
    			out.print(temp.key[h] + ",");
    		}
    		out.print("]\n");
        }
        out.println ("-------------------------------------------");
    } // print


    /********************************************************************************
     * Hash the key using the low resolution hash function.
     * @param key  the key to hash
     * @return  the location of the bucket chain containing the key-value pair
     */
    private int h (Object key)
    {
        return key.hashCode () % mod1;
    } // h

    /********************************************************************************
     * Hash the key using the high resolution hash function.
     * @param key  the key to hash
     * @return  the location of the bucket chain containing the key-value pair
     */
    private int h2 (Object key)
    {
        return key.hashCode () % mod2;
    } // h2

    /********************************************************************************
     * The main method used for testing.
     * @param  the command-line arguments (args [0] gives number of keys to insert)
     */
    public static void main (String [] args)
    {

        int totalKeys    = 30;
        boolean RANDOMLY = false;

        LinHashMap <Integer, Integer> ht = new LinHashMap <> (Integer.class, Integer.class);
        if (args.length == 1) totalKeys = Integer.valueOf (args [0]);

        if (RANDOMLY) {
            Random rng = new Random ();
            for (int i = 1; i <= totalKeys; i += 2) ht.put (rng.nextInt (2 * totalKeys), i * i);
        } else {
            for (int i = 1; i <= totalKeys; i += 2) ht.put (i, i * i);
        } // if

        ht.print ();
        for (int i = 0; i <= totalKeys; i++) {
            out.println ("key = " + i + " value = " + ht.get (i));
        } // for
        out.println ("-------------------------------------------");
        out.println ("Average number of buckets accessed = " + ht.count / (double) totalKeys);
    } // main

} // LinHashMap class

