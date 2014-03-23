package repFactory;

import java.util.LinkedList;

import bufmgr.pageDsc;

public class LRU extends Policy {

	protected LRU() {
		// TODO Auto-generated constructor stub
		countZero = new LinkedList<pageDsc>();

		requested = new LinkedList<pageDsc>();	
	}

	@Override
	public boolean isEmpty() {
		for(int i = 0; i<requested.size(); i++){
			if(requested.get(i).getPin_count() == 0 )
				return false;
		}
		return true;
	}
	
	@Override
	public pageDsc getFrame() {
		for(int i = 0; i<requested.size(); i++){
			if(requested.get(i).getPin_count() == 0 )
				return requested.get(i);
		}
		return null;
	}



}
