package com.docker.springdockerd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OccurenceExample1 {

	public static void main(String[] args) {
List<String> list = new ArrayList<String>();
		
		list.add("Viram");
		list.add("Adish");
		list.add("Viram");
		list.add("Adish");
		list.add("Adish");
		list.add("Arti");
		
		Map<String, Integer> map = new HashMap<>();
		
		for(String str : list) {
			if(map.containsKey(str)) {
				map.put(str, map.get(str)+1);
			} else {
				map.put(str, 1);
			}
		}
		
		System.out.println(map.toString());
	}
}
