import java.lang.StringBuilder;
import java.util.Arrays;

public class Message {

   private final String text;

   public Message(String text) {
      this.text = text;
   }

   public String print() {
      return text;
   }

   // this is an overly simplified and mostly wrong implementation!
   // Try to come up with a correct implementation and use a test
   // driven process.
   public Message indent() {
    return new Message("  " + text);
   }
}
