package lifya.generic.rule;

/**
 * A parsing rule for a rule enclosed in quotes
 */
public class RuleInQuotes extends JoinRule{
	/**
	 * <p>Creates a syntactic rule for parsing a rule enclosed in quotes. Consider a typical tuple rule:</p>
	 * <p> &lt;TUPLE&gt; :- (&lt;ARGS&gt;) </p>
	 * <p>Can be defined using a constructor call like this:</p>
	 * <p><i>new ListRule("TUPLE", "ARGS", Symbol.TAG, "(", ")")</i></p>
	 * @param type Type of the rule
	 * @param quoted_rule Quoted rule
	 * @param quotes_type Type of the quotes 
	 * @param left Left quotation
	 * @param right Right quotation
	 */
	public RuleInQuotes(String type, String quoted_rule, String quotes_type, String left, String right) {
		super(type, new String[] {quotes_type, quoted_rule, quotes_type}, new String[] {left, null, right});
	}
}
