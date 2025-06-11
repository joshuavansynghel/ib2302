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
		InfoMessage infomsg = new InfoMessage();
		for (Channel c: getRandomOutgoingChannels()) {
			send(infomsg, c);
		}
		send(new TokenMessage(), futureChild);
	}

	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		super.receive(m, c);
	}
}
