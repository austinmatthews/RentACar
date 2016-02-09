import org.junit.Test;
import static org.junit.Assert.assertEquals;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

public class LinHashMapTest {
   private String message;

   //Constructor
   //@param message to be printed
   public LinHashMapTest(String message){
      this.message = message;
   }
      
   // prints the message
   public String printMessage(){
      System.out.println(message);
      return message;
   }  
}