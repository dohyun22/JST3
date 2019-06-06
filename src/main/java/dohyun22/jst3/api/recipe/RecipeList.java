package dohyun22.jst3.api.recipe;

import java.util.ArrayList;
import java.util.Iterator;

public class RecipeList {
	public final ArrayList<RecipeContainer> list = new ArrayList();
	public final String name;
	
	public RecipeList(String name) {
		this.name = name;
	}
	
	public boolean add(RecipeContainer c) {
		return list.add(c);
	}
	
	public Iterator getIterator() {
		return list.iterator();
	}
	
	public void remove(RecipeContainer c) {
		if (c == null) return;
	    for (Iterator<RecipeContainer> itr = list.iterator(); itr.hasNext();) {
	    	RecipeContainer r = itr.next();
	    	if (c.equals(r)) itr.remove();
	    }
	}
}
