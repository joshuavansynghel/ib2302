package week78;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;

public class BrachaTouegInitiator extends BrachaTouegProcess {

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
			System.out.println("Send Done to " + getIncomingToOutgoing(c));
			send(new DoneMessage(), getIncomingToOutgoing(c));
		}
	
		
		
		if (getOutRequests().size() == 0) {
			System.out.println("Number of requests when out request empty: " + requests);
			print(String.valueOf(isNotDeadlocked()));
		}
	}
	
	private boolean isNotDeadlocked () {
		return requests == 0;
	}
}

	