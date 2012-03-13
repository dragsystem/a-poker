package logic;

import java.util.Vector;


/**
 * <pre>
 * Ranking          Rank-card(s)           Kicker-card(s)
 * ------------------------------------------------------
 * HighCard         Top card               Remaining 4
 * OnePair          Pair card              Remaining 3
 * TwoPair          1st & 2nd Pair card    Remaining 1
 * ThreeOfAKind     Trips card             Remaining 2
 * Straight         Top card               -
 * Flush            Flush cards            -
 * FullHouse        Trips & Pair card      -
 * FourOfAKind      FourOfAKind card       Remaining 1
 * StraightFlush    Top card               -
 * </pre>
 */

public class PokerHandStrength implements Comparable<PokerHandStrength>, Cloneable
{
	public enum HandRanking
	{
		HIGHCARD, ONEPAIR, TWOPAIR, THREEOFAKIND, STRAIGHT,
		FLUSH, FULLHOUSE, FOUROFAKIND, STRAIGHTFLUSH;

		@Override
		public String toString()
		{
			switch (this)
			{
			case HIGHCARD:
				return "HighCard";
			case ONEPAIR:
				return "OnePair";
			case TWOPAIR:
				return "TwoPair";
			case THREEOFAKIND:
				return "ThreeOfAKind";
			case STRAIGHT:
				return "Straight";
			case FLUSH:
				return "Flush";
			case FULLHOUSE:
				return "FullHouse";
			case FOUROFAKIND:
				return "FourOfAKind";
			case STRAIGHTFLUSH:
				return "StraightFlush";
			default:
				throw new InternalError();
			}
		}
	}
	
	int id;

	HandRanking ranking;

	final Vector<Card> rankCards = new Vector<Card>();
	final Vector<Card> kickerCards = new Vector<Card>();	

	public HandRanking getRanking()
	{
		return this.ranking;
	}

	public void setRanking(HandRanking ranking)
	{
		this.ranking = ranking;
	}

	public Vector<Card> getRankCards()
	{
		Vector<Card> cRank = new Vector<Card>();
		for (Card c : this.rankCards)
			cRank.add(c); //Does this destroy c from rankCards?

		return cRank;
	}

	public Vector<Card> getKickerCards()
	{
		Vector<Card> cKicker = new Vector<Card>();
		for (Card c : this.kickerCards)
			cKicker.add(c); //Does this destroy c from kickerCards?

		return cKicker;
	}



	public int compareTo(PokerHandStrength o)
	{
		int cret = this.ranking.compareTo(o.ranking);

		// both hands have the same ranking, compare
		if (cret == 0)
		{
			// compare rank-cards value
			for (int i = 0; i < this.rankCards.size(); ++i)
			{
				cret = (this.rankCards.get(0).mValue.compareTo(o.rankCards.get(0).mValue));

				if (cret != 0)
					break;
			}

			// hands have the same rank-cards
			if (cret == 0)
			{
				// compare kicker-cards values
				for (int i = 0; i < this.kickerCards.size(); ++i)
				{
					cret = (this.kickerCards.get(0).mValue.compareTo(o.kickerCards.get(0).mValue));

					if (cret != 0)
						break;
				}
			}
		}

		return cret;
	}


	@Override
	public String toString()
	{
		return "ranking=" + this.ranking + " rank=" + this.rankCards + " kicker=" + this.kickerCards;
	}

	public int getId() {
		return id;
	}

}
