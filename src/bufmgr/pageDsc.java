package bufmgr;

import global.*;

public class pageDsc {
	protected PageId pagenumber;
	protected int pin_count;
	protected boolean dirtybit;
	protected boolean lovebit;

	public pageDsc(PageId pgnum, int count, boolean d, boolean l) {
		// TODO Auto-generated constructor stub
		pagenumber = pgnum;
		pin_count = count;
		dirtybit = d;
		lovebit = l;
	}
}
