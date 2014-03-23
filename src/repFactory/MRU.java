package repFactory;

import java.util.LinkedList;
import java.util.Stack;

import bufmgr.pageDsc;

public class MRU extends Policy {

	protected MRU(int max) {
		// TODO Auto-generated constructor stub
		super();
		MAX = max;
	}

	@Override
	public boolean isEmpty() {
		for (int i = 0; i < requested.size(); i++) {
			if (requested.get(i).getPin_count() == 0)
				return false;
		}
		return true;
	}

	@Override
	 public pageDsc getFrame() {
		for (int i = 0; i < requested.size(); i++) {
			if (requested.get(i).getPin_count() == 0  && requested.get(i).getPageID().pid == -1)
				return requested.get(i);
		}
		pageDsc a = null;
		for (int i = 0; i < requested.size(); i++) {
			if (requested.get(i).getPin_count() == 0)
				a = requested.get(i);
		}
		// System.out.println(a.getPageID().pid);
		return a;
	 }
}
