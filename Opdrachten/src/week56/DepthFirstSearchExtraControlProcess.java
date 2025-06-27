package week56;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import framework.Channel;
import framework.Process;
import framework.IllegalReceiveException;
import framework.Message;

public abstract class DepthFirstSearchExtraControlProcess extends WaveProcess {

	private List<Channel> randomOutgoingChannels;
	private List<Channel> channelsThatNeedToSendAck;

	@Override
	public void init() {
		randomizeOutgoingChannels();
		initChannelsThatNeedToSendAck();
	}

	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		System.out.println("\nMessage: " + m + " received from channel " + c);
		
		// invalid message
		if (!((m instanceof TokenMessage) || (m instanceof InfoMessage) ||
				(m instanceof AckMessage))) {
			throw new IllegalReceiveException();
		}
		// if process is done, it should not receive a token
		else if ((m instanceof TokenMessage) && isPassive()) {
			throw new IllegalReceiveException();
		}
		// incoming channel should not have sent ack message
		else if (m instanceof AckMessage && 
				!(getChannelsThatNeedToSendAck().contains(c))) {
			throw new IllegalReceiveException();
		}
		else if (m instanceof AckMessage) {
			
			removeChannelThatNeedToSendAck(c);
		}
		else if (m instanceof InfoMessage) {
			// don't send future token through reversed outgoing channel
			removeSpecificOutgoingChannel(getIncomingToOutgoing(c));
			
			// send ack message in reverse direction
			send(new AckMessage(), getIncomingToOutgoing(c));
		}
		else if (m instanceof TokenMessage) {
			// don't send token to channels who have have sent token themselves
			removeSpecificOutgoingChannel(getIncomingToOutgoing(c));
		}
	}
	
	protected void randomizeOutgoingChannels () {
		List<Channel> outgoingChannels = new ArrayList<>(getOutgoing());
		Collections.shuffle(outgoingChannels);
		randomOutgoingChannels = outgoingChannels;
	}
	
	protected List<Channel> getRandomOutgoingChannels () {
		return randomOutgoingChannels;
	}
	
	protected void removeNextOutgoingChannel () {
		randomOutgoingChannels.remove(0);
	}
	
	protected void removeSpecificOutgoingChannel (Channel c) {
		randomOutgoingChannels.remove(c);
	}
	
	private void initChannelsThatNeedToSendAck () {
		channelsThatNeedToSendAck = new ArrayList<>(getIncoming());
	}
	
	protected List<Channel> getChannelsThatNeedToSendAck () {
		return channelsThatNeedToSendAck;
	}
	
	protected void removeChannelThatNeedToSendAck (Channel c) {
		channelsThatNeedToSendAck.remove(c);
	}
	
	protected List<Channel> getReversedChannels(List<Channel> channels) {
		List<Channel> reversedChannels = new ArrayList<>();
		for (Channel c : channels) {
			reversedChannels.add(getIncomingToOutgoing(c));
		}
		return reversedChannels;
	}
	
	protected Channel getIncomingToOutgoing (Channel c) {
		Channel reversedChannel = null;
		for (Channel cOut: getOutgoing()) {
			if (cOut.getReceiver() == c.getSender()) {
				reversedChannel = cOut;
			}
		}
		return reversedChannel;
	}
	
	protected Channel getOutgoingToIncoming (Channel c) {
		Channel reversedChannel = null;
		for (Channel cIn: getIncoming()) {
			if (cIn.getReceiver() == c.getSender()) {
				reversedChannel = cIn;
			}
		}
		return reversedChannel;
	}
}
