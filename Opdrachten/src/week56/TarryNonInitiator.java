package week56;

import framework.Channel;
import framework.Process;
import framework.IllegalReceiveException;
import framework.Message;

public class TarryNonInitiator extends TarryProcess {
	
	private Process parent;

	@Override
	public void init() {
	}

	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		super.receive(m, c);
		if ((m instanceof TokenMessage) && getParent() == null) {
			Process p = c.getSender();
			setParent(p);
			super.init();
			removeSpecificOutgoingChannel(getOutgoingToParent(p));
		}
		
		if (getRandomOutgoingChannels().size() != 0) {
			send(m, getRandomOutgoingChannels().get(0));
			removeNextOutgoingChannel();
		}	
		else if (isActive()) {
			send(m, getOutgoingToParent(getParent()));
			done();
		}
	}
	
	public Process getParent() {
		return parent;
	}
	
	public void setParent (Process p) {
		this.parent = p;
	}
	
	private Channel getOutgoingToParent (Process p) {
		Channel channelToParent = null; 
		for (Channel c: getOutgoing()) {
			if (c.getReceiver() == p) {
				channelToParent = c;
			}
		}
		return channelToParent;
	}
}
