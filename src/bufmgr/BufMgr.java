package bufmgr;

import java.io.*;
import java.security.AllPermission;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.Queue;

import repFactory.Policy;
import diskmgr.*;
import repFactory.*;
import global.*;

public class BufMgr {
	private pageDsc[] bufDescr;// numbufs // page size
	private byte[][] bufPool;
	private Page[] pages;
	private Hashtable<Integer, Integer> google;
	static int MAX;
	static Policy rep;

	/**
	 * Create the BufMgr object Allocate pages (frames) for the buffer pool in
	 * main memory and make the buffer manager aware that the replacement policy
	 * is specified by replaceArg (i.e. FIFO, LRU, MRU, love/hate)
	 * 
	 * @param numbufs
	 *            number of buffers in the buffer pool
	 * @param replaceArg
	 *            name of the buffer replacement policy
	 */
	public BufMgr(int numBufs, String replaceArg) {
		bufPool = new byte[numBufs][global.GlobalConst.MINIBASE_PAGESIZE];
		bufDescr = new pageDsc[numBufs];
		google = new Hashtable<Integer, Integer>();
		rep = Policy.getInstance(replaceArg);
		MAX = numBufs;
		pages = new Page[numBufs];
		for (int i = 0; i < numBufs; i++) {
			pages[i] = new Page(bufPool[i]);
			bufDescr[i] = new pageDsc(i);
			rep.addToCountZero(bufDescr[i]);
			rep.addToRequested(bufDescr[i]);
		}
	}

