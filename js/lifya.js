/**
*
* lifya.js
* <P>Java Script for language processing.</P>
* <P> Requires base64.js and kompari.js. </P>
* <P>A numtseng module <A HREF="https://numtseng.com/modules/lifya.js">https://numtseng.com/modules/lifya.js</A> 
*
* Copyright (c) 2021 by Jonatan Gomez-Perdomo. <br>
* All rights reserved. See <A HREF="https://github.com/jgomezpe/lifya">License</A>. <br>
*
* @author <A HREF="https://disi.unal.edu.co/~jgomezpe/"> Professor Jonatan Gomez-Perdomo </A>
* (E-mail: <A HREF="mailto:jgomezpe@unal.edu.co">jgomezpe@unal.edu.co</A> )
* @version 1.0
*/

/////// Lifya.js ////////////
/**
 * Character analyze functions
 */
class CharacterClass {
    isDigit(c){ return '0'<=c && c<='9' }
    isLowerCase(c){ return ('a'<=c && c<='z') }
    isUpperCase(c){ return ('A'<=c && c<='Z') }
    isLetter(c){ return this.isLowerCase(c) || this.isUpperCase(c) }
    isHexa(c){ return this.isDigit(c) || ('A'<=c&&c<='F') || ('a'<=c&&c<='f') }
    isAlphabetic(c){ return this.isDigit(c) || this.isLetter(c) }    
}

/**
 * Character utilities
 */
Character = new CharacterClass();

/**
 * <p>Read objects from an input source</p>
 *
 */
class Read{  
    /**
     * Reads an object from the input source 
     * @param input Symbol source
     * @return Object read from the symbol source
     * @throws IOException if the object could not be read
     */
    get(input){
        var t = this.match(input)
        if(t.isError()) {
            input.setError(t)
            throw JSON.stringify(input.error.json())
        }
        return t.value   
    }
    
    /**
     * Reads a token from the input source 
     * @param input Symbol source
     * @return Token read from the symbol source
     */
    match(input){ return null }
}

/**
 * <p>Abstract definition of a language with parser and meaner</p>
 */
class Language extends Read{
    /**
     * Creates a language with the given parser
     * @param parser Language syntactic parser
     */
    constructor( parser ){ 
        super()
        this.parser = parser 
    }
    
    /**
     * 
     * @param t Creates an object with meaning
     * @return Semantic token (from syntactic token)
     */
    mean(t){ return null }
    
    /**
     * Reads a semantic token from the input source starting at the position given up to the ending position
     * @param input Symbol source
     * @return Semantic token read from the source
     */
    match(input) { return this.mean(this.parser.match(input)) }
}

/**
 * <p>Abstract syntactic parser</p>
 *
 */
class Parser extends Read{    
    /**
     * Creates a syntactic parser using the given tokenizer
     * @param tokenizer Tokenizer
     */
    constructor(tokenizer) { 
        super()
        this.tokenizer = tokenizer 
        this.leftover = false
    }
    
    /**
     * Creates a syntactic token (parser tree) from the list of tokens
     * @return Syntactic token
     */
    analyze(){ return null }
    
    /**
     * Reads an object from the input source (limited to the given starting and ending positions) 
     * @param input Symbol source
     * @return Syntactic token read from the symbol source
     */
    match(input){
        var t = this.tokenizer.match(input) 
        if( t.isError() ) return t
        this.lexer = new TokenSource(t.value)
        t = this.analyze()
        if(!this.leftover && this.lexer.current()!=null) t = this.lexer.current().toError()
        return t
    }
    
    /**
     * Determines if a type name is a token type or not
     * @param type type name to analyze
     * @return <i>true</i> If the type name represents a token type, <i>false</i> otherwise.
     */
    isToken(type) { return this.tokenizer.isTokenType(type) }
    
    current() { return this.lexer.current() }
    
    /**
     * Reads an object from the input source 
     * @param input Symbol source
     * @return Object read from the symbol source
     * @throws IOException if the object could not be read
     */
    get(input){
        var t = this.match(input)
        if(t.isError()) {
            input.setError(t)
            throw JSON.stringify(input.error.json())
        }
        return t  
    }
}

/**
 * Position of the reading cursor in the input source
 */
class Position{
    /**
     * Source name TAG
     */
    static INPUT = "input"
    /**
     * Starting position TAG
     */
    static START = "start"
    /**
     * Row position TAG (when considering as a 2D position in the source)
     */
    static ROW = "row"
    /**
     * Column position TAG (when considering as a 2D position in the source)
     */ 
    static COLUMN = "column"
 
    /**
     * Creates a position for the given source 
     * @param input Input source
     * @param start Absolute position on the source 
     */
    constructor(input, start){
        this.input = input
        this.start = start 
    }
    
    /**
     * Shifts the absolute position a <i>delta</i> amount
     * @param delta delta moving of the absolute position
     */
    shift(delta) { start+=delta }

    /**
     * Configures the position with the given JSON info
     * @param json JSON configuration information
     */
    config(json) {
        this.input = json.input
        this.start = json.start
    }

    /**
     * Gets a JSON version of the position
     * @return JSON version  of the position
     */
    json() {
        var pos = this.input.location(this.start)
        return {"input":this.input.id, "start":this.start,
        "row":pos[0], "column":pos[1]}
    }
    
    /**
     * Stringifies the position
     * @return Strigified versionof the position
     */
    stringify(){ 
        return JSON.stringify(this.json()) 
    }
}

/**
 * <p>Considers a given String as an input for the language recognizer</p>
 *
 */
class Source {
    /**
     * End of input character
     */
    static EOI = '\0'

    /**
     * Creates a source input from the given String
     * @param id Identification TAG of the input source
     * @param input String used as input source
     * @param start Initial position of the source
     * @param end Final position of the source
     */
    constructor(id, input, start=0, end=input.length) {
        this. id = id
        this.error = null
        this.start = start
        this.end = end
        this.pos = start
        this.input = input
        this.rows = []
        this.search = new SortedSearch(l2h)
        this.search.sorted = this.rows
        this.rows.push(0)
        for(var i=0;i<input.length; i++) 
            if(input.charAt(i)=='\n') this.rows.push(i+1)
    }
   
    /**
     * Gets [row,column] array when considering position a 2D position
     * @param index Linear position to analyze
     * @return [row,column] array when considering the given absolute position a 2D position
     */
    location(index) {
        var idx = this.search.findLeft(index)
        if(idx+1<this.rows.length && this.rows[idx+1]==index) return [idx+1,0]
        return [idx, index-this.rows[idx]]
    }
    
    /**
     * Advances one character in the input
     * @return New current character
     */
    next() {
        this.pos++
        if(this.pos>=this.end) {
            this.pos=this.end
            return Source.EOI
        }else return this.input.charAt(this.pos)
    }
    
    /**
     * Gets the current character 
     * @return Current character
     */
    current() { return this.valid(this.pos)?this.input.charAt(this.pos):Source.EOI }
    
    
    /**
     * Locates the reading cursor
     * @param index New cursor's position
     * @return Character at the cursor's position
     */
    locate( index ) {
        if( index < this.start ) index = this.start
        else if(index>this.end) index = this.end
        this.pos = index
        return this.current()
    }
      
    /**
     * Determines if the index is a valid one (start&lt;=index && index&lt;end)
     * @param index Position to analyze
     * @return <i>true</i> if the position is valid, <i>false</i> otherwise
     */
    valid(index) { return (this.start<=index && index<this.end) }
    
    /**
     * Determines if the reading cursor reaches the end of the source
     * @return <i>true</i> if the reading cursor is at the end of the source, <i>false</i> otherwise.
     */
    eoi() { return this.pos==this.end }
    
    /**
     * Obtains a substring of the input String
     * @param start Starting position of the substring to obtain
     * @param end Final position (not included) of the substring to obtain
     * @return Substring of the input String
     */
    substring(start, end) {
        start = Math.max(this.start, start)
        end = Math.min(this.end, end)
        return this.input.substring(start,end)
    }
    
    /**
     * Gets the input length
     * @return Input length
     */
    length() { return this.end-this.start }
    
    /**
     * Resets the source (locates the cursor at the start positions, cleans the error token)
     */
    reset() {
        this.pos = this.start
        this.error = null
    }
    
    /**
     * Sets the error token for the source
     * @param t Error token
     */
    setError(t) { if(t==null || this.error==null || t.start>this.error.start) this.error = t }    
}

/**
 * <p>Language token (may be a lexeme, a syntactic rule, an object associated with a position in the source </p>
 *
 */
class Token extends Position{
    
    /**
     * Token type's TAG
     */
    static TYPE = "type"
    
    /**
     * Error's TAG. Used for identifying error tokens
     */
    static ERROR = "error"
    
    /**
     * End's TAG. Used for identifying no available tokens/ end of input reached
     */
    static END = "end"
    
    /**
     * Value's TAG. Used for retrieving the value stored by the token 
     */
    static VALUE = "value"

    /**
     * Creates a token
     * @param type Token type
     * @param input Input source from which the token was built
     * @param start Starting position of the token in the input source
     * @param end Ending position (not included) of the token in the input source
     * @param value Value stored by the token
     */
    constructor(input, start, end, type=Token.ERROR, 
                value=input.substring(start, Math.min(Math.max(end,start+1), input.length))){
        super(input,start)
        this.type = type
        this.end = end
        this.value = value
        if(type==Token.ERROR) input.setError(this)
    }

    /**
     * Computes the length (number of symbols) consumed by the token 
     * @return Length (number of symbols) consumed by the token
     */
    length(){ return this.end-this.start }
    
    /**
     * Shifts the absolute position a <i>delta</i> amount
     * @param delta delta moving of the absolute position
     */
    shift(delta) {
        this.start+=delta
        this.end+=delta
    }

    /**
     * Gets a JSON version of the position
     * @return JSON version  of the position
     */
    json() {
        var json = super.json()
        json[Token.END] = this.end
        json[Token.VALUE] = this.value
        json[Token.TYPE] = this.type
        return json
    }
        
    toString() { return this.print(0); }
    
    /**
     * Converts the token to an error version of it
     * @return Error version of the token
     */
    toError() { return new Token(this.input,this.start,this.end) }
    
    /**
     * Determines if it is an error token or not 
     * @return <i>true</i> if an error token, <i>false</i> otherwise 
     */
    isError() { return this.type==Token.ERROR }
    
    print( tab ) {
        var sb = ""
        var obj = this.value
        if( Array.isArray(obj) ) {
            for( var k=0; k<tab; k++ ) sb += ' '
            sb += this.type
            for( var i=0; i<obj.length; i++ ) {
                sb += '\n'
                sb += obj[i].print(tab+1)
            }
        }else {
            for( var k=0; k<tab; k++ ) sb += ' '
            sb += ("["+this.type+','+this.start+','+this.end+',')
            if(obj instanceof Token) {
                sb += ']\n'
                sb += obj.print(tab+1)
            }else sb += this.value+']'
        }
        return sb
    }
}

