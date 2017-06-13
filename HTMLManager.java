// Christian Hahn
// CSE 143 - Section DE
// TA: Sarah House
// 8 October 2016
// An HTML Manager class which manages the contents of an HTML page and
// fixes any HTML with bad or missing tags

import java.util.*;

public class HTMLManager {
   
	// Queue of HTMLTags to manage
	private Queue<HTMLTag> pageTags;
	
	// constructor that takes initializes the size and creates a new queue
	// takes in the page of HTMLTags and puts them into the queue
	// throws IllegalArgumentException if the queue passed in is null
	public HTMLManager(Queue<HTMLTag> page) {
		this.pageTags = new LinkedList<HTMLTag>();
		if(page == null) {
			throw new IllegalArgumentException();
		}
		while (page.peek() != null) {
			HTMLTag tag = page.remove();
			this.pageTags.add(tag);
		}
	}
	
	// passed in an HTMLTag and adds it to the end of the queue
	// throws IllegalArgumentException if tag passed in is null
	public void add(HTMLTag tag) {
		if (tag == null) {
			throw new IllegalArgumentException();
		}
		this.pageTags.add(tag);
	}
	
	// removes all instances in the queue of the given tag passed in
	// cycles through whole queue and removes tags while adjusting the size
	// throws IllegalArgumentException if tag passed in is null
	public void removeAll(HTMLTag tag) {
		if (tag == null) {
			throw new IllegalArgumentException();
		}		
		Queue<HTMLTag> temp = new LinkedList<HTMLTag>();
		while (this.pageTags.peek() != null) {
			HTMLTag currentTag = this.pageTags.remove();
			if (!currentTag.equals(tag)) {
				temp.add(currentTag);
			}
		}
		this.pageTags = temp;
	}
	
	// creates a list of HTMLTags and transfers all the tags in the queue
	// to the list and returns the list of tags
	public List<HTMLTag> getTags() {
		List<HTMLTag> tags = new ArrayList<HTMLTag>();
		Queue<HTMLTag> temp = new LinkedList<HTMLTag>();
		while (this.pageTags.peek() != null) {
			HTMLTag currentTag = this.pageTags.remove();
			tags.add(currentTag);
			temp.add(currentTag);
		}
		this.pageTags = temp;
		return tags;
	}
	
	// goes through the page of HTMLTags and fixes the HTML in case there
	// are any extra or missing tags
	// if there is a opening tag that does not match the closing tag found,
	// a matching tag is added
	// if there are any closing tags without opening tags, the tag is
	// discarded
	public void fixHTML() {
		Stack<HTMLTag> stackOfTags = new Stack<HTMLTag>();
		Queue<HTMLTag> output = new LinkedList<HTMLTag>();
		while (!pageTags.isEmpty()) {
			HTMLTag currentTag = this.pageTags.peek();
			if (currentTag.isOpening()) {
				stackOfTags.push(currentTag);
				output.add(currentTag);
				this.pageTags.remove();
			} else if (currentTag.isSelfClosing()) {
				output.add(currentTag);
				this.pageTags.remove();
			}  else if (currentTag.isClosing()) {
				if (!stackOfTags.isEmpty()) {
					HTMLTag openTag = stackOfTags.pop();
					if (openTag.matches(currentTag)) {
						output.add(currentTag);
						this.pageTags.remove();
					} else {
						// adds matching tag to output
						output.add(openTag.getMatching());
					}
					
				} else {
					this.pageTags.remove();
				}
			}
		}
		while (!stackOfTags.isEmpty()) {
			output.add(stackOfTags.pop().getMatching());
		}
		this.pageTags = output;
	}
}     
