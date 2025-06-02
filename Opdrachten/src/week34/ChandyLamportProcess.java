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
	}

	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		// if not a ChandyLamportMessage, throw error
		if (!(m instanceof ChandyLamportControlMessage ||
				m instanceof ChandyLamportBasicMessage)) {
			throw new IllegalReceiveException();
		}
		// if control message is received and snapshot finished, throw error
		else if ((m instanceof ChandyLamportControlMessage) &&
				(hasFinished())) {
			throw new IllegalReceiveException();
		}
		// if channel has sent control message twice, throw error
		else if (duplicateControlMessage(c)) {
			throw new IllegalReceiveException();
		}
		// if control message from non-initiator, record all messages received
		else if ((m instanceof ChandyLamportControlMessage) && hasStarted()) {
			record(c, getAllBasicMessages(c));
		}
		
		addMessage(m, c);
		
		// if all control messages are received, finish snapshot
		if (receivedAllControllMesssages()) {
			finishSnapshot();
		}
	}
	
	// add message to receivedMessages attribute
	protected void addMessage(Message m, Channel c) {
		receivedMessages.computeIfAbsent(c, k -> new ArrayList<>()).add(m);
	}
	
	// check if channel has already sent a control message
	private boolean duplicateControlMessage(Channel c) {
		boolean found = false;
		if (!(receivedMessages.containsKey(c))) {
			return false;
		}
		for (Message m: receivedMessages.get(c)) {
			if (m instanceof ChandyLamportControlMessage) {
				found = true;
			}
		}
		return found;
	}
	
	// check if control messages from incoming channels have been received
	protected boolean receivedAllControllMesssages() {
		int numberOfControlMessages = 0;
		for (Map.Entry<Channel, List<Message>> entry : receivedMessages.entrySet()) {
			for (Message m : entry.getValue()) {
				if (m instanceof ChandyLamportControlMessage) {
					numberOfControlMessages += 1;
				}
			}
		}
		return numberOfControlMessages == getIncoming().size();
	}
	
	// pull all basic messages of specific channel from receivedMessages
	private List<Message> getAllBasicMessages(Channel c) {
		List<Message> basicMessagesFromChannel = new ArrayList<>();
		if (!(receivedMessages.containsKey(c))) {
			return basicMessagesFromChannel;
		}
		for (Message m : receivedMessages.get(c)) {
			if (m instanceof ChandyLamportBasicMessage) {
				basicMessagesFromChannel.add(m);
			}
		}
		return basicMessagesFromChannel;
		
	}
}
