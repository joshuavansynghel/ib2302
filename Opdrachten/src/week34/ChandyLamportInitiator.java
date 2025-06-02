package week34;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;

public class ChandyLamportInitiator extends ChandyLamportProcess {

	@Override
	public void init() {
		for (Channel c: getOutgoing()) {
			send(new ChandyLamportControlMessage(), c);
		}
		startSnapshot();
	}
	
	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		super.receive(m, c);
	}
}
