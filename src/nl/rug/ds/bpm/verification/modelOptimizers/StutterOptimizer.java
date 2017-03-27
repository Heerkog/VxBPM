package nl.rug.ds.bpm.verification.modelOptimizers;

import nl.rug.ds.bpm.verification.models.kripke.Kripke;
import nl.rug.ds.bpm.verification.models.kripke.State;

import java.util.*;

/**
 * Created by Heerko Groefsema on 06-Mar-17.
 */
public class StutterOptimizer {
	private Kripke kripke;
	List<Block> toBeProcessed, stable, BL;
	
	public StutterOptimizer(Kripke kripke) {
		this.kripke = kripke;
		
		toBeProcessed = new ArrayList<>();
		stable = new ArrayList<>();
		BL = new ArrayList<>();
		
		preProcess();
	}
	
	public int optimize() {
		while(!toBeProcessed.isEmpty()) {
			Block bAccent = toBeProcessed.get(0);
			// Scan incoming relations
			for(State entryState: bAccent.getEntry()) {
				// Take start state and raise its flag
				entryState.setFlag(true);
				// Test state's block flag, raise and add to BL if not raised
				if(!entryState.getBlock().getFlag()) {
					BL.add(entryState.getBlock());
					entryState.getBlock().setFlag(true);
				}
			}
			
			// Scan BL
			for(Block b: BL) {
				boolean isSplitter = false;
				Iterator<State> i = b.getBottom().iterator();
				while (i.hasNext() && !isSplitter)
					isSplitter = !i.next().getFlag();
				
				if(isSplitter) {
					toBeProcessed.remove(b);
					stable.remove(b);
					
					Block b2 = b.split();
					//if additional bottom states are created, clear stable
					if(b2.reinit()) {
						toBeProcessed.addAll(stable);
						stable.clear();
					}
				}
			}
			BL.clear();
			
			//reset flags
			for(State entryState: bAccent.getEntry()) {
				entryState.setFlag(false);
				entryState.getBlock().setFlag(false);
			}
			
			//move to stable
			stable.add(bAccent);
			toBeProcessed.remove(bAccent);
		}
		
		int stutterBlocks = 0;
		//merge blocks with size > 1
		for(Block b: stable) {
			if(b.size() > 1) {
				stutterBlocks++;
				
				Iterator<State> i = b.getBottom().iterator();
				State s = i.next();
				Set<State> previous = new HashSet<State>(s.getPreviousStates());
				Set<State> next = new HashSet<State>(s.getNextStates());
				while (i.hasNext()) {
					State n = i.next();
					previous.addAll(n.getPreviousStates());
					
				}
					
			}
		}
		
		return stutterBlocks;
	}
	
	private void preProcess() {
		for(State s: kripke.getInitial()) {
			Block b = new Block();
			b.addState(s);
			s.setBlock(b);
			toBeProcessed.add(b);
			
			preProcessBSF(s);
		}
		
		for(Block b: toBeProcessed)
			b.init();
	}
	
	private void preProcessBSF(State s) {
		for(State next: s.getNextStates()) {
			if(s.APequals(next)) {
				if(next.getBlock() == null) {
					s.getBlock().addState(next);
					next.setBlock(s.getBlock());
					
					preProcessBSF(next);
				}
				else {
					Block merge = next.getBlock();
					toBeProcessed.remove(merge);
					s.getBlock().merge(merge);
				}
			}
			else {
				if(next.getBlock() == null) {
					Block b = new Block();
					b.addState(next);
					next.setBlock(b);
					toBeProcessed.add(b);
					
					preProcessBSF(next);
				}
				//else already preprocessed correctly
			}
		}
	}
	
	public String toString(boolean fullOutput) {
		StringBuilder sb = new StringBuilder();
		if (fullOutput) {
			sb.append("\nproposition Optimized Transitions:\n");
			for (State s : stutterOptimizedStates)
				sb.append(s.toFriendlyString() + "\n");
			
		}
		
		sb.append("Number of stutter Optimized States: " + stutterOptimizedStates.size() + "\n");
		
		return sb.toString();
	}
	
	public String toString() {
		return toString(true);
	}
	
}
