package week56;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;

public class DepthFirstSearchInitiator extends DepthFirstSearchProcess {
	
	private int tokensReceived = 0;

	@Override
	public void init() {
		super.init();
		send(new TokenMessage(), getRandomOutgoingChannels().get(0));
		removeNextOutgoingChannel();
	}

	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		super.receive(m, c);
		if (m instanceof TokenMessage) {
			incrementTokensReceived();
		}
		
		// if all tokens received, finish algorithm
		if (getTokensReceived() == getOutgoing().size()) {
			done();
		}
		// if rule 1 and 2 allow it, send message back to sender
		else if ((getRandomOutgoingChannels().size() != 0) &&
				(getRandomOutgoingChannels().contains(getReversedChannel(c)))){
			send(m, getReversedChannel(c));
			removeSpecificOutgoingChannel(getReversedChannel(c));
			}
		else {
			send(m, getRandomOutgoingChannels().get(0));
			removeNextOutgoingChannel();;
		}
		
	}
	
	public int getTokensReceived() {
		return tokensReceived;
	}
	
	public void incrementTokensReceived() {
		tokensReceived += 1;
	}
}
