package week56;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;
import framework.Process;

public class DepthFirstSearchExtraControlNonInitiator extends DepthFirstSearchExtraControlProcess {

	private Process parent;
	private boolean hasReceivedToken = false;
	
	@Override
	public void init() {
	}

	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		super.receive(m, c);
		if ((m instanceof TokenMessage) && getParent() == null) {
			Process p = c.getSender();
			setParent(p);
			super.init();
			removeSpecificOutgoingChannel(getOutgoingToParent(p));
		}
		
		if (m instanceof TokenMessage) {
			// if first time receiving token, set to  true
			if (!getHasReceivedToken()) {
				setHasReceivedToken();
			}
			
			// add parent to lists to avoid sending info or ack messages
			getIncomingInfoFromProcesses().add(c.getSender());
			getIncomingAcksFromProcesses().add(c.getSender());
			
			// if no outgoing channels remain, send token back to parent
			if (getRandomOutgoingChannels().isEmpty()) {
				send(m, getOutgoingToParent(getParent()));
			}
			// else send info messages to each remaining outgoing channel 
			else {
				Channel futureChild = getRandomOutgoingChannels().get(0);
				removeNextOutgoingChannel();
				resetIncomingAcksProcesses();
				for (Channel cOut: getRandomOutgoingChannels()) {
					send(new InfoMessage(), cOut);
				}
				
				// if no remaining outgoing channels
				// don't wait for ack message but send token forward
				if (getRandomOutgoingChannels().size() == 0) {
					send(new TokenMessage(), futureChild);
				}
			}
		}
			
		// if all incoming channels, have sent info message and this process
		// received token at least once and all acks received, finish algorithm
		if (allIncomingProcessesHaveSentInfo() && 
				allIncomingProcessesHaveSentAck() && 
				getHasReceivedToken()) {
			done();
		}
		else if (allIncomingProcessesHaveSentAck() &&
				(!getRandomOutgoingChannels().isEmpty())) {
			send (new TokenMessage(), getRandomOutgoingChannels().get(0));
			removeNextOutgoingChannel();
		}
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
	
	private void setHasReceivedToken () {
		this.hasReceivedToken = true;
	}
	
	private boolean getHasReceivedToken() {
		return hasReceivedToken;
	}
}
