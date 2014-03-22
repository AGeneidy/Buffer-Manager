package repFactory;

import java.util.LinkedList;
import java.util.Queue;

import bufmgr.pageDsc;

public class LRU extends Policy {
	static Queue<pageDsc> LRU;

	protected LRU() {
		// TODO Auto-generated constructor stub
		LRU = new LinkedList<pageDsc>();
	}

	@Override
	public boolean isEmpty() {
		return LRU.isEmpty();
	}
	
	@Override
	public pageDsc poll() {
		return LRU.poll();
	}
	
	@Override
	public void check(pageDsc pageDsc) {
		if(LRU.contains(pageDsc)){ //pin
			if(pageDsc.getPin_count() > 0)
				LRU.remove(pageDsc);
		}else{ //unpin
			if(pageDsc.getPin_count() == 0)
				LRU.add(pageDsc);
		}
	}


}
