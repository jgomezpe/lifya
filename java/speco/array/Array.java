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
package speco.array;

import java.util.Iterator;

import utila.Fibonacci;

/**
 * <p>Title: Array</p>
 *
 * <p>Description: An array of objects (parameterized).</p>
 *
 */
public class Array<T> implements Iterable<T>{
    /**
     * Elements of the array
     */
    protected Object buffer;
    
    /**
     * Size of the array
     */
    protected int size;

    /**
     * Growing/Shrinking strategy
     */
    Fibonacci sizeManager;

    /**
     * Creates an array having an initial buffer with length 100
     */
    public Array(){ this(100); }
	
    /**
     * Creates an array having an initial buffer with the given length
     * @param n Initial buffer's length
     */
    public Array(int n){
	this(java.lang.reflect.Array.newInstance(Object.class,n),0);
    }

    /**
     * Creates an array using the given buffer of elements
     * @param buffer Initial elements of the array. Size is set to buffers length
     */
    public Array(Object buffer){ this(buffer, java.lang.reflect.Array.getLength(buffer)); }

    /**
     * Creates an array using the given buffer of elements and the given initial size
     * @param buffer Initial elements of the array. 
     * @param size Initial size of the array. 
     */    
    public Array(Object buffer, int size) {
	this.buffer = buffer;
	sizeManager = new Fibonacci(memsize());
	sizeManager.prev();
	this.size = size; 
	resize();
    }

    /**
     * Creates an iterator for the Array, starting at the given index.
     * @param start Initial position for the iterator
     * @return An iterator for the Array starting at the given position
     */
   public Iterator<T> iterator( int start ) {
	return new Iterator<T>() {
	    protected int pos=start;
	    @Override
	    public boolean hasNext(){ return pos<size(); }
	    @Override
	    public T next() { return get(pos++); }
	};
    }
 
    @Override
    public Iterator<T> iterator() { return iterator(0); }

    /**
     * Size of the buffer
     * @return Size of the buffer
     */
    protected int memsize() { return java.lang.reflect.Array.getLength(buffer); }

    /**
     * Creates a buffer of <i>n</i> objects  
     * @param n Length of the buffer to be created
     * @return A buffer of elements
     */
    protected Object alloc(int n) {
	return java.lang.reflect.Array.newInstance(Object.class,n);
    }

    /**
     * Creates a shallow copy of the buffer 
     * @param n Size of the buffer
     * @return A buffer of the given size with a shallow copy of the elements in the array's buffer
     */
    protected Object copy(int n) {
	Object nbuffer = alloc(n);
	System.arraycopy(buffer, 0, nbuffer, 0, Math.min(n, memsize()));
	return nbuffer;
    }

    /**
     * Gets the element that is located at the given position.
     * @param index Position of the element to obtain
     * @return The element that is located at the given position
     * @throws NoSuchElementException If the index is a non valid position
     */
    @SuppressWarnings("unchecked")
    public T get(int index) { return (T)java.lang.reflect.Array.get(buffer, index); };

    /**
     * Sets the element at the given position.
     * @param index Position of the element to set
     * @param data Element to set at the given position
     * @return <i>true</> if the element could be set, <i>false</i> otherwise.
     */
    public boolean set(int index, T data) {
	java.lang.reflect.Array.set(buffer, index, data);
	return index<size();
    }

   /**
    * Determines the number of objects stored by the array
    * @return Number of objects stored by the array.
    */
    public int size() { return size; }
    
    public Array<T> instance(int n){
	if( buffer != null ) return new Array<T>(java.lang.reflect.Array.newInstance(buffer.getClass().getComponentType(),n));
	return new Array<T>(n);
    }
		
    /**	
     * Reset the array to initial values (including the buffer size)
     */
    public void clear(){
	sizeManager.clear();
	resize();
	size = 0;
    }

    /**
     * Removes the element at the given position
     * @param index The position of the object to be deleted
     * @return <i>true</i> if the element could be removed, <i>false</i> otherwise
     */
    public boolean remove( int index ){
	if(0<=index && index<size() && ready4Remove()){
	    leftShift( index );
	    return true;
	}	
	return false;
    }	
	
    /**
     * Adds a data element at the end of the array
     * @param data Data element to be inserted
     * @return <i>true</i> if the element could be added, <i>false</i> otherwise
     */
    public boolean add( T data ){
	if( ready4Add() ) {
	    java.lang.reflect.Array.set(buffer, size, data);
	    size++;
	    return true;
	}else return false;
    }

    /**
     * Adds an element at the given position. Elements at positions <i>index+1...size()-1</i> 
     * are moved one position ahead. 
     * @param index Position to be occupied by the new element
     * @param data Element that will be added into the Vector
     * @return <i>true</i> If the element could be added at the given position, <i>false</i> otherwise
     */
    public boolean add( int index, T data ){
	if( index < 0 || index > size() || !ready4Add() ) return false;
	rightShift(index);
	set( index, data );
	return true;
    }
			
    protected void leftShift( int index ) throws IndexOutOfBoundsException{
	size--;
	System.arraycopy(buffer, index+1, buffer, index, size-index);
    }
	
    protected void rightShift( int index ) throws IndexOutOfBoundsException{
	System.arraycopy(buffer, index, buffer, index+1, size-index);
	size++;
    }	

    
    /**
     * Sets the size of the array
     * @param n The new size of the array
     */
    protected void resize( int n ){
	int x = sizeManager.n();
	if( x!=sizeManager.find_fib(n) ) resize();
	size = n; 
    }
	
    /**
     * Resizes the inner buffer according to the associated Fibonacci numbers (new buffer size must be <i>c</i>)
     */
    protected void resize() { buffer = copy(sizeManager.n_2()); }

    /**
     * Determines if the dynamic array is ready for adding a new element. Basically,
     * it increases the size of the buffer according to the associated Fibonacci numbers
     * (new buffer size will be <i>b+c</i>)
     * @return <i>true</i> if the array is ready for adding a new element, <i>false</i> otherwise
     */
     public boolean ready4Add() {
	if(memsize()==0) buffer = alloc(sizeManager.n_2());
	if( size()==sizeManager.n_2() ){
	    sizeManager.next();
	    resize();        
	}
	return true;
    }
	
    /**
     * Determines if the dynamic array is ready for removing an element. Basically,
     * it decreases the size of the buffer according to the associated Fibonacci numbers 
     * (new buffer size will be <i>b</i>)
     * @return <i>true</i> if the dynamic array is ready for removing an element, <i>false</i> otherwise
     */
    public boolean ready4Remove() {
	if(  size() < sizeManager.n() && sizeManager.n()!=sizeManager.prev() ) resize();
	return true;
    }    
}