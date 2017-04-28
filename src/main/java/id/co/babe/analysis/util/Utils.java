package id.co.babe.analysis.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Utils {
	
	public static class MapUtil
	{
	    public static <K, V extends Comparable<? super V>> Map<K, V> 
	        sortByValue( Map<K, V> map )
	    {
	        List<Map.Entry<K, V>> list =
	            new LinkedList<Map.Entry<K, V>>( map.entrySet() );
	        Collections.sort( list, new Comparator<Map.Entry<K, V>>()
	        {
	            public int compare( Map.Entry<K, V> o1, Map.Entry<K, V> o2 )
	            {
	                return (o2.getValue()).compareTo( o1.getValue() );
	            }
	        } );

	        Map<K, V> result = new LinkedHashMap<K, V>();
	        for (Map.Entry<K, V> entry : list)
	        {
	            result.put( entry.getKey(), entry.getValue() );
	        }
	        return result;
	    }
	}
	
	public static void printArray(double[] values) {
		System.out.println();
		for(int i = 0 ; i < values.length ; i ++) {
			System.out.print(" " + values[i]);
		}
		System.out.println();
	}
	
	
	public static void printArray(Collection<String> values) {
		System.out.println();
		for(String v : values) {
			System.out.println(v);
		}
		System.out.println();
	}
	
	public static void printCollection(Collection<?> values) {
		for(Object v : values) {
			System.out.println(v.toString());
		}
	}
	

}
