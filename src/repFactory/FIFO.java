package repFactory;

import java.util.LinkedList;
import java.util.Queue;

import bufmgr.pageDsc;

public class FIFO extends Policy {
	static Queue<pageDsc> FIFO;

	protected FIFO() {
		// TODO Auto-generated constructor stub
		FIFO = new LinkedList<pageDsc>();
	}

	@Override
	public boolean isEmpty() {
		return FIFO.isEmpty();
	}

	@Override
	public pageDsc poll() {
		return FIFO.poll();
	}

	@Override
	public void check(pageDsc pageDsc) {
	}

	@Override
	public void add(pageDsc pageDsc) {
		FIFO.add(pageDsc);
	}
}
