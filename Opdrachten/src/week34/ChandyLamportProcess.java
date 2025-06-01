package week34;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;

public abstract class ChandyLamportProcess extends SnapshotProcess {
	
	private Map<Channel, List<Message>> receivedMessages = new LinkedHashMap<>();

	@Override
	public void init() {
		// TODO
	}

	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		if (!(m instanceof ChandyLamportControlMessage) ||
				!(m instanceof ChandyLamportBasicMessage)) {
			throw new IllegalReceiveException();
		}
		else if ((m instanceof ChandyLamportControlMessage) &&
				(hasFinished())) {
			throw new IllegalReceiveException();
		}
		else if (duplicateControlMessage(c)) {
			throw new IllegalReceiveException();
		}
		addMessage(c, m);
	}
	
	public void addMessage(Channel c, Message m) {
		receivedMessages.computeIfAbsent(c, k -> new ArrayList<>()).add(m);
	}
	
	public boolean duplicateControlMessage(Channel c ) {
		int countControlMessages = 0;
		for (Message m: receivedMessages.get(c)) {
			if (m instanceof ChandyLamportControlMessage) {
				countControlMessages += 1;
			}
		}
		return countControlMessages > 1;
	}
}
