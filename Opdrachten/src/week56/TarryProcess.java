package week56;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;

public abstract class TarryProcess extends WaveProcess {
	
	private List<Channel> randomOutgoingChannels;

	@Override
	public void init() {
		randomizeOutgoingChannels();
	}

	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		// invalid message
		if (!(m instanceof TokenMessage)) {
			throw new IllegalReceiveException();
		}
		else if ((m instanceof TokenMessage) && isPassive()) {
			throw new IllegalReceiveException();
		}
	}
	
	protected void randomizeOutgoingChannels () {
		List<Channel> outgoingChannels = new ArrayList<>(getOutgoing());
		Collections.shuffle(outgoingChannels);
		randomOutgoingChannels = outgoingChannels;
	}
	
	protected List<Channel> getRandomOutgoingChannels () {
		return randomOutgoingChannels;
	}
	
	protected void removeNextOutgoingChannel () {
		randomOutgoingChannels.remove(0);
	}
	
	protected void removeSpecificOutgoingChannel (Channel c) {
		randomOutgoingChannels.remove(c);
	}
}
