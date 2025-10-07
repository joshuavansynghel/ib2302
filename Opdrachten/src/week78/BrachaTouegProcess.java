package week78;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;

public class BrachaTouegProcess extends DeadlockDetectionProcess {

	@Override
	public void init() {
	}

	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		
		System.out.println("Message " + m + " received from " + c);
		
		if (!validMessage(m)) {
			throw new IllegalReceiveException();
		}
		else if (m instanceof DoneMessage) {
			processDoneMessage(c);
		}
		else if (m instanceof AckMessage) {
			processAckMessage(c);
		}
		else if (m instanceof GrantMessage) {
			processGrantMessage(c);
		}
	}

	
	// send messages to neighbours methods
	protected void sendNotifyToOutRequests() {
		for (Channel c: getOutRequests()) {
			send(new NotifyMessage(), c);
		}
	}
	
	protected void sendGrantToInRequests() {
		for (Channel c: getInRequests()) {
			send(new GrantMessage(), getIncomingToOutgoing(c));
		}
	}
	
	
	// reverse channel methods
	protected Channel getIncomingToOutgoing (Channel c) {
		Channel reversedChannel = null;
		for (Channel cOut: getOutgoing()) {
			if (cOut.getReceiver() == c.getSender()) {
				reversedChannel = cOut;
			}
		}
		return reversedChannel;
	}
	
	protected Channel getOutgoingToIncoming (Channel c) {
		Channel reversedChannel = null;
		for (Channel cIn: getIncoming()) {
			if (cIn.getSender() == c.getReceiver()) {
				reversedChannel = cIn;
			}
		}
		return reversedChannel;
	}
	
	// Received message methods
	protected boolean validMessage(Message m) {
		return ((m instanceof AckMessage) || (m instanceof DoneMessage) ||
				(m instanceof GrantMessage) || (m instanceof NotifyMessage));
	}
	
	protected void processDoneMessage(Channel c) throws IllegalReceiveException {
		if (getOutRequests().contains(c)) {
			getOutRequests().remove(c);
			setRequests(getOutRequests().size());
		}
		else {
			throw new IllegalReceiveException();
		}
	}
	
	protected void processAckMessage(Channel c) throws IllegalReceiveException {
		if (getInRequests().contains(getIncomingToOutgoing(c))) {
			getInRequests().remove(getIncomingToOutgoing(c));
		}
		else {
			throw new IllegalReceiveException();
		}
	}
	
	protected void processGrantMessage(Channel c) throws IllegalReceiveException {
		// Decrease number of requests
		if (getOutRequests().contains(c)) {
			decrementOutRequests();
		}
		
		// If process has resources free, send grants to each incoming request
		if(super.requests == 0) {
			sendGrantToInRequests();
		}
		
		// Send ack back
		send(new AckMessage(), getIncomingToOutgoing(c));
	}
	
	protected void decrementOutRequests() {
		if (super.requests == 0) {
			setRequests(Integer.MIN_VALUE);
		}
		else {
			setRequests(super.requests - 1);
		}
	}
}

