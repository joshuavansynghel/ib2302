package week34;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;

public class ChandyLamportNonInitiator extends ChandyLamportProcess {
	
	
	@Override
	public void init() {
	}
	
	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		super.receive(m, c);
		if (!hasStarted() && (m instanceof ChandyLamportControlMessage)) {
			startSnapshot();
			addMessage(m, c);
		}
		for (Channel cReceive: super.getOutgoing()) {
			send(new ChandyLamportControlMessage(), cReceive);
		}
		if (!hasStarted()) {
			super.startSnapshot();
		}
		
	}
}
