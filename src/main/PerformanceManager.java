package main;

import java.util.ArrayList;
import java.util.HashMap;

public class PerformanceManager {
	private HashMap<String, ArrayList<Long>> timingData = new HashMap<String, ArrayList<Long>> ();
	
	public void enter (String code, Long value) {
		synchronized (timingData) {
			timingData.putIfAbsent(code, new ArrayList<Long>());
			timingData.get(code).add(value);
		}
	}
	
	public Long averageForCode (String code) {
		long total = 0l;
		synchronized (timingData) {
			if (!timingData.containsKey(code)) return 0l;
			for (Long l : timingData.get(code)) total += l;
			total /= timingData.get(code).size();
		}
		return total;
	}
}
