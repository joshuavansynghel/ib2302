package week34;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;

public class ChandyLamportInitiator extends ChandyLamportProcess {

	@Override
	public void init() {
		for (Channel c: super.getOutgoing()) {
			send(new ChandyLamportControlMessage(), c);
		}
		super.startSnapshot();
	}
	
	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		super.receive(m, c);
		if (m instanceof ChandyLamportControlMessage) {
			super.record(c, getChannelState(c));
		}
		super.startSnapshot();
	}
}
