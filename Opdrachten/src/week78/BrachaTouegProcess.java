package week78;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;

public class BrachaTouegProcess extends DeadlockDetectionProcess {
	
	private Channel grantParent;

	@Override
	public void init() {
	}

	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {

		if (!validMessage(m)) {
			throw new IllegalReceiveException();
		}
		else if (m instanceof DoneMessage) {
			processDoneMessage(c);
		}
		else if (m instanceof AckMessage) {
			processAckMessage(c);
			
			if ((getInRequests().size() == 0) && (grantParent != null)) {
				send(new AckMessage(), getIncomingToOutgoing(getGrantParent()));
			}
		}
		else if (m instanceof GrantMessage) {
			processGrantMessage(c);
		}
	}
	
	
	private void setGrantParent(Channel c) {
		this.grantParent = c;
	}
	
	private Channel getGrantParent() {
		return this.grantParent;
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
		if (getOutRequests().contains(getIncomingToOutgoing(c))) {
			getOutRequests().remove(getIncomingToOutgoing(c));
		}
		else {
			throw new IllegalReceiveException();
		}
	}
	
	protected void processAckMessage(Channel c) throws IllegalReceiveException {
		if (getInRequests().contains(c)) {
			getInRequests().remove(c);
		}
		else {
			throw new IllegalReceiveException();
		}
	}
	
	protected void processGrantMessage(Channel c) throws IllegalReceiveException {
		// Decrease number of requests
		if (getOutRequests().contains(getIncomingToOutgoing(c))) {
			decrementOutRequests();
		}
		
		// If process has resources free, send grants to each incoming request
		if(super.requests == 0) {
			if (getInRequests().size() > 0) {
				sendGrantToInRequests();
				setGrantParent(c);
			}
			else {
				send(new AckMessage(), getIncomingToOutgoing(c));
			}
		}
		else {
			// Send ack back
			send(new AckMessage(), getIncomingToOutgoing(c));
		}
	}
	
	protected void decrementOutRequests() {
		setRequests(super.requests - 1);
	}
}

