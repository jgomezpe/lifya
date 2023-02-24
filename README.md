# lifya
Language processing for the numtseng infrastructure. 

&nbsp;Source code: <A HREF="https://github.com/jgomezpe/lifya/">https://github.com/jgomezpe/lifya/</A>

<h3>Version 1.0</h3>
<h3>Copyright (c)</h3>
&nbsp;Author: <A HREF="https://disi.unal.edu.co/~jgomezpe/"> Jonatan Gomez-Perdomo </A>

&nbsp;E-mail: <A HREF="mailto:jgomezpe@unal.edu.co">jgomezpe@unal.edu.co</A>

&nbsp;All rights reserved.

<h3>Java</h3>

&nbsp;Ready to use: <A HREF="https://github.com/jgomezpe/libs/lifya.jar">https://github.com/jgomezpe/libs/lifya.jar</A>

&nbsp;API documentation: <A HREF="https://numtseng.com/api/lifya/java/">https://numtseng.com/api/lifya/java/</A>

&nbsp;Compilation requires kompari.jar available at <A HREF="https://github.com/jgomezpe/libs/">https://github.com/jgomezpe/libs/</A>

<h3>JavaScript</h3>

&nbsp;Ready to use: <A HREF="https://numtseng.com/modules/lifya.js">https://numtseng.com/modules/lifya.js</A>

&nbsp;Requires kompari.js available at <A HREF="https://numtseng.com/modules/">https://numtseng.com/modules/</A>

&nbsp;API documentation: <A HREF="https://numtseng.com/api/lifya/js/">https://numtseng.com/api/lifya/js/</A>

