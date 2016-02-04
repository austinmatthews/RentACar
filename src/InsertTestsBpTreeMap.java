

import static java.lang.System.out;
import java.io.*;
import java.lang.reflect.Array;
import java.util.*;

import static java.lang.System.out;
import java.util.Random;

public class InsertTestsBpTreeMap {
	
	public static void main (String [] args)
	{
		int totalKeys    = 21;
		boolean RANDOMLY = false;
		boolean negative = false;
		//02-04-16 @3:14pm. total keys = 21 and negative=RANDOMLY=false passes test!!!

		BpTreeMap <Integer, Integer> bpt = new BpTreeMap <> (Integer.class, Integer.class);
		if (args.length == 1) { 
			totalKeys = Integer.valueOf (args [0]);
		}

		if (RANDOMLY) {
			Random rng = new Random ();
			for (int i = 1; i <= totalKeys; i += 2) { 
				bpt.put (rng.nextInt (2 * totalKeys), i * i); 
			}
		} 
		else if (negative) {
			for (int i = 1; i <= totalKeys; i++) { 
				if ((i % 2) == 0) {
					bpt.put (i, i + 100);
				}
				else {
					bpt.put(-i,  i +100);
				}
			}
		} 
		else {
			for (int i = 1; i <= totalKeys; i++) { 
				bpt.put (i, i + 100);
			}
		} // if


		bpt.print (bpt.getRoot(), 0);
		for (int i = 1; i <= totalKeys; i++) {
			out.println ("key = " + i + " value = " + bpt.get (i));
		} // for
		out.println ("-------------------------------------------");
//		out.println ("Average number of nodes accessed = " + bpt.count / (double) totalKeys);
	} // main
}
