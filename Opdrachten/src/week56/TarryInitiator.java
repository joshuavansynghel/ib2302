package week56;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;

public class TarryInitiator extends TarryProcess {
	
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
		
		if (getTokensReceived() == getOutgoing().size()) {
			done();
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