<h3>Grammar</h3>
     <p>Creates a parser from an string with a lifya language specification. Lifya language specification
     is a specification mechanism defined as follows:</p>
     <h4>Rule and lexemes ids</43>
     Rules and token recognizers ids have the following form: <i>&lt;%?\w+&gt;</i>, i.e. a sequence of
     alphabetic characters quoted by &lt; and &gt; characters. The initial character <i>%</i> indicates to the 
     tokenizer that such token recognizer is removable.
     <h4>Token recognizers</h4>
     <p>Category of characters</p>
     <ul>
     <li> <i>.</i> : Any character </li>
     <li> <i>\d</i> : Digit character </li>
     <li> <i>\w</i> : Alphabetic character </li>
     <li> <i>\l</i> : Letter character </li>
     <li> <i>\D</i> : Non digit character </li>
     <li> <i>\W</i> : Non alphabetic character </li>
     <li> <i>\L</i> : Non letter character </li>
     <li> <i>\S</i> : Non space character (not \s) </li>
     </ul>
     <p>Escaped characters</p>
     <ul>
     <li> <i>\s</i> : White space character </li>
     <li> <i>\n</i> : new line character </li>
     <li> <i>\r</i> : carriage return character </li>
     <li> <i>\t</i> : tabulation character </li>
     <li> <i>\\uWXYZ</i> : Unicode character. W, X, Y, and Z must be hexadecimal characters.</li>
     <li> <i>\.</i> : . character </li>
     <li> <i>\+</i> : + character </li>
     <li> <i>\*</i> : * character </li>
     <li> <i>\?</i> : ? character </li>
     <li> <i>\-</i> : - character </li>
     <li> <i>\(</i> : ( character </li>
     <li> <i>\)</i> : ) character </li>
     <li> <i>\[</i> : [ character </li>
     <li> <i>\]</i> : ] character </li>
     <li> <i>\{</i> : { character </li>
     <li> <i>\}</i> : } character </li>
     <li> <i>\[</i> : [ character </li>
     <li> <i>\]</i> : ] character </li>
     <li> <i>\|</i> : | character </li>
     <li> <i>\:</i> : : character </li>
     <li> <i>\&lt;</i> : &lt; character </li>
     <li> <i>\&gt;</i> : &gt; character </li>
     <li> <i>\=</i> : = character </li>
     <li> <i>\%</i> : % character </li>
     </ul>
     <p> Characters <i>= : &lt; &gt; %</i> are not escaped when just generating a token recognizer (function lexeme). Those must be escaped when generating a full language parser (parser function), since these have special meaning. </p>
     <p>Operations</p>
     <ul>
     <li> <i>%</i> : A comment line</li>
     <li> <i>$</i> : Characters up to the end of the line</li>
     <li> <i>*</i> : zero or more times the previous set of characters. For example, <i>doom*</i> indicates 
     zero or more times the word <i>doom</i> not just the character <i>m</i> </li>
     <li> <i>+</i> : one or more times the previous set of characters. For example, <i>doom+</i> indicates 
     one or more times the word <i>doom</i> not just the character <i>m</i> </li>
     <li> <i>?</i> : zero or one times the previous set of characters. For example, <i>doom?</i> indicates 
     zero or one times the word <i>doom</i> not just the character <i>m</i> </li>
     <li> <i>Range</i>: Produces the set of characters between two characters (both limits included). For example,
     <i>A-F</i> Produces the set of characters <i>A,B,C,D,E,F</i>
     <li> <i>set</i>: Produces the set of character defined by the considered elements. For example, <i>[\d|A-F|a-f]</i> indicates a character that is a hexadecimal character. Elements in the set are separated by pipe characters (|) and each one may be a single character, escaped character, or a category character.</li>
     <li> <i>-</i> : Produces the complement of the associated set. For example <i>-[\d|A-F|a-f]</i> indicates
     a character that is not a hexadecimal character</li>
     <li> <i>words</i> : Produces a collection of optional word sequences. For example, <i>{false|true|null}</i> produces an optional rule defined by words <i>false</i>, <i>true</i>, and <i>null</i></li>
     <li> <i>|</i>: Produces a collection of optional expressions. For example, <i>\d+ | [A-F]+</i> produces an optional matching of sequences of digits (<i>\d+</i>) or sequences of letters <i>A,B,C,D,E,F</i>    
     <li> <i>()</i>: Are used for grouping expressions</li>    
     </ul>
     <p> A token recognizer is defined as <i>&lt;id&gt; = expression</i>. For example, 
     a lifya expression for real numbers can be defined as follows: </p>
     <p> <i>&lt;number&gt; = [\+|\-]?\d+(\.\d+)?([e|E][\+|\-]?\d+)?</i> </p>
     <p>If a token recognizer is removable, its name must initiates with symbol <i>%</i>. For example, the following lifya expression indicates that spaces are removable tokens:</p>
     <p> <i>&lt;%space&gt; = [\n|\r|\t|\s]+</i> </p>
     <p>The parser may define as many lexemes (token recognizers) as wanted. Order of definition is important
     for ambiguity resolution (first defined, first applied).</p>
     <h4>Rules</h4>
     <p>Parsing rules can be conventional rules or expression rules.</p>
     <h5>Conventional Rules</h5>
     <p>Defined in a similar fashion to the token recognizers, but including rule and token recognizer ids.
     <ul>
     <li> <i>*</i> : zero or more times the component. For example, <i>doom*</i> and <i>&lt;number&gt;*</i>indicates zero or more times the word <i>doom</i> and zero or more times the component (rule or token recognizer) <i>&lt;number&gt;</i>, respectively</li>
     <li> <i>+</i> : one or more times the component. For example, <i>doom+</i> and <i>&lt;number&gt;+</i>indicates one or more times the word <i>doom</i> and one or more times the component (rule or token recognizer) <i>&lt;number&gt;</i>, respectively</li>
     <li> <i>?</i> : zero or one times the component. For example, <i>doom?</i> and <i>&lt;number&gt;?</i>indicates zero or one times the word <i>doom</i> and zero or one times the component (rule or token recognizer) <i>&lt;number&gt;</i>, respectively</li>
     <li> <i>|</i>: Produces a collection of optional expressions. For example, <i>doom+ | &lt;number&gt;+</i> produces an optional matching of sequences of the word (<i>\doom</i>) or sequences of <i>&lt;number&gt;</i>
     <li> <i>()</i>: Are used for grouping expressions</li>   
     </ul> 
     <p>Conventional rules are defined as <i>&lt;id&gt; = regular_body.</i>. For example, a rule for list of
     numbers separated by commas may be defined as follows:</p>
     <p> <i>&lt;list&gt; :- &lt;list&gt; (, &lt;list&gt;)*</i> </p>
     <h5>Expression Rules</h5>
     <p>Rules for ambiguous expressions with operators precedence. For example, an expression for unsigned numbers
     may be defined as follows:</p>
     <p> <i>&lt;exp&gt; :- {&#94;} {\*,/} {\+,\-} &lt;number&gt; &lt;unsignnumber&gt; </i> </p>
     <p> Order of definition of operators sets defines the operators priority. In this example, 
     operator <i>&#94;</i> has a higher than <i>*</i> and <i>/</i>. Operator in the same set have the same priority. Also, the first component , in this case <i>&lt;number&gt;</i>, indicates the component for the first element in the expression (for example if it can have associated a minus character), while the second component, in this case <i>&lt;unsignnumber&gt;</i>, indicates the component for the rest of the expression.

<h3>License</h3>
Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:

<ul>
    <li> Redistributions of source code must retain the above copyright notice,
            this list of conditions and the following disclaimer.</li>
    <li> Redistributions in binary form must reproduce the above copyright notice,
            this list of conditions and the following disclaimer in the documentation
            and/or other materials provided with the distribution.</li>
    <li> Neither the name of the copyright owners, their employers, nor the
            names of its contributors may be used to endorse or promote products
            derived from this software without specific prior written permission.</li>
</ul>

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
        AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
        IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
        DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNERS OR CONTRIBUTORS BE
        LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
        CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
        SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
        HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
        OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF 
        THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
