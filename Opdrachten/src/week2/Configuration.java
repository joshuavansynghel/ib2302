package week2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Configuration {
	
	public static Configuration parse(String s, Map<String, Configuration> configurations) {
		if (!configurations.containsKey(s)) {
			configurations.put(s, new Configuration());
		}
		
		return configurations.get(s);
	}
	
	public static List<Configuration> parseList(String s, Map<String, Configuration> configurations) {
		List<Configuration> list = new ArrayList<>();
		
		String[] tokens = s.split(" ");
		for (String token : tokens) {
			//System.out.println("Token: " + token);
			list.add(parse(token, configurations));
			//System.out.println("New list: " + list);
		}
		//System.out.println("Final list" + list);
		return list;
	}
}
