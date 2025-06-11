package week56;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;
import framework.Process;

public class DepthFirstSearchExtraControlNonInitiator extends DepthFirstSearchExtraControlProcess {

	private Process parent;
	
	@Override
	public void init() {
		// TODO
	}

	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		super.receive(m, c);
	}
	
	public Process getParent() {
		return parent;
	}
	
	public void setParent (Process p) {
		this.parent = p;
	}
	
	private Channel getOutgoingToParent (Process p) {
		Channel channelToParent = null; 
		for (Channel c: getOutgoing()) {
			if (c.getReceiver() == p) {
				channelToParent = c;
			}
		}
		return channelToParent;
	}
}
