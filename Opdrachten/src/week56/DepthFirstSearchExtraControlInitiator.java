package week56;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;

public class DepthFirstSearchExtraControlInitiator extends DepthFirstSearchExtraControlProcess {

	@Override
	public void init() {
		super.init();
		setChannelToFutureChild(getRandomOutgoingChannels().get(0));
		//removeNextOutgoingChannel();
		removeChannelThatNeedToSendAck(getOutgoingToIncoming(getChannelToFutureChild()));
		
		System.out.println("info channels: " + getReversedChannels(getChannelsThatNeedToSendAck()));
		
		// send info message to all outgoing channels except future child
		for (Channel c: getReversedChannels(getChannelsThatNeedToSendAck())) {
			send(new InfoMessage(), c);
		}
		
		// in case no outgoing channels remain, send token message
		if (getChannelsThatNeedToSendAck().isEmpty()) {
			send(new TokenMessage(), getChannelToFutureChild());
		}
	}

	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		super.receive(m, c);
		
		if (m instanceof TokenMessage) {
			removeSpecificOutgoingChannel(getIncomingToOutgoing(c));
		}

		// if token received an all channels have sent info or token, finish
		if ((m instanceof TokenMessage) && getRandomOutgoingChannels().isEmpty()) {
			done();
		}
		// if process still holds token and all acks are received
		// forward token to next process
		else if (m instanceof AckMessage && getChannelsThatNeedToSendAck().isEmpty()) {
			send (new TokenMessage(), getChannelToFutureChild());
			removeNextOutgoingChannel();
		}
		// if token was received from child and not all channels have received token
		// forward token to next process
		else if (m instanceof TokenMessage) {
			setChannelToFutureChild(getRandomOutgoingChannels().get(0));
			send (new TokenMessage(), getChannelToFutureChild());
			removeNextOutgoingChannel();
		}
	}
}
