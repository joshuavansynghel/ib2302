package week2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import framework.Network;

public class CausalOrder {

	private Set<Pair> pairs = new LinkedHashSet<>();

	public CausalOrder() {
	}

	public CausalOrder(List<Event> sequence) {

		// loop over all events in sequence 
		outerLoop:
		for (int i = 0; i < sequence.size(); i++) {
			
			// loop over all events starting from next index outer loop
			for (int j = i + 1; j < sequence.size(); j++) {
				
				// if 2 sequential events in within same process
				if (sequence.get(i).getProcess() == sequence.get(j).getProcess()) {
					
					// create pair and add to pairs
					// first event first argument, second event last argument
					pairs.add(new Pair(sequence.get(i), sequence.get(j)));
					
					// continue outer loop to avoid chaining
					continue outerLoop;
				}
			}
		}
				
		// loop over all events to create causal relations between send and receive events
		for (Event e: sequence) {
			if (e instanceof SendEvent) {
				SendEvent sendEvent = (SendEvent) e;
				ReceiveEvent receiveEvent = (ReceiveEvent) sendEvent.getCorrespondingReceiveEvent(sequence);
				pairs.add(new Pair(sendEvent, receiveEvent));
			}
		}
	}

	public Set<List<Event>> toComputation(Set<Event> events) {
		
		Set<List<Event>> res = new LinkedHashSet<>();
		List<Event> initialList = new ArrayList<>(events);
		List<List<Event>> perms = new ArrayList<>();
		
		permuteComputation(initialList, 0, perms);
		
		for (List<Event> perm: perms) {
			if (validComputation(perm)) {
				res.add(perm);
			}
		}
				
		return res;
	}
	
	private void permuteComputation(
			List<Event> events, int start, List<List<Event>> res) {
		
		if (start == events.size()) {
			res.add(new ArrayList<>(events));
			return;
		}
		
		for (int i = start; i < events.size(); i++) {
			Collections.swap(events, i, start); 
			permuteComputation(events, start + 1, res);
			Collections.swap(events,  i,  start);
		}
	}
	
	private boolean validComputation(List<Event> events) {
		for (Pair p: pairs) {
			if (events.indexOf(p.getLeft()) > events.indexOf(p.getRight())) {
				return false;
			}
		}
		return true;
	}
	
	/*
	 * -------------------------------------------------------------------------
	 */

	@Override
	public boolean equals(Object o) {
		if (o instanceof CausalOrder) {
			CausalOrder that = (CausalOrder) o;
			return this.pairs.equals(that.pairs);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return pairs.size();
	}

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		for (Pair p : pairs) {
			b.append(" ").append(p);
		}
		return b.toString().trim();
	}

	public void addPair(Event left, Event right) {
		pairs.add(new Pair(left, right));
	}

	public Set<Pair> getPairs() {
		return new LinkedHashSet<>(pairs);
	}

	public static CausalOrder parse(String s, Network n) {

		CausalOrder order = new CausalOrder();

		Map<String, Event> events = new LinkedHashMap<>();

		String[] tokens = s.split(" ");
		for (String token : tokens) {

			String[] subtokens = token.split("<");
			if (subtokens.length != 2) {
				throw new IllegalArgumentException();
			}

			String left = subtokens[0];
			String right = subtokens[1];

			if (!events.containsKey(left)) {
				events.put(left, Event.parse(left, n));
			}
			if (!events.containsKey(right)) {
				events.put(right, Event.parse(right, n));
			}

			order.addPair(events.get(left), events.get(right));
		}

		return order;
	}
}
