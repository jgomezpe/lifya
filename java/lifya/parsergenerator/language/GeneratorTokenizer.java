package lifya.parsergenerator.language;

import java.io.IOException;

import lifya.generic.GenericTokenizer;
import lifya.generic.lexeme.Lexeme;
import lifya.generic.lexeme.Symbol;
import lifya.parsergenerator.GeneratorConstants;
import lifya.parsergenerator.lexeme.Any;
import lifya.parsergenerator.lexeme.Category;
import lifya.parsergenerator.lexeme.Set;
import lifya.parsergenerator.lexeme.Space;

public class GeneratorTokenizer extends GenericTokenizer{
	
	protected static Lexeme[] lexemes() {
		return new Lexeme[] { 
				new Any(GeneratorConstants.ANY, false), new Set(GeneratorConstants.SET, false), 
				new Category(GeneratorConstants.CATEGORY, false), new Symbol(GeneratorConstants.DOLLAR,"$"), 
				new Symbol(GeneratorConstants.CLOSURE,"?*+"), new Symbol(GeneratorConstants.PIPE,"|"), 
				new Symbol(GeneratorConstants.SYMBOL,"{}()-"), new Space() };
	}
	
	protected static Lexeme[] embeded_lexemes() {
		try {
			return new Lexeme[] { 
					new Any(GeneratorConstants.ANY, true), new Set(GeneratorConstants.SET, true), 
					new Category(GeneratorConstants.CATEGORY, true), new Symbol(GeneratorConstants.DOLLAR,"$"),
					new Symbol(GeneratorConstants.CLOSURE,"?*+"), new Symbol(GeneratorConstants.PIPE,"|"), 
					new Symbol(GeneratorConstants.SYMBOL,"{}()[]-=:"), new Space(), 
					new GeneratorLexeme(GeneratorConstants.ID, "<(%)?\\w+>", false),
					new GeneratorLexeme(GeneratorConstants.COMMENT, "%$",false)};
		} catch (IOException e) { e.printStackTrace(); }
		return lexemes();
	}
	
	/**
	 * Created generator for lifya tokenizer recognizer generation or lifya language parser generator
	 * @param embeded A <i>true</i> value indicates a lifya language parser generator, a <i>false</i> value indicates
	 * a lifya tokenizer recognizer generation
	 */
	public GeneratorTokenizer(boolean embeded) {
		super(embeded?embeded_lexemes():lexemes());
		removableTokens.put(GeneratorConstants.SPACE, GeneratorConstants.SPACE);
		removableTokens.put(GeneratorConstants.COMMENT,GeneratorConstants.COMMENT);
	}
}
