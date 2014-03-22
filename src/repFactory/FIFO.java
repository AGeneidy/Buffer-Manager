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

	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return FIFO.isEmpty();
	}

	public pageDsc poll() {
		// TODO Auto-generated method stub
		return FIFO.poll();
	}

	public boolean contains(pageDsc pageDsc) {
		// TODO Auto-generated method stub
		return FIFO.contains(pageDsc);
	}

	public void remove(pageDsc pageDsc) {
		// TODO Auto-generated method stub
		FIFO.remove(pageDsc);

	}

}