/**
 * <p>Language tokenizer</p>
 *
 */
class Tokenizer extends Read{
    /**
     * TAG for Array of tokens
     */
    static TOKEN_LIST = "Array<Token<String>>"

    constructor(){
        super()
        this.remove = true;
    }
    
    /**
     * Sets the collection of removable tokens
     * @param removableTokens Tokens that will be removed from analysis
     */
    removables(removableTokens=[]) {
        this.remove = true
        this.removableTokens = {} 
        for( var i=0; i<removableTokens.length; i++ )
            this.removableTokens[removableTokens[i]] = removableTokens[i] 
    }

    /**
     * Determines if a token type is removable or not
     * @param type Lexeme type to analyze
     * @return <i>true</i> If the token type can be removed, <i>false</i> otherwise.
     */
    removable(type) { return this.removableTokens[type]!==undefined }

    /**
     * Determines if a type name is a token type or not
     * @param type type name to analyze
     * @return <i>true</i> If the type name represents a token type, <i>false</i> otherwise.
     */
    isTokenType(type){ return false }
    
    /**
     * Return the set of types of token
     * @return Types of token
     */
    tokenTypes(){ return [] }
    
    /**
     * Gets the last read/available Token 
     * @return Last read/available Token
     */
    analyze(input){ return null }
    
    /**
     * Gets the Token list
     * @param input Input source
     * @return Token list
     */
    match(input){
        var start = input.pos
        var list = []
        var t;
        while(!input.eoi()) {
            t = this.analyze(input) 
            if(t.isError()) {
                input.locate(start)
                return t
            }
            if(!this.remove || !this.removable(t.type)) list.push(t)
        }
        input.setError(null)
        return new Token(input, start, input.pos, Tokenizer.TOKEN_LIST, list)
    }
    
    /**
     * Removes from the Token lists tokens with the given tag
     * @param tokens Tokens to be analyzed
     * @param toremove Tag of the tokens to be removed
     * @return The Array of tokens without the desired tokens
     */
    static remove(tokens, toremove ){
        for( var i=tokens.length-1; i>=0; i-- ) if( toremove.indexOf(tokens[i].type) >= 0 ) tokens.remove(i)
        return tokens
    }
}

/**
 * <p>Tokens source</p>
 *
 */
class TokenSource{
    
    /**
     * Creates a Tokenizer source from an Array of tokens
     * @param tokens Array of Tokens used for configuring a Tokenizer source
     */
    constructor(tokens) {
        this.tokens = tokens
        this.pos = 0
        this.types = {}
        for( var i=0; i<tokens.length; i++) this.types[tokens[i].type] = ""
    }
    
    /**
     * Gets the input source
     * @return Input source
     */
    input() { return (this.tokens.length>0)?this.tokens[0].input:null }
        
    /**
     * Advances one token
     * @return New current token
     */
    next() {
        this.pos++
        if(this.pos>this.tokens.length) this.pos=this.tokens.length
        return this.current()
    }
    
    /**
     * Gets the current character 
     * @return Current character
     */
    current() { return (0<=this.pos && this.pos<this.tokens.length)?this.tokens[this.pos]:null; }
    
    /**
     * Locates the token reading source
     * @param index New reading position
     * @return New current token
     */
    locate( index ) {
        if(-1<=index && index<=this.tokens.length) {
            this.pos = index
            return this.current()
        }else return null
    }
}

/////// lifya.generic.lexeme package ///////
/**
 * <p>Abstract token type recognizer</p>
 *
 */
class Lexeme extends Read{
    /**
     * Creates a token type recognizer
     * @param type Token type
     */
    constructor(type) {
        super()
        this.type = type 
    }
    
    /**
     * Determines if the token type can start with the given character
     * @param c Character to analyze
     * @return <i>true</i> If the token type can start with the given character <i>false</i> otherwise
     */
    startsWith(c){ return false }

    /**
     * Creates an error token with the token  type
     * @param input Input source from which the token was built
     * @param start Starting position of the token in the input source
     * @return Error token with the token type
     */
    error(input, start) {
        input.locate(start);
        return new Token(input,start,input.pos)
    }
    
    /**
     * Creates a token with the token  type
     * @param input Input source from which the token was built
     * @param start Starting position of the token in the input source
     * @param value Value stored by the token
     * @return Token
     */
    token(input, start, value) { return new Token(input,start,input.pos,this.type,value) }
}

/**
 * <p>Parses any of the characters/symbols in a symbol collection</p>
 *
 */
class Symbol extends Lexeme{ 
    
    /**
     * Creates a parser for a set of symbols
     * @param type Type for the symbols lexema
     * @param symbols Symbols that will be considered in the lexema
     */
    constructor(type, symbols){
        super(type)
        this.table = {}
        for( var i=0; i<symbols.length; i++ ) this.table[symbols.charAt(i)] = symbols.charAt(i)
    }
    
    /**
     * Creates a token with the symbol type
     * @param input Input source from which the token was built
     * @return Symbol token
     */
    match(input) {
        if(this.startsWith(input.current())) {
            var c = input.current()
            input.next()
            return this.token(input,input.pos-1,c)
        }
        else return this.error(input,input.pos)
    }
    
    /**
     * Determines if the symbol lexeme can start with the given character (a character in the set)
     * @param c Character to analyze
     * @return <i>true</i> If the lexeme can start with the given character <i>false</i> otherwise
     */
    startsWith(c) { return this.table[c] !== undefined }   
}

/////// lifya.generic.rule package ///////
/**
 * <p>Parsing rule</p>
 *
 */
class Rule{    
    /**
     * <p>Creates a syntactic rule for a parser. 
     * @param type Type of the rule
     */
    constructor(type) { 
        this.type = type 
        this.parser = null
    }
    
    check(type, value, lexer) {
        if(this.parser.isRule(type)) return this.parser.rule(type).analyze(lexer);
        var current = lexer.current()
        if(current==null) return this.eof(lexer)
        if(this.check_lexeme(current, type, value)) lexer.next()
        else current = current.toError()
        return current
    }
    
    check_lexeme( t, type, value ) {
        if(t==null || (value!=null && value != t.value)) return false
        if( t.type == type ) return true
        var embeded_types = t.type.split("|")

        for( var x in embeded_types ) if(x==type) return true
        
        return false
    }

    /**
     * Determines if the rule can start with the given token
     * @param t Token to analyze
     * @return <i>true</i> If the rule can start with the given token <i>false</i> otherwise
     */
    startsWith(t){ return false }

    /**
     * Creates a rule token 
     * @param lexer Token source 
     * @return Rule token
     */
    analyze(lexer){ return null }
    
    
    error(lexer, t, pos){
        lexer.locate(pos)
        return t
    }
    
    /**
     * Creates a eof token 
     * @param lexer Token source 
     * @return EOF token
     */
    eof(lexer) { 
        var input = lexer.input()
        return new Token(input,input.length(),input.length()) 
    }
        
    /**
     * Creates a token with the rule type
     * @param input Input source from which the token was built
     * @param start Starting position of the token in the input source
     * @param end Ending position (not included) of the token in the input source
     * @param value Value stored by the token
     * @return Rule token
     */
    token(input, start, end, value) { return new Token(input, start, end, this.type, value) }
      
    print( tab ) {
        var sb = ''
        for( var k=0; k<tab; k++ ) sb += ' '
        sb += this.type
        return sb
    }
    
    toString() { return this.print(0) }
}

/**
 * Empty rule (lambda production)
 */
class EmptyRule extends Rule{
    /**
     * Lambda type /TAG of the lambda rule
     */
    static TAG = "lambda"
    
    /**
     * Creates the empty rule (lambda production)
     */
    constructor() { super(EmptyRule.TAG) }

    /**
     * Determines if the rule can start with the given token
     * @param t Token to analyze
     * @return <i>true</i> If the rule can start with the given token <i>false</i> otherwise
     */
    startsWith(t) { return true }

    /**
     * Creates a rule token using the <i>current</i> token as first token to analyze
     * @param lexer Tokens source 
     * @return Rule token
     */
    analyze(lexer) {
        var start
        if(lexer.current()==null) start = lexer.input().length()           
        else start = lexer.current().start
        return this.token(lexer.input(), start, start, null)
    }   
}

class JoinRule extends Rule{
    init(n) {
        var x = []
        for( var i=0; i<x.length; i++) x.push(null)
        return x
    }

    /**
     * <p>Creates a syntactic join rule for a parser. Consider a typical assignment rule:</p>
     * <p> &lt;ASSIGN&gt; :- &lt;id&gt; = &lt;EXP&gt; </p>
     * <p>Can be defined using a constructor call like this (arrays notation simplified):</p>
     * <p><i>new Rule(parser, "ASSIGN", ["id", "symbol","EXP"], [null,"=",null])</i></p>
     * <p>Here a <i>null</i> value indicates that the associated lexeme can take any value.</p>
     * @param type Type of the rule
     * @param type_list Types of the rule components
     * @param value_list Values of the rule components. 
     */
    constructor(type, type_list, value_list=null) {
        super(type)
        this.type_list = type_list
        if(value_list==null){
            value_list = []
            for(var i=0; i<type_list.length; i++) value_list.push(null)
        }
        this.value_list = value_list
    }

    check_lexeme( t, type_or_index, value=null ) {
        if(typeof type_or_index == 'number') 
            return super.check_lexeme( t, this.type_list[type_or_index], this.value_list[type_or_index])
        else return super.check_lexeme( t, type_or_index, value)
    }

    /**
     * Determines if the rule can start with the given token
     * @param t Token to analyze
     * @return <i>true</i> If the rule can start with the given token <i>false</i> otherwise
     */
    startsWith(t) {
        if(this.parser.isRule(this.type_list[0])) return this.parser.rule(this.type_list[0]).startsWith(t)
        else return this.check_lexeme(t, 0)
    }

    /**
     * Creates a rule token using the <i>current</i> token as first token to analyze
     * @param lexer Tokens source 
     * @return Rule token
     */
    analyze(lexer) {
        var pos = lexer.pos
        var current = lexer.current()
        if(current==null) return this.eof(lexer)
        if(!this.startsWith(current)) return this.error(lexer,current.toError(),pos)
        var start = current.start
        var end = current.end
        var list = []
        for( var i=0; i<this.type_list.length; i++) {
            pos = lexer.pos
            var t = this.check(this.type_list[i], this.value_list[i], lexer)
            if( t.isError() ) return this.error(lexer,t,pos)
            if(this.value_list[i]==null) list.push(t)
            end = t.end
        }
        return this.token(lexer.input(),start,end,list)
    }   
    
