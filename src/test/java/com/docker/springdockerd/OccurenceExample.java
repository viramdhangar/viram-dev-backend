package com.docker.springdockerd;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class OccurenceExample {

	public static void main(String[] args) {
		List<String> list = new ArrayList<String>();
		
		list.add("Viram");
		list.add("Adish");
		list.add("Viram");
		list.add("Adish");
		list.add("Adish");
		list.add("Arti");
		
		Map<String, Long> map = list.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		System.out.println(map.toString());
		
	}
	
}
