package com.oreilly.demo.android.pa.searchdemo;

public class Sonnet {
	public int num;
	public String title;
	public String[] lines;
	
	public Sonnet(String n, String[] lins) {
		title = n;
		lines = lins;
		num = RomanNum.convert(title);
	}
	
	private static class RomanNum {
		public char symbol;
		public int num;
		
		public RomanNum(char sym, int n) {
			symbol = sym;
			num = n;
		}
		
		public static RomanNum[] SYMS	= {
			new RomanNum('M',1000),
			new RomanNum('D',500),
	   		new RomanNum('C',100),
	   		new RomanNum('L',50),
	   		new RomanNum('X',10),
	   		new RomanNum('V',5),
	   		new RomanNum('I',1)
		};
		
		public static int convert(String sym) {
			int tot = 0;
			int max = 0;
			char ch[] = sym.toUpperCase().toCharArray();
			for(int p=ch.length-1;p>=0;p--) {
				for(int i=0;i<SYMS.length;i++) {
					if(SYMS[i].symbol == ch[p]) {
						if(SYMS[i].num >= max) {
							tot += (max = SYMS[i].num);
						} else {
							tot -= SYMS[i].num;
						}
					}
				}
			}
			return tot;
		}
	}
}
