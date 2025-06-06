package week56;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;


public abstract class RingProcess extends WaveProcess {

	@Override
	public void init() {
		
	}

	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		// invalid message
		if (!(m instanceof TokenMessage)) {
			throw new IllegalReceiveException();
		}
		// duplicate control message
		else if (isPassive()) {
			throw new IllegalReceiveException();
		}
	}
}