    print( tab ) {
        var sb = ''
        sb += super.print(tab)
        tab++
        for( var i=0; i<type_list.length; i++) {
            sb += '\n'
            for( var k=0; k<tab; k++ ) sb += ' '
            sb += this.type_list[i]
            if(this.value_list[i]!=null) sb += ":"+this.value_list[i]
        }
        return sb
    }
}

/**
 * <p>A parser rule for selecting from multiple rules</p>
 *
 */
class DisjointRule extends JoinRule{
    /**
     * <p>Creates a syntactic disjoint rule for a parser. Consider a typical value rule:</p>
     * <p> &lt;VALUE&gt; :- &lt;id&gt; | &lt;string&gt; | &lt;EXP&gt; </p>
     * <p>Can be defined using a constructor call like this (arrays notation simplified):</p>
     * <p><i>new DisjointRule("VALUE", ["id", "string","EXP"], [null,null,null])</i></p>
     * <p>Here a <i>null</i> value indicates that the associated lexeme can take any value.</p>
     * @param type Type of the rule
     * @param options Types of the rule components
     * @param values Values of the rule components. 
     */
    constructor(type, options, values=null) { super(type, options, values) }

    /**
     * Determines if the rule can start with the given token
     * @param t Token to analyze
     * @return <i>true</i> If the rule can start with the given token <i>false</i> otherwise
     */
    startsWith(t) {
        var flag=false
        var i=0
        while(i<this.type_list.length && !flag) {
            if(this.parser.isRule(this.type_list[i])) flag = this.parser.rule(this.type_list[i]).startsWith(t)
            else flag = this.check_lexeme(t, i)
            i++
        }
        return flag
    }

    /**
     * Creates a rule token using the <i>current</i> token as first token to analyze
     * @param lexer Tokens source 
     * @return Rule token
     */
    analyze(lexer){
        var pos = lexer.pos
        var e = null
        var t = null
        for(var i=0; i<this.type_list.length && t==null; i++) {
            var current = lexer.current()
            if(this.parser.isRule(this.type_list[i]) && this.parser.rule(this.type_list[i]).startsWith(current)) {
                t = this.parser.rule(this.type_list[i]).analyze(lexer)
                if(t.isError()) {
                    if(e==null || e.end < t.end) e = t
                    t=null
                    lexer.locate(pos)
                }
            }
            if(this.check_lexeme(current, i)) {
                lexer.next()
                return current
            }
        }
        if(t!=null) return t
        return e
    }   
}

/**
 * <p>Kleene closure (lists) Parsing rule</p>
 *
 */
class KleeneRule extends Rule{
    
    /**
     * <p>Creates a syntactic rule for Kleene closure of rules (lists). Consider a typical definition list rule:</p>
     * <p> &lt;DEFLIST&gt;+ </p>
     * <p>Can be defined using a constructor call like this :</p>
     * <p><i>new KleenRule(parser, "DEFLIST", "DEF", false)</i></p>
     * @param type Type of the rule
     * @param item_type Item being closured
     * @param item_value Item value
     * @param star A <i>true</i> value indicates an empty closure, <i>false</i> a non-empty closure. 
     */
    constructor(type, star, item_type, item_value=null) {
        super(type)
        this.item_type = item_type
        this.item_value = item_value
        this.star = star
    }

    /**
     * Determines if the rule can start with the given token
     * @param t Token to analyze
     * @return <i>true</i> If the rule can start with the given token <i>false</i> otherwise
     */
    startsWith(t) {
        return this.star || 
                (this.parser.isRule(this.item_type)?this.parser.rule(this.item_type).startsWith(t):
                this.check_lexeme(t, this.item_type, this.item_value))
    }
    
    /**
     * Creates a rule token using the <i>current</i> token as first token to analyze
     * @param lexer Token source
     * @return Rule token
     */
    analyze(lexer) {
        var list = []
        var input = lexer.input()
        var pos = lexer.pos
        var current = lexer.current()
        if(current==null) 
            if(this.star) return this.token(input, input.length(), input.length(), list)
            else return this.eof(lexer)
        if(!this.startsWith(current)) return this.error(lexer,current.toError(),pos)
        var r = this.parser.rule(this.item_type)
        var t = r!=null?r.analyze(lexer):current
        if(t.isError())
            if(this.star) {
                lexer.locate(pos)
                return this.token(input, current.start, current.end, list)
            }else return this.error(lexer,t,pos)
        if(r==null) lexer.next()
        do{
            list.push(t)
            pos = lexer.pos
            if(r!=null) t = r.analyze(lexer)
            else {
                t = lexer.current()
                if(this.check_lexeme(t, this.item_type, this.item_value)) lexer.next()
                else t = t.toError()
            }
        }while(t!=null && !t.isError())    
        if(t!=null) lexer.locate(pos)
        return this.token(input,list[0].start,list[list.length-1].end,list)     
    }
    
    print( tab ) {
        var sb = ''
        for( var k=0; k<tab; k++ ) sb += ' '
        sb += (this.star?'*':'+')+" "+this.type+ " "+ this.item_type
        if(this.item_value!=null) sb += ":"+this.item_value
        return sb
    }
}

/**
 * A parsing rule for ambiguous expressions (tree is produced according to operators priorities
 */
class ExpressionRule extends JoinRule{
    /**
     * <p>Creates an expression rule. Consider a typical numeric expression rule:</p>
     * <p> &lt;EXP&gt; :- &lt;TERM1&gt; (&lt;OPER&gt; &lt;TERM&gt;)* </p>
     * <p>Can be defined using a constructor call like this (arrays and maps notation simplified):</p>
     * <p><i>new ExpressionRule("EXP", ["TERM1","TERM"], [null,null], OPER, {"^":1,"*":2,"/":2,"+":3,"-":3})</i></p>
     * <p>Here a <i>null</i> value indicates that the associated lexeme can take any value.</p>
     * @param type Expression type
     * @param term_type Types of the terms in the expression rule (type of the first term may be different from other terms in the expression)
     * @param term_value Values for terms (if explicit values must be taken)
     * @param operator Operators type
     * @param operator_priority Operators priority
     */
    constructor(type, term_type, term_value, operator, operator_priority) {
        super(type, term_type, term_value)
        this.item = new JoinRule(type+"item", [operator, term_type[1]], [null, term_value[1]])
        this.list = new KleeneRule(type+"itemlist", true, type+"item")
        this.operator_priority = operator_priority
    }
    
    /**
     * Creates a rule token using the <i>current</i> token as first token to analyze
     * @param lexer Tokens source 
     * @return Rule token
     */
    analyze(lexer) {
        if(!this.parser.isRule(this.item.type)) {
            this.parser.add(this.item)
            this.parser.add(this.list)
        }
        var pos = lexer.pos
        var current = lexer.current()
        if(current==null) return this.eof(lexer);
        if(!this.startsWith(current)) return this.error(lexer,current.toError(),pos)
        var t = this.check(this.type_list[0], this.value_list[0],lexer)
        if(t.isError()) return this.error(lexer,t,pos)
        var a = this.parser.rule(this.type+"itemlist").analyze(lexer)
        var l = a.value
        for( var i=l.length-1; i>=0; i--) {
            var term = l[i].value
            l.splice(i+1, 0, term[1])
            l[i] = term[0]
        }   
        l.splice(0, 0, t)
        return this.tree(l)
    }

    tree(list) {
        if( list.length==1 ) return list[0]

        var p = this.operator_priority[list[1].value]
        var k = 1      
        for( var i=3; i<list.length; i+=2) { 
            var pi = this.operator_priority[list[i].value]
            if(pi<p) {
                k = i
                p = pi
            }
        }
        var call = [list[k], list[k-1], list[k+1]]
        var t = new Token(call[1].input, call[1].start, call[2].end, this.type, call)
        list.splice(k+1,1)
        list[k] = t
        list.splice(k-1, 1)
        return this.tree(list)
    }
    
    print( tab ) {
        var sb = ''
        sb = super.print(tab)
        for( var i=0; i<type_list.length; i++) {
            sb += ' ' + this.type_list[i]
            if(this.value_list[i]!=null) sb += ':'+this.value_list[i]
        }   
        return sb
    }
}

/**
 * A parsing rule for a rule enclosed in quotes
 */
class RuleInQuotes extends JoinRule{
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
    constructor(type, quoted_rule, quotes_type, left, right) {
        super(type, [quotes_type, quoted_rule, quotes_type], [left, null, right])
    }
}

/////// lifya.generic package ///////
/**
 * <p>Generic tokenizer. Matches each possible token type (lexemes) against input, and 
 * returns the first one (in the order provided by the array) that matches and consumes more input characters
 * (eager strategy) </p>
 *
 */
class GenericTokenizer extends Tokenizer{
    /**
     * Creates a tokenizer from a set of lexemes (token type recognizers)
     * @param lexemes Set of lexemes  that can recognize the Tokenizer
     */
    constructor( lexemes ){
        super();
        this.lexeme = {}
        this.priority = {}
        for( var i=0; i<lexemes.length; i++ ) {
            this.lexeme[lexemes[i].type] = lexemes[i]
            this.priority[lexemes[i].type] = i
        }
    }
 
    /**
     * Gets the last read/available Token 
     * @return Last read/available Token
     */
    analyze(input) {
        var start = input.pos
        var c = input.current()
        var opt = []
        var error = []
        for( var k in this.lexeme ) {
            var l = this.lexeme[k]
            if(l.startsWith(c)) {
                var t = l.match(input)
                if(t.isError()) error.push(t)
                else  opt.push(t)
                input.locate(start)
            }
        }
        var current=null
        if( opt.length > 0 ) {
            current = opt[0]
            for( var i=1; i<opt.length; i++ ) {
                var e2 = opt[i]
                if( e2.length()>current.length() ||
                    (e2.length()==current.length() && priority[e2.type]<priority[current.type]) )
                    current = e2
            }
           
        }else {
            if(error.length>0) {
                current = error[0]
                for( var i=1; i<error.size(); i++ ) {
                    var e2 = error[i]
                    if(e2.length()>current.length()) current = e2
                }
            }else { current = new Token(input,start, start+1) }
        }
        input.locate(start+current.length())
        return current
    }
    
    /**
     * Determines if a type name is a token type or not
     * @param type type name to analyze
     * @return <i>true</i> If the type name represents a token type, <i>false</i> otherwise.
     */
    isTokenType(type) { return this.lexeme[type] !== undefined }
    
    /**
     * Return the set of types of tokens
     * @return Types of tokens
     */
    tokenTypes(){
        var lex = []
        for(var k in this.lexeme ) 
            if( !this.removable(k) ) lex.push(k)
        return lex
    }
}

/**
 * <p>Generic parser. Checks the token source, an tries to apply each rule to match the token. </p>
 *
 */
