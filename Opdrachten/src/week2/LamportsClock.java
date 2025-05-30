package week2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import framework.Process;

public class LamportsClock extends LogicalClock<Integer> {

	public LamportsClock(Map<Process, List<Event>> sequences) {
		
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
						continue;
					}
					
					int maxTimestamp = calculateMaxTimestampProces(e.getProcess());
					
					// if internal or send event
					// increment timestamp with 1 and add to HashMap
					if (e instanceof InternalEvent || e instanceof SendEvent) {
						addTimestamp(e, maxTimestamp + 1);
					}
					
					// if receive event check if send event has calculated timestamp
					else if (e instanceof ReceiveEvent) {
						SendEvent send = ((ReceiveEvent) e).getCorrespondingSendEvent(allEvents);
						if (containsTimestamp(send)) {
							int timestampSend = getTimestamp(send);
							addTimestamp(e, (Math.max(maxTimestamp, timestampSend) + 1));
						}
						else {
							continue outerLoop;
						}
					}
				}
			}
		}
	}
	
	private int calculateMaxTimestampProces (Process p) {
		int max = 0;
		
		for (Event e: getTimestamps().keySet()) {
			if (e.getProcess() == p && containsTimestamp(e)) {
				int timestamp = getTimestamp(e);
				if (timestamp > max) {
					max = timestamp;
				}
			}
		}
		return max;
	}
}
