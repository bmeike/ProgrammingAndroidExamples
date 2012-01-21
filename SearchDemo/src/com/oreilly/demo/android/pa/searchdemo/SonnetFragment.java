package com.oreilly.demo.android.pa.searchdemo;

public class SonnetFragment {
	public int num;
	public String line;
	
	public SonnetFragment(int n, String l) {
		num = n;
		line = l;
	}
	
	@Override
	public String toString() {
		return num+". "+line;
	}
}