class GenericParser extends Parser{    
    /**
     * Create a generic syntactic parser
     * @param tokenizer Tokenizer
     * @param rules Rules defining the syntactic parser
     * @param main Main rule id
     */
    constructor(tokenizer, rules, main) {
        super(tokenizer)
        this.rules = {}
        this.main = main
        for(var r in rules) this.add(rules[r])
        
    }
        
    /**
     * Add a rule to the parser
     * @param rule Rule to add
     */
    add(rule) { 
        this.rules[rule.type] = rule 
        rule.parser = this
    }

    /**
     * Sets the main rule
     * @param r Id of the main rule
     * @return Main syntactic rule 
     */
    rule(r) { return this.rules[r] }

    /**
     * Determines if the type name represents a rule or not
     * @param type Type name to analyze
     * @return <i>true</i> if the type represents a rule, <i>false</i> otherwise. 
     */
    isRule(type) { return this.rule(type)!=null }
    
    /**
     * Gets a syntactic token (derivation tree) using the provided rule
     * @param rule Rule id
     * @return Syntactic token generated by the rule 
     */
    analyze(rule=null) {
        if(rule==null) rule = this.main
        var r = this.rule(rule)
        return r.analyze(this.lexer)
    }
}

/////// lifya.parsergenerator.lexeme package ///////
/**
 * Generic token type recognizer for the Lifya parser/token recognizer generator
 */
class Matcher extends Lexeme{
    
    
    /**
     * Creates an abstract character token type recognizer
     * @param type Token type
     * @param embedded If the token type recognizer is embedded in a Lifya parser or not
     */ 
    constructor(type, embedded) {
        super(type);
        this.category="SwWdDlL"
        this.escape="\\{}()|[]-?*+.$nrts"
        this.symbol=" \n\t\r{}()|[]-?*+.$"
        this.embedded=":=%<>"
        if(embedded) {
            this.escape += this.embedded
            this.symbol += this.embedded
        }
    }
    
    match_error(input, start) {
        input.locate(start)
        return false      
    }

    /**
     * Matches the current character in the input source with a character in a range (advances the input source if matches)
     * @param input Source input
     * @return <i>true</i> if the current character in the input source falls in the range, <i>false</i> otherwise
     */
    match_range(input) {
        var pos = input.pos
        if(!this.match_one(input)) return false
        if(input.current()!='-') return this.match_error(input,pos)
        input.next();
        if(!this.match_one(input)) return this.match_error(input,pos)
        return true
    }

    /**
     * Matches the current character in the input source with a character category (advances the input source if matches)
     * @param input Source input
     * @return <i>true</i> if the current character in the input source falls in a category, <i>false</i> otherwise
     */
    match_category(input) {
        var c = input.current()
        if(c=='.') {
            input.next()
            return true
        }
        if(c!='\\') return false
        var pos = input.pos
        c = input.next()
        if(this.category.indexOf(c)<0) return this.match_error(input,pos)
        input.next()
        return true
    }
    
    /**
     * Matches the current character in the input source with a character (advances the input source if matches)
     * @param input Source input
     * @return <i>true</i> if the current character in the input source is a character (neither a category, or special symbol),
     *  <i>false</i> otherwise
     */
    match_one(input) {
        if(input.eoi()) return false
        var c = input.current()
        if(this.symbol.indexOf(c)>=0) return false
        var start = input.pos
        if(c=='\\') {
            c = input.next()
            if(input.eoi()) return this.match_error(input, start)
            if(c=='u') {
                c = input.next()
                var counter = 0
                while(!input.eoi() && counter<4 && (('0'<=c && c<='9') || 
                        ('A'<=c && c<='F') || ('a'<=c && c<='f'))){
                    c = input.next()
                    counter++
                }
                if(counter!=4) return this.match_error(input, start)
            }else if(this.escape.indexOf(c)<0) return this.match_error(input, start)
        }
        input.next()
        return true
    }
}

/**
 * A character recognizer
 */
class Any extends Matcher{
    /**
     * Creates a character token type recognizer
     * @param type Token type
     * @param embedded If the token type recognizer is embedded in a Lifya parser or not
     */ 
    constructor(type, embedded) { super(type,embedded) }
    
    /**
     * Reads a token from the input source 
     * @param input Symbol source
     * @return Token read from the symbol source
     */
    match(input) {
        if(input.eoi()) return this.error(input, input.pos)
        var count = 0
        var pos = input.pos
        while(this.match_one(input)) count++
        if(count == 0 ) return this.error(input, input.pos)
        return this.token(input,pos,input.substring(pos, input.pos))
    }

    /**
     * Determines if the token type can start with the given character
     * @param c Character to analyze
     * @return <i>true</i> If the token type can start with the given character <i>false</i> otherwise
     */
    startsWith(c) { return this.symbol.indexOf(c)<0 }
}

/**
 * Category token type recognizer.
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
 */
class Category extends Matcher{
    /**
     * Creates a category token type recognizer
     * @param type Token type
     * @param embedded If the token type recognizer is embedded in a Lifya parser or not
     */ 
    constructor(type, embedded) { super(type, embedded) }

    /**
     * Reads a token from the input source 
     * @param input Symbol source
     * @return Token read from the symbol source
     */
    match(input) {
        var pos = input.pos
        if(this.match_category(input)) return this.token(input,pos,input.substring(pos,input.pos))
        return this.error(input, pos)
    }

    /**
     * Determines if the token type can start with the given character
     * @param c Character to analyze
     * @return <i>true</i> If the token type can start with the given character <i>false</i> otherwise
     */
    startsWith(c) { return c=='\\' || c=='.' }
}

/**
 * Range token type recognizer (recognizes characters in a range)
 */
class Range extends Matcher{
    /**
     * Creates a range token type recognizer
     * @param type Token type
     * @param embedded If the token type recognizer is embedded in a Lifya parser or not
     */ 
    constructor(type, embedded) { super(type, embedded) }

    /**
     * Reads a token from the input source 
     * @param input Symbol source
     * @return Token read from the symbol source
     */
    match(input) {
        var pos = input.pos
        if(!this.match_one(input)) return this.error(input, input.pos)
        var c = input.current()
        if(c!='-')  return this.error(input, input.pos)
        input.next()
        if(!this.match_one(input)) return this.error(input, input.pos)
        return this.token(input,pos,input.substring(pos, input.pos))     
    }
    
    /**
     * Determines if the token type can start with the given character
     * @param c Character to analyze
     * @return <i>true</i> If the token type can start with the given character <i>false</i> otherwise
     */
    startsWith(c) { return this.symbol.indexOf(c)<0 }
}

/**
 * Set token type recognizer (recognizes characters in a set)
 */
class Set extends Matcher{
    /**
     * Creates a set token type recognizer
     * @param type Token type
     * @param embedded If the token type recognizer is embedded in a Lifya parser or not
     */ 
    constructor(type, embedded) { super(type, embedded) }

    /**
     * Determines if the token type can start with the given character
     * @param c Character to analyze
     * @return <i>true</i> If the token type can start with the given character <i>false</i> otherwise
     */
    startsWith(c) { return c=='[' || c=='-' }

    /**
     * Reads a token from the input source 
     * @param input Symbol source
     * @return Token read from the symbol source
     */
    match(input) {
        var c = input.current()
        if(!this.startsWith(c)) return this.error(input, input.pos)
        var pos = input.pos
        if(c=='-' && input.next()!='[') return this.error(input, pos)

        input.next()
        
        while(this.match_range(input) || this.match_one(input) || this.match_category(input)) {
            c = input.current()
            switch(c) {
                case ']':
                    input.next()
                    return this.token(input,pos,input.substring(pos, input.pos))
                case '|':
                    input.next()
                break;
                default: return this.error(input, input.pos)
            }
        }
        return this.error(input, input.pos)
    }
}

/**
 * Spaces recognizer ([\n|\t|\r|\s]+)
 */
class Space extends Lexeme{
    /**
     * Creates a space recognizer
     */
    constructor() { super(GeneratorConstants.SPACE) }

    /**
     * Reads a token from the input source 
     * @param input Symbol source
     * @return Token read from the symbol source
     */
    match(input) {
        var start = input.pos
        var c = input.current()
        if(!this.startsWith(c)) return this.error(input, input.pos)
        while(this.startsWith(input.next())){}
        return this.token(input,start," ")
    }

    /**
     * Determines if the token type can start with the given character
     * @param c Character to analyze
     * @return <i>true</i> If the token type can start with the given character <i>false</i> otherwise
     */
    startsWith(c) { return c==' ' || c=='\r' || c=='\t' || c=='\n' }
}

/////// lifya.parsergenerator.language package ///////
class GeneratorTokenizer extends GenericTokenizer{
    
    static lexemes() {
        return [new Any(GeneratorConstants.ANY, false), new Set(GeneratorConstants.SET, false), 
                new Category(GeneratorConstants.CATEGORY, false), new Symbol(GeneratorConstants.DOLLAR,"$"), 
                new Symbol(GeneratorConstants.CLOSURE,"?*+"), new Symbol(GeneratorConstants.PIPE,"|"), 
                new Symbol(GeneratorConstants.SYMBOL,"{}()-"), new Space()]
    }
    
    static embeded_lexemes() {
        return [new Any(GeneratorConstants.ANY, true), new Set(GeneratorConstants.SET, true), 
                new Category(GeneratorConstants.CATEGORY, true), new Symbol(GeneratorConstants.DOLLAR,"$"),
                new Symbol(GeneratorConstants.CLOSURE,"?*+"), new Symbol(GeneratorConstants.PIPE,"|"), 
                new Symbol(GeneratorConstants.SYMBOL,"{}()[]-=:"), new Space(), 
                new GeneratorLexeme(GeneratorConstants.ID, "<(%)?\\w+>", false),
                new GeneratorLexeme(GeneratorConstants.COMMENT, "%$",false)]
    }
    
    /**
     * Created generator for lifya tokenizer recognizer generation or lifya language parser generator
     * @param embeded A <i>true</i> value indicates a lifya language parser generator, a <i>false</i> value indicates
     * a lifya tokenizer recognizer generation
     */
    constructor(embeded) {
        super(embeded?GeneratorTokenizer.embeded_lexemes():GeneratorTokenizer.lexemes())
        this.removableTokens = {}
        this.removableTokens[GeneratorConstants.SPACE] = GeneratorConstants.SPACE
        this.removableTokens[GeneratorConstants.COMMENT] = GeneratorConstants.COMMENT
    }
}

    
/**
 * Parser of the Lifya language for generating parsers
 */
class GeneratorParser extends GenericParser{   
    // Parsing Lexeme expressions
            
    static quoted() { 
        return new RuleInQuotes(GeneratorConstants.QUOTED, GeneratorConstants.EXP, GeneratorConstants.SYMBOL, "(", ")")
    }
    
