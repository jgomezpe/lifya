/**
*
* lifya_wrap.js
* <P>Java Script for language processing.</P>
* <P> contains base64.js and kompari.js. </P>
* <P>A numtseng module <A HREF="https://numtseng.com/modules/lifya.js">https://numtseng.com/modules/lifya.js</A> 
*
* Copyright (c) 2021 by Jonatan Gomez-Perdomo. <br>
* All rights reserved. See <A HREF="https://github.com/jgomezpe/lifya">License</A>. <br>
*
* @author <A HREF="https://disi.unal.edu.co/~jgomezpe/"> Professor Jonatan Gomez-Perdomo </A>
* (E-mail: <A HREF="mailto:jgomezpe@unal.edu.co">jgomezpe@unal.edu.co</A> )
* @version 1.0
*/

/** Object for coding/decoding uint8 arrays tto/from byte64 strings  */
class Base64Class{
    constructor(){
        /**
        * From int to char
        */
        i2a : ['A','B','C','D','E','F','G','H','I','J','K','L','M',
                     'N','O','P','Q','R','S','T','U','V','W','X','Y','Z',
                     'a','b','c','d','e','f','g','h','i','j','k','l','m',
                     'n','o','p','q','r','s','t','u','v','v','x','y','z',
                     '0','1','2','3','4','5','6','7','8','9','+','/']
    }
   
   /** 
    * Generates the dictionary for decoding a char to int
    */ 
    init(){
        if( this.a2i === undefined ){
            this.a2i = {}
            for( var k=0; k<this.i2a.length; k++ )
                this.a2i[this.i2a[k]] = k           
        }        
    }
    
    /**
     * Decodes a base64 string into a uint8 array if possible
     * @param str Base64 string
     * @return The uint8 array encode by the base64 string if possible
     * @throws An exception if the string does not represent a valid base64 code 
     */
    decode(str){
        this.init()
        var end = str.length
        while(end>=0 && str.charAt(end-1)=='=') end--
        if(end<2) throw '·Invalid Base64 string at· ' + end
        var m = (end%4)
        if(m==1) throw '·Invalid Base64 string at· ' + (end-1)
        if(m>1) m--
        var n = 3*Math.floor(end/4) + m
        var blob = new Uint8Array(n)
        var control =[[2,4,1],[4,2,1],[6,0,2]]
        var left, right
        var k=0
        var c=0
        for(var i=0; i<n; i++){
            left = this.a2i[str.charAt(k)]
            right = this.a2i[str.charAt(k+1)]
            if(left===undefined || right===undefined) throw '·Invalid Base64 string at· ' + k
            blob[i] =  (left << control[c][0]) |( right >> control[c][1])
            k+=control[c][2]
            c = (c+1)%3
        } 
        return blob
    }
    
    /**
     * Encodes a uint8 array into a base64 string if possible 
     * @param blob uint8 array to encode
     * @return A base64 string representation of the uint8 array
     * @throws An exception if the argument is not a uint8 array 
     */
    encode(blob){
        this.init()
        if( blob.byteLength === undefined ) throw '·Not a byte array·'
        var str=''
        var m = (blob.length%3)
        if(m>0) m++        
        var n = 4*Math.floor(blob.length/3) + m
        var k=0
        var c=0
        for(var i=0; i<n; i++){
            c=i&3
            switch(c){
                case 0: str += this.i2a[blob[k]>>2]; break;
                case 1: str += this.i2a[((blob[k]&3)<<4) | (blob[k+1]>>4)]; break;
                case 2: str += this.i2a[((blob[k]&15)<<2) | (blob[k+1]>>6)]; break;
                case 3: str += this.i2a[blob[k]&63]; break;
            }
            if(c!=0) k++        
        }
        while(m<4){
            str+='='
            m++
        }     
        return str
    }
    
    /**
     * Encodes a string into a base64 string if possible 
     * @param str str to encode
     * @param encoder Byte level encoder for the source string
     * @return A base64 string representation of the string
     * @throws An exception if the argument is not a string
     */
    atob(str, encoder=new TextEncoder()){
        return this.enconde(encoder.encode(str))
    }

    /**
     * Decodes a base64 string into a string if possible
     * @param str Base64 string
     * @param encoder Byte level encoder for the traget uint8 array
     * @return The string encode by the base64 string if possible
     * @throws An exception if the string does not represent a valid base64 code 
     */
    btoa(str, decoder=new TextDecoder()){
        return decoder.decode(this.decode(str))
    }
}

/** Object for coding/decoding uint8 arrays tto/from byte64 strings  */
Base64 = new Base64Class()

