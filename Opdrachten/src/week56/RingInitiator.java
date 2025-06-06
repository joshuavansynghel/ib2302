package week56;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;


public class RingInitiator extends RingProcess {

	@Override
	public void init() {
		for (Channel c: getOutgoing()) {
			send(new TokenMessage(), c);
		}
	}

	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		super.receive(m,  c);
		done();
	}
}
