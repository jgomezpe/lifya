package utila;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Template {
	public static String escapechars() { return "<>()[]{}\\^-=$!|?*+."; }
	public static boolean escapechar(char c) { return escapechars().indexOf(c)>=0; }

	public static Pattern get(char c) {
		String cs = (escapechar(c)?"\\\\":"")+c;
		return Pattern.compile(cs+"([^\\\\"+cs+"]|\\\\[\\\\"+cs+"])*"+cs);
	}
	
	/**
	 * Obtains a String from a template by replacing the set of tags with their associated values. A tag is limited both sides by a character <i>c</i>. 
	 * For example, if <i>str='lorem ·X· dolor ·haha· amet'</i>, <i>c='·'</i> and <i>dictionary={'X':'ipsum', 'haha':'sit' }
	 * then this method will return the string <i>lorem ipsum dolor sit amet'</i>
	 * @param str Template used for generating the String
	 * @param dictionary Set of pairs <i>(TAG,value)</i> used for replacing each <i>TAG</> by its corresponding <i>value</i>
	 * @return A String from a template by replacing the set of tags with their associated values. 
	 */
	public static String get(String str, HashMap<String,String> dictionary){
	    return get(str, dictionary, '·');
	}
	
	/**
	 * Obtains a String from a template by replacing the set of tags with their associated values. A tag is limited both sides by a character <i>c</i>. 
	 * For example, if <i>str='lorem ·X· dolor ·haha· amet'</i>, <i>c='·'</i> and <i>dictionary={'X':'ipsum', 'haha':'sit' }
	 * then this method will return the string <i>lorem ipsum dolor sit amet'</i>
	 * @param str Template used for generating the String
	 * @param dictionary Set of pairs <i>(TAG,value)</i> used for replacing each <i>TAG</> by its corresponding <i>value</i>
	 * @param c Enclosing tag character
	 * @return A String from a template by replacing the set of tags with their associated values. 
	 */
	public static String get(String str, HashMap<String,String> dictionary, char c){
		Pattern pattern = get(c);
		Matcher matcher = pattern.matcher(str);
		int start = 0;
		if( matcher.find(start) ){
			StringBuilder sb = new StringBuilder();
		    	do{
		    	    	int nstart = matcher.start();
			    	String matched = matcher.group();
			    	String tag = matched.substring(1, matched.length()-1);
			    	tag = tag.replace("\\"+c, ""+c);
			    	sb.append(str.substring(start,nstart));
			    	String txt = dictionary.get(tag);
			    	if( txt==null ) txt = tag;
			    	sb.append(txt);
			    	start = nstart+matched.length();
			}while( matcher.find() );
		    	sb.append(str.substring(start));
			return sb.toString();
		}else return str; 
	}
}