/////// Kompari.js ////////////
/**
 * Determines if the first number is less than (in some order) the second number(one&lt;two)
 * @param one First number
 * @param two Second number
 * @return (one&lt;two)
 */
function l2h(one,two){ return (one-two) }

/**
 * Determines if the first number is greater than (in some order) the second number(one&gt;two)
 * @param one First number
 * @param two Second number
 * @return (one&gt;two)
 */
function h2l(one,two){ return (two-one) }

Compare = {
    equals(one, two){
        if(one.equals !== undefined) return one.equals(two)
        else return one==two
    }    
}

/** Searching algorithm for sorted arrays of objects */
class SortedSearch {
    /**
     * Creates a search operation for the given sorted array
     * @param sorted Array of elements (should be sorted)
     * @param order Order used for locating the object
     */
    constructor(order, sorted){ 
        this.order = order
        this.sorted = sorted
    }
    
    /**
     * Searches for the position of the given element. The vector should be sorted
     * @param x Element to be located
     * @param start Starting searching position
     * @param end Ending (not included) searching position
     * @return The position of the given object, -1 if the given object is not in the array
     */
    find(x, start, end) { 
        end = end || this.sorted.length
        start = start || 0
        var pos = this.findRight(x, start, end)
        if (pos > start && this.order(x, this.sorted[pos-1]) == 0) pos--
        else pos = -1
        return pos
    }

    /**
     * Determines if the sorted array contains the given element (according to the associated order)
     * @param x Element to be located
     * @param start Starting searching position
     * @param end Ending (not included) searching position
     * @return <i>true</i> if the element belongs to the sorted array, <i>false</i> otherwise
     */
    contains(x, start, end){ return (this.find(x, start, end) != -1) }

    /**
     * Searches for the position of the first element in the array that is bigger
     * than the element given. The array should be sorted
     * @param x Element to be located
     * @param start Starting searching position
     * @param end Ending (not included) searching position
     * @return Position of the object that is bigger than the given element
     */
    findRight(x, start, end){ 
        end = end || this.sorted.length
        start = start || 0
        if(end > start) {
            var a = start
            var b = end - 1
            if (this.order(x, this.sorted[a]) < 0)  return start
            if (this.order(x, this.sorted[b]) >= 0) return end
            while (a + 1 < b) {
                var m = Math.floor((a + b) / 2)
                if (this.order(x, this.sorted[m]) < 0) b = m
                else a = m
            }
            return b
        }else return start
    }

    /**
     * Searches for the position of the last element in the array that is smaller
     * than the element given. The array should be sorted
     * @param start Starting searching position
     * @param end Ending (not included) searching position
     * @param x Element to be located
     * @return Position of the object that is smaller than the given element
     */
    findLeft(x, start, end) {
        end = end || this.sorted.length
        start = start || 0
        if (end > start) {
            var a = start
            var b = end - 1
            if (this.order(x, this.sorted[a]) <= 0)  return start-1
            if (this.order(x, this.sorted[b]) > 0) return b
            while (a + 1 < b) {
                var m = Math.floor((a + b) / 2)
                if (this.order(x, this.sorted[m]) <= 0) b = m
                else a = m
            }
            return a
        }else return start
    } 
}

/////// Lyfia.js ////////////
/**
 * Considers a given String as an input for the language recognizer
 */
class Source{   
    /**
     * Creates a source input from the given String
     * @param id Identification TAG of the input source
     * @param input String used as input source
     */
    constructor(input, id) {
        this.id = id || 'noname'
        this.input = input
        this.rows = []
        this.search = new SortedSearch(l2h, this.rows)
        this.rows.push(0)
        for(var i=0; i<input.length; i++) {
            if(input.charAt(i)=='\n') this.rows.push(i+1)
        }
        this.length = input.length
    }
   
    /**
     * Gets [row,column] array when considering position a 2D position
     * @param index Absolute position to be analyzed
     * @return [row,column] array when considering the given absolute position a 2D position
     */
    pos(index) {
        var idx = this.search.findLeft(index)
        if(idx+1<this.rows.length && this.rows[idx+1]==index)
            return [idx+1,0]
        return [idx, index-this.rows[idx]]
    }
    
    /**
     * Gets the character at the given position
     * @param index Position of the character to obtain
     * @return Character at the given position
     */
    get(index) { return this.input.charAt(index) }
    
    /**
     * Obtains a substring of the input String
     * @param start Starting position of the substring to obtain
     * @param end Final position (not included) of the substring to obtain
     * @return Substring of the input String
     */
    substring(start,end) { return this.input.substring(start,end) }
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
        var pos = this.input.pos(this.start)
        return {"input":this.input.id, "start":this.start,
        "row":pos[0], "column":pos[1]}
    }
    
    /**
     * Stringifies the position
     * @return Strigified versionof the position
     */
    stringify(){ return JSON.stringify(this.json()) }
}

