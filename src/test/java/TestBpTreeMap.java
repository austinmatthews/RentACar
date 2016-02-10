import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class TestBpTreeMap {
  
  @Test
  public void testSplitL() {
    System.out.println("SPLIT L TESTS");
    
    BpTreeMap <Integer, Integer> bpt = new BpTreeMap <> (Integer.class, Integer.class);
    
    
    for(int i = 0; i < 15; i = i+3){
      bpt.put(i, i);
    } 
    
    System.out.println("Test initial splitL");
    assertEquals("3", bpt.rootToString(0));
    
    System.out.println("Test splitL left child.");
    bpt.put(1, 1);
    bpt.put(2, 2);
    bpt.put(-1, -1);
    assertEquals("1", bpt.rootToString(0));
    
    System.out.println("Test splitL right Child.");
    bpt.put(4, 4);
    bpt.put(5, 5);
    assertEquals("6", bpt.rootToString(2));
  }
  
  @Test
  public void testWedgeL() {
    System.out.println("WEDGE L TESTS");
    BpTreeMap <Integer, Integer> bpt = new BpTreeMap <> (Integer.class, Integer.class);
    
    
    for(int i = 0; i < 15; i = i+3){
      bpt.put(i, i);
    } 
    
    assertEquals("3", bpt.rootToString(0));
    bpt.put(1, 1);
    bpt.put(2, 2);
    bpt.put(-1, -1);
    assertEquals("1", bpt.rootToString(0));
    bpt.put(4, 4);
    bpt.put(5, 5);
    assertEquals("6", bpt.rootToString(2));
  }
  
  @Test
  public void testSplitI() {
    System.out.println("SPLIT I TESTS");
    BpTreeMap <Integer, Integer> bpt = new BpTreeMap <> (Integer.class, Integer.class);
    
    /*Sets tree up from where SplitL left off*/
    for(int i = 0; i < 15; i = i+3){
      bpt.put(i, i);
    } 
    assertEquals("3", bpt.rootToString(0));
    bpt.put(1, 1);
    bpt.put(2, 2);
    bpt.put(-1, -1);
    bpt.put(4, 4);
    bpt.put(5, 5);
    
    /*Ends set up from where SplitL left off.*/
    bpt.put(7, 7);
    bpt.put(8, 8);
    bpt.put(10, 10);
    bpt.put(11, 11);
    System.out.println("Test SplitI");
    bpt.put(13, 13);
    assertEquals("3", bpt.rootToString(0));
  }
  
  @Test
  public void testWedgeI() {
    System.out.println("WEDGE I TESTS");
    BpTreeMap <Integer, Integer> bpt = new BpTreeMap <> (Integer.class, Integer.class);
    
    /*Sets tree up from where SplitL left off*/
    for(int i = 0; i < 15; i = i+3){
      bpt.put(i, i);
    } 
    assertEquals("3", bpt.rootToString(0));
    bpt.put(1, 1);
    bpt.put(2, 2);
    bpt.put(-1, -1);
    bpt.put(4, 4);
    bpt.put(5, 5);
    
    /*Ends set up from where SplitL left off.*/
    bpt.put(7, 7);
    bpt.put(8, 8);
    bpt.put(10, 10);
    bpt.put(11, 11);
    bpt.put(13, 13);
    assertEquals("3", bpt.rootToString(0));
  }
  
  @Test
  public void testInsert() {
    BpTreeMap <Integer, Integer> bpt = new BpTreeMap <> (Integer.class, Integer.class);
    
    /*Sets tree up from where SplitL left off*/
    for(int i = 0; i < 15; i = i+3){
      bpt.put(i, i);
    } 
    assertEquals("3", bpt.rootToString(0));
    bpt.put(1, 1);
    bpt.put(2, 2);
    bpt.put(-1, -1);
    bpt.put(4, 4);
    bpt.put(5, 5);
    
    /*Ends set up from where SplitL left off.*/
    bpt.put(7, 7);
    bpt.put(8, 8);
    bpt.put(10, 10);
    bpt.put(11, 11);
    bpt.put(13, 13);
    assertEquals("3", bpt.rootToString(0));
  }
}