package bufmgr;

import chainexception.ChainException;

public class PageUnpinnedExcpetion extends ChainException {

	public PageUnpinnedExcpetion(Exception ex , String name){
		
		super(ex, name);
	}

}
