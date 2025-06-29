package week56;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;

public class DepthFirstSearchExtraControlInitiator extends DepthFirstSearchExtraControlProcess {

	@Override
	public void init() {
		super.init();
		Channel futureChild = getRandomOutgoingChannels().get(0);
		removeNextOutgoingChannel();
		removeChannelThatNeedToSendAck(getOutgoingToIncoming(futureChild));
		
		// send info message to all outgoing channels except future child
		for (Channel c: getRandomOutgoingChannels()) {
			send(new InfoMessage(), c);
		}
		
		// in case no outgoing channels remain, send token message
		if (getRandomOutgoingChannels().size() == 0) {
			send(new TokenMessage(), futureChild);
		}
	}

	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		super.receive(m, c);
		
		System.out.println("Ack channels: " + getChannelsThatNeedToSendAck());
		System.out.println("Outgoing Channels: " + getRandomOutgoingChannels());
		
		if (m instanceof TokenMessage) {
			removeSpecificOutgoingChannel(getIncomingToOutgoing(c));
		}

		// if token received an all channels have sent info or token, finish
		if ((m instanceof TokenMessage) && getRandomOutgoingChannels().isEmpty()) {
			done();
		}
		// if process still holds token and all acks are received
		// forward token to next process
		else if (getChannelsThatNeedToSendAck().isEmpty() &&
				(!getRandomOutgoingChannels().isEmpty())) {
			System.out.println("breakpoint");
			send (new TokenMessage(), getRandomOutgoingChannels().get(0));
			removeNextOutgoingChannel();
		}
		// if token was received and not all outgoing processes have received token
		// forward token to next process
		else if (m instanceof TokenMessage) {
			send (new TokenMessage(), getRandomOutgoingChannels().get(0));
			removeNextOutgoingChannel();
		}
	}
}
