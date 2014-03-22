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
	// kol row hwa page
	// 3dd el rows numbufs
	static pageDsc[] bufDescr;// numbufs // page size
	static byte[][] bufPool;
	static Page[] pages;
	static Hashtable<Integer, Integer> google;
	static int num, MAX;
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
		for (int i = 0; i < numBufs; i++)
			bufDescr[i] = new pageDsc();
		google = new Hashtable<Integer, Integer>();
		rep = Policy.getInstance(replaceArg);
		MAX = numBufs;
		pages = new Page[numBufs];
		for (int i = 0; i < numBufs; i++)
			pages[i] = new Page(bufPool[i]);
		num = 0;
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
	 */
	public void pinPage(PageId pgid, Page page, boolean emptyPage, boolean loved) {
		if (!google.contains(pgid.pid)) { // not in RAM
		// System.out.println("a77a");
			int rowIndex = 0;
			if (num >= MAX) { // RAM is Full

				if (rep.isEmpty()) { // Policy can not be accessed
					// System.out.println("EMPTY FIFO");
					return;
				}

				PageId pageIdToBeRemoved = rep.getFrame().pageID;
				rowIndex = google.get(pageIdToBeRemoved.pid);
				if (bufDescr[rowIndex].dirtybit)
					flushPage(pageIdToBeRemoved); // save in Disk

				rep.removeFromAdded(bufDescr[rowIndex]);
				rep.removeFromRequested(bufDescr[rowIndex]);
				google.remove(pageIdToBeRemoved.pid); // remove from RAM

			} else { // RAM has empty place
				rowIndex = num;
				num++;
			}

			try {
				Page tmp = new Page();
				SystemDefs.JavabaseDB.read_page(pgid, tmp); // Read from Disk
				pages[rowIndex]= tmp;
				bufPool[rowIndex] = tmp.getpage(); // Put the page in the
				 page.setpage(tmp.getpage()); // selected place
				google.put(pgid.pid, rowIndex);
				bufDescr[rowIndex].update(pgid, 1, false, loved);

				rep.addToAdded(bufDescr[rowIndex]); // add in queue FIFO ONLY
				rep.addToRequested(bufDescr[rowIndex]); // add in queue FIFO
														// ONLY

			} catch (InvalidPageNumberException | FileIOException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else { // in RAM
			int reqPgRow = google.get(pgid.pid);
			page.setpage(bufPool[reqPgRow]);
			bufDescr[reqPgRow].increment();
			rep.addToRequested(bufDescr[reqPgRow]);

		}
		System.out.println(pgid + "   " + google.get(pgid.pid) + "    "
				+ bufPool[google.get(pgid.pid)].toString());
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
	 * @throws InvalidPageNumberException
	 */
	public void unpinPage(PageId pgid, boolean dirty, boolean loved)
			throws PageUnpinnedExcpetion {
		if (google.contains(pgid)) {
			int rowPlace = google.get(pgid);
			if (bufDescr[rowPlace].getPin_count() == 0)// throws exception
				throw new PageUnpinnedExcpetion();

			bufDescr[rowPlace].decrement();
			bufDescr[rowPlace].dirtybit = dirty;
			bufDescr[rowPlace].lovebit = loved;
			// FIFO >> no action
			// LRU >> add if count == 0

		} else {
			// throw new PageUnpinnedExcpetion();
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
	 */
	public PageId newPage(Page firstPage, int howmany) {
		PageId[] b = new PageId[howmany];
		for (int i = 0; i < howmany; i++)
			b[i] = new PageId();
		int i = 0;
		try {
			for (; i < howmany; i++) {
				SystemDefs.JavabaseDB.allocate_page(b[i]);
			}
			pinPage(b[0], firstPage, false, false);
			return b[0];
		} catch (OutOfSpaceException | InvalidRunSizeException
				| InvalidPageNumberException | FileIOException
				| DiskMgrException | IOException e) {
			// TODO Auto-generated catch block
			for (int j = 0; j < i; j++)
				try {
					SystemDefs.JavabaseDB.deallocate_page(b[j]);
				} catch (InvalidRunSizeException | InvalidPageNumberException
						| FileIOException | DiskMgrException | IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * This method should be called to delete a page that is on disk. This
	 * routine must call the method in diskmgr package to deallocate the page.
	 * 
	 * @param pgid
	 *            the page number in the database.
	 */
	public void freePage(PageId pgid) {
		try {
			SystemDefs.JavabaseDB.deallocate_page(pgid);
		} catch (InvalidRunSizeException | InvalidPageNumberException
				| FileIOException | DiskMgrException | IOException e) {
			// TODO Auto-generated catch block
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
		try {
			SystemDefs.JavabaseDB.write_page(pgid,
					new Page(bufPool[google.get(pgid)]));
		} catch (InvalidPageNumberException | FileIOException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void flushAllPages() {
		// TODO Auto-generated method stub
		for (int u = 0; u < MAX; u++)
			if (bufDescr[u].dirtybit)
				flushPage(bufDescr[u].pageID);

	}

	public int getNumUnpinnedBuffers() {
		// TODO Auto-generated method stub
		int i = 0;
		for (int u = 0; u < MAX; u++)
			if (bufDescr[u].getPin_count() == 0)
				i++;
		return i;
	}
}