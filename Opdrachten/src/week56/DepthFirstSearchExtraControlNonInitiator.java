package week56;

import java.util.ArrayList;
import java.util.List;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;
import framework.Process;

public class DepthFirstSearchExtraControlNonInitiator extends DepthFirstSearchExtraControlProcess {

	private Process parent;
	private Channel channelToFutureChild; 
	
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
		
		if (m instanceof TokenMessage) {
			
			// if first time receiving token, set parent
			if (getParent() == null) {
				Process p = c.getSender();
				setParent(p);
				removeChannelThatNeedToSendAck(c);
			}
			
			
			// if no outgoing channels left, forward to parent
			if (getRandomOutgoingChannels().isEmpty() &&
					getChannelsThatNeedToSendAck().isEmpty()) {
				send(m, getOutgoingToParent(getParent()));
				done();
			}
			else if (getRandomOutgoingChannels().isEmpty()) {
				for (Channel cAck : getReversedChannels(getChannelsThatNeedToSendAck())) {
					send (new InfoMessage(), cAck);
				}
			}
			else {
				setChannelToFutureChild(getRandomOutgoingChannels().get(0));
				removeNextOutgoingChannel();
				removeChannelThatNeedToSendAck(getOutgoingToIncoming(getChannelToFutureChild()));

				// else send info messages to each all outgoing channels
				// except parent and future child
				for (Channel cAck : getReversedChannels(getChannelsThatNeedToSendAck())) {
					System.out.println("send info to " + cAck);
					send (new InfoMessage(), cAck);
				}
				
				if (getRandomOutgoingChannels().isEmpty()) {
					send(new TokenMessage(), getChannelToFutureChild());
				}
			}
		}
			
		if (m instanceof AckMessage && getChannelsThatNeedToSendAck().isEmpty()) {
			//done();
			if (getChannelToFutureChild() == null) {
				send(new TokenMessage(), getOutgoingToParent(getParent()));
			}
			else {
				System.out.println("Send Token to child: " + getChannelToFutureChild());
				send(new TokenMessage(), getChannelToFutureChild());
			}
		}
	}
	
	public Process getParent() {
		return parent;
	}
	
	public void setParent (Process p) {
		this.parent = p;
	}
	
	public void setChannelToFutureChild (Channel c) {
		channelToFutureChild = c;
	}
	
	public Channel getChannelToFutureChild () {
		return channelToFutureChild;
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
