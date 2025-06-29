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
		
		// non-initiator should not receive acks if it hasn't received token from parent
		if (m instanceof AckMessage && getParent() == null) {
			throw new IllegalReceiveException();
		}
		
		// logic for token message
		if (m instanceof TokenMessage) {
			
			// if first time receiving token, set parent
			if (getParent() == null) {
				Process p = c.getSender();
				setParent(p);
				removeChannelThatNeedToSendAck(c);
				removeSpecificOutgoingChannel(getIncomingToOutgoing(c));
				
				// if channel only has 1 incoming channel which is the parent, send token back
				if (getChannelsThatNeedToSendAck().isEmpty()) {
					send(m, getOutgoingToParent(getParent()));
					removeSpecificOutgoingChannel(getIncomingToOutgoing(c));
					done();
				}
				// if all channels have already received token
				// send info to all channels with exception of parent
				else if (getRandomOutgoingChannels().isEmpty()) {
					for (Channel cAck : getReversedChannels(getChannelsThatNeedToSendAck())) {
						System.out.println("Send info to " + cAck);
						send(new InfoMessage(), cAck);
					}
				}
				else {
					// set first future child and remove from info channels
					setChannelToFutureChild(getRandomOutgoingChannels().get(0));
					removeChannelThatNeedToSendAck(getOutgoingToIncoming(getChannelToFutureChild()));
					
					// if only 2 channels remain, parent and child, forward token immediately
					if (getChannelsThatNeedToSendAck().isEmpty()) {
						send(m, getChannelToFutureChild());
					}
					else {
						// send info message to each remaining info channel
						for (Channel cAck : getReversedChannels(getChannelsThatNeedToSendAck())) {
							System.out.println("Send info to " + cAck);
							send(new InfoMessage(), cAck);
						}
					}
				}
			}
			// if token was received back from a child
			else if (getIncomingToOutgoing(c) == getChannelToFutureChild()) {
				removeSpecificOutgoingChannel(getIncomingToOutgoing(c));
				
				// if only parent remains as outgoing channel, return token to parent
				if (getRandomOutgoingChannels().isEmpty()) {
					send(m, getOutgoingToParent(getParent()));
					removeSpecificOutgoingChannel(getIncomingToOutgoing(c));
					done();
				}
				else {
					// set next future child and remove from info channels
					setChannelToFutureChild(getRandomOutgoingChannels().get(0));
					removeChannelThatNeedToSendAck(getOutgoingToIncoming(getChannelToFutureChild()));
					
					send(new TokenMessage(), getChannelToFutureChild());
				}
			}
		}
			
		if (m instanceof AckMessage && getChannelsThatNeedToSendAck().isEmpty()) {
			// if only parent remains as outgoing channel, return token to parent
			if (getRandomOutgoingChannels().isEmpty()) {
				send(m, getOutgoingToParent(getParent()));
				removeSpecificOutgoingChannel(getIncomingToOutgoing(c));
				done();
			}
			else {
				// set first child and remove from info channels
				//setChannelToFutureChild(getRandomOutgoingChannels().get(0));
				System.out.println("Token send to child: " + getChannelToFutureChild());
				send(new TokenMessage(), getChannelToFutureChild());
			}
		}
			/**
			//done();
			if (getChannelToFutureChild() == null) {
				send(new TokenMessage(), getOutgoingToParent(getParent()));
			}
			else {
				System.out.println("Send Token to child: " + getChannelToFutureChild());
				send(new TokenMessage(), getChannelToFutureChild());
			}
			**/
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
