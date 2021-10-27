package lifya.parsergenerator;

import java.io.IOException;
import java.util.Base64;
import java.util.Base64.Decoder;

import lifya.Parser;
import lifya.Source;
import lifya.Token;
import lifya.generic.GenericParser;
import lifya.generic.lexeme.Lexeme;
import lifya.parsergenerator.language.GeneratorLanguage;
import lifya.parsergenerator.language.GeneratorLexeme;
import lifya.stringify.Stringifier;

/**
 * Parsing generator utilities
 */
public class ParserGenerator {
	/**
	 * Gets a blob (byte array) from a base64 string. Supposes it is a valid string 
	 * @param txt Valid base64 string
	 * @return Byte array encoded by the string
	 */
	public static byte[] raw_blob(String txt) {
		Decoder dec = Base64.getMimeDecoder();
		return dec.decode(txt); 
	}

	/**
	 * Gets a base64 recognizer
	 * @return Base64 recognizer
	 */
	public static Lexeme blob() { 
		try { return new GeneratorLexeme("blob", "[\\+|/|\\l|\\d]+(=(=?))?"); } catch (IOException e) { e.printStackTrace(); }
		return null;
	}

	/**
	 * Gets a blob (byte array) from a base64 string. Throws an error if not a valid string 
	 * @param txt Base64 string
	 * @return Byte array encoded by the string
	 * @throws IOException if not valid string or input error 
	 */
	public static byte[] blob(String txt) throws IOException { 
		Lexeme lexeme = blob();
		Source input = new Source("blob", txt);
		txt = lexeme.get(input);
		int n = txt.length();
		if(txt.charAt(n-1)=='=' && n%4!=0) 
			throw new IOException(Stringifier.apply(new Token(input,n-1,n)));
		return raw_blob(txt);
	}

	/**
	 * Gets an int recognizer
	 * @return int recognizer
	 */
	public static Lexeme integer() { 
		try { return new GeneratorLexeme("int", "[\\+|\\-]?\\d+"); } catch (IOException e) {}
		return null;
	}

	/**
	 * Gets an integer from a string. Throws an error if not a valid string 
	 * @param txt integer string
	 * @return integer encoded by the string
	 * @throws IOException if not valid string or input error 
	 */
	public static int integer(String txt) throws IOException { 
		Lexeme lexeme = integer();
		return Integer.parseInt(lexeme.get(new Source("int", txt)));
	}
		
	/**
	 * Gets a natural numbers recognizer
	 * @return Natural numbers recognizer
	 */
	public static Lexeme natural() { 
		try { return new GeneratorLexeme("nat", "\\d+"); } catch (IOException e) {}
		return null;
	}
	
	/**
	 * Gets a natural number from a string. Throws an error if not a valid string 
	 * @param txt natural number string
	 * @return Natural number encoded by the string
	 * @throws IOException if not valid string or input error 
	 */
	public static int natural(String txt) throws IOException { 
		Lexeme lexeme = natural();
		return Integer.parseInt(lexeme.get(new Source("nat", txt)));
	}
	
	/**
	 * Gets a real numbers recognizer
	 * @return real numbers recognizer
	 */
	public static Lexeme real() { 
		try { return new GeneratorLexeme("number", "[\\+|\\-]?\\d+(\\.\\d+)?([e|E][\\+|\\-]?\\d+)?"); } catch (IOException e) {}
		return null;
	}

	/**
	 * Gets a real number from a string. Throws an error if not a valid string 
	 * @param txt real number string
	 * @return Real number encoded by the string
	 * @throws IOException if not valid string or input error 
	 */
	public static double real(String txt) throws IOException { 
		Lexeme lexeme = real();
		return Double.parseDouble(lexeme.get(new Source("noname", txt)));
	}
	
	/**
	 * Gets a space recognizer: multiple \n,\r,\t and whitespace characters.
	 * @return Space recognizer
	 */
	public static Lexeme space() {
		try { return new GeneratorLexeme("space",  "[\\n|\\t|\\r|\\s]+"); } 
		catch (IOException e) { e.printStackTrace(); }
		return null;		
	}
	