/**
 * Language token (may be a lexeme, a syntactic rule, an object associated with a position in the source 
 */
class Token extends Position{    
    /**
     * Error's TAG. Used for identifying error tokens
     */
    static ERROR = 'error'
        
    /**
     * Creates a token
     * @param input Input source from which the token was built
     * @param start Starting position of the token in the input source
     * @param end Ending position (not included) of the token in the input source
     * @param value Value stored by the token
     * @param type Token type
     */
    constructor(input, start, end, value, type){
        super(input, start)
        this.end = end
        this.type = type || Token.ERROR
        this.value = value
    }

    /**
     * Computes the length (number of symbols) consumed by the token 
     * @return Length (number of symbols) consumed by the token
     */
    size(){ return this.end-this.start }
    
    /**
     * Shifts the absolute position a <i>delta</i> amount
     * @param delta delta moving of the absolute position
     */
    shift(delta) {
        this.start+=delta
        this.end+=delta
    }

    /**
     * Gets a JSON version of the token
     * @return JSON version  of the token
     */
    json() {
        var json = super.json()
        json.end = this.end
        json.value = this.value
        json.type = this.type
        return json
    }
    
    /**
     * Converts the token to an error version of it
     * @return Error version of the token
     */
    toError() { return new Token(this.input,this.start,this.end,this.type) }
    
    /**
     * Determines if it is an error token or not 
     * @return <i>true</i> if an error token, <i>false</i> otherwise 
     */
    isError() { return this.type==Token.ERROR }
}

/**
 * Character analyze functions
 */
Character = {
    isDigit(c){ return '0'<=c && c<='9' },
    isLowerCase(c){ return ('a'<=c && c<='z') },
    isUpperCase(c){ return ('A'<=c && c<='Z') },
    isLetter(c){ return this.isLowerCase(c) || this.isUpperCase(c) },
    isHexa(c){ return Character.isDigit(c) || ('A'<=c&&c<='F') || ('a'<=c&&c<='f') },
    isAlphabetic(c){ return Character.isDigit(c) || Character.isLetter(c) }    
}

/**
 * Read objects from an input source 
 */
class Read {
    /**
     * Reads an object from the input source (limited to the given starting and ending positions) 
     * @param input Symbol source
     * @param start Starting position for reading a token
     * @param end Ending position for reading a token (not included)
     * @return Object read from the symbol source
     */
    get(input, start, end){
        start = start || 0
        end = end || input.length
        if( typeof input === 'string' )
            input = new Source(input)
        var t = this.match(input,start,end)
        if(t.isError()) throw t.stringify()
        return t.value
    }

    /**
     * Reads a token from the input String (limited to the given starting and ending positions) 
     * @param input String source
     * @param start Starting position for reading a token
     * @param end Ending position for reading a token (not included)
     * @return Token read from the input String
     */
    match(input, start, end){}
}


//////// LEXEME ////////////
/**
 * Abstract lexeme
 */
class Lexeme extends Read{
    /**
     * Determines if the lexeme can star with the given character
     * @param c Character to analyze
     * @return <i>true</i> If the lexeme can start with the given character <i>false</i> otherwise
     */
    startsWith(c){ return false }

    /**
     * Creates an error token with the lexema type
     * @param input Input source from which the token was built
     * @param start Starting position of the token in the input source
     * @param end Ending position (not included) of the token in the input source
     * @return Error token with the lexema type
     */
    error(input, start, end) {
        return new Token(input,start,end,this.type)
    }
    
    /**
     * Creates a token with the lexema type
     * @param input Input source from which the token was built
     * @param start Starting position of the token in the input source
     * @param end Ending position (not included) of the token in the input source
     * @param value Value stored by the token
     * @return Lexema token
     */
    token(input, start, end, value) {
        return new Token(input,start,end,value,this.type)
    }
}

/**
 * Parses spaces (' ', '\n', '\r', and '\t') </p>
 */
class Space extends Lexeme{
    /**
     * Space lexema TAG
     */
    static TAG = "space"
    
    /** 
    * Default constructor
    */
    constructor(){ 
        super()
        this.type = Space.TAG 
        this.white = new RegExp(/^\s$/)
    }
    
    /**
     * Creates a token with the space type
     * @param input Input source from which the token was built
     * @param start Starting position of the token in the input source
     * @param end Ending position (not included) of the token in the input source
     * @return Integer token
     */
    match(input, start, end) {
        start = start || 0
        end = end || input.length
        if(typeof input === 'string') input = new Source(input)
        if( !this.startsWith(input.get(start)) )
            return this.error(input, start, start+1)
        var n = end
        end=start+1
        while(end<n && this.startsWith(input.get(end))) end++
        return this.token(input,start,end," ")
    }

