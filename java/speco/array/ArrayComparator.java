package speco.array;

import kompari.Comparator;

public class ArrayComparator implements Comparator{
    protected Comparator inner;
    
    public ArrayComparator(Comparator inner) { this.inner = inner; }
    
    public boolean eq(Object one, int n, Object two, int m) {
	if( n!=m ) return false;
	boolean flag = true;
	for( int i=0; flag && i<n; i++ ) 
	    flag = inner.eq(java.lang.reflect.Array.get(one, i),
		    java.lang.reflect.Array.get(two, i));
	return flag; 	
    }

    public boolean eq(Object one, Object two) {
	return eq(one, java.lang.reflect.Array.getLength(one), 
		two, java.lang.reflect.Array.getLength(two)); 
    }
    
    public boolean eq(Array<?> one, Array<?> two) {
	return eq(one.buffer, one.size(), two.buffer, two.size());
    }
}