    static word() { return new KleeneRule(GeneratorConstants.WORD, false, GeneratorConstants.ANY); }
    
    static wordlist() { 
        return new ExpressionRule(GeneratorConstants.WORDLIST, 
                [GeneratorConstants.WORD, GeneratorConstants.WORD],
                [null, null], GeneratorConstants.PIPE, {"|":1})
    }
    
    static wordset() { 
        return new RuleInQuotes(GeneratorConstants.WORDSET, GeneratorConstants.WORDLIST, GeneratorConstants.SYMBOL, "{", "}")
    }
        
    static single() {
        return new DisjointRule(GeneratorConstants.SINGLE, 
                                [GeneratorConstants.QUOTED, GeneratorConstants.SET, GeneratorConstants.DOLLAR,
                                GeneratorConstants.ANY, GeneratorConstants.CATEGORY, GeneratorConstants.WORDSET])
    }

    static closed() {
        return new DisjointRule(GeneratorConstants.CLOSED, [GeneratorConstants.CLOSURE, EmptyRule.TAG])
    } 
    
    static term() {
        return new JoinRule(GeneratorConstants.TERM, [GeneratorConstants.SINGLE, GeneratorConstants.CLOSED])
    } 
    
    static join() { return new KleeneRule(GeneratorConstants.JOIN, false, GeneratorConstants.TERM) }
    
    static exp() {
        return new ExpressionRule(GeneratorConstants.EXP, 
                [GeneratorConstants.JOIN, GeneratorConstants.JOIN],
                [null, null], GeneratorConstants.PIPE,{"|": 1})
    }

    static rules() {
        return [new EmptyRule(), GeneratorParser.word(), GeneratorParser.wordlist(), GeneratorParser.wordset(), 
                GeneratorParser.quoted(), GeneratorParser.single(), GeneratorParser.closed(),
                GeneratorParser.term(), GeneratorParser.join(), GeneratorParser.exp()]
    } 
    
    // Parsing language rules
    static lexemedef() {
        return new JoinRule(GeneratorConstants.LEXEME, 
                [GeneratorConstants.ID, GeneratorConstants.SYMBOL, GeneratorConstants.EXP], [null, "=", null])
    }
    
    static lexerdef() { return new KleeneRule(GeneratorConstants.LEXER, false, GeneratorConstants.LEXEME) }
    
    static wrap() {
        return new RuleInQuotes(GeneratorConstants.WRAP, GeneratorConstants.RULEEXP, GeneratorConstants.SYMBOL, "(", ")")
    }
    
    static options() {
        return new DisjointRule(GeneratorConstants.OPTIONS, 
                            [GeneratorConstants.ID, GeneratorConstants.ANY, GeneratorConstants.WRAP])
    }
    
    static itemelement() {
        return new JoinRule(GeneratorConstants.ITEMELEMENT, 
                            [GeneratorConstants.OPTIONS,GeneratorConstants.CLOSED])
    }

    static item() { return new KleeneRule(GeneratorConstants.ITEM, false, GeneratorConstants.ITEMELEMENT) }

    static ruleexp() {
        return new ExpressionRule(GeneratorConstants.RULEEXP, 
                [GeneratorConstants.ITEM,  GeneratorConstants.ITEM],
                [null, null], GeneratorConstants.PIPE,{"|": 1})
    }

    static metaopers() {
        return new KleeneRule(GeneratorConstants.METAOPERS, false, GeneratorConstants.WORDSET)
    }

    static metaexp() {
        return new JoinRule(GeneratorConstants.METAEXP, 
                [GeneratorConstants.METAOPERS,GeneratorConstants.ID,GeneratorConstants.ID])
    }
    
    static rhs() {
        return new DisjointRule(GeneratorConstants.RULERHS, 
                [GeneratorConstants.RULEEXP, GeneratorConstants.METAEXP])
    }   
    
    static rhsdot() {
        return new JoinRule(GeneratorConstants.RULERHSDOT, 
                            [GeneratorConstants.RULERHS, GeneratorConstants.CATEGORY], [null,"."])
    }   
    
    static ruledef() {
        return new JoinRule(GeneratorConstants.RULE, 
                            [GeneratorConstants.ID, GeneratorConstants.SYMBOL, GeneratorConstants.SYMBOL, 
                            GeneratorConstants.RULERHSDOT], [null, ":", "-", null])
    }
    
    static parserdef() { return new KleeneRule(GeneratorConstants.PARSER, false, GeneratorConstants.RULE) }

    static language() {
        return new JoinRule(GeneratorConstants.LANG, 
                            [GeneratorConstants.LEXER, GeneratorConstants.PARSER])
    }
    
    // Set of rules
    static full_rules() {
        return [ new EmptyRule(), GeneratorParser.word(), GeneratorParser.wordlist(), GeneratorParser.wordset(), 
                GeneratorParser.quoted(), GeneratorParser.single(), GeneratorParser.closed(), 
                GeneratorParser.term(), GeneratorParser.join(), GeneratorParser.exp(), 
                GeneratorParser.lexemedef(), GeneratorParser.lexerdef(), GeneratorParser.wrap(), 
                GeneratorParser.options(), GeneratorParser.itemelement(), GeneratorParser.item(), 
                GeneratorParser.ruleexp(),GeneratorParser.metaopers(), GeneratorParser.metaexp(), 
                GeneratorParser.rhs(), GeneratorParser.rhsdot(), GeneratorParser.ruledef(), 
                GeneratorParser.parserdef(), GeneratorParser.language()]
    }       

    expand_set_inner( input, pos, set ) {
        var init = pos
        pos++
        var not = (set.charAt(0)=='-')
        if(not) {
            set = set.substring(2,set.length-1)
            pos++
        }else set = set.substring(1,set.length-1)
        var items = set.split("|")
        var a = []
        for( var i=0; i<items.length; i++) {
            var s = new Source("inner",items[i])
            if( this.matcher.match_range(s) ) {
                s.locate(0)
                this.matcher.match_one(s)
                var start = s.substring(0, s.pos)
                s.next()
                var p = s.pos
                this.matcher.match_one(s)
                var end = s.substring(p, s.length())
                var limits = [
                    new Token(input, pos, start.length, GeneratorConstants.ANY, start),
                    new Token(input, pos+start.length+1, pos+items[i].length, GeneratorConstants.ANY, end)
                ]
                a.push( new Token(input, pos, items[i].length, GeneratorConstants.RANGE, limits) )
            }else if(this.matcher.match_category(s)){
                a.push(new Token(input, pos, items[i].length, GeneratorConstants.CATEGORY, items[i]))
            }else a.push(new Token(input, pos, items[i].length, GeneratorConstants.ANY, items[i]))
            pos += items[i].length
            pos++
        }
        var t = new Token(input, init, pos, GeneratorConstants.EXP, a)
        if(not) t = new Token(input, init , pos, GeneratorConstants.NOT, t)
        return t
    }

    expand_set( t ) {
        if( Array.isArray(t.value) ) {
            var a = t.value
            for(var i=0; i<a.length; i++) a[i] = this.expand_set(a[i])
        }else if( t.type == GeneratorConstants.SET ) t = this.expand_set_inner(t.input, t.start, t.value)
        return t
    }
    
    expand_dollar( t ) {
        if( Array.isArray(t.value) ) {
            var a = t.value
            for(var i=0; i<a.length; i++) a[i] = this.expand_dollar(a[i])
        }else if( t.value == "$" ) {
            var c = [new Token(t.input, t.start, t.end, GeneratorConstants.SET, "-[\\n]"),
                    new Token(t.input, t.start, t.end, GeneratorConstants.CLOSURE, "*")]

            var d = [new Token(t.input, t.start, t.end, GeneratorConstants.ANY, "\\n"),
                    new Token(t.input, t.start, t.end, GeneratorConstants.CLOSURE, "?")]

            var b = [new Token(t.input, t.start, t.end, GeneratorConstants.TERM, c),
                    new Token(t.input, t.start, t.end, GeneratorConstants.TERM, d)]
                    
            t = new Token(t.input, t.start, t.end, GeneratorConstants.JOIN, b)
        }
        return t
    }
    
    wordset(a) {
        for( var i=0; i<a.length-1; i++) {
            var list = a[i].value
            var first = list[0]
            var start = first.value
            var s = [a[i]]
            var j=i+1; 
            while(j<a.length) {
                var list_j = a[j].value
                var start_j = list_j[0].value
                if(start == start_j) {
                    s.push(a[j])
                    a.splice(j,1)
                }else j++
            }
            var empty=false
            var k=0 
            while(k<s.length) {
                list = s[k].value
                list.splice(0,1)
                if(list.length==0) {
                    s.splice(k,1)
                    empty=true
                }else k++
            }
            if(s.length>0) {
                var next = wordset(s)
                if(empty) {
                    var b = [next, new Token(next.input, next.start, next.end, GeneratorConstants.CLOSURE, "?")]
                    next = new Token(next.input, next.start, next.end, GeneratorConstants.TERM, b)
                }
                var c = [first,next]
                a[i] = new Token(first.input, first.start, next.end, GeneratorConstants.JOIN, c)
                
            }else a[i] = first
        }
        for( var i=0; i<a.length; i++) a[i] = ProcessDerivationTree.reduce_size_1(a[i])
        if(a.length>1) 
            return new Token(a[0].input, a[0].start, a[a.length-1].end, GeneratorConstants.EXP, a)
        return a[0]
    }
    
    process_word_set(t) {
        if( Array.isArray(t.value) ) {
            var a = t.value
            for( var i=0; i<a.length; i++ ) a[i] = this.process_word_set(a[i])
        }

        if( t.type == GeneratorConstants.WORD ) t.type = GeneratorConstants.JOIN
        else if( t.type == GeneratorConstants.WORDLIST ){
            var a = t.value
            for( var i=0; i<a.length; i++) {
                var x = a[i]
                if(!(Array.isArray(x.value))) {
                    var l = [x]
                    a[i] = new Token(x.input, x.start, x.end, GeneratorConstants.WORD, l );
                }
            }
            for( var i=0; i<a.length-1; i++)
                for( var j=i+1; j<a.lrngth; j++) {
                    var x = a[i].value
                    var y = a[j].value
                    if(x.length<y.length) {
                        var z = a[i]
                        a[i] = a[j]
                        a[j] = z
                    }
                }
            t = this.wordset(a)
        }
        return t
    }
    
