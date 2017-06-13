// CSE 143, Homework 2: HTML Manager
// Instructor-provided code.  You should not modify this file!
// Resource file for HTMLManager.  Put this file in the same directory
// as HTMlManager.java, HTMLTag.java, HTMLTagType.java and HTMLMain.java.

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/** An HTMLTag object represents an HTML tag, such as <b> or </table>. */
public class HTMLTag {
    private static final Set<String> SELF_CLOSING_TAGS = new HashSet<String>(Arrays.asList("!DOCTYPE","area","base","basefont",
        "br","col","command","embed","hr","img","input","keygen","link","meta","param","wbr","track","?xml"));

    private final String element;
    private final String attributes;
    private final String contents;
    private final HTMLTagType type;
    public static final String INDENT_STRING = "    ";

    /** Constructs an HTML tag with the given element (e.g. "table"), type
      * and content as internal content.
      * Self-closing tags like <br /> are considered to be "opening" tags,
      * and return true from the isOpenTag method.
      * Throws a NullPointerException if element is null. */
    public HTMLTag(String element, HTMLTagType type, String contents) {
        if (contents == null) {
            contents = "";
        }
        if (element.startsWith("--")) {
            this.element = element;
            this.attributes = "";
        } else {
            int space = element.indexOf(" ");
            if (space != -1) {
                this.element = element.substring(0, space);
                this.attributes = element.substring(space + 1);
            } else {
                this.element = element;
                this.attributes = "";
            }
        }

        if (SELF_CLOSING_TAGS.contains(this.element)) {
            this.type = HTMLTagType.SELF_CLOSING;
        } else {
            this.type = type;
        }

        if (contents != null) {
            contents = contents.replaceAll("\n$", "");
        }

        this.contents = contents;
    }

     /** Constructs an HTML tag with the given element (e.g. "table") and type.
      * Self-closing tags like <br /> are considered to be "opening" tags,
      * and return true from the isOpenTag method.
      * Throws a NullPointerException if element is null. */
    public HTMLTag(String element, HTMLTagType type) {
        this(element, type, null);
    }
    
    /** Returns true if this HTML tag is an "opening" (starting) tag.
      * Self-closing tags like <br /> are NOT considered to be "opening" tags. */
    public boolean isOpening() {
        return this.type == HTMLTagType.OPENING;
    }

    /** Returns true if this HTML tag is an "closing" (finishing) tag.
      * Self-closing tags like <br /> are NOT considered to be "closing" tags. */
    public boolean isClosing() {
        return this.type == HTMLTagType.CLOSING;
    }

    /** Returns true if this tag does not requires a matching closing tag,
      * which is the case for certain elements such as br and img. */
    public boolean isSelfClosing() {
        return this.type == HTMLTagType.SELF_CLOSING;
    }

    /** Returns true if this tag is an HTML comment */
    public boolean isComment() {
        return this.element.startsWith("--");
    }
    
    /** Returns true if the given other tag matches this tag;
      * that is, if they have the same element but opposite types,
      * such as <body> and </body>. 
      *
      * pre: If other is null, this method will throw a NullPointerException */
    public boolean matches(HTMLTag other) {
        return this.element.equalsIgnoreCase(other.element) && (
            (this.type == HTMLTagType.OPENING && other.type == HTMLTagType.CLOSING) || 
            (this.type == HTMLTagType.CLOSING && other.type == HTMLTagType.OPENING));
    }
 
    /** Returns true if the given other tag equals this tag;
      * that is, if they have the same element and the same types:
      * such as <body> and <body>, or </body> and </body>
      *
      * pre: If other is null, this method will throw a NullPointerException */
    public boolean equals(HTMLTag other) {
        return this.element.equalsIgnoreCase(other.element) && this.type == other.type;
    }

    /** Returns a tag that matches this tag, and has the same element.
      * 
      * pre: If this tag is a self-closing tag, it matches itself */
    public HTMLTag getMatching() {
        if (this.type == HTMLTagType.SELF_CLOSING) {
            return new HTMLTag(this.element, HTMLTagType.SELF_CLOSING);
        } else if (this.type == HTMLTagType.OPENING) {
            return new HTMLTag(this.element, HTMLTagType.CLOSING);
        } else if (this.type == HTMLTagType.CLOSING) {
            return new HTMLTag(this.element, HTMLTagType.OPENING);
        } else {
            System.err.println("Invalid Tag Type.");
            System.exit(1);
            return null;
        }
    }

    /** Returns a string representation of this HTML tag, such as "</table>". */
    public String toString() {
        if (this.isComment()) {
            return "<!" + this.element + "-->";
        }
        else if (this.type == HTMLTagType.OPENING) {
            String attr = (this.attributes.length() > 0 ? " " + this.attributes : "");
            String contents = this.contents == null ? "" : this.contents;
            return "<" + this.element + attr + ">" + contents;
        }
        else if (this.type == HTMLTagType.CLOSING) {
            String contents = this.contents == null ? "" : this.contents;
            return "</" + this.element + ">" + contents;
        }
        else if (this.type == HTMLTagType.SELF_CLOSING) {
            String attr = (this.attributes.length() > 0 ? " " + this.attributes : "");
            return "<" + this.element + attr + " />" + contents;
        }
        else {
            System.err.println("Invalid Tag Type.");
            System.exit(1);
            return null;
        }
    }

     /** Returns a String representation of the tag, with the given indent */
    public String toString(int indent) {
        StringBuilder result = new StringBuilder();
        for (int j = 0; j < indent; j++) {
            result.append(INDENT_STRING);
        }
        result.append(this);
        return result.toString();
    }    
}
