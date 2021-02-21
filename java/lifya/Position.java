package lifya;

import java.util.HashMap;

import lifya.stringify.Stringifier;
import lifya.stringify.Stringifyable;

public class Position implements Stringifyable{
    public static final String INPUT = "input";
    public static final String START = "start";
    public static final String ROW = "row";
    public static final String COLUMN = "column";
    
    protected Source input;
    protected int start;
    public Position(Source input, int start){
	this.input = input;
	this.start = start;	
    }
    
    public void start(int start) { this.start = start; }
    public int start() { return start; }
    
    public void shift(int delta) {
	start+=delta;
    }

    public void input(Source input) { this.input = input; }  
    public Source input(){ return this.input; }

    protected HashMap<String, Object> map(){
	HashMap<String, Object> map = new HashMap<String, Object>();
	map.put(INPUT, input.id());
	map.put(START, start);
	int[] pos = input.pos(start);
	map.put(ROW, pos[0]);
	map.put(COLUMN, pos[1]);	
	return map;
    }
    
    @Override
    public String stringify() { return Stringifier.apply(map()); }
}