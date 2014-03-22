package bufmgr;

import global.*;

public class pageDsc {
	protected PageId pageID;
	private int pin_count;
	protected boolean dirtybit;
	protected boolean lovebit;

	public pageDsc(PageId pgnum, int count, boolean d, boolean l) {
		pageID = pgnum;
		setPin_count(count);
		dirtybit = d;
		lovebit = l;
	}

	public pageDsc() {
		pageID = null;
		setPin_count(0);
		dirtybit = false;
		lovebit = false;
		// TODO Auto-generated constructor stub
	}

	protected void update(PageId pgnum, int count, boolean d, boolean l) {
		pageID = pgnum;
		setPin_count(count);
		dirtybit = d;
		lovebit = l;
	}

	public void increment() {
		// TODO Auto-generated method stub
		setPin_count(getPin_count() + 1);
	}
	public void decrement() {
		// TODO Auto-generated method stub
		setPin_count(getPin_count() - 1);
	}

	public int getPin_count() {
		return pin_count;
	}

	public void setPin_count(int pin_count) {
		this.pin_count = pin_count;
	}
	
}
