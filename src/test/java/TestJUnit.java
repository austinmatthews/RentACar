import org.junit.Test;
import static org.junit.Assert.assertEquals;
public class TestJUnit {
	
   String message = "Hello World";	
   LinHashMapTest messageUtil = new LinHashMapTest(message);
   String message2 = "Hello World!";
   LinHashMapTest messageUtil2 = new LinHashMapTest(message2);

   @Test
   public void testEntrySet() {
      assertEquals(message,messageUtil.printMessage());
   }

   @Test
   public void testPrintMessage2(){
   	  assertEquals(message, messageUtil2.printMessage());
   }


}