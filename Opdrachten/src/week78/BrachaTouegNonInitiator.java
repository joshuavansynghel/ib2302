package week78;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;

public class BrachaTouegNonInitiator extends BrachaTouegProcess {
	
	private Channel parent;
	public boolean grantSent = false;

	@Override
	public void init() {
		// TODO
	}

	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		super.receive(m, c);
		
		System.out.println("Parent: " + getParent());
		if (m instanceof NotifyMessage) {
			processNotifyMessage(c);
		}
		else if (m instanceof AckMessage) {
			
			// if all inRequests are finished, send done to parent
//			if (getInRequests().size() == 0 && getParent() != null) {
//				System.out.println("Send done to " + getIncomingToOutgoing(getParent()));
//				
//				send(new DoneMessage(), getIncomingToOutgoing(getParent()));
//			}
		}
		else if (m instanceof DoneMessage) {
			// If all done messages received, send Done to parent
			if (getOutRequests().size() == 0) {
				System.out.println("Send done to " + getIncomingToOutgoing(getParent()));
				send(new DoneMessage(), getIncomingToOutgoing(getParent()));
			}
		}
	}
	
	
	private void setParent(Channel c) {
		this.parent = c;
	}
	
	private Channel getParent() {
		return this.parent;
	}


	
	
	private void processNotifyMessage(Channel c) {
		// Set Parent on first notify message
		if (getParent() == null) {
			setParent(c);
			sendNotifyToOutRequests();
		}
		// If already received notify message, send Done back
		else {
			System.out.println("Send done to " + getIncomingToOutgoing(c));
			send(new DoneMessage(), getIncomingToOutgoing(c));
		}
		
		// Send acks if no pending requests
		if ((super.requests == 0) && (!grantSent)) {
			sendGrantToInRequests();
			grantSent = true;
		}
	}
	
	// Override method to avoid sending to parent
	protected void sendNotifyToOutRequests() {
		for (Channel c: getOutRequests()) {
			System.out.println("Send Notify to " + c);
			send(new NotifyMessage(), c);
		}
	}
}