    reduce(t) {
        t = ProcessDerivationTree.eliminate_lambda(t)
        t = ProcessDerivationTree.eliminate_token(t, GeneratorConstants.PIPE, null)
        t = this.expand_dollar(t)
        t = this.expand_set(t)
        t = ProcessDerivationTree.reduce_size_1(t)
        t = ProcessDerivationTree.reduce_exp(t, GeneratorConstants.EXP)
        t = ProcessDerivationTree.reduce_exp(t, GeneratorConstants.WORDLIST)
        t = ProcessDerivationTree.reduce_exp(t, GeneratorConstants.RULEEXP)
        t = this.process_word_set(t)
        return t
    }
    
    /**
     * Reads an object from the input source (limited to the given starting and ending positions) 
     * @param input Symbol source
     * @return Object read from the symbol source
     */
    match(input){
        var t = super.match(input)
        return this.process_word_set(this.reduce(t))
    }
    
    /**
     * Creates a parser for Lifya token type recognizers or Lifya parser specifications
     * @param parser A <i>true</i> value indicates a parser for Lyfya parser specification, a <i>false</i> value
     * indicates a token type recognizer parser
     * @param embeded If the parser must be processing inside of a Lifya parser or not
     */
    constructor( parser, embeded=parser ) { 
        super(new GeneratorTokenizer(embeded), parser?GeneratorParser.full_rules():GeneratorParser.rules(), 
                parser?GeneratorConstants.LANG:GeneratorConstants.EXP) 
        this.matcher = new Matcher("matcher-inner", embeded)
    }
}

/**
 * A Lexeme (token type recognizer) generated by the Lifya parser generator
 */
class GeneratorLexeme extends Lexeme{
    /**
     * Creates a token type recognizer
     * @param type Token type
     * @param code_or_tree Lifya token type recognizer specification or lifya tree
     * @param embedded If the token type recognizer is embedded in a Lifya parser or not
     * @throws IOException If not valid Lifya token type recognizer specification
     */ 
    constructor( type, code_or_tree, embedded=false ){
        super(type)
        if(typeof code_or_tree == "string" ){
            var p = new GeneratorParser(false, embedded)
            this.tree = p.get(new Source("generator-lexeme",code_or_tree))
        }else this.tree = code_or_tree
    }
        
    match_exp(input, t) {
        var pos=input.pos
        var a = t.value
        var final_consumed = null
        for( var i=0; i<a.length; i++ ) {
            var consumed = this.match(input, a[i])
            input.locate(pos)
            if(final_consumed==null || (final_consumed[0]==0 && consumed[0]==1) ||
                (final_consumed[0]==consumed[0] && final_consumed[1]<consumed[1])) final_consumed = consumed
        }
        if(final_consumed[0]==1) input.locate(pos+final_consumed[1])
        return final_consumed
    }

    match_join(input, t) {
        var pos=input.pos
        var a = t.value
        var total_consumed = [1,0]
        for( var i=0; i<a.length; i++ ) {
            var consumed = this.match(input, a[i])
            if(consumed[0]==0) {
                input.locate(pos)
                consumed[1] = total_consumed[1]
                return consumed
            }
            total_consumed[1] += consumed[1]
        }
        return total_consumed
    }

    match_term(input, t) {
        var start=input.pos
        var a = t.value
        var consumed = this.match(input, a[0])
        var closure = a[1].value.charAt(0)
        switch(closure) {
            case '?':
                consumed[0]=1
                return consumed
            case '+':
                if(consumed[0]==0) return consumed;                
            break;
            case '*':
                if(consumed[0]==0) {
                    consumed[0]=1
                    return consumed                                       
                }
        }
        
        while(consumed[0]==1) consumed = this.match(input, a[0])
        consumed[0] = 1
        consumed[1] = input.pos-start
           
        return consumed
    }
    
    match_char_category(CAT, c) {
        switch(CAT) {
            case 'l': return ('a'<=c && c<='z') || ('A'<=c && c<='Z')
            case 'L': return !(('a'<=c && c<='z') || ('A'<=c && c<='Z'))
            case 'd': return Character.isDigit(c)
            case 'D': return !Character.isDigit(c)
            case 'w': return Character.isAlphabetic(c)
            case 'W': return !Character.isAlphabetic(c)
            case 'S': return (c!=' ')
            default: return false
        }           
    }
    
    match_category(input, t) {
        var pos = input.pos
        var value = t.value
        var c = input.current()
        switch(value.charAt(0)) {
            case '.':
                input.next()
                return [1,1]
            case '\\':
                if(this.match_char_category(value.charAt(1),c)) {
                    input.next()
                    return [1,1]
                }else {
                    input.locate(pos)
                    return [0,0]
                }   
            default:    
                return [0,0]
        }
    }
        
    match_any(input, t) {
        var pos = input.pos
        var value = ParserGenerator.escape_all(t.value)
        var c = input.current()
        for( var k=0; k<value.length; k++) {
            if(c!=value.charAt(k)) {
                input.locate(pos)
                return [0,k]
            }
            c = input.next()
        }   
        return [1,value.length]
    }
    
    match_range(input, t) {
        var a = t.value
        var start = ParserGenerator.escape(a[0].value)
        var end = ParserGenerator.escape(a[1].value)
        var c = input.current()
        if(start<=c && c<=end) {
            input.next()
            return [1,1]     
        }else return [0,0]
    }   

    match_not(input, t) {
        if(input.eoi()) return [0,0]
        var pos = input.pos
        t = t.value
        var current = this.match(input,t)
        if(current[0]==1) {
            current[1] = 0
            input.locate(pos)
        }else {
            current[1] = 1
            input.next()
        }
        current[0] = 1-current[0]
        return current
    }   
    
    /**
     * Reads a token from the input source 
     * @param input Symbol source
     * @return Token read from the symbol source
     */
    match(input, t=null) {
        if( t==null){
            var pos = input.pos
            var consumed = this.match(input,this.tree)
            if(consumed[0]==0) return this.error(input,pos)
            return this.token(input,pos,input.substring(pos, input.pos))
        }else{
            switch(t.type) {
                case GeneratorConstants.EXP: return this.match_exp(input, t)
                case GeneratorConstants.ANY: return this.match_any(input, t)
                case GeneratorConstants.CATEGORY: return this.match_category(input, t)
                case GeneratorConstants.RANGE: return this.match_range(input, t)
                case GeneratorConstants.TERM: return this.match_term(input,t)
                case GeneratorConstants.JOIN: return this.match_join(input,t)
                case GeneratorConstants.NOT: return this.match_not(input,t)
            }
            return [0,0]         
        }
    }


    /**
     * Determines if the token type can star with the given character
     * @param c Character to analyze
     * @return <i>true</i> If the token type can start with the given character <i>false</i> otherwise
     */
    startsWith(c) {
        var consumed = this.match(new Source("inner-lexeme",""+c),this.tree)
        return consumed[1]>=1
    }
}

/**
 * Language for the lifya parser generator
 */
class GeneratorLanguage extends Language{
    /**
     * Creates the Lifya language for parser generator
     */
    constructor() { super(new GeneratorParser(true)) }

    lexeme(lexeme){
        var a = lexeme.value
        return new GeneratorLexeme(a[0].value, a[1])
    }
    
    tokenizer(lexemes, symbols) {
        var lexeme = []
        if( symbols != null ) lexeme.push(symbols)
        var n
        if(Array.isArray(lexemes.value)) {
            var a = lexemes.value
            n = a.length
            for( var i=0; i<n; i++)
                lexeme.push(this.lexeme(a[i]))
        }else {
            lexeme.push(this.lexeme(lexemes))
        }
                
        var removable = []
        for( var i=0; i<lexeme.length; i++) if(lexeme[i].type.charAt(1)=='%') removable.push(lexeme[i].type)   
        var tokenizer = new GenericTokenizer(lexeme)
        tokenizer.removables(removable)
        return tokenizer
    }
    
    ruleoperators(t, op, p, map) {
        var a
        switch(t.type) {
            case GeneratorConstants.EXP:
                a = t.value
                for( var i=0; i<a.length; i++ )
                    this.ruleoperators(a[i],op,p,map)
            break;
            case GeneratorConstants.JOIN:
                a = t.value
                this.ruleoperators(a[1],op+a[0].value,p,map)
            break;
            case GeneratorConstants.TERM:
                map[op] = p
                a = t.value
                this.ruleoperators(a[0],op,p,map)
            break;
            case GeneratorConstants.ANY:
                map[op+t.value] = p
            break;
            default:
                console.log("--->"+t.type)    
        }
    }
    
    ruleoperators_one(t) {
        var a = t.value
        var x = a[0].value
        t = a[1] 
        if(t.type==GeneratorConstants.METAEXP) {
            var map = {}
            a = t.value
            t = a[0]
            var ftype = a[1].type
            var type = a[2].type
            var fvalue = a[1].value
            var value = a[2].value
            if(ftype==GeneratorConstants.ID) {
                ftype = fvalue
                fvalue = null
            }else ftype = GeneratorConstants.CHAR
            
            if(type==GeneratorConstants.ID) {
                type = value
                value = null
            }else type = GeneratorConstants.CHAR
            
            if(t.type==GeneratorConstants.METAOPERS) {
                t.type = GeneratorConstants.EXP
                a = t.value
                for( var i=0; i<a.length; i++)
                    this.ruleoperators(a[i], "", i, map)
            }else this.ruleoperators(t, "", 0, map)
            return [x, t, map, [ftype,type], [fvalue,value]]
        }
        return null
    }
    
    operators(t) {
        var l = []
        if(t.type==GeneratorConstants.RULE) {
            var x = this.ruleoperators_one(t)
            if(x!=null) l.push(x)
        }else {
            var a = t.value
            for( var i=0; i<a.length; i++ ) {
                var x = this.ruleoperators_one(a[i])
                if(x!=null) l.push(x)
            }
        }
        return l
    }

    symbols(t, current_symbols) {
        var a
        switch(t.type) {
            case GeneratorConstants.ANY:
                 current_symbols[t.value] = 1
            break;
            case GeneratorConstants.RULE:
                a = t.value
                if(a[1].type!=GeneratorConstants.METAEXP)
                    this.symbols(a[1],current_symbols)
            break;  
            default:
                if(Array.isArray(t.value)) {
                    a = t.value
                    for(var i=0; i<a.length; i++)
                        this.symbols(a[i],current_symbols)
                }       
        }
    }
    
