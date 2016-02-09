

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
		boolean negative = true;
		boolean manual = false;
		//02-04-16 @3:14pm.  keys = 21 and negative=RANDOMLY=false passes test!!!

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

					bpt.put(-i,  i + 100);
				}
			}
		} 
		else if (manual) {

			bpt.put(5, 500);
			bpt.put(10, 1000);
			bpt.put(15, 1500);
			bpt.put(20, 2000);
			bpt.put(1, 100);
//			bpt.put(3, 300);
//			bpt.put(30, 3000);
//			bpt.put(6, 600);
//			bpt.put(7, 700);

			out.println ("key = " + 5 + " value = " + bpt.get (5));
			out.println ("key = " + 10 + " value = " + bpt.get (10));
			out.println ("key = " + 15 + " value = " + bpt.get (15));
			out.println ("key = " + 20 + " value = " + bpt.get (20));
			out.println ("key = " + 1 + " value = " + bpt.get (1));

			
		}
		else {
			for (int i = 1; i <= totalKeys; i++) { 
				bpt.put (i, i + 100);
			}
		} // if


		bpt.print (bpt.getRoot(), 0);
		for (int i = 1; i <= totalKeys; i++) {
			//			out.println ("key = " + i + " value = " + bpt.get (i));
			if (negative) {
				if ((i % 2) == 0) {
					out.println ("key = " + i + " value = " + bpt.get (i));
				}
				else {
					out.println ("key = -" + i + " value = " + bpt.get (-i));
				}
			}
			else if (manual) {
				//do nothing in loop
			}
			else {
				out.println ("key = " + i + " value = " + bpt.get (i));

			}
		} // for
		out.println ("-------------------------------------------");
		//		out.println ("Average number of nodes accessed = " + bpt.count / (double) totalKeys);
	} // main
}
