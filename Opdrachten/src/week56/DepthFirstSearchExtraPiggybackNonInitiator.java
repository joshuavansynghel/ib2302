package week56;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;
import framework.Process;

public class DepthFirstSearchExtraPiggybackNonInitiator extends DepthFirstSearchExtraPiggybackProcess {

	private Process parent;
	
	@Override
	public void init() {
	}

	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		super.receive(m, c);
		if ((m instanceof TokenWithIdsMessage) && getParent() == null) {
			Process p = c.getSender();
			setParent(p);
			super.init();
			removeSpecificOutgoingChannel(getOutgoingToParent(p));
		}
		
		if (m instanceof TokenWithIdsMessage) {
			removeVisitedProcesses((TokenWithIdsMessage) m);
		}
		
		if (getRandomOutgoingChannels().size() != 0) {
			((TokenWithIdsMessage)m).addId(getName());;
			send(m, getRandomOutgoingChannels().get(0));
			removeNextOutgoingChannel();
		}
		// if all messages are sent to outgoing channels, send back to parent
		else if (isActive()) {
			((TokenWithIdsMessage)m).addId(getName());;
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
