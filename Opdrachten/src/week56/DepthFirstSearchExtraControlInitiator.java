package week56;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;

public class DepthFirstSearchExtraControlInitiator extends DepthFirstSearchExtraControlProcess {

	@Override
	public void init() {
		super.init();
		removeNextOutgoingChannel();
		for (Channel c: getRandomOutgoingChannels()) {
			send(new InfoMessage(), c);
		}
		if (incomingAcksFromProcessses.size() == getRandomOutgoingChannels.size()) {
			send(new TokenMessage(), c);
		}
	}

	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		super.receive(m, c);
	}
}
