package week56;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;

public class DepthFirstSearchExtraPiggybackInitiator extends DepthFirstSearchExtraPiggybackProcess {

	@Override
	public void init() {
		super.init();
		send(new TokenWithIdsMessage(getName()), getRandomOutgoingChannels().get(0));
		removeNextOutgoingChannel();
	}

	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		super.receive(m, c);
		if (m instanceof TokenWithIdsMessage) {
			removeVisitedProcesses((TokenWithIdsMessage) m);
		}
		
		// if token contains id of all outgoing channels, finish algorithm
		if (getRandomOutgoingChannels().size() == 0) {
			done();
		}
		// else send token to next channel
		else {
			send(m, getRandomOutgoingChannels().get(0));
			removeNextOutgoingChannel();;
		}
	}
}
