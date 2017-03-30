package nl.rug.ds.bpm.verification.comparators;

import nl.rug.ds.bpm.verification.models.kripke.State;

import java.util.Comparator;

/**
 * Created by p256867 on 30-3-2017.
 */
public class ListComparator implements Comparator<State>
{
    @Override
    public int compare(State a, State b) { return 1; }
}