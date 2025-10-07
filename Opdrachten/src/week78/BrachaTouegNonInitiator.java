package week78;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;

public class BrachaTouegNonInitiator extends BrachaTouegProcess {
	
	private Channel parent;
	private boolean grantSent = false;

	@Override
	public void init() {
		// TODO
	}

	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		super.receive(m, c);
		if (m instanceof NotifyMessage) {
			processNotifyMessage(c);
		}
		else if (m instanceof AckMessage) {
			processAckMessage(c);
			
			// if all inRequests are finished, send done to parent
			if (getInRequests().size() == 0) {
				send(new DoneMessage(), getIncomingToOutgoing(getParent()));
			}
		}
		else if (m instanceof DoneMessage) {
			processDoneMessage(c);
		}
	}
	
	
	private void setParent(Channel c) {
		this.parent = c;
	}
	
	private Channel getParent() {
		return this.parent;
	}
	
	private boolean getGrantSent() {
		return grantSent;
	}
	
	
	private void processNotifyMessage(Channel c) {
		// Set Parent on first notify message
		if (getParent() == null) {
			setParent(c);
			sendNotifyToOutRequests();
		}
		// If already received notify message, send Done back
		else {
			send(new DoneMessage(), getIncomingToOutgoing(c));
		}
		
		// Send acks if no pending requests
		if (super.requests == 0) {
			sendGrantToInRequests();
		}
	}
	
	// Override method to avoid sending to parent
	protected void sendNotifyToOutRequests() {
		for (Channel c: getOutRequests()) {
			// Don't send notify message to parent
			if (getOutgoingToIncoming(c) != getParent()) {
				send(new NotifyMessage(), c);
			}
		}
	}
	
	protected void processDoneMessage(Channel c) {
		// If all done messages received, send Done to parent
		if (getInRequests().size() == 0) {
			send(new DoneMessage(), getIncomingToOutgoing(getParent()));
		}
	}
	
}
