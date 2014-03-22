package repFactory;

import java.util.LinkedList;
import java.util.Queue;

import bufmgr.pageDsc;

public class FIFO extends Policy {

	protected FIFO() {
		requested = new LinkedList<pageDsc>();
		added = new LinkedList<pageDsc>();
	}

	@Override
	public boolean isEmpty() {
		for(int i = 0; i<added.size(); i++){
			if(added.get(i).getPin_count() == 0 )
				return false;
		}
		return true;
	}
	
	@Override
	public pageDsc getFrame() {
		for(int i = 0; i<added.size(); i++){
			if(added.get(i).getPin_count() == 0 )
				return added.get(i);
		}
		return null;
	}

	@Override
	public void update(pageDsc pageDsc) {
	}
}
