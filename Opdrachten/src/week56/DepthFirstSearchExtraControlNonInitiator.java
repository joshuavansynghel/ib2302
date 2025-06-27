package week56;

import java.util.ArrayList;
import java.util.List;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;
import framework.Process;

public class DepthFirstSearchExtraControlNonInitiator extends DepthFirstSearchExtraControlProcess {

	private Process parent;
	
	@Override
	public void init() {
	}

	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		// initialize outgoing channels if not present yet
		if ((getRandomOutgoingChannels() == null) && (getParent() == null)) {
			super.init();
		}
		
		// call superclass receive
		super.receive(m, c);
		
		// if first time token message, add process as parent
		if ((m instanceof TokenMessage) && getParent() == null) {
			Process p = c.getSender();
			setParent(p);
			removeChannelThatNeedToSendAck(c);
		}
		
		System.out.println("Outgoing Channels: " + getRandomOutgoingChannels());
		
		if (m instanceof TokenMessage) {
			// if no outgoing channels remain, send token back to parent
			if (getRandomOutgoingChannels().isEmpty()) {
				send(m, getOutgoingToParent(getParent()));
				done();
			}
			else {
				Channel futureChild = getRandomOutgoingChannels().get(0);
				removeNextOutgoingChannel();
				removeChannelThatNeedToSendAck(getOutgoingToIncoming(futureChild));
				
				if (getRandomOutgoingChannels().isEmpty()) {
					send(m, futureChild);
					System.out.println("AckChannels: " + getChannelsThatNeedToSendAck());
				}
							
				for (Channel cAck : getReversedChannels(getChannelsThatNeedToSendAck())) {
					send (new InfoMessage(), cAck);
				}
			}
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
