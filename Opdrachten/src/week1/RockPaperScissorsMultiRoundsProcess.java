package week1;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import framework.Channel;
import framework.IllegalReceiveException;
import framework.Message;
import framework.Process;

public class RockPaperScissorsMultiRoundsProcess extends Process {
	
	private Set<Channel> incomingChannels = null;
	private Map<Channel, Message> buffer = new LinkedHashMap<>();
	
	private int n;
	private boolean win = false;
	private boolean lose = false;
	private boolean stillPlaying = true;
	private Item move;

	@Override
	public void init() {
		// Initialize instance variables
		this.n = getOutgoing().size(); // set number of message to receive
		this.incomingChannels = getIncoming(); // channels who still need to send message
		
		// Send message through all outgoing channels if still playing
		if (this.stillPlaying) {		
			this.move = Item.random(); // a random move
			Message m = new RockPaperScissorsMessage(this.move);
			for (Channel c: getOutgoing()) {
				System.out.println("Send to channel: " + c);
				send(m, c);
			}
		}
	}

	@Override
	public void receive(Message m, Channel c) throws IllegalReceiveException {
		// if message not of correct class, throw error
		if (!(m instanceof RockPaperScissorsMessage)) {
			throw new IllegalReceiveException();
		}
		// if channel has sent move for this and next round, throw error
		if (!this.incomingChannels.contains(c) && buffer.containsKey(c)) {
			throw new IllegalReceiveException();
		}
		// if channel has sent move for this round only, add next message to buffer
		else if (!this.incomingChannels.remove(c)){
			this.buffer.put(c, m);
			return;
		}
		
		// if already fully lost, return same message
		if (!this.stillPlaying) {
			// search for corresponding outgoing channel based on received message
			for (Channel cOut : getOutgoing()) {
				if (cOut.getReceiver() == c.getSender()) {
					send(m, cOut);
				}
			}
			
		}
		
		calculateResult(((RockPaperScissorsMessage) m).getItem());
		
		this.n -= 1;

		// if all message received print result
		if (this.n == 0) {
			endRound();
		}
	}
	
	public void calculateResult(Item moveOpponent) {
		// don't adjust game stats if already lost
		if (!this.stillPlaying) {
			return;
		}
		else if ((this.move).beats(moveOpponent)) {
			this.win = true;
		}
		else if (moveOpponent.beats(this.move)) {
			this.lose = true;
		}
	}
	
	public void endRound() throws IllegalReceiveException{
		if (this.win == this.lose) {
			resetGameStats();
			init();
			emptyBuffer();
		}
		else if (this.lose) {
			print(String.valueOf(this.win));
			this.stillPlaying = false;
			init();
			emptyBuffer();
		}
		else {
			print(String.valueOf(this.win)); 
		}
	}
	
	public void emptyBuffer() throws IllegalReceiveException {
		for (Channel c : this.buffer.keySet()) {
			receive(buffer.get(c), c);
		}
		this.buffer.clear();
	}
	
	public void resetGameStats() {
		this.win = false;
		this.lose = false;
	}
}
