package week34;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;

public abstract class LaiYangProcess extends SnapshotProcess {
	
	private Map<Channel, List<Message>> receivedMessages = new LinkedHashMap<>();

	@Override
	public void init() {
		// TODO
	}
	
	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		// if not a ChandyLamportMessage, throw error
		if (!(m instanceof LaiYangControlMessage ||
				m instanceof LaiYangBasicMessage)) {
			throw new IllegalReceiveException();
		}
		// if control message is received and snapshot finished, throw error
		else if ((m instanceof LaiYangControlMessage) &&
				(hasFinished())) {
			throw new IllegalReceiveException();
		}
		// if channel has sent control message twice, throw error
		else if ((m instanceof LaiYangControlMessage) && 
				duplicateControlMessage(c)) {
			throw new IllegalReceiveException();
		}
		// reject any false piggybacked message when snapshot is finished
		else if ((m instanceof LaiYangBasicMessage) &&
				(!((LaiYangBasicMessage)m).getTag()) &&
				hasFinished()) {
			throw new IllegalReceiveException();
		}
		// if control message from non-initiator, record all messages received
		if ((m instanceof LaiYangControlMessage) &&  
				hasStarted()) {
			record(c, getAllBasicMessages(c));
		}
		
		if (m instanceof LaiYangControlMessage ||
				!((LaiYangBasicMessage)m).getTag()){
			// only store false piggybacked or control messages
			addMessage(m, c);
		}
		
		// if all control messages are received, finish snapshot
		if (receivedAllControllMesssages() && 
				receivedAllFalsePiggybackedBasicMessages()) {
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
			if (m instanceof LaiYangControlMessage) {
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
				if (m instanceof LaiYangControlMessage) {
					numberOfControlMessages += 1;
				}
			}
		}
		return numberOfControlMessages == getIncoming().size();
	}
	
	protected boolean receivedAllFalsePiggybackedBasicMessages() {
		for (Map.Entry<Channel, List<Message>> entry : receivedMessages.entrySet()) {
			System.out.println("Entry " + entry);
			for (Message m : entry.getValue()) {
				// check if 
				if ((m instanceof LaiYangControlMessage) && 
						((LaiYangControlMessage)m).getN() != entry.getValue().size() - 1) {
					return false;
				}
			}
		}
		return true;
	}
	
	protected boolean receivedAllFalsePiggybackedBasicMessagesFromChannel(Channel c) {
		List<Message> messages = receivedMessages.get(c);
		int controlMessageN = 0;
		for (Message m : messages) {
			if (m instanceof LaiYangControlMessage) {
				controlMessageN = ((LaiYangControlMessage)m).getN();
			}
		}
		return controlMessageN == getAllBasicMessages(c).size();
	}
	
	// get all basic messages of specific channel from receivedMessages
	private List<Message> getAllBasicMessages(Channel c) {
		List<Message> basicMessagesFromChannel = new ArrayList<>();
		if (!(receivedMessages.containsKey(c))) {
			return basicMessagesFromChannel;
		}
		for (Message m : receivedMessages.get(c)) {
			if (m instanceof LaiYangBasicMessage) {
				basicMessagesFromChannel.add(m);
			}
		}
		return basicMessagesFromChannel;
	}
}