	/**
	 * Process the start of a string considering escape characters  codification
	 * @param txt String to be processed
	 * @return First character in the string (considering escape codification)
	 */
	public static char escape(String txt) {
		if(txt.charAt(0) != '\\') return txt.charAt(0);
		char c = txt.charAt(1);
		switch(c){
			case 'u': return (char)Integer.parseInt(txt.substring(2,6),16);
			case 'n': return '\n';
			case 'r': return '\r'; 
			case 't': return '\t';
			case 'b': return '\b';
			case 'f': return '\f';
			case 's': return ' ';
			default: return c;
		}		
	}
	
	/**
	 * Process a string for processing possible escape characters codification 
	 * @param txt String to be processed
	 * @return String (considering escape codification)
	 */	
	public static String escape_all(String txt) {
		StringBuilder sb = new StringBuilder();
		while(txt.length()>0) {
			int i=0;
			char c = escape(txt);
			sb.append(c);
			if(txt.charAt(i)=='\\') {
				i++;
				if(txt.charAt(i)=='u') i+=4;
			}
			i++;
			txt = txt.substring(i);
		}
		return sb.toString();
	}
	
	/**
	 * Gets a string from a encoded string. Supposes it is a valid string 
	 * @param txt Valid encoded string
	 * @param quotation Quotation character of strings
	 * @return String encoded by the string
	 */
	public static String raw_string(String txt, char quotation) {
		String str = "";
		int n = txt.length();
		int end = 1;
		char c=txt.charAt(end);
		while(end<n && c!=quotation){
			if(c=='\\'){
				str += escape(txt.substring(end,Math.min(n, end+6)));
				end++;
				c=txt.charAt(end);
				if(c=='u') end += 4;
			}else str += c;
			end++;
			c=txt.charAt(end);
		}
		return str;
	}

	/**
	 * Gets a string recognizer.
	 * @param quotation Quotation character of strings
	 * @return Space recognizer
	 */
	public static Lexeme string(char quotation) { 
		try {
			String code = quotation + 
					"(-[\\\\|"+quotation+"]|\\\\([\\\\|n|r|t|"+quotation+"]|u[A-F|a-f|\\d][A-F|a-f|\\d][A-F|a-f|\\d][A-F|a-f|\\d]))*"+quotation;
			return new GeneratorLexeme("string",  code); 
		} catch (IOException e) { e.printStackTrace(); }
		return null;
	}

	/**
	 * Gets a string from a encoded string. Throws an error if not a valid string 
	 * @param txt Encoded string
	 * @param quotation Quotation character of strings
	 * @return String decoded string
	 * @throws IOException if not valid string or input error 
	 */
	public static String string(String txt, char quotation) throws IOException { 
		Lexeme lexeme = string(quotation);
		return raw_string(lexeme.get(new Source("string", txt)), quotation);
	}
	
