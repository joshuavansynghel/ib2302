package week56;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;
import framework.Process;

public class DepthFirstSearchNonInitiator extends DepthFirstSearchProcess {
	
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
		
		// if rule 1 and 2 allow it, send message back to sender
		if ((getRandomOutgoingChannels().size() != 0) &&
				(getRandomOutgoingChannels().contains(getReversedChannel(c)))){
			send(m, getReversedChannel(c));
			removeSpecificOutgoingChannel(getReversedChannel(c));
		}
		// else send message to next random outgoing channel
		else if (getRandomOutgoingChannels().size() != 0) {
			send(m, getRandomOutgoingChannels().get(0));
			removeNextOutgoingChannel();
		}
		// if all messages are sent to outgoing channels, send back to parent
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