    exprule(t, rules, id) {
        var a
        switch(t.type) {
            case GeneratorConstants.RULE:
                a = t.value
                if(a[1].type!=GeneratorConstants.METAEXP) {
                    id += a[0].value
                    this.exprule(a[1], rules, id)
                }
            break;
            case GeneratorConstants.RULEEXP:
            case GeneratorConstants.ITEM:
                a = t.value
                var type = []
                var value = []
                for( var i=0; i<a.length; i++) {
                    switch(a[i].type) {
                        case GeneratorConstants.ID:
                            type.push(a[i].value)
                            value.push(null)
                        break;
                        case GeneratorConstants.ANY:
                            type.push(GeneratorConstants.CHAR)
                            value.push(ParserGenerator.escape_all(a[i].value))
                        break;
                        default:
                            type.push(id+GeneratorConstants.ITEM_TAG+i)
                            value.push(null)
                            this.exprule(a[i],rules,type[i])
                    }
                }
                if( t.type==GeneratorConstants.RULEEXP ) rules.push(new DisjointRule(id, type, value))
                else rules.push(new JoinRule(id, type, value))
            break;
            case GeneratorConstants.ITEMELEMENT:
                a = t.value
                var i_type
                var i_value
                switch(a[0].type) {
                    case GeneratorConstants.ID:
                        i_type = a[0].value
                        i_value = null
                    break;
                    case GeneratorConstants.ANY:
                        i_type = GeneratorConstants.CHAR
                        i_value = a[0].value
                    break;
                    default:
                        i_type = id+GeneratorConstants.ITEM_TAG
                        i_value = null
                        this.exprule(a[0],rules,i_type)
                }
                var c = a[1].value.charAt(0)
                if(c=='+' || c=='*')
                    rules.push(new KleeneRule(id, c=='*', i_type, i_value))
                else {
                    rules.push(new EmptyRule())
                    rules.push(new DisjointRule(id,
                            [i_type,EmptyRule.TAG], [i_value,null]))
                }                   
            break;
            case GeneratorConstants.PARSER:
                a = t.value
                for( var i=0; i<a.length; i++ )
                    this.exprule(a[i],rules,id)
            break;  
            default:
                var d_type
                var d_value
                if(t.type == GeneratorConstants.ID) {
                    d_type = t.value
                    d_value = null
                }else{
                    d_type = GeneratorConstants.CHAR
                    d_value = t.value               
                }
                rules.push(new JoinRule(id, [d_type], [d_value]))
        }
    }
    
    /**
     * Creates a parser for the derivation tree obtained by the Lifya generator
     * @param t Derivation tree
     * @return Parser for the derivation tree obtained by the Lifya generator
     */
    mean(t) {
        if(t.isError()) return t
        var a = t.value
        var symbols = {}
        this.symbols(a[1],symbols)
        var tokenizer=null
        var parser = null
        var rules = []
        var exp = this.operators(a[1])
        for(var i=0; i<exp.length; i++) {
            var e = exp[i]
            var type = e[0]
            var p = e[2]
            var pf = {}
            var opers = []
            var opers_type = []
            for(var op in p) {
                //if(symbols[op] === undefined) {
                    var key = ParserGenerator.escape_all(op)
                    symbols[op] = p[op]
                    opers.push(key)
                    opers_type.push(GeneratorConstants.CHAR)
                    pf[key] = p[op]
               //}
            }
            rules.push(new DisjointRule(type+GeneratorConstants.OPER, opers_type, opers))
            var ftype = e[3]
            var fvalue = e[4]
            rules.push(new ExpressionRule(type, ftype, fvalue, type+GeneratorConstants.OPER, pf))
        }
        var obj = null
        var sb = ''
        var c = ''
        for(var word in symbols) {
            sb += c + word
            c='|'
        }
        if(sb.length>0) {
            sb = '{'+sb+'}'
            obj = new GeneratorLexeme(GeneratorConstants.CHAR, sb,true)
        }
        tokenizer = this.tokenizer(a[0], obj)
            
        this.exprule(a[1], rules, "")
        parser = new GenericParser(tokenizer,rules,rules[0].type)
        return new Token(t.input, t.start, t.end, t.type, parser)
    }
}

/////// lifya.parsergenerator package ///////


/**
 *  Constants for the parser generator
 */
class GeneratorConstants {
    static ANY ="lifya_lexeme_any"
    static DOLLAR ="lifya_lexeme_dollar"
    static SPACE ="lifya_lexeme_space"
    static CATEGORY ="lifya_lexeme_category"
    static SYMBOL ="lifya_lexeme_symbol"
    static CLOSURE ="lifya_lexeme_closure"
    static PIPE ="lifya_lexeme_pipe"
    static NOT = "lifya_lexeme_not"
    static RANGE = "lifya_lexeme_range"
    static COMPLEMENT = "lifya_lexeme_complement"
    static ELEMENT = "lifya_lexeme_element"
    static LIST = "lifya_lexeme_list"
    static BASICSET = "lifya_lexeme_basicset"
    static SET = "lifya_lexeme_set"
    static TERM = "lifya_lexeme_term"
    static EXP = "lifya_lexeme_expression"
    static QUOTED = "lifya_lexeme_quoted"
    static SINGLE = "lifya_lexeme_single"
    static JOIN = "lifya_lexeme_join"
    static CLOSED = "lifya_lexeme_closed"
    static WORD = "lifya_lexeme_word"
    static WORDLIST = "lifya_lexeme_wordlist"
    static WORDSET = "lifya_lexeme_wordset"

    static LANG = "lifya_language"
    static ID = "lifya_id"
    static COMMENT = "lifya_comment"
    static LEXEME = "lifya_lexeme"
    static LEXER = "lifya_lexer"
    static ITEMELEMENT = "lifya_rule_item_element"
    static ITEM = "lifya_rule_item"
    static OPTIONS = "lifya_rule_options"
    static WRAP = "lifya_exp_wrap"
    static RULE = "lifya_rule"
    static RULERHS = "lifya_rule_rhs"
    static RULERHSDOT = "lifya_rule_rhsdot"
    static METAOPERS = "lifya_meta_opers"
    static METAEXP = "lifya_meta_exp"
    static RULEEXP = "lifya_rule_exp"
    static PARSER = "lifya_parser"

    static OPER = "oper"
    static CHAR = "symbol"
    static ITEM_TAG = "-item-"  
}

/**
 * Parsing generator utilities
 */
class ParserGeneratorClass {
    /**
     * Gets a blob (byte array) from a base64 string. Supposes it is a valid string 
     * @param txt Valid base64 string
     * @return Byte array encoded by the string
     */
    raw_blob(txt) { return Base64.decode(txt) }

    /**
     * Gets a base64 recognizer
     * @return Base64 recognizer
     */
    blob_lexeme() { return new GeneratorLexeme("blob", "[\\+|/|\\l|\\d]+(=(=?))?") }

    /**
     * Gets a blob (byte array) from a base64 string. Throws an error if not a valid string 
     * @param txt Base64 string
     * @return Byte array encoded by the string
     * @throws IOException if not valid string or input error 
     */
    blob(txt){
        var lexeme = this.blob_lexeme()
        var input = new Source("blob", txt)
        var txt = lexeme.get(input)
        var n = txt.length
        if(txt.charAt(n-1)=='=' && n%4!=0) throw Stringifier.apply(new Token(input,n-1,n))
        return this.raw_blob(txt)
    }

    /**
     * Gets an int recognizer
     * @return int recognizer
     */
    integer_lexeme() { return new GeneratorLexeme("int", "[\\+|\\-]?\\d+") }

    /**
     * Gets an integer from a string. Throws an error if not a valid string 
     * @param txt integer string
     * @return integer encoded by the string
     * @throws IOException if not valid string or input error 
     */
    integer(txt){ 
        var lexeme = this.integer_lexeme()
        return parseInt(lexeme.get(new Source("int", txt)))
    }
        
    /**
     * Gets a natural numbers recognizer
     * @return Natural numbers recognizer
     */
    natural_lexeme() { return new GeneratorLexeme("nat", "\\d+") }
    
    /**
     * Gets a natural number from a string. Throws an error if not a valid string 
     * @param txt natural number string
     * @return Natural number encoded by the string
     * @throws IOException if not valid string or input error 
     */
    natural(txt){ 
        var lexeme = this.natural_lexeme()
        return parseInt(lexeme.get(new Source("nat", txt)))
    }
    
    /**
     * Gets a real numbers recognizer
     * @return real numbers recognizer
     */
    real_lexeme() { return new GeneratorLexeme("number", "[\\+|\\-]?\\d+(\\.\\d+)?([e|E][\\+|\\-]?\\d+)?") }

    /**
     * Gets a real number from a string. Throws an error if not a valid string 
     * @param txt real number string
     * @return Real number encoded by the string
     * @throws IOException if not valid string or input error 
     */
    real(txt){ 
        var lexeme = this.real_lexeme()
        return parseFloat(lexeme.get(new Source("noname", txt)))
    }
    
    /**
     * Gets a space recognizer: multiple \n,\r,\t and whitespace characters.
     * @return Space recognizer
     */
    space_lexeme() { return new GeneratorLexeme("space",  "[\\n|\\t|\\r|\\s]+") }
    
    /**
     * Process the start of a string considering escape characters  codification
     * @param txt String to be processed
     * @return First character in the string (considering escape codification)
     */
    escape(txt) {
        if(txt.charAt(0) != '\\') return txt.charAt(0)
        var c = txt.charAt(1)
        switch(c){
            case 'u': return String.fromCharCode(parseInt(txt.substring(2,6),16))
            case 'n': return '\n'
            case 'r': return '\r'
            case 't': return '\t'
            case 'b': return '\b'
            case 'f': return '\f'
            case 's': return ' '
            default: return c
        }       
    }
    
    /**
     * Process a string for processing possible escape characters codification 
     * @param txt String to be processed
     * @return String (considering escape codification)
     */ 
    escape_all(txt) {
        var sb = ''
        while(txt.length>0) {
            var i=0
            var c = this.escape(txt)
            sb += c
            if(txt.charAt(i)=='\\') {
                i++
                if(txt.charAt(i)=='u') i+=4
            }
            i++
            txt = txt.substring(i)
        }
        return sb
    }
    
    /**
     * Gets a string from a encoded string. Supposes it is a valid string 
     * @param txt Valid encoded string
     * @param quotation Quotation character of strings
     * @return String encoded by the string
     */
    raw_string(txt, quotation='"') {
        var str = ""
        var n = txt.length
        var end = 1
        var c=txt.charAt(end)
        while(end<n && c!=quotation){
            if(c=='\\'){
                str += this.escape(txt.substring(end,Math.min(n, end+6)))
                end++
                c=txt.charAt(end)
                if(c=='u') end += 4
            }else str += c
            end++
            c=txt.charAt(end)
        }
        return str
    }

    /**
     * Gets a string recognizer.
     * @param quotation Quotation character of strings
     * @return Space recognizer
     */
    string_lexeme(quotation='"') { 
        var code = quotation + 
                    "(-[\\\\|"+quotation+"]|\\\\([\\\\|n|r|t|"+quotation+"]|u[A-F|a-f|\\d][A-F|a-f|\\d][A-F|a-f|\\d][A-F|a-f|\\d]))*"+quotation
                    console.log(code)
        return new GeneratorLexeme("string",  code)
    }