    /**
     * Determines if the lexeme can star with the given character (' ', '\n', '\r', and '\t')
     * @param c Character to analyze
     * @return <i>true</i> If the lexeme can start with the given character <i>false</i> otherwise
     */
    startsWith(c) { return this.white.test(c) }
}

/**
 * Parses any of the characters/symbols in a symbol collection</p>
 */
class Symbol extends Lexeme{
    /**
     * General symbol lexema type TAG
     */
    static TAG = "symbol"
    
    /**
     * Creates a parser for a set of symbols
     * @param symbols Symbols that will be considered in the lexema
     * @param type Type for the symbols lexema (default "symbol")
     */
    constructor(symbols, type=Symbol.TAG){
        super()
        this.type = type
        for( var i=0; i<symbols.length; i++ )
            this[symbols.charAt(i)] = symbols.charAt(i)
    }
    
    /**
     * Creates a token with the symbol type
     * @param input Input source from which the token was built
     * @param start Starting position of the token in the input source
     * @param end Ending position (not included) of the token in the input source
     * @return Symbol token
     */
    match(input, start, end) {
        start = start || 0
        end = end || input.length
        if(typeof input === 'string') input = new Source(input)
        if(this.startsWith(input.get(start)))
            return this.token(input,start,start+1,input.get(start))
        else 
            return this.error(input,start,start+1)
    }
    
    /**
     * Determines if the symbol lexeme can star with the given character (a character in the set)
     * @param c Character to analyze
     * @return <i>true</i> If the lexeme can start with the given character <i>false</i> otherwise
     */
    startsWith(c) { return this[c] !== undefined }    
}

/**
 * Parses ids: [_a-zA-Z][_a-zA-Z0-9]*</p>
 */
class ID extends Lexeme{
    /**
     * IDs TAG
     */
    static TAG = 'ID'
    
    /**
     * Creates an ID lexeme
     * @param type Type of the ID lexema
     */
    constructor(type=ID.TAG){ 
        super()
        this.type = type
    }
    
    /**
     * Creates a token with the ID type
     * @param input Input source from which the token was built
     * @param start Starting position of the token in the input source
     * @param end Ending position (not included) of the token in the input source
     * @return ID token
     */
    match(input, start, end) {
        start = start || 0
        end = end || input.length
        if(typeof input === 'string') input = new Source(input)
        if( !this.startsWith(input.get(start)) )
            return this.error(txt, start, start+1)
        var n = end
        end = start
        while(end<n && input.get(end)=='_') end++
        if( end==n ) return this.error(input,start,end)
        if(!Character.isLetter(input.get(end)))
        return this.error(input,start,end)
        while(end<n && Character.isAlphabetic(input.get(end))) end++
        return this.token(input,start,end,input.substring(start,end))
    }

    /**
     * Determines if the lexeme can star with the given character (a letter or '_')
     * @param c Character to analyze
     * @return <i>true</i> If the lexeme can start with the given character <i>false</i> otherwise
     */
    startsWith(c){ return c=='_' || Character.isLetter(c) }
}

/**
 * Parses any of the strings/words provided in the set of words, useful for reserved words in a programming language.
 */
class Words extends Lexeme{
    /**
     * Creates a parser for the provided set of strings/words 
     * @param type Type of the set of words
     * @param word Set of words defining the lexema
     */
    constructor(type, word) {
        super()
        this.word = word
        this.type = type
    }
    
    /**
     * Creates a token with the set of words type
     * @param input Input source from which the token was built
     * @param start Starting position of the token in the input source
     * @param end Ending position (not included) of the token in the input source
     * @return Number token
     */
    match(input, start, end) {
        start = start || 0
        end = end || input.length
        if(typeof input === 'string') input = new Source(input)
        for(var i=0; i<this.word.length; i++) {
            var x = input.substring(start,Math.min(end, start+this.word[i].length))
            if(this.word[i]==x) return this.token(input,start,start+x.length,x)
        }
        return this.error(input,start,start+1)
    }

    /**
     * Determines if the set of words lexeme can star with the given character (a character in the set)
     * @param c Character to analyze
     * @return <i>true</i> If the lexeme can start with the given character <i>false</i> otherwise
     */
    startsWith(c) {
        for(var i=0; i<this.word.length; i++)
            if(this.word[i].charAt(0)==c) return true
        return false
    }
}

/**
 * Parses numbers (integer or real)
 */
