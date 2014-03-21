package bufmgr;

import global.*;

public class pageDsc {
	protected PageId pagenumber;
	protected int pin_count;
	protected boolean dirtybit;
	protected boolean lovebit;

	public pageDsc(PageId pgnum, int count, boolean d, boolean l) {
		pagenumber = pgnum;
		pin_count = count;
		dirtybit = d;
		lovebit = l;
	}

	protected void update(PageId pgnum, int count, boolean d, boolean l) {
		pagenumber = pgnum;
		pin_count = count;
		dirtybit = d;
		lovebit = l;
	}

	public void increment() {
		// TODO Auto-generated method stub
		pin_count++;
	}
	public void decrement() {
		// TODO Auto-generated method stub
		pin_count--;
	}
	
}
