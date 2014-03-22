package repFactory;

import bufmgr.pageDsc;

public class MRU extends Policy{

	protected MRU() {
		// TODO Auto-generated constructor stub
	}
	@Override
	public boolean isEmpty() {
		for(int i = requested.size()-1 ; i>=0; i--){
			if(requested.get(i).getPin_count() == 0 )
				return false;
		}
		return true;
	}
	
	@Override
	public pageDsc getFrame() {
		for(int i = requested.size()-1 ; i>=0; i--){
			if(requested.get(i).getPin_count() == 0 )
				return requested.get(i);
		}
		return null;
	}

	@Override
	public void update(pageDsc pageDsc) {
	}

}
