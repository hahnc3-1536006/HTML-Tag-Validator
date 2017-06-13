// CSE 143, Homework 2: HTML Manager
// Instructor-provided code.  You should not modify this file!
// Resource file for HTMLManager.  Put this file in the same directory
// as HTMlManager.java, HTMLTag.java, HTMLTagType.java and HTMLMain.java.

import java.net.URL;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.io.Reader;
import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.*;

/** Parses a File, String, or URL into a List<HTMLTag>
 */
public class HTMLParser {
    private class HTMLParserException extends Exception {}
    public String unparsedPage;

    /** Parses the given input stream from the source with the given name */
    private void parseStream(String name, InputStream stream) {
        try {
            /* Read the HTML */
            Reader in = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
            StringBuilder response = new StringBuilder();
            int c = in.read();
            while (c >= 0) {
                response.append((char)c);
                c = in.read();
            }
            this.unparsedPage = response.toString();
        } catch (IOException e) {
            System.err.println("The " + name + " is invalid.");
            System.exit(1);
        }

    }

    /** Creates a parser based off the HTML at the given source URL */
    public HTMLParser(URL url) {
        try {
            /* Create the GET request. */
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            parseStream("URL '" + url.toString() + "'", conn.getInputStream());
        } catch (IOException e) {
            System.err.println("The URL " + url.toString() + " is invalid.");
            System.exit(1);
        }
    }

    /** Creates a parser based off the given source File */
    public HTMLParser(File file) {
        try {
            parseStream("file " + file.toString() + "'", new FileInputStream(file));
        } catch (FileNotFoundException ee) {
            System.err.println("The file '" + file.toString() + "' is invalid.");
            System.exit(1);
        }

    }

    /** Creates a parser based off the given source String */ 
    public HTMLParser(String str) {
        this.unparsedPage = str;
    }

    /** HTMLLexer to parse a string of tags into HTMLTags.  Iterates
     * over the given source String */
    private class HTMLLexer implements Iterator<HTMLTag> {
        private String page;
        private int index;
        private boolean inString;
        private char quoteType;

        /** Creates an HTMLLexer based off the given source String */
        public HTMLLexer(String page) {
            this.page = page;
            this.index = 0;
            this.inString = false; 
        }

        /** Returns the next HTMLTag in the source String */
        public HTMLTag next() {
            int begin = this.index; 

            /* If we've found an HTML comment... (that isn't a DOCTYPE...) */
            if (this.page.substring(begin + 1).startsWith("!--")) {
                this.index = this.page.indexOf("-->", this.index);
                begin = begin + 2;
                int end = this.index - 1; 
                return new HTMLTag(this.page.substring(begin, end + 1), HTMLTagType.SELF_CLOSING);
            }
            int before = this.index;
            movePastString('>');
            int end = this.index;

            String contents = "";

            movePastString('<');
           
            if (end + 1 < this.index) {
                contents = this.page.substring(end + 1, this.index);
            }

            if (this.page.charAt(begin + 1) == '/') {
                
                return new HTMLTag(this.page.substring(begin + 2, end), HTMLTagType.CLOSING, contents);
            }
            else if (this.page.charAt(end - 1) == '/') {
                return new HTMLTag(this.page.substring(begin + 1, end - 1), HTMLTagType.SELF_CLOSING, contents);
            }
            else {
                /* If we've found a script element... */
                if (this.page.substring(begin + 1, end).startsWith("script")) {
                    this.index = this.page.indexOf("</script>", begin + 1);
                }

                this.index = this.page.indexOf("<", this.index);
                contents = "";
               
                if (this.index > -1) {
                    contents = this.page.substring(end + 1, this.index);
                }
                return new HTMLTag(this.page.substring(begin + 1, end), HTMLTagType.OPENING, contents);
            }
        }

        /** Returns true if there is another HTMLTag in the source String
         * returns false otherwise. */
        public boolean hasNext() {
            int potentialNextIndex = this.page.indexOf("<", this.index);
            if (potentialNextIndex != -1) {
                this.index = potentialNextIndex;
                return true;
            }
            return false;
        } 

        /** Moves the current index in the source String up to the next 
         * needle not contained in the middle of a String */
        private boolean movePastString(char needle) {
            int potentialNextIndex = this.page.indexOf(needle, this.index);

            if (potentialNextIndex == -1) {
                return false;
            }

            int nextSingleQuote = this.page.indexOf("'", this.index);
            if (nextSingleQuote != -1 && nextSingleQuote < potentialNextIndex) {
                this.inString = !this.inString;
                this.quoteType = '\'';
                this.index = nextSingleQuote + 1;
                return movePastString(needle);
            }

            int nextDoubleQuote = this.page.indexOf("\"", this.index);
            if (nextDoubleQuote != -1 && nextDoubleQuote < potentialNextIndex) {
                this.inString = !this.inString;
                this.quoteType = '"';
                this.index = nextDoubleQuote + 1;
                return movePastString(needle);
            }

            if (this.inString) {
                this.index++;
                return movePastString(needle);
            }

            this.index = potentialNextIndex;
            return true;
        }

        /** Throws UnsupportedOperationException */ 
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    /** Parses the source String and returns the List of HTMLTags */
    public Queue<HTMLTag> parse() {
        Queue<HTMLTag> parsed = new LinkedList<HTMLTag>();
        HTMLLexer lexer = new HTMLLexer(this.unparsedPage);
        while (lexer.hasNext()) {
            parsed.add(lexer.next());
        }
        return parsed;
    } 
}
