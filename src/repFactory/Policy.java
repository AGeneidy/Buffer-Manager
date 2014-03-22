package repFactory;

import bufmgr.pageDsc;

public class Policy {
	static Policy instance;
	static String policy;

	protected Policy() {
		// TODO Auto-generated constructor stub
	}

	public static Policy getInstance(String u) {
		if (instance != null) {
			if (!policy.equals(u))// moshkela
				return null;
			return instance;
		} else {
			switch (u) {
			case "FIFO":
				return (instance = new FIFO());
			case "LRU":
				return (instance = new LRU());
			case "MRU":
				return (instance = new MRU());
			case "love/hate":
				return (instance = new LOVE());
			default:
				return null;
			}
		}
	}
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	public pageDsc poll() {
		// TODO Auto-generated method stub
		return null;
	}

	public void check(pageDsc pageDsc) {
		// TODO Auto-generated method stub	
	}

	public void add(pageDsc pageDsc) {
		// TODO Auto-generated method stub
	}

}