class NumberParser extends Lexeme{
    /**
     * Number lexema type TAG
     */
    static TAG = "number"

    constructor(){
        super()
        this.type = NumberParser.TAG
    }
    
    /**
     * Determines if the character is a '+' or '-'
     * @param c Character to analyze 
     * @return <i>true</i> if the character is a '+' or '-', <i>false</i> otherwise.
     */
    isSign(c){ return ('-'==c || c=='+') }

    /**
     * Determines if the lexeme can star with the given character (a digit or '+', '-')
     * @param c Character to analyze
     * @return <i>true</i> If the lexeme can start with the given character <i>false</i> otherwise
     */
    startsWith(c){ return this.isSign(c) || Character.isDigit(c) }

    /**
     * Creates a token with the number type
     * @param input Input source from which the token was built
     * @param start Starting position of the token in the input source
     * @param end Ending position (not included) of the token in the input source
     * @return Number token
     */
    match(input, start, end){
        start = start || 0
        end = end || input.length
        if(typeof input === 'string') input = new Source(input)
        if(!this.startsWith(input.get(start)))
            return this.error(input, start, start)
        var n = end
        end=start+1
        while(end<n && Character.isDigit(input.get(end))) end++
        if(end==n) 
            return this.token(input, start, end, Number.parseInt(input.substring(start,end)))
        var integer = true
        if(input.get(end)=='.'){
            integer = false
            end++
            var s=end
            while(end<n && Character.isDigit(input.get(end))) end++
            if(end==n) 
                return this.token(input, start, end, Number.parseFloat(input.substring(start,end)))
            if(end==s) return this.error(input, start, end)
        }
        if(input.get(end)=='E' || input.get(end)=='e'){
            integer = false
            end++
            if(end==n) return this.error(input, start, end)
            if(this.isSign(input.get(end))) end++
            if(end==n) return this.error(input, start, end)
            var s = end
            while(end<n && Character.isDigit(input.get(end))) end++
            if(end==s) return this.error(input, start, end)
        }
        if( integer ) return this.token(input, start, end, Number.parseInt(input.substring(start,end)))
        return this.token(input, start, end, Number.parseFloat(input.substring(start,end)))
    }   
}

/**
 * Parses an String
 */
class StringParser extends Lexeme{
    /**
     * String lexema type TAG
     */
    static TAG = "string"
    
    /**
     * Creates a parsing method for strings, using the provided quotation character
     * @param quotation Quotation character (default '"')
     */
    constructor(quotation='"') {
        super()
        this.type = StringParser.TAG
        this.quotation = quotation
    }

    /**
     * Determines if the lexeme can star with the given character (quotation character)
     * @param c Character to analyze
     * @return <i>true</i> If the lexeme can start with the given character <i>false</i> otherwise
     */
    startsWith(c){ return c==this.quotation }

    /**
     * Creates a token with the String type
     * @param input Input source from which the token was built
     * @param start Starting position of the token in the input source
     * @param end Ending position (not included) of the token in the input source
     * @return Number token
     */
    match(input, start, end) {
        start = start || 0
        end = end || input.length
        if(typeof input === 'string') input = new Source(input)
        if(!this.startsWith(input.get(start))) return this.error(input, start, start)
        var n = end
        end = start+1
        if(end==n) return this.error(input, start, end)
        var str = ""
        while(end<n && input.get(end)!=this.quotation){
            if(input.get(end)=='\\'){
                end++
                if(end==n) return this.error(input, start, end)
                if(input.get(end)=='u') {
                    end++
                    var c = 0
                    while(end<n && c<4 && Character.isHexa(input.get(end))){
                        end++
                        c++
                    }
                    if(c!=4) return this.error(input, start, end)
                    str += String.fromCharCode(Number.parseInt(input.substring(end-4,end),16))    
                }else {
                    switch(input.get(end)){
                        case 'n': str += '\n'; break;
                        case 'r': str += '\r'; break;
                        case 't': str += '\t'; break;
                        case 'b': str += '\b'; break;
                        case 'f': str += '\f'; break;
                        case '\\': case '/': str += input.get(end); break;
                        default:
                            if(input.get(end)!=this.quotation)
                                return this.error(input, start, end)
                            str += this.quotation
                    }
                    end++
                }
            }else{
                str += input.get(end)
                end++
            }
        }
        if(end==n) return this.error(input, start, end)
        end++
        return this.token(input, start, end, str)
    }   
}

/**
 * Parses a Blob/Byte array using Base64
 */
class BlobParser extends Lexeme{     
    /**
     * Starter character of a blob, if required
     */
    static STARTER = '#'
    /**
     * Blob type TAG
     */
    static TAG = "byte[]"

