package repFactory;

import java.util.LinkedList;

import bufmgr.pageDsc;

public class LOVE extends Policy {

	protected LOVE() {	
	// TODO Auto-generated constructor stub
	countZero = new LinkedList<pageDsc>();

	requested = new LinkedList<pageDsc>();
	}

	@Override
	public boolean isEmpty() {
		
		// LRU Hated
		for(int i = 0; i<requested.size(); i++){
			if(requested.get(i).getPin_count() == 0 && !requested.get(i).isLovebit())
				return false;
		}
		// MRU Loved
		for(int i = (requested.size()-1) ; i>=0; i--){
			if(requested.get(i).getPin_count() == 0 && requested.get(i).isLovebit())
				return false;
		}
		return true;
	}
	
	@Override
	public pageDsc getFrame() {
		// LRU Hated
		for(int i = 0; i<requested.size() ; i++ ){
			if(requested.get(i).getPin_count() == 0  && !requested.get(i).isLovebit())
				return requested.get(i);
		}
		
		//MRU loved
		for (int i = 0; i < requested.size(); i++) {
			if (requested.get(i).getPin_count() == 0  && requested.get(i).getPageID().pid == -1 && requested.get(i).isLovebit())
				return requested.get(i);
		}
		pageDsc a = null;
		for (int i = 0; i < requested.size(); i++) {
			if (requested.get(i).getPin_count() == 0 && requested.get(i).isLovebit())
				a = requested.get(i);
		}
		// System.out.println(a.getPageID().pid);
		return a;
	}

}
