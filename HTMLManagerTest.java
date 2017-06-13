// CSE 143
// This testing program stub creates a queue of HTML tags 
// in a valid sequence.
// You may use this as a starting point for testing
// your removeAll method.
import java.util.*;

public class HTMLManagerTest {
	public static void main(String[] args) {
		// <b>Hi</b><br/>
		Queue<HTMLTag> tags = new LinkedList<HTMLTag>();
		tags.add(new HTMLTag("b", HTMLTagType.OPENING));      // <b>
		tags.add(new HTMLTag("b", HTMLTagType.CLOSING));     // </b>
		tags.add(new HTMLTag("br", HTMLTagType.SELF_CLOSING));   // <br/> 
          		
		HTMLManager manager = new HTMLManager(tags);

		// YOUR TESTS GO HERE
      
      // test 1
      List<HTMLTag> list = manager.getTags();
      HTMLTag tag = list.get(0);
      manager.removeAll(new HTMLTag("b", HTMLTagType.OPENING));
      checkTags(tag, list, manager);
      
      // test 2
      HTMLTag tag2 = list.get(0);
      manager.removeAll(new HTMLTag("b", HTMLTagType.CLOSING));
      checkTags(tag2, list, manager);
      
      // test 3
      HTMLTag tag3 = list.get(0);
      manager.removeAll(new HTMLTag("br", HTMLTagType.SELF_CLOSING));
      checkTags(tag3, list, manager);
   }
   
   // this method gets the list, removed tag, and manager to test the removeAll
   // the list with the removed tags is printed to see the list then tested if
   // the remove tag is contained in the list
   // if the list contains the tag that should be removed the test fails
   // otherwise the test passes
   public static void checkTags(HTMLTag tag, List<HTMLTag> list, 
                                HTMLManager manager) {
      list = manager.getTags();
      System.out.println(list);
      if (list.contains(tag)) {
         System.out.println("Test failed.");
      } else {
         System.out.println("Test passed!");
      } 
   } 
}