package repFactory;

import java.util.LinkedList;
import java.util.Queue;

import bufmgr.pageDsc;

public class FIFO extends Policy {

	protected FIFO() {
		super();

}

	@Override
	public boolean isEmpty() {
		if (countZero.size() == 0)
			return true;
		return false;
	}

	@Override
	public pageDsc getFrame() {
		if (countZero.size() != 0) {
			return countZero.getFirst();
		}
		return null;
	}

}
