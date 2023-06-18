package com.viram.dev.controller;

import java.util.*;
import java.util.stream.*;

public class Main {
    public static void main(String[] args) {
        String arr[] = { "abcdp", "efNAgi", "hnPGDRE", "xNmoP", "tqGlA","adAFe9" };
                            //0     //1         //2        //3      //4     //5
        
        
        String[] str = arr[0].split("");
        Map<String, List<Integer>> mapList = new HashMap<>();
        
        List<Integer> countingIndex = null;
        for(String c: str){
            int flag = 0;
            countingIndex = new ArrayList<>();

         for(String cr: arr){
             if(!arr[0].equalsIgnoreCase(cr) && cr.toLowerCase().contains(c.toLowerCase())){
                 countingIndex.add(flag);
             }
             flag++;
         }
             if(countingIndex.size()>0)
                mapList.put(c, countingIndex);
        }
        
       
        
        System.out.println(mapList.toString());
       
   }
}
//A=1,4,5

