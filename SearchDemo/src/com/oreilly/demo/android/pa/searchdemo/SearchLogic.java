package com.oreilly.demo.android.pa.searchdemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import android.content.Context;

public class SearchLogic implements SearchLogicInterface {
	private Sonnet[] sonnets;
	private HashMap<String, HashSet<SonnetRef>> termindex;
	
	public SearchLogic(Context context, int resourceId) throws Exception {
		termindex = new HashMap<String, HashSet<SonnetRef>>();
		sonnets = readInSonnets(context, resourceId, termindex);
		if(sonnets == null) throw new Exception("No Sonnets!");
	}
	
	public Sonnet getSonnet(int i) {
		return sonnets != null ? sonnets[i] : null;
	}
	
	@SuppressWarnings("unchecked")
	public SonnetFragment[] search(String query) {
		if(query == null || query.trim().length() < 1) return new SonnetFragment[0];
		query = query.trim().toLowerCase();
		ArrayList<SonnetFragment> frags = new ArrayList<SonnetFragment>();
	
		String[] terms = query.split(" ");
		if(terms == null) terms = new String[]{query};
		ArrayList<HashSet<SonnetRef>> sets = new ArrayList<HashSet<SonnetRef>>();
		for(String term: terms) {
			if(termindex.containsKey(term)) {
				sets.add((HashSet<SonnetRef>) (termindex.get(term).clone()));
			}
		}
		if(!sets.isEmpty()) {
			HashSet<SonnetRef> main = null;
			for(HashSet<SonnetRef> set: sets) {
				if(main == null) main = set;
				else {
					main.retainAll(set);
				}
			}
			
			if(main != null && !main.isEmpty()) {
				Iterator<SonnetRef> it = main.iterator();
				while(it.hasNext()) {
					SonnetRef s = it.next();
					Sonnet son = sonnets[s.num];
					frags.add(new SonnetFragment(s.num, son.lines[s.line]));
				}
			}
		}
		
		return frags.isEmpty() ? 
				new SonnetFragment[0] : 
				frags.toArray(new SonnetFragment[frags.size()]);
	}
	
	private static Sonnet[] readInSonnets(Context context, int resourceId, HashMap<String, HashSet<SonnetRef>> index) throws Exception {
		ArrayList<Sonnet> sons = new ArrayList<Sonnet>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(
					new InputStreamReader(context.getResources().openRawResource(resourceId)));
			String line = null;
			
			String num = null;
			ArrayList<String> ls = new ArrayList<String>();
			
			while ((line = br.readLine()) != null) {
				if(line.length() < 15 && line.endsWith(".".intern())) {
					String curnum = line.substring(0, line.length() - 1);
					if(num != null) {
						int size = ls.size();
						Sonnet sonnet = new Sonnet(num, ls.toArray(new String[size]));
						sons.add(sonnet);
						if(index != null) {
							for(int i=0;i<size;i++) {
								String[] words = sonnet.lines[i].split(" ".intern());
								if(words != null) {
									for(String word: words) {
										while(word.endsWith(",".intern()) || 
												word.endsWith(".".intern()) || 
												word.endsWith("?".intern())) {
											word = word.substring(0, word.length() - 1);
										}
										word = word.trim().toLowerCase();
										HashSet<SonnetRef> set = null;
										if(index.containsKey(word)) {
											set = index.get(word);
										} else set = new HashSet<SonnetRef>();
										
										set.add(new SonnetRef(sons.size() - 1, i));
										
										index.put(word, set);
									}
								}
							}
						}
					} 
					num = curnum;
					ls.clear();
				} else if(line.trim().length() > 1) {
					ls.add(line);
				}
			}
		} finally {
            try {
            	if(br != null) {
            		br.close();
            	}
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
		return !sons.isEmpty() ? sons.toArray(new Sonnet[sons.size()]) : null;
	}
	
	private static class SonnetRef {
		int num;
		int line;
		
		public SonnetRef(int n, int l) {
			num = n;
			line = l;
		}
		
		@Override
		public boolean equals(Object ob) {
			if(super.equals(ob)) return true;
			if(ob != null && ob instanceof SonnetRef) {
				SonnetRef s = (SonnetRef) ob;
				return num == s.num && line == s.line;
			}
			return false;
		}
		
		@Override
		public int hashCode() {
			int hash = 1;
			hash = hash * 17 + num;
			hash = hash * 31 + line;
			return hash;
		}
	}
}
