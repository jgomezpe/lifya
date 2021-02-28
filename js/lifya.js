/**
*
* lifya.js
* <P>Java Script for language processing.</P>
* <P> Requires base64.js and kompari.js. </P>
*
* Copyright (c) 2021 by Jonatan Gomez-Perdomo. <br>
* All rights reserved. See <A HREF="https://github.com/jgomezpe/lifya">License</A>. <br>
*
* @author <A HREF="https://disi.unal.edu.co/~jgomezpe/"> Professor Jonatan Gomez-Perdomo </A>
* (E-mail: <A HREF="mailto:jgomezpe@unal.edu.co">jgomezpe@unal.edu.co</A> )
* @version 1.0
*/

/////// Lyfia.js ////////////
class Source{   
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
   
    pos(index) {
        var idx = this.search.findLeft(index)
        if(idx+1<this.rows.length && this.rows[idx+1]==index)
            return [idx+1,0]
        return [idx, index-this.rows[idx]]
    }
    
    get(index) { return this.input.charAt(index) }
    
    substring(start,end) { return this.input.substring(start,end) }
}

class Position{
    static INPUT = "input"
    static START = "start"
    static ROW = "row"
    static COLUMN = "column"
    constructor(input, start){
        this.input = input
        this.start = start 
    }
    
    shift(delta) { start+=delta }

    config(json) {
        this.input = json.input
        this.start = json.start
    }

    json() {
        var pos = this.input.pos(this.start)
    return {"input":this.input.id, "start":this.start,
        "row":pos[0], "column":pos[1]}
    }
}

class Token extends Position{    
    static ERROR = 'error'
        
    constructor(input, start, end, value, type){
        super(input, start)
        this.end = end
        this.type = type || Token.ERROR
        this.value = value
    }

    size(){ return this.end-this.start }
    
    shift(delta) {
        this.start+=delta
        this.end+=delta
    }

    json() {
        var json = super.json()
        json.end = this.end
        json.value = this.value
        json.type = this.type
        return json
    }
    
    toError() { return new Token(this.input,this.start,this.end,this.type) }
    
    isError() { return this.type==Token.ERROR }
}

Character = {
    isDigit(c){ return '0'<=c && c<='9' },
    isLowerCase(c){ return ('a'<=c && c<='z') },
    isUpperCase(c){ return ('A'<=c && c<='Z') },
    isLetter(c){ return this.isLowerCase(c) || this.isUpperCase(c) },
    isHexa(c){ return Character.isDigit(c) || ('A'<=c&&c<='F') || ('a'<=c&&c<='f') },
    isAlphabetic(c){ return Character.isDigit(c) || Character.isLetter(c) }
    
}

class Read {
    get(input, start, end){
        start = start || 0
        end = end || input.length
        if( typeof input === 'string' )
            input = new Source(input)
        var t = this.match(input,start,end)
        if(t.isError()) throw JSON.stringify(t.json())
        return t.value
    }

    match(input, start, end){}
}


//////// LEXEME ////////////

class Lexeme extends Read{
    /**
     * Determines if the lexeme can star with the given character
     * @param c Character to analize
     * @return <i>true</i> If the lexeme can start with the given character <i>false</i> otherwise
     */
    startsWith(c){ return false }

    error(input, start, end) {
        return new Token(input,start,end,this.type)
    }
    
    token(input, start, end, value) {
        return new Token(input,start,end,value,this.type)
    }
}

class Space extends Lexeme{
    static TAG = "space"
    
    constructor(){ 
        super()
        this.type = Space.TAG 
        this.white = new RegExp(/^\s$/)
    }
    
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

    startsWith(c) { return this.white.test(c) }
}

class Symbol extends Lexeme{
    static TAG = "symbol"
    
    constructor(symbols, type=Symbol.TAG){
        super()
        this.type = type
        for( var i=0; i<symbols.length; i++ )
            this[symbols.charAt(i)] = symbols.charAt(i)
    }
    
    match(input, start, end) {
        start = start || 0
        end = end || input.length
        if(typeof input === 'string') input = new Source(input)
        if(this.startsWith(input.get(start)))
            return this.token(input,start,start+1,input.get(start))
        else 
            return this.error(input,start,start+1)
    }
    
    startsWith(c) { return this[c] !== undefined }    
}

class ID extends Lexeme{
    static TAG = 'ID'
    constructor(type=ID.TAG){ 
        super()
        this.type = type
    }
    
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

    startsWith(c){ return c=='_' || Character.isLetter(c) }
}


class Words extends Lexeme{
    constructor(type, word) {
        super()
        this.word = word
        this.type = type
    }
    
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

    startsWith(c) {
        for(var i=0; i<this.word.length; i++)
            if(this.word[i].charAt(0)==c) return true
        return false
    }
}

class NumberParser extends Lexeme{
    static TAG = "number"

    constructor(){
        super()
        this.type = NumberParser.TAG
    }
    
