/**
 * <p>Copyright: Copyright (c) 2019</p>
 *
 * <h3>License</h3>
 *
 * Copyright (c) 2019 by Jonatan Gomez-Perdomo. <br>
 * All rights reserved. <br>
 *
 * <p>Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * <ul>
 * <li> Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * <li> Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * <li> Neither the name of the copyright owners, their employers, nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * </ul>
 * <p>THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 *
 *
 * @author <A HREF="http://disi.unal.edu.co/profesores/jgomezpe"> Jonatan Gomez-Perdomo </A>
 * (E-mail: <A HREF="mailto:jgomezpe@unal.edu.co">jgomezpe@unal.edu.co</A> )
 * @version 1.0
 */
package utila;

/**
 * <p>Title: Fibonacci</p>
 *
 * <p>Description: Keeps tracks of a Fibonacci number</p>
 *
 */
public class Fibonacci {

	protected int a, b, c;

	protected int DEFAULT_A = 0;
	protected int DEFAULT_B = 1;
	protected int DEFAULT_C = 1;

	/**
	 * Default Fibonacci number <i>1</i>
	 */
	public Fibonacci() { this(0); }
	
	/**
	 * Determines the Fibonacci numbers associated with the given number <i>b &lt; n &lt;= n()</i>
	 * @param n Number to be analyzed 
	 */
	public Fibonacci( int n ) {
		find_fib(n);
		DEFAULT_A = a;
		DEFAULT_B = b;
		DEFAULT_C = c;
	}
	
	/**
	 * Determines the Fibonacci numbers associated with the given number <i>b &lt; n &lt;= n()</i>
	 * @param n Number to be analyzed 
	 * @return Fibonacci number associated to the given number, i.e., <i>c</i> such that <i>n_1() &lt; n &lt;= n()</i>  
	 */
	public final int find_fib( int n ){
		a = DEFAULT_A;
		b = DEFAULT_B;
		c = DEFAULT_C;
		while(n>b){
			a=b;
			b=c;
			c=a+b;
		}
		return c;
	}
	
	/**
	 * Resets the Fibonacci to the first one <i>n()=1</i>
	 */
	public void clear(){
		a = DEFAULT_A;
		b = DEFAULT_B;
		c = DEFAULT_C;
	}

	/**
	 * Moves to the next Fibonacci number (<i>n()+n_1()</i>)
	 * @return The next Fibonacci number (<i>n()+n_1()</i>)
	 */
	public int next(){
		// It requires than a > buffer.length/2
		a = b;
		b = c;
		c = a+b;
		return a;
	}

	/**
	 * Moves to the prev Fibonacci number (<i>n_1()</i>) if possible, the first Fibonacci number will be <i>DEFAULT_A</i> 
	 * @return The prev Fibonacci number (<i>n_1()</i>)
	 */
	public int prev(){
		// It maintains a > buffer.length/2
		if( c > DEFAULT_C ){
			c = b;
			b = a;
			a = c-b;
		}else {
			a = DEFAULT_A;
			b = DEFAULT_B;
			c = DEFAULT_C;
		}
		return a;
	}
	
	/**
	 * Gets the current Fibonacci number 
	 * @return Fibonacci number 
	 */
	public int n() { return a; }

	/**
	 * Gets the previous  Fibonacci number 
	 * @return Previous Fibonacci number 
	 */
	public int n_1() { return b; }

	/**
	 * Gets the previous to the previous Fibonacci number 
	 * @return Previous to the previous Fibonacci number 
	 */
	public int n_2() { return c; }		
}