    /**
     * Creates a Blob parser 
     * @param useStarter <i>true</i> indicates that parser must check for starter character, <i>false</i> indicates
     * that parser does no requires starter character (default <i>false</i>)
     */
    constructor(useStarter=false) {
        super()
        this.useStarter = useStarter 
        this.type = BlobParser.TAG
    }

    /**
     * Determines if a character is a valid Base64 character
     * @param c Character to analyze
     * @return <i>true</i> if a character is a valid Base64 character, <i>false</i> otherwise
     */
    valid(c) { return Character.isAlphabetic(c) || c=='+'||c=='/' }
    
    /**
     * Creates a token with the blob/bitarray type
     * @param input Input source from which the token was built
     * @param start Starting position of the token in the input source
     * @param end Ending position (not included) of the token in the input source
     * @return Blob token
     */
    match(input, start, end){
        start = start || 0
        end = end || input.length
        if(typeof input === 'string') input = new Source(input)
        if(!this.startsWith(input.get(start)))
            return this.error(input,start,start+1)
        var n=end
        end=start+1
        while(end<n && this.valid(input.get(end))) end++
        var s = (this.useStarter)?start+1:start
        var m = (end-s)%4
        if(s==end || m==1) return this.error(input,start,end)
        if(m>0) {
            while(end<n && m<4 && input.get(end)=='=') {
                end++
                m++
            }
            if(m<4) return this.error(input,start,end)
        }
        return this.token(input,start,end,Base64.decode(input.substring(s,end)))
    }

    /**
     * Determines if the lexeme can star with the given character
     * @param c Character to analyze
     * @return <i>true</i> If the lexeme can start with the given character <i>false</i> otherwise
     */
    startsWith(c) {
        return this.useStarter?(c==BlobParser.STARTER):this.valid(c)
    }
}

///////////// LEXER ////////////////
/**
 * TAG for Array of tokens
 */
const TOKEN_LIST = "Token[]"

/**
 * Language lexer
 */
class Lexer extends Read{
    /**
     * Creates a Lexer that removes (does not take into account) the given Token types
     * @param removableTokens Tokens that will be removed from analysis
     */
    constructor(removableTokens){
        super()
        this.removableTokens = removableTokens || []
        this.remove = true
        this.back = false
    }
    
    /**
     * Makes the Lexer (Tokenizer) to consider or not (put in the generated Array of tokens) the removable tokens 
     * @param remove <i>true</i>: Lexer will not consider removable tokens, <i>false</i>: Lexer will consider removable tokens. 
     */
    removeTokens(remove) { this.remove = remove }
    
    /**
     * Initialize the lexer over the input String (limited to the given starting and ending positions) 
     * @param input String source
     * @param start Starting position for reading a token
     * @param end Ending position for reading a token (not included)
     */
    init(input, start, end) {
        this.start = start || 0
        this.end = end || input.length
        if(typeof input === 'string') input = new Source(input)
        this.input = input
        this.back = false 
    }
    
    /**
     * Gets the last read/available Token 
     * @return Last read/available Token
     */
    obtain(){}
    
    /**
     * Determines if a Token is removable or not
     * @param t Token to analyze
     * @return <i>true</i> If the Token can e removed, <i>false</i> otherwise.
     */
    removable(t) {
        var i=0
        while(i<this.removableTokens.length && t.type!=this.removableTokens[i]) i++
        return(i!=this.removableTokens.length) 
    }
    
    /**
     * Gets the next available Token
     * @return Nex available Token
     */
    next() {
        if(this.back) {
            this.back = false
            return this.current
        }
        do { this.current = this.obtain() }
        while(this.current!=null && this.remove && this.removable(this.current))
        return this.current
    }
    
    /**
     * Makes the Lexer to go back one token
     */
    goback() { this.back = true; }
    
    /**
     * Reads a token from the input Source (limited to the given starting and ending positions) 
     * @param input String source
     * @param start Starting position for reading a token
     * @param end Ending position for reading a token (not included)
     * @return Token read from the input String
     */
    match(input, start, end) {
        this.init(input,start,end)
        var list = []
        var t;
        while((t=this.next())!=null && t.type!=Token.ERROR) { list.push(t) }
        if(t==null) 
            return new Token(input, start, list[list.length-1].end, list, TOKEN_LIST)
        else 
            return t
    }
    
    /**
     * Removes from the Token lists tokens with the given tag
     * @param tokens Tokens to be analyzed
     * @param toremove Tag of the tokens to be removed
     * @return The Array of tokens without the desired tokens
     */
    remove(tokens, toremove ){
        for( var i=tokens.size()-1; i>=0; i-- )
            if( this.toremove.indexOf(tokens[i].type) >= 0 ) tokens.splice(i,1)
        return tokens
    }
    
