package week2;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import framework.Network;
import framework.Process;

public class VectorClock extends LogicalClock<Map<Process, Integer>> {

	public VectorClock(Map<Process, List<Event>> sequences) {
		
		List<Event> allEvents = new ArrayList<>();
		
		for (List<Event> list: sequences.values()) {
			allEvents.addAll(list);
		}
		
		// loop till all events are sorted into timestamps
		while (getTimestamps().size() != allEvents.size()) {
			
			// loop over all event lists
			outerLoop:
			for (List<Event> events: sequences.values()) {
				
				// loop over all events in list
				for (Event e: events) {
					
					// skip over event if it already has a timestamp
					if (containsTimestamp(e)) {
						//System.out.println("Continue after event: " + e);
						continue;
					}
					
					Map<Process, Integer> maxTimestamp = lastTimestampProcess(e.getProcess());
					
					//System.out.println("LastTimestamp process: " + maxTimestamp);
					
					//System.out.println("MaxTimestamp: " + maxTimestamp);
					
					if (maxTimestamp.isEmpty() ) {
						for (Process p: sequences.keySet()) {
							maxTimestamp.put(p,  0);
						}
					}
					
					//System.out.println("MaxTimestamp update: " + maxTimestamp);
					
					// if internal or send event
					// increment timestamp with 1 and add to HashMap
					if (e instanceof InternalEvent || e instanceof SendEvent) {
						int i = maxTimestamp.get(e.getProcess());
						maxTimestamp.put(e.getProcess(), i + 1);
						//System.out.println("MaxTimestamp after increment: " + maxTimestamp + " at event " + e);
						addTimestamp(e, maxTimestamp);
					}
					
					// if receive event check if send event has calculated timestamp
					else if (e instanceof ReceiveEvent) {
						SendEvent send = ((ReceiveEvent) e).getCorrespondingSendEvent(allEvents);
						if (containsTimestamp(send)) {
							// Map<Process, Integer> timestampSend = getTimestamp(send);
							// Map<Process, Integer> timestampProcess = lastTimestampProcess(e.getProcess());
							
							Map<Process, Integer> maxTimestampBetweenProcesses = maxTimestamp(
									getTimestamp(send), lastTimestampProcess(e.getProcess()));
							
							maxTimestampBetweenProcesses.put(
									e.getProcess(), maxTimestampBetweenProcesses.get(e.getProcess()) + 1);
							
							addTimestamp(e, maxTimestampBetweenProcesses);
						}
						else {
							continue outerLoop;
						}
					}
					System.out.println("All timestamps: " + getTimestamps());
				}
			}
		}
		//System.out.println("All timestamps: " + getTimestamps());
	}
		
	private Map<Process, Integer> maxTimestamp(
				Map<Process, Integer> firstTimestamp, 
				Map<Process, Integer> secondTimestamp) {
		
		Map<Process, Integer> res = new LinkedHashMap<>();
		
		if (secondTimestamp.isEmpty()) {
			// make copy of values and store in res
			for (Process p: firstTimestamp.keySet()) {
				res.put(p, firstTimestamp.get(p));
			}
			return res;
		}
		
		// System.out.println("FirstTimestamp: " + firstTimestamp);
		
		for (Process p: firstTimestamp.keySet()) {		
			// System.out.println("checkpoint reached");
			if (firstTimestamp.get(p) > secondTimestamp.get(p)) {
				// System.out.println("Firsttimestamp p value: " + firstTimestamp.get(p));
				res.put(p,  firstTimestamp.get(p));
			}
			else {
				// System.out.println("Secondtimestamp p value: " + secondTimestamp.get(p));
				res.put(p,  secondTimestamp.get(p));
			}
		}
		return res;
	}
	
	private Map<Process, Integer> lastTimestampProcess (Process p) {
		Map<Process, Integer> max = new LinkedHashMap<>();
		Map<Process, Integer> temp = new LinkedHashMap<>();
		
		for (Event e: getTimestamps().keySet()) {
			//System.out.println("Event: " + e);
			if (e.getProcess() == p && containsTimestamp(e)) {
				//System.out.println("Timestamp event : " + getTimestamp(e));
				temp = maxTimestamp(getTimestamp(e), temp);
			}
		}
		// make copy of values and store in max
		for (Process pTemp: temp.keySet()) {
			max.put(pTemp, temp.get(pTemp));
		}
		return max;
	}
	
	/*
	 * -------------------------------------------------------------------------
	 */
	
	public static Map<Process, Integer> parseTimestamp(String s, Network n) {
		String[] tokens = s.split(",");
		List<Process> processes = new ArrayList<>(n.getProcesses().values());
		if (tokens.length != processes.size()) {
			throw new IllegalArgumentException();
		}
		
		Map<Process, Integer> timestamp = new LinkedHashMap<>();
		
		for (int i = 0; i< tokens.length; i++) {
			try {
				timestamp.put(processes.get(i), Integer.parseInt(tokens[i]));
			} catch (Throwable t) {
				throw new IllegalArgumentException();
			}
		}
		
		return timestamp;
	}
}
