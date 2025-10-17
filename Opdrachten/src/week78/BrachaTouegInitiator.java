package week78;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;

public class BrachaTouegInitiator extends BrachaTouegProcess {
	
	private boolean decisionMade = false;

	@Override
	public void init() {
		super.init();
		sendNotifyToOutRequests();
		
		// If initial list is empty, send acks
		if(super.requests == 0) {
			sendGrantToInRequests();
		}
	}

	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		super.receive(m,  c);
		
		// Initiator sends Done upon receiving Notify
		if (m instanceof NotifyMessage) {
			send(new DoneMessage(), getIncomingToOutgoing(c));
		}
		
		if (getOutRequests().size() == 0 && (!decisionMade)) {
			print(String.valueOf(isNotDeadlocked()));
			decisionMade = true;
		}
	}
	
	private boolean isNotDeadlocked () {
		return requests == 0;
	}
	
}

	