package nl.rug.ds.bpm.verification.modelOptimizers.stutterOptimizer;

import nl.rug.ds.bpm.verification.comparators.ListComparator;
import nl.rug.ds.bpm.verification.models.kripke.State;

import java.util.*;

/**
 * Created by Heerko Groefsema on 09-Mar-17.
 */
public class Block {
	private boolean flag;
	private Set<State> bottom, nonbottom, entry;

	public Block() {
		flag = false;
		bottom = new TreeSet<>(new ListComparator());
		nonbottom = new TreeSet<>(new ListComparator());
		entry = new TreeSet<>(new ListComparator());
	}

	public Block(Set<State> bottom, Set<State> nonbottom) {
		flag = false;
		this.bottom = bottom;
		this.nonbottom = nonbottom;
		entry = new TreeSet<>(new ListComparator());
	}

	public Block split() {
		Set<State> bot = new TreeSet<>(new ListComparator());
		Set<State> nonbot = new TreeSet<>(new ListComparator());

		for(State b: bottom)
			if(!b.getFlag())
				bot.add(b);

		//if flag down and next in bot or nonbot, add to nonbot
		//BSF added, so iterate back to front
		Iterator<State> iterator = ((TreeSet)nonbottom).descendingIterator();
		while (iterator.hasNext()) {
			State nb = iterator.next();
			if (!nb.getFlag()) {
				boolean isB2 = true;
				Iterator<State> next = nb.getNextStates().iterator();
				while (next.hasNext() && isB2) {
					State n = next.next();
					isB2 = bot.contains(n) || nonbot.contains(n);
				}

				if (isB2)
					nonbot.add(nb);
			}
		}

		//split lists
		bottom.removeAll(bot);
		nonbottom.removeAll(nonbot);

		//keep only B1 entries
		entry.clear();
		for(State s: nonbottom)
			for(State previous: s.getPreviousStates())
				if(previous.getBlock() != this)
					entry.add(previous);
		for(State s: bottom)
			for(State previous: s.getPreviousStates())
				if(previous.getBlock() != this)
					entry.add(previous);

		//nonbot was filled in reverse
		nonbot = ((TreeSet)nonbot).descendingSet();

		//make B2
		Block block = new Block(bot, nonbot);

		for(State state: bot)
			state.setBlock(block);
		for(State state: nonbot)
			state.setBlock(block);

		return block;
	}

	public void merge(Block b) {
		for(State s: b.getNonbottom())
			s.setBlock(this);

		for(State s: b.getBottom())
			s.setBlock(this);

		nonbottom.addAll(b.getNonbottom());
		bottom.addAll(b.getBottom());
		entry.addAll(b.getEntry());

		flag = flag && b.getFlag();
		b = null;
	}

	public void init() {
		for(State s: nonbottom) {
			boolean isBottom = true;

			Iterator<State> i = s.getNextStates().iterator();
			while (i.hasNext() && isBottom) {
				State state = i.next();
				if(nonbottom.contains(state))
					isBottom = false;
			}

			for(State previous: s.getPreviousStates())
				if(previous.getBlock() != this)
					entry.add(previous);

			if(isBottom)
				bottom.add(s);
		}

		nonbottom.removeAll(bottom);
	}

	public boolean reinit() {
		List<State> newBottom = new ArrayList<>();
		entry.clear();

		for(State s: nonbottom) {
			boolean isBottom = true;

			Iterator<State> i = s.getNextStates().iterator();
			while (i.hasNext() && isBottom) {
				State state = i.next();
				if(nonbottom.contains(state) || bottom.contains(state))
					isBottom = false;
			}

			for(State previous: s.getPreviousStates())
				if(previous.getBlock() != this)
					entry.add(previous);

			if(isBottom)
				newBottom.add(s);
		}

		for(State s: bottom)
			for(State previous: s.getPreviousStates())
				if(previous.getBlock() != this)
					entry.add(previous);

		bottom.addAll(newBottom);
		nonbottom.removeAll(newBottom);

		//return true if new bottom states were found
		return !newBottom.isEmpty();
	}

	public Set<State> getBottom() { return bottom;	}

	public Set<State> getNonbottom() { return nonbottom; }

	public Set<State> getEntry() { return entry; }

	public void addState(State state) {
		nonbottom.add(state);
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public boolean getFlag() {
		return flag;
	}

	public int size() { return nonbottom.size() + bottom.size(); }

	public String toString() {
		StringBuilder sb = new StringBuilder("{");

		Iterator<State> bi = bottom.iterator();
		while(bi.hasNext()) {
			sb.append(bi.next().toFriendlyString());
			if (bi.hasNext())
				sb.append(", ");
		}

		Iterator<State> nbi = nonbottom.iterator();
		if(nbi.hasNext())
			sb.append(" | ");
		while(nbi.hasNext()) {
			sb.append(nbi.next().toFriendlyString());
			if (nbi.hasNext())
				sb.append(", ");
		}

		sb.append("}");
		return sb.toString();
	}
}
