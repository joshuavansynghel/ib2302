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
		for (Channel c: getRandomOutgoingChannels()) {
			send(new InfoMessage(), c);
		}
		if (getIncomingInfoFromProcesses().size() == getRandomOutgoingChannels().size()) {
			send(new TokenMessage(), futureChild);
		}
	}

	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		super.receive(m, c);
		// if all tokens received, finish algorithm
		if (getIncomingInfoFromProcesses().size() == getOutgoing().size()) {
			done();
		}
	}
}