    /**
     * Gets a string from a encoded string. Throws an error if not a valid string 
     * @param txt Encoded string
     * @param quotation Quotation character of strings
     * @return String decoded string
     * @throws IOException if not valid string or input error 
     */
    string(txt, quotation='"'){ 
        var lexeme = this.string_lexeme(quotation)
        return this.raw_string(lexeme.get(new Source("string", txt)), quotation)
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
    lexeme(type, code){ return new GeneratorLexeme(type, code) }
    
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
    parser(code, main_rule){
        var language = new GeneratorLanguage()
        var parser = language.get(new Source("parser generator", code))
        parser.main = main_rule
        return parser
    }
}

ParserGenerator = new ParserGeneratorClass();

/**
 * Derivation tree utility process functions
 */
class ProcessDerivationTreeClass {
    /**
     * Eliminates lambda tokens
     * @param t Derivation tree to process
     * @return Derivation tree without lambda tokens
     */
    eliminate_lambda( t ) {
        if( Array.isArray(t.value) ) {
            var a = t.value
            for(var i=a.length-1; i>=0; i-- ) {
                if(a[i].type==EmptyRule.TAG) a.splice(i,1)
                else a[i] = this.eliminate_lambda(a[i])
            }   
        }
        return t
    }

    /**
     * Eliminates specific type of tokens
     * @param t Derivation tree to process
     * @param type Type of tokens to eliminate
     * @param value Specific value for the token
     * @return Derivation tree without specific type of tokens
     */
    eliminate_token( t, type, value ) {
        if( Array.isArray(t.value) ) {
            var a = t.value
            for(var i=a.length-1; i>=0; i-- ) {
                if(a[i].type==EmptyRule.TAG || 
                        a[i].type ==type &&
                    (value==null || t.value==value)) a.splice(i,1)
                else a[i] = this.eliminate_token(a[i],type,value)
            }   
        }
        return t
    }

    /**
     * Reduces branches of the tree with a single branch (moves unique child as parent)
     * @param t Derivation tree to process
     * @return Derivation tree without unique branches
     */
    reduce_size_1( t ) {
        if( Array.isArray(t.value) ) {
            var a = t.value
            if(a.length==1) t = this.reduce_size_1(a[0])
            else for(var i=a.length-1; i>=0; i-- ) a[i] = this.reduce_size_1(a[i])
        }else if( t.value instanceof Token ) 
            t.value = this.reduce_size_1(t.value)
        return t
    }

    /**
     * Reduces expression with unique operator (produces list instead of single joined with binary)
     * @param t Derivation tree to process
     * @param exp_type Type of the expression
     * @return Derivation tree with specific expressions reduced (forming lists)
     */
    reduce_exp( t, exp_type ) {
        if( Array.isArray(t.value) ) {
            var a = t.value
            for(var i=0; i<a.length; i++) a[i] = this.reduce_exp(a[i], exp_type)
            if(t.type == exp_type) {
                if(a.length>0) {
                    if(a[0].type==exp_type) {
                        var x = a[0].value
                        x.push(a[1])
                        t = a[0]
                    }else if(a[1].type==exp_type) {
                        var x = a[1].value
                        x.splice(0,0,a[0])
                        t = a[1]
                    } 
                }
            }
        }
        return t
    }   
    
    /**
     * Replaces tokens with a given type with another one type
     * @param t Derivation tree to process
     * @param old_type Type to replace
     * @param new_type New type for such tokens
     * @return Derivation tree with the tokens been replaced
     */
    replace( t, old_type, new_type ) {
        if(t.type==old_type) t.type = new_type
        if( Array.isArray(t.value) ) {
            var a = t.value
            for(var i=0; i<a.length; i++) a[i] = this.replace(a[i], old_type, new_type)
        } 
        return t
    }

    apply(t, opers){
        for(var i=0; i<opers.length; i++){
           switch( opers[i][0] ){
               case 'LAMBDA': t = ProcessDerivationTree.eliminate_lambda(t); break;
               case 'DEL':
                 if(opers[i].length == 2) opers[i].push(null)
                   t = ProcessDerivationTree.eliminate_token( t, opers[i][1], opers[i][2] )
               break; 
               case 'REDUCE':
                 if(opers[i].length == 1)
                   t = ProcessDerivationTree.reduce_size_1( t )
                 else
                   t = ProcessDerivationTree.reduce_exp( t, opers[i][1] )
               break; 
               case 'REPLACE':
                 t = ProcessDerivationTree.replace( t, opers[i][1], opers[i][2] )
               break;
           } 
        }
        return t;
    }
}

/**
 * Derivation tree utility methods (global object)
 */
ProcessDerivationTree = new ProcessDerivationTreeClass()

/////// lifya.stringify package ///////

/**
 * <p>Stringifies (Stores into a String) an object</p>
 *
 */
class StringifierClass {  
    /**
     * Stringifies an array with the associated formatting characters
     * @param array Array to be stringified
     * @param OPEN Array opening character 
     * @param CLOSE Array closing character
     * @param SEPARATOR Array elements separating character
     * @return Stringified version of the portion of the array
     */
    apply_array(array, OPEN='[', CLOSE=']', SEPARATOR=',') {
        var str = new ArrayStringifier(OPEN,CLOSE,SEPARATOR)
        return str.apply(array)  
    }
    
    /**
     * Stringifies a hashmap with the associated formatting characters
     * @param map HashMap to be stringified
     * @param OPEN Array opening character 
     * @param CLOSE Array closing character
     * @param SEPARATOR Array elements separating character
     * @param ASSIGN key/value assign character
     * @return Stringified version of the hashmap
     */
    apply_object( map, OPEN='{', CLOSE='}', SEPARATOR=',', ASSIGN=':') {
        var sb = ''
        var flag = false
        if( OPEN != '\0' ) sb += OPEN
        for( var key in map ) {
            if( flag ) sb += SEPARATOR
            sb += this.apply(key)
            sb += ASSIGN
            sb += this.apply(map[key])
            flag = true
        }
        if( CLOSE != '\0' ) sb += CLOSE
        return sb
    }
    
    /**
     * Stringifies an object
     * @param obj Object to be stringified
     * @return Stringified version of the object
     */
    apply( obj ){
        if(obj==null) return "null";
        if(obj.byteLength !== undefined) return this.apply_blob(obj)
        if(Array.isArray(obj)) return this.apply_array(obj, '[', ']', ',')
        if(typeof obj == "string") return this.apply_string(obj)
        if(typeof obj == "number" || typeof obj == "boolean") return ""+obj
        if(typeof obj.json == "function") return this.apply_object(obj.json())
        return this.apply_object(obj)
    }

    /**
     * Stringifies a String using the provided character as quotation
     * @param str String to be stringified
     * @param quotation Character used as quotation for the string
     * @return Stringified version of the String
     */
    apply_string(str, quotation='"') {
        var sb = quotation
        for( var i=0; i<str.length; i++ ){
            var c = str.charAt(i)
            switch( c ){
                case '\\': sb += "\\\\"; break;
                case '\b': sb += "\\b"; break;
                case '\f': sb += "\\f"; break;
                case '\n': sb += "\\n"; break;
                case '\r': sb += "\\r"; break;
                case '\t': sb += "\\t"; break;
                default:
                    var cc = c.charCodeAt(0)
                    if( cc < 32 || cc > 255 ){
                        sb += "\\u"
                        c = cc.toString(16)
                        while( c.length < 4 ) c = '0'+c
                        sb += c
                    }else if(c==quotation)
                        sb += "\\"+quotation
                    else
                        sb += c
                break;
            }
        }
        sb += quotation
        return sb               
    }
    
    /**
     * Stringifies a blob (byte array) using Base64 and character # as starter for identifying a blob
     * @param blob Byte array/blob to stringify
     * @return Stringified version of the blob/byte array
     */
    apply_blob(blob) { return Base64.encode(blob) }
}

/**
 * Stringifier global object
 */
Stringifier = new StringifierClass()

/////// speco.array package ///////
/**
 * <p>Stringify method for array of objects.</p>
 *
 */
class ArrayStringifier {
    /**
     * Creates an array stringifier with the associated formatting characters
     * @param open Array opening character 
     * @param close Array closing character
     * @param separator Array elements separating character
     */
    constructor( open='[', close=']', separator=',' ) {
        this.CLOSE = close
        this.OPEN = open
        this.SEPARATOR = separator
    }
    
    /**
     * Stringifies a portion of an array
     * @param array Array to be stringified
     * @param start Initial position of the portion of the array to be stringified
     * @param end Final position (not included) of the portion of the array to be stringified 
     * @return An stringified version of the portion of the array
     */
    apply( array, start=0, end=array.length ) {
        var n = array.length
        start = Math.max(0, start)
        end = Math.min(end, n)
        var sb = ''
        if( this.OPEN != '\0' ) sb += this.OPEN
        var flag = false
        for( var i=0; i<n; i++ ){
            if( flag ) sb += this.SEPARATOR
            var x = Stringifier.apply(array[i])
            if( x==null ) return null
            sb += x
            flag = true            
        }   
        if( this.CLOSE != '\0' ) sb += this.CLOSE
        return sb   
    }    
}


function generate_parser( rule, language, tree_process, name = 'NonameLanguage' ){
    var lines = language.split('\n')
    language = ''
    var sep =  ''
    for(var i=0; i<lines.length; i++){   
      if(lines[i].length > 0 )
        language += '    ' + sep + '\''+ lines[i] + '\'\n'
        sep = ' + '
    }
    var tp = tree_process.split('\n') 
    sep =  ''
    tree_process = '[' 
    for(var i=0; i<tp.length; i++){
      if(tp[i].length>0){
        tp[i] = tp[i].split(',')
        tpt = ''
        var sep2 = ''
        for(var j=0; j<tp[i].length; j++){
            tpt += sep2 + '\'' + tp[i][j] + '\''
            sep2 = ',' 
        }
        tree_process += sep + '\n      ['+ tpt + ']' 
        sep = ','
      }
    }  
    tree_process += '\n    ]'
    var code = 
      'class ' + name + ' extends Language{ \n' +
      '  static lang = \n' +
      language + 
      '  static init() { return ParserGenerator.parser(this.parser,'+rule+') }\n'  +
      '  constructor() { super('+name+'.init()) }\n' +
      '  process(t){ //@TODO: Process the derivation tree here }\n' +
      '  mean(t){\n' +
      '    t = ProcessDerivationTree.apply(t, ' + tree_process + ')\n' +
      '    var obj = this.process(t)\n' +
      '    t.value = obj\n' +
      '    return t\n' +
      '  }\n' +
      '  apply(input){\n'+
      '    var parser = new ' + name + '()\n' +
      '    return parser.get(new Source("noname",input))\n' +
      '  }\n' +       
      '}'
    return code 
}