    /**
     * Removes from the Token lists space tokens
     * @param tokens Tokens to be analyzed
     * @return The Array of tokens without space tokens
     */
    remove_space(tokens ){ 
        return remove(tokens, Space.TAG)
    }   
}

/**
 * Look a Head (LL1) lexer. Checks the next character in the input to determine the Lexema to use
 */
class LookAHeadLexer extends Lexer{
    /**
     * Creates a Lexer from a set of lexema each one with an associated priority of analysis and
     * removes (does not take into account) the given Token types
     * @param lexemes Set of lexema that can recognize the Lexer
     * @param removableTokens Tokens that will be removed from analysis
     * @param priority Priority of the lexema (default null)
     */
    constructor( removableTokens, lexemes, priority=null ){
        super(removableTokens)
        this.lexeme = {}
        this.priority={}
        for( var i=0; i<lexemes.length; i++ ) {
            this.lexeme[lexemes[i].type] = lexemes[i]
            this.priority[lexemes[i].type] = priority!==null?priority[i]:1
        }
    }
    
    /**
     * Gets the last read/available Token 
     * @return Last read/available Token
     */
    obtain() {
        if(this.start>=this.end) return null
        var c = this.input.get(this.start)
        var opt = []
        var error = []
        for( var x in this.lexeme ) {
            var l = this.lexeme[x]
            if(l.startsWith(c)) {
                var t = l.match(this.input, this.start, this.end)
                if(t.isError()) error.push(t)
                else opt.push(t)
            }
        }
        if( opt.length > 0 ) {
            this.current = opt[0]
            for( var i=1; i<opt.length; i++ ) {
                var e2 = opt[i]
                if(e2.size()>this.current.size() || 
                    (e2.size()==this.current.size() && 
                    this.priority[e2.type]>this.priority[this.current.type])) 
                this.current = e2;
            }
        }else {
            if(error.length>0) {
                this.current = error[0]
                for( var i=1; i<error.length; i++ ) {
                    e2 = error[i]
                    if(e2.size()>this.current.size()) this.current = e2
                }
            }else {
                this.current = new Token(this.input, this.start, this.start+1, c)
            }
        }
        this.start = this.current.end
        return this.current
    }
}

/////////// PARSER RULE'S /////////////////////

/**
 * Parsing rule
 */
class Rule{ 
    /**
     * Creates a syntactic rule for a parser
     * @param type Type of the rule
     * @param parser Syntactic parser using the rule
     */
    constructor(type, parser) { 
        this.parser = parser
        this.type = type
    }
    
    /**
     * Determines if the rule can start with the given token
     * @param t Token to analyze
     * @return <i>true</i> If the rule can start with the given token <i>false</i> otherwise
     */
    startsWith(t){}

    /**
     * Determines if the symbol token comes from a given symbol lexema type and its value
     * is the same as the character passed as argument 
     * @param token Symbol token
     * @param c Character to analyze
     * @param TAG Type of the lexema (default Symbol Tag)
     * @return <i>true</i> if the symbol token comes from a given symbol lexema type and its value
     * is the same as the character passed as argument, <i>false</i> otherwise.
     */
    check_symbol(token, c, TAG=Symbol.TAG) {
        return token.type==TAG && token.value==c
    }
    
    /**
     * Creates a rule token using the <i>current</i> token as first token to analyze
     * @param lexer Lexer 
     * @param current Initial token (default lexer.next())
     * @return Rule token
     */
    analyze(lexer, current=lexer.next()){}
    
    /**
     * Creates a eof token 
     * @param input Input source 
     * @param end Position to be considered the end of the input source
     * @return EOF token
     */
    eof(input, end) { return new Token(input,end,end,this.type) }
        
    /**
     * Creates a token with the rule type
     * @param input Input source from which the token was built
     * @param start Starting position of the token in the input source
     * @param end Ending position (not included) of the token in the input source
     * @param value Value stored by the token
     * @return Rule token
     */
    token(input, start, end, value) {
        return new Token(input, start, end, value, this.type)
    }
}

/**
 * A Parsing rule for lists
 */
class ListRule extends Rule{
    /**
     * Creates a lists syntactic rule 
     * @param type Type of the rule
     * @param parser Syntactic parser using the rule
     * @param item_rule Rule type of the elements of the list
     * @param left Left character of the list (default '[')
     * @param right Right character of the list (default ']')
     * @param separator Elements separating character (default ',')
     */ 
    constructor(type, parser, item_rule, left='[', right=']', separator=',') { 
        super(type, parser)
        this.item_rule = item_rule
        this.LEFT = left
        this.RIGHT = right
        this.SEPARATOR = separator
    }
    
