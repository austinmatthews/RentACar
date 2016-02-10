import org.junit.Test;
import static org.junit.Assert.assertEquals;
import java.util.*;

public class TestLinHashMap {
	
   LinHashMap <Integer, Integer> map = new LinHashMap<>(Integer.class, Integer.class, 4);

   /**
   * This test is for insert and print. This is not a traditional method
   * but I feel it is the best way to accurately see how our tree inserts and prints
   */
   @Test
   public void testInsert() {
      
      Random r = new Random();

      for(int i = 0; i < 100; i++){
         int rand = r.nextInt(1000);
         map.put(rand, rand);
      }

      map.print();

   }

   /**
   * This methods adds a key, value pair to the map (in case it was not inserted randomly)
   * and then retrieves that number. The second test is to try and grab a value we know it not in
   * the hash table ( > 1000) which should be null
   */
   @Test
   public void testGet(){

      map.put(57, 57);
      assertEquals((Integer)map.get(57), (Integer)57);

      assertEquals((Integer)map.get(1001), null);

   }

   /**
   * This tests the entrySet() method. It adds two key, value pairs to a new hashTable and calls entrySet().
   * Then it creates a hashSet and inserts Simple Map entries of the same key, value pairs. 
   * Then it checks if they are equal.
   */
   @Test
   public void testEntrySet(){
      LinHashMap <Integer, Integer> map2 = new LinHashMap<>(Integer.class, Integer.class, 4);
      map.put(1, 1);
      map.put(3, 3);

      Set <Map.Entry <Integer, Integer >> set = new HashSet<>();
      set.add(new AbstractMap.SimpleEntry<Integer, Integer>(1, 1));
      set.add(new AbstractMap.SimpleEntry<Integer, Integer>(3, 3));

      assertEquals(map.entrySet(), set);

   }

}