	/**
	 * <p>Creates a token recognizer from an string with a lifya tokens specification. Lifya tokens specification
	 * language is a regular language specification mechanism defined as follows:</p>
	 * <p>Category of characters</p>
	 * <ul>
	 * <li> <i>.</i> : Any character </li>
	 * <li> <i>\d</i> : Digit character </li>
	 * <li> <i>\w</i> : Alphabetic character </li>
	 * <li> <i>\l</i> : Letter character </li>
	 * <li> <i>\D</i> : Non digit character </li>
	 * <li> <i>\W</i> : Non alphabetic character </li>
	 * <li> <i>\L</i> : Non letter character </li>
	 * <li> <i>\S</i> : Non space character (not \s) </li>
	 * </ul>
	 * <p>Escaped characters</p>
	 * <ul>
	 * <li> <i>\s</i> : White space character </li>
	 * <li> <i>\n</i> : new line character </li>
	 * <li> <i>\r</i> : carriage return character </li>
	 * <li> <i>\t</i> : tabulation character </li>
	 * <li> <i>\\uWXYZ</i> : Unicode character. W, X, Y, and Z must be hexadecimal characters.</li>
	 * <li> <i>\.</i> : . character </li>
	 * <li> <i>\+</i> : + character </li>
	 * <li> <i>\*</i> : * character </li>
	 * <li> <i>\?</i> : ? character </li>
	 * <li> <i>\-</i> : - character </li>
	 * <li> <i>\(</i> : ( character </li>
	 * <li> <i>\)</i> : ) character </li>
	 * <li> <i>\[</i> : [ character </li>
	 * <li> <i>\]</i> : ] character </li>
	 * <li> <i>\{</i> : { character </li>
	 * <li> <i>\}</i> : } character </li>
	 * <li> <i>\[</i> : [ character </li>
	 * <li> <i>\]</i> : ] character </li>
	 * <li> <i>\|</i> : | character </li>
	 * </ul>
	 * <p> Characters <i>= : &lt; &gt; %</i> are not escaped when just generating a token recognizer. Those must be escaped
	 * when generating a full language parser (parser function), since these have special meaning. </p>
	 * <p>Operations</p>
	 * <ul>
	 * <li> <i>$</i> : Characters up to the end of the line</li>
	 * <li> <i>*</i> : zero or more times the previous set of characters. For example, <i>doom*</i> indicates 
	 * zero or more times the word <i>doom</i> not just the character <i>m</i> </li>
	 * <li> <i>+</i> : one or more times the previous set of characters. For example, <i>doom+</i> indicates 
	 * one or more times the word <i>doom</i> not just the character <i>m</i> </li>
	 * <li> <i>?</i> : zero or one times the previous set of characters. For example, <i>doom?</i> indicates 
	 * zero or one times the word <i>doom</i> not just the character <i>m</i> </li>
	 * <li> <i>Range</i>: Produces the set of characters between two characters (both limits included). For example,
	 * <i>A-F</i> Produces the set of characters <i>A,B,C,D,E,F</i>
	 * <li> <i>set</i>: Produces the set of character defined by the considered elements. For example, <i>[\d|A-F|a-f]</i> indicates
	 * a character that is a hexadecimal character. Elements in the set are separated by pipe characters (|) and
	 * each one may be a single character, escaped character, or a category character.</li>
	 * <li> <i>-</i> : Produces the complement of the associated set. For example <i>-[\d|A-F|a-f]</i> indicates
	 * a character that is not a hexadecimal character</li>
	 * <li> <i>words</i> : Produces a collection of optional word sequences. For example, <i>{false|true|null}</i> produces
	 * an optional rule defined by words <i>false</i>, <i>true</i>, and <i>null</i></li>
	 * <li> <i>|</i>: Produces a collection of optional expressions. For example, <i>\d+ | [A-F]+</i> produces an optional
	 * matching of sequences of digits (<i>\d+</i>) or sequences of letters <i>A,B,C,D,E,F</i>    
	 * <li> <i>()</i>: Are used for grouping expressions</li>    
	 * </ul>
	 * <p>A lifya expression for real numbers can be defined as follows: </p>
	 * <p> <i>[\+|\-]?\d+(\.\d+)?([e|E][\+|\-]?\d+)?</i> </p>
	 * @param type Type of the tokens recognized by the lexeme
	 * @param code Lifya expression defining the recognizer
	 * @return A recognizer if expression is valid
	 * @throws IOException if expression is not valid
	 */
	public static Lexeme lexeme(String type, String code) throws IOException{
		return new GeneratorLexeme(type, code);
	}
	
	/**
	 * <p>Creates a parser from an string with a lifya language specification. Lifya language specification
	 * is a specification mechanism defined as follows:</p>
	 * <h3>Rule and lexemes ids</h3>
	 * Rules and token recognizers ids have the following form: <i>&lt;%?\w+&gt;</i>, i.e. a sequence of
	 * alphabetic characters quoted by &lt; and &gt; characters. The initial character <i>%</i> indicates to the 
	 * tokenizer that such token recognizer is removable.
	 * <h3>Token recognizers</h3>
	 * <p>Category of characters</p>
	 * <ul>
	 * <li> <i>.</i> : Any character </li>
	 * <li> <i>\d</i> : Digit character </li>
	 * <li> <i>\w</i> : Alphabetic character </li>
	 * <li> <i>\l</i> : Letter character </li>
	 * <li> <i>\D</i> : Non digit character </li>
	 * <li> <i>\W</i> : Non alphabetic character </li>
	 * <li> <i>\L</i> : Non letter character </li>
	 * <li> <i>\S</i> : Non space character (not \s) </li>
	 * </ul>
	 * <p>Escaped characters</p>
	 * <ul>
	 * <li> <i>\s</i> : White space character </li>
	 * <li> <i>\n</i> : new line character </li>
	 * <li> <i>\r</i> : carriage return character </li>
	 * <li> <i>\t</i> : tabulation character </li>
	 * <li> <i>\\uWXYZ</i> : Unicode character. W, X, Y, and Z must be hexadecimal characters.</li>
	 * <li> <i>\.</i> : . character </li>
	 * <li> <i>\+</i> : + character </li>
	 * <li> <i>\*</i> : * character </li>
	 * <li> <i>\?</i> : ? character </li>
	 * <li> <i>\-</i> : - character </li>
	 * <li> <i>\(</i> : ( character </li>
	 * <li> <i>\)</i> : ) character </li>
	 * <li> <i>\[</i> : [ character </li>
	 * <li> <i>\]</i> : ] character </li>
	 * <li> <i>\{</i> : { character </li>
	 * <li> <i>\}</i> : } character </li>
	 * <li> <i>\[</i> : [ character </li>
	 * <li> <i>\]</i> : ] character </li>
	 * <li> <i>\|</i> : | character </li>
	 * <li> <i>\:</i> : : character </li>
	 * <li> <i>\&lt;</i> : &lt; character </li>
	 * <li> <i>\&gt;</i> : &gt; character </li>
	 * <li> <i>\=</i> : = character </li>
	 * <li> <i>\%</i> : % character </li>
	 * </ul>
	 * <p> Characters <i>= : &lt; &gt; %</i> are not escaped when just generating a token recognizer (function lexeme).
	 *  Those must be escaped when generating a full language parser (parser function), since these have special meaning. </p>
	 * <p>Operations</p>
	 * <ul>
	 * <li> <i>%</i> : A comment line</li>
	 * <li> <i>$</i> : Characters up to the end of the line</li>
	 * <li> <i>*</i> : zero or more times the previous set of characters. For example, <i>doom*</i> indicates 
	 * zero or more times the word <i>doom</i> not just the character <i>m</i> </li>
	 * <li> <i>+</i> : one or more times the previous set of characters. For example, <i>doom+</i> indicates 
	 * one or more times the word <i>doom</i> not just the character <i>m</i> </li>
	 * <li> <i>?</i> : zero or one times the previous set of characters. For example, <i>doom?</i> indicates 
	 * zero or one times the word <i>doom</i> not just the character <i>m</i> </li>
	 * <li> <i>Range</i>: Produces the set of characters between two characters (both limits included). For example,
	 * <i>A-F</i> Produces the set of characters <i>A,B,C,D,E,F</i>
	 * <li> <i>set</i>: Produces the set of character defined by the considered elements. For example, <i>[\d|A-F|a-f]</i> indicates
	 * a character that is a hexadecimal character. Elements in the set are separated by pipe characters (|) and
	 * each one may be a single character, escaped character, or a category character.</li>
	 * <li> <i>-</i> : Produces the complement of the associated set. For example <i>-[\d|A-F|a-f]</i> indicates
	 * a character that is not a hexadecimal character</li>
	 * <li> <i>words</i> : Produces a collection of optional word sequences. For example, <i>{false|true|null}</i> produces
	 * an optional rule defined by words <i>false</i>, <i>true</i>, and <i>null</i></li>
	 * <li> <i>|</i>: Produces a collection of optional expressions. For example, <i>\d+ | [A-F]+</i> produces an optional
	 * matching of sequences of digits (<i>\d+</i>) or sequences of letters <i>A,B,C,D,E,F</i>    
	 * <li> <i>()</i>: Are used for grouping expressions</li>    
	 * </ul>
	 *  <p> A token recognizer is defined as <i>&lt;id&gt; = expression</i>. For example, 
	 * a lifya expression for real numbers can be defined as follows: </p>
	 * <p> <i>&lt;number&gt; = [\+|\-]?\d+(\.\d+)?([e|E][\+|\-]?\d+)?</i> </p>
	 * <p>If a token recognizer is removable, its name must initiates with symbol <i>%</i>. For example, the following lifya expression
	 * indicates that spaces are removable tokens:</p>
	 * <p> <i>&lt;%space&gt; = [\n|\r|\t|\s]+</i> </p>
	 * <p>The parser may define as many lexemes (token recognizers) as wanted. Order of definition is important
	 * for ambiguity resolution (first defined, first applied).</p>
	 * <h3>Rules</h3>
	 * <p>Parsing rules can be conventional rules or expression rules.</p>
	 * <h4>Conventional Rules</h4>
	 * <p>Defined in a similar fashion to the token recognizers, but including rule and token recognizer ids.
	 * <ul>
	 * <li> <i>*</i> : zero or more times the component. For example, <i>doom*</i> and <i>&lt;number&gt;*</i>indicates 
	 * zero or more times the word <i>doom</i> and zero or more times the component (rule or token recognizer) 
	 * <i>&lt;number&gt;</i>, respectively
	 * </li>
	 * <li> <i>+</i> : one or more times the component. For example, <i>doom+</i> and <i>&lt;number&gt;+</i>indicates 
	 * one or more times the word <i>doom</i> and one or more times the component (rule or token recognizer) 
	 * <i>&lt;number&gt;</i>, respectively
	 * </li>
	 * <li> <i>?</i> : zero or one times the component. For example, <i>doom?</i> and <i>&lt;number&gt;?</i>indicates 
	 * zero or one times the word <i>doom</i> and zero or one times the component (rule or token recognizer) 
	 * <i>&lt;number&gt;</i>, respectively
	 * </li>
	 * <li> <i>|</i>: Produces a collection of optional expressions. For example, <i>doom+ | &lt;number&gt;+</i> produces
	 *  an optional matching of sequences of the word (<i>\doom</i>) or sequences of <i>&lt;number&gt;</i>    
	 * <li> <i>()</i>: Are used for grouping expressions</li>   
	 * </ul> 
	 * <p>Conventional rules are defined as <i>&lt;id&gt; = regular_body.</i>. For example, a rule for list of
	 * numbers separated by commas may be defined as follows:</p>
	 * <p> <i>&lt;list&gt; :- &lt;list&gt; (, &lt;list&gt;)*</i> </p>
	 * <h4>Expression Rules</h4>
	 * <p>Rules for ambiguous expressions with operators precedence. For example, an expression for unsigned numbers
	 * may be defined as follows:</p>
	 * <p> <i>&lt;exp&gt; :- {&#94;} {\*,/} {\+,\-} &lt;number&gt; &lt;unsignnumber&gt; </i> </p>
	 * <p> Order of definition of operators sets defines the operators priority. In this example, 
	 * operator <i>&#94;</i> has a higher than <i>*</i> and <i>/</i>. Operator in the same set have the same priority. Also,
	 * the first component , in this case <i>&lt;number&gt;</i>, indicates the component for the first element in the expression 
	 * (for example if it can have associated a minus character), while the second component, in this case <i>&lt;unsignnumber&gt;</i>,
	 * indicates the component for the rest of the expression.
	 * 
	 * @param code Lifya language specification
	 * @param main_rule Main rule for parsing inputs
	 * @return A parser if the specification is valid
	 * @throws IOException if the specification is not valid
	 */
	public static Parser parser(String code, String main_rule) throws IOException{
		GeneratorLanguage language = new GeneratorLanguage();
		GenericParser parser = language.get(new Source("parser generator", code));
		parser.main(main_rule);
		return parser;
	}
}