    isSign(c){ return ('-'==c || c=='+') }

    startsWith(c){ return this.isSign(c) || Character.isDigit(c) }

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

class StringParser extends Lexeme{
    static TAG = "string"
    
    constructor(quotation) {
        super()
        this.type = StringParser.TAG
        this.quotation = quotation || '"'
    }

    startsWith(c){ return c==this.quotation }

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



class BlobParser extends Lexeme{     
    static STARTER = '#'
    static TAG = "byte[]"

    constructor(useStarter=false) {
        super()
        this.useStarter = useStarter 
        this.type = BlobParser.TAG
    }

    valid(c) { return Character.isAlphabetic(c) || c=='+'||c=='/' }
    
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

    startsWith(c) {
        return this.useStarter?(c==BlobParser.STARTER):this.valid(c)
    }
}

///////////// LEXER ////////////////
const TOKEN_LIST = "Token[]"

class Lexer extends Read{
    constructor(removableTokens){
        super()
        this.removableTokens = removableTokens || []
        this.remove = true
        this.back = false
    }
    
    removeTokens(remove) { this.remove = remove }
    
    init(input, start, end) {
        this.start = start || 0
        this.end = end || input.length
        if(typeof input === 'string') input = new Source(input)
        this.input = input
        this.back = false 
    }
    
    obtain(){}
    
    removable(t) {
        var i=0
        while(i<this.removableTokens.length && t.type!=this.removableTokens[i]) i++
        return(i!=this.removableTokens.length) 
    }
    
    next() {
        if(this.back) {
            this.back = false
            return this.current
        }
        do { this.current = this.obtain() }
        while(this.current!=null && this.remove && this.removable(this.current))
        return this.current
    }
    
    goback() { this.back = true; }
    
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
    
    remove(tokens, toremove ){
        for( var i=tokens.size()-1; i>=0; i-- )
            if( this.toremove.indexOf(tokens[i].type) >= 0 ) tokens.splice(i,1)
        return tokens
    }
    
    remove_space(tokens ){ 
        return remove(tokens, Space.TAG)
    }   
}

class LookAHeadLexer extends Lexer{
    constructor( removableTokens, lexemes, priority=null ){
        super(removableTokens)
        this.lexeme = {}
        this.priority={}
        for( var i=0; i<lexemes.length; i++ ) {
            this.lexeme[lexemes[i].type] = lexemes[i]
            this.priority[lexemes[i].type] = priority!==null?priority[i]:1
        }
    }
    
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

class Rule{ 
    constructor(type, parser) { 
        this.parser = parser
        this.type = type
    }
    
    startsWith(t){}

    check_symbol(token, c, TAG=Symbol.TAG) {
        return token.type==TAG && token.value==c
    }
    
    analize(lexer, current=lexer.next()){}
    
    eof(input, end) { return new Token(input,end,end,this.type) }
        
    token(input, start, end, value) {
        return new Token(input, start, end, value, this.type)
    }
}

class ListRule extends Rule{

    constructor(type, parser, item_rule, left='[', right=']', separator=',') { 
        super(type, parser)
        this.item_rule = item_rule
        this.LEFT = left
        this.RIGHT = right
        this.SEPARATOR = separator
    }
    
    startsWith(t) { return this.check_symbol(t, this.LEFT) }
    
    analize(lexer, current=lexer.next()) {
        if(!this.startsWith(current)) return current.toError()
        var input = current.input
        var start = current.start
        var end = current.end
        var list = []
        current = lexer.next()
        while(current!=null && !this.check_symbol(current, this.RIGHT)){
            var t = this.parser.rule(this.item_rule).analize(lexer, current)
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

class Options extends Rule{
    constructor(type, parser, options) {
        super(type, parser)
        this.option = options
    }

    rule(t){
        var i=0
        while(i<this.option.length && !parser.rule(this.option[i]).startsWith(t)) i++
        return i
    }
    
    startsWith(t) { return this.rule(t)<this.option.length }

    analize(lexer, current=lexer.next()){
        var r=this.rule()
        if(r==this.option.length) return current.toError()   
        return this.option[r].analize(lexer, current)
    }    
}

class Parser{
    constructor(rules, main) {
        this.main = main
        this.rules = {}
        for(var i=0; i<rules.length; i++) {
            this.rules[rules[i].type] = rules[i]
            rules[i].parser = this
        }      
    }

    rule(r) { return this.rules[r] }

    analize(lexer, r) {
        return this.rule(r || this.main).analize(lexer)
    }
}

class Meaner{
    apply(t){}
}

/////////// LANGUAGE //////////////

class Language extends Read{
    constructor( lexer, parser, meaner ){
        super()
        this.lexer = lexer
        this.parser = parser
        this.meaner = meaner
    }
    
    match(input, start, end) {
        this.lexer.init(input, start, end)
        var t = this.parser.analize(this.lexer)
        if(!t.isError()) t = this.meaner.apply(t)
        return t
    }
}