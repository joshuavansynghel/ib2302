package week34;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;

public class LaiYangNonInitiator extends LaiYangProcess {

	@Override
	public void init() {
		// TODO
	}
	
	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		super.receive(m,  c);
		if (!hasStarted()) {
			if ((m instanceof LaiYangControlMessage) ||
					((LaiYangBasicMessage)m).getTag()) {
				startSnapshot();
			}
		}
		/**
		if (!hasStarted() && (m instanceof LaiYangControlMessage)) {
			startSnapshot();
			addMessage(m, c);
		}
		for (Channel cReceive: super.getOutgoing()) {
			send(new LaiYangControlMessage(0), cReceive);
		}
		if (!hasStarted()) {
			super.startSnapshot();
		}
		**/
	}
}
