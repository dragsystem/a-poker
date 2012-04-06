package server;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Vector;

import android.text.format.Time;

import logic.CommunityCards;
import logic.Deck;
import logic.Player;
import logic.PokerHandStrength;

public class Table {

	public Deck deck;
	CommunityCards communitycards;

	State state;

	HashMap<Integer, Seat> seats;
	int dealer, sb, bb;
	int currentPlayer;
	int lastBetPlayer;

	BettingRound betround;
	
	int bet_amount;
	int last_bet_amount;
	Vector<Pot> pots;
	
	boolean nomoreaction;
	
	Time delay_start;
	int delay;

	Time timeout_start;

	

	public Table(){

	}

	public int getNextPlayer(int i){

		int start, cur;
		start = i;
		cur = i;
		boolean found = false;

		while (!found)
		{
			cur++;

			if(cur >= 5) //We have done the full turn
				cur = 0;

			if(cur == start)
				return -1;

			else if(seats.get(cur).occupied)
				found = true;
		}
		return cur;
	}

	public int getNextActivePlayer(int pos){
		int start, cur;
		start = pos;
		cur = pos;
		boolean found = false;

		do{
			cur++;
			if(cur>=5)
				cur = 0;

			if(start==cur){
				return -1;
			}

			if(seats.get(cur).in_round)
				found = true;
		} while (!found);

		return cur;
	}

	public int countPlayers(){

		int count = 0;

		for(int i=0; i<5; i++){
			if(seats.get(i).occupied)
				count++;
		}

		return count;
	}

	public int countActivePlayers(){

		int count = 0;

		for(int i=0; i<5; i++){
			if(seats.get(i).occupied && seats.get(i).in_round)
				count++;
		}

		return count;
	}

	public boolean isAllin(){

		//All (or except one) players are allin
		int count, active_players;
		count = 0;
		active_players = 0;

		for (int i=0; i<5; i++){
			if(seats.get(i).occupied && seats.get(i).in_round){
				active_players++;

				Player p = seats.get(i).player;

				if(p.getStake() == 0){
					count++;
				}
			}
		}

		return count >= active_players;
	}

	public void resetLastPlayerActions(){
		//Reset last player action
		for(int i=0; i<5; i++)
		{
			if(!seats.get(i).occupied)
				continue;

			seats.get(i).player.resetLastAction();
		}

	}

	public void collectBets(){

		do{
			// find smallest bet
			int smallest_bet = 0;
			boolean need_sidepot = false;

			for(int i=0; i<5; i++){
				//Skip folded and already handled players
				if(!seats.get(i).occupied || !seats.get(i).in_round || seats.get(i).bet == 0)
					continue;
				if(smallest_bet==0) //Set and initial value
					smallest_bet = seats.get(i).bet;
				else if (seats.get(i).bet < smallest_bet){ //New smallest bet
					smallest_bet = seats.get(i).bet;
					need_sidepot = true;
				}
				else if (seats.get(i).bet > smallest_bet) //Bets are not equal
					need_sidepot = true; // So there must be a smallest bet
			}

			//There are no bets, do nothing
			if(smallest_bet == 0)
				return;

			//Last pot is current pot
			Pot cur_pot = pots.lastElement();

			//If current pot is final, create a new one
			if(cur_pot.isFinal){
				Pot pot = new Pot();
				pot.amount=0;
				pot.isFinal=false;
				pots.add(pot);

				cur_pot = pots.lastElement();
			}

			//Collect the bet of each player
			for(int i=0; i<5; i++){
				//Skip invalid seats
				if(!seats.get(i).occupied)
					continue;
				//Skip already handled players
				if(seats.get(i).bet==0)
					continue;

				//Collect bet of folded players and skip them
				if(!seats.get(i).in_round){
					cur_pot.amount += seats.get(i).bet;
					seats.get(i).bet = 0;
					continue;
				}

				//Collect the bet into pot
				if(!need_sidepot){
					cur_pot.amount += seats.get(i).bet;
					seats.get(i).bet=0;
				}
				else{
					cur_pot.amount += smallest_bet;
					seats.get(i).bet -= smallest_bet;
				}

				//Mark pot as final if at least one player is allin
				Player p = seats.get(i).player;
				if(p.getStake() == 0)
					cur_pot.isFinal = true;

				//Set player 'involved in pot'
				if(!isSeatInvolvedInPot(cur_pot, i))
					cur_pot.vsteats.add(i);					
			}

			if(!need_sidepot) //All players bets are the same, end here
				break;

		} while(true);
	}

	public boolean isSeatInvolvedInPot(Pot pot, int s){

		for (int i=0; i<pot.vsteats.size(); i++){
			if(pot.vsteats.get(i)==s)
				return true;
		}
		return false;
	}

	public int getInvolverInPotCount(Pot pot, Vector<PokerHandStrength> wl){

		int involved_count = 0;

		for(int i=0; i<pot.vsteats.size(); i++){
			int s = pot.vsteats.get(i);

			//FIXME
			for(int j=0; j<wl.size(); j++){
				//if(wl.get(j).getId() == s)
				//involved_count++;
			}
		}

		return involved_count;
	}

	public void scheduleState(State schedState, int delay_sec){

		state = schedState;
		delay = delay_sec;
		delay_start = null; //FIXME

	}

	public void tick(){

	}

	public enum State {
		GameStart,
		ElectDealer,
		NewRound,
		Blinds,
		Betting,
		BettingEnd, //pseudo-state
		AskShow,
		AllFolded,
		Showdown,
		EndRound;
	}

	public enum BettingRound{
		Preflop,
		Flop,
		Turn,
		River;
	}

	public class Seat {
		public boolean occupied;
		public int seat_no;
		public Player player; //FIXME Int apuntando al player en vez de una copia
		public int bet;
		public boolean in_round; //is player involved in current hand?
		public boolean showcards; //does the player want to show cards?
	}

	public class Pot {
		public int amount;
		public Vector<Integer> vsteats;
		public boolean isFinal;
	}

}