	/**
	 * Pin a page First check if this page is already in the buffer pool. If it
	 * is, increment the pin_count and return pointer to this page. If the
	 * pin_count was 0 before the call, the page was a replacement candidate,
	 * but is no longer a candidate. If the page is not in the pool, choose a
	 * frame (from the set of replacement candidates) to hold this page, read
	 * the page (using the appropriate method from diskmgr package) and pin it.
	 * Also, must write out the old page in chosen frame if it is dirty before
	 * reading new page. (You can assume that emptyPage == false for this
	 * assignment.)
	 * 
	 * @param pgid
	 *            page number in the minibase.
	 * @param page
	 *            the pointer point to the page.
	 * @param emptyPage
	 *            true (empty page), false (nonempty page).
	 * @throws BufferPoolExceededException
	 */
	public void pinPage(PageId pgid, Page page, boolean emptyPage, boolean loved)
			throws BufferPoolExceededException {
		if (!google.containsKey(pgid.pid)) { // not in RAM
			int rowIndex = 0;
			if (rep.isEmpty()) { // Policy can not be accessed
				throw new BufferPoolExceededException(null, "");
			}

			rowIndex = rep.getFrame().getFrameIndex();
			if (bufDescr[rowIndex].dirtybit)
				flushPage(bufDescr[rowIndex].pageID); // save in Disk

			rep.removeFromCountZero(bufDescr[rowIndex]);
			google.remove(bufDescr[rowIndex].pageID.pid); // remove from RAM

			try {
				Page tmp = new Page();
				SystemDefs.JavabaseDB.read_page(pgid, tmp); // Read from Disk
				pages[rowIndex] = tmp;
				bufPool[rowIndex] = tmp.getpage(); // Put the page in the
				page.setpage(tmp.getpage()); // selected place
				google.put(pgid.pid, rowIndex);
				bufDescr[rowIndex]
						.update(new PageId(pgid.pid), 1, false, loved);

				rep.addToRequested(bufDescr[rowIndex]); // add in LRU/MRU

			} catch (InvalidPageNumberException | FileIOException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else { // in RAM
			int reqPgRow = google.get(pgid.pid);
			page.setpage(bufPool[reqPgRow]);
			bufDescr[reqPgRow].increment();
			rep.addToRequested(bufDescr[reqPgRow]);
			rep.removeFromCountZero(bufDescr[reqPgRow]);

		}
	}

	/**
	 * Unpin a page specified by a pageId. This method should be called with
	 * dirty == true if the client has modified the page. If so, this call
	 * should set the dirty bit for this frame. Further, if pin_count > 0, this
	 * method should decrement it. If pin_count = 0 before this call, throw an
	 * excpetion to report error. (for testing purposes, we ask you to throw an
	 * exception named PageUnpinnedExcpetion in case of error.)
	 * 
	 * @param pgid
	 *            page number in the minibase
	 * @param dirty
	 *            the dirty bit of the frame.
	 * @throws PageUnpinnedExcpetion
	 * @throws HashEntryNotFoundException
	 * @throws InvalidPageNumberException
	 */
	public void unpinPage(PageId pgid, boolean dirty, boolean loved)
			throws PageUnpinnedExcpetion, HashEntryNotFoundException {

		if (google.containsKey(pgid.pid)) {
			int rowPlace = google.get(pgid.pid);
			if (bufDescr[rowPlace].getPin_count() == 0)// throws exception
				throw new PageUnpinnedExcpetion(null, "");

			bufDescr[rowPlace].decrement();
			bufDescr[rowPlace].dirtybit = dirty;
			if (!bufDescr[rowPlace].lovebit)
				bufDescr[rowPlace].lovebit = loved;

			rep.addToCountZero(bufDescr[rowPlace]);
		} else {
			throw new HashEntryNotFoundException(null, "");
		}
	}

	/**
	 * Allocate new page(s). Call DB Object to allocate a run of new pages and
	 * find a frame in the buffer pool for the first page and pin it. (This call
	 * allows a client f the Buffer Manager to allocate pages on disk.) If
	 * buffer is full, i.e., you can\t find a frame for the first page, ask DB
	 * to deallocate all these pages, and return null.
	 * 
	 * @param firstPage
	 *            the address of the first page.
	 * @param howmany
	 *            total number of allocated new pages.
	 * 
	 * @return the first page id of the new pages. null, if error.
	 * @throws BufferPoolExceededException
	 */
	public PageId newPage(Page firstPage, int howmany)
			throws BufferPoolExceededException {

		if (rep.isEmpty())
			return null;

		PageId temp = new PageId();

		try {
			SystemDefs.JavabaseDB.allocate_page(temp, howmany);

		} catch (OutOfSpaceException | InvalidRunSizeException
				| InvalidPageNumberException | FileIOException
				| DiskMgrException | IOException e2) {
			e2.printStackTrace();
		}

		pinPage(temp, firstPage, false, false);
		return temp;
	}

	/**
	 * This method should be called to delete a page that is on disk. This
	 * routine must call the method in diskmgr package to deallocate the page.
	 * 
	 * @param pgid
	 *            the page number in the database.
	 * @throws HashEntryNotFoundException
	 * @throws PageUnpinnedExcpetion
	 */
	public void freePage(PageId pgid) throws PagePinnedException,
			PageUnpinnedExcpetion, HashEntryNotFoundException {
		if (!google.containsKey(pgid.pid))
			return;
		if (bufDescr[google.get(pgid.pid)].getPin_count() >= 2)
			throw new PagePinnedException(null, "");
		try {
			if (bufDescr[google.get(pgid.pid)].getPin_count() == 1)
				unpinPage(pgid, false, false);

			SystemDefs.JavabaseDB.deallocate_page(pgid);
		} catch (InvalidRunSizeException | InvalidPageNumberException
				| FileIOException | DiskMgrException | IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Used to flush a particular page of the buffer pool to disk. This method
	 * calls the write_page method of the diskmgr package.
	 * 
	 * @param pgid
	 *            the page number in the database.
	 */
	public void flushPage(PageId pgid) {

		if (!google.containsKey(pgid.pid)) {
			return;
		}
		try {
			SystemDefs.JavabaseDB.write_page(pgid,
					new Page(bufPool[google.get(pgid.pid)]));
		} catch (InvalidPageNumberException | FileIOException | IOException e) {
			e.printStackTrace();
		}
	}

	public void flushAllPages() {
		for (int i = 0; i < MAX; i++) {
			if (bufDescr[i].dirtybit)
				flushPage(bufDescr[i].pageID);
		}
	}

	public int getNumUnpinnedBuffers() {
		int i = 0;
		for (int y = 0; y < MAX; y++)
			if (bufDescr[y].getPin_count() == 0)
				i++;
		return i;
	}
}