    /**
     * Determines if the rule can start with the given token (left character)
     * @param t Token to analyze
     * @return <i>true</i> If the rule can start with the given token <i>false</i> otherwise
     */
    startsWith(t) { return this.check_symbol(t, this.LEFT) }
    
    /**
     * Creates a rule token using the <i>current</i> token as first token to analyze
     * @param lexer Lexer 
     * @param current Initial token
     * @return List rule token
     */
    analyze(lexer, current=lexer.next()) {
        if(!this.startsWith(current)) return current.toError()
        var input = current.input
        var start = current.start
        var end = current.end
        var list = []
        current = lexer.next()
        while(current!=null && !this.check_symbol(current, this.RIGHT)){
            var t = this.parser.rule(this.item_rule).analyze(lexer, current)
            if(t.isError()) return t
            list.push(t)
            end = current.end
            current = lexer.next()
            if(current==null) return this.eof(input,end)
            if(this.check_symbol(current, this.SEPARATOR)) {
                end = current.end
                current = lexer.next()
                if(current==null) return this.eof(input,end)
                if(this.check_symbol(current, this.RIGHT)) return current.toError() 
            }else if(!this.check_symbol(current, this.RIGHT)) return current.toError()
        }
        if(current==null) return this.eof(input,end)
        return this.token(input,start,current.end,list)
    }
}

/**
 * A parser rule for selecting from multiple rules
 */
class Options extends Rule{
    /**
     * Creates an optional syntactic rule for a parser
     * @param type Type of the options rule
     * @param parser Syntactic parser using the rule
     * @param options Optional syntactic rules
     */
    constructor(type, parser, options) {
        super(type, parser)
        this.option = options
    }

    rule(t){
        var i=0
        while(i<this.option.length && !parser.rule(this.option[i]).startsWith(t)) i++
        return i
    }
    
    /**
     * Determines if the rule can start with the given token
     * @param t Token to analyze
     * @return <i>true</i> If the rule can start with the given token <i>false</i> otherwise
     */
    startsWith(t) { return this.rule(t)<this.option.length }

    /**
     * Creates a rule token using the <i>current</i> token as first token to analyze
     * @param lexer Lexer 
     * @param current Initial token
     * @return Rule token
     */
    analyze(lexer, current=lexer.next()){
        var r=this.rule()
        if(r==this.option.length) return current.toError()   
        return this.option[r].analyze(lexer, current)
    }    
}

/**
 * Look a Head (LL1) parser. Checks the next Token in the token list to determine the Rule to use
 */
class Parser{
    /**
     * Create a look a head syntactic parser with the given trial set of rules
     * @param rules Rules defining the syntactic parser
     * @param main Type of the main rule
     */
    constructor(rules, main) {
        this.main = main
        this.rules = {}
        for(var i=0; i<rules.length; i++) {
            this.rules[rules[i].type] = rules[i]
            rules[i].parser = this
        }      
    }

    /**
     * Sets the type of the main rule
     * @param rule Type of the main rule
     * @return Main syntactic rule 
     */
    rule(r) { return this.rules[r] }

    /**
     * Gets a syntactic token from the given lexer/tokenizer using the type of rule provided 
     * @param rule Type of the analyzing rule 
     * @param lexer Lexer to analyze
     * @return Syntactic token from the given lexer/tokenizer using the type of rule provided 
     */
    analyze(lexer, r) {
        return this.rule(r || this.main).analyze(lexer)
    }
}

/**
 * Gives meaning to parser trees/objects produced by the language parser
 */
class Meaner{
    /**
     * Creates a semantic token from a Syntactic token
     * @param g_obj Syntatic token 
     * @return Semantic token from a Syntactic token
     */
    apply(t){}
}

/////////// LANGUAGE //////////////
/**
 * Abstract definition of a language with lexer, parser and meaner
 */
class Language extends Read{
    /**
     * Creates a language with the given lexer, parser, and meaner
     * @param lexer Language lexer
     * @param parser Language syntactic parser
     * @param meaner Language semantic meaner
     */
    constructor( lexer, parser, meaner ){
        super()
        this.lexer = lexer
        this.parser = parser
        this.meaner = meaner
    }
    
    /**
     * Reads a semantic token from the input source starting at the position given up to the ending position
     * @param input Symbol source
     * @param start Starting position for reading a token
     * @param end Ending position for reading a token (not included)
     * @return Semantic token read from the source
     */
    match(input, start, end) {
        this.lexer.init(input, start, end)
        var t = this.parser.analyze(this.lexer)
        if(!t.isError()) t = this.meaner.apply(t)
        return t
    }
}