package week34;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;

public class LaiYangInitiator extends LaiYangProcess {

	@Override
	public void init() {
		for (Channel c: getOutgoing()) {
			send(new LaiYangControlMessage(0), c);
		}
		startSnapshot();
	}
	
	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		super.receive(m, c);
	}
}
