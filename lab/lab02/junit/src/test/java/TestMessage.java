import org.junit.*;
import static org.junit.Assert.*;

public class TestMessage {

   @Test
   public void testPrintingMessages() {
      printIsIdempotentOn("foo");
      printIsIdempotentOn("");
      printIsIdempotentOn("foo\nbar");
      printIsIdempotentOn("foo\nbar\n");
      printIsIdempotentOn("\n");
   }

   @Test
   public void testIndentingMessages() {
      indentingShouldBe("foo", "  foo");
   }

   private Message msg(String text) {
      return new Message(text);
   }

   private void printIsIdempotentOn(String str) {
      assertEquals(str, msg(str).print());
   }

   // This is a helper method for easier specification of expected indentation.
   // You can also try to come up with your own assertion method to test for
   // correct indentation.
   private void indentingShouldBe(String in, String expected) {
      assertEquals(expected, msg(in).indent().print());
   }
}
