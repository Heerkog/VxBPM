package nl.rug.ds.bpm.verification.modelConverters;

import nl.rug.ds.bpm.verification.comparators.StringComparator;
import nl.rug.ds.bpm.verification.models.cpn.BindingElement;
import nl.rug.ds.bpm.verification.models.cpn.CPN;
import nl.rug.ds.bpm.verification.models.cpn.Transition;
import nl.rug.ds.bpm.verification.models.kripke.Kripke;
import nl.rug.ds.bpm.verification.models.kripke.State;

import java.util.*;

public class CPN2KripkeConverter {
    private CPN cpn;
    private Kripke kripke;


    public CPN2KripkeConverter(CPN cpn) {

        this.cpn = cpn;
    }


    public Kripke convert() {
        State.resetStateId();
        int[] m = cpn.getInitialMarking();
        kripke = new

                Kripke();

        List<List<Transition>> enabled = getTransitionSets(m);
        for (List<Transition> e : enabled) {
//			List<Transition> e = getEnabledTransitions(kripkeModel);    //test
            TreeSet<String> ap = new TreeSet<String>(new StringComparator());
            for (Transition t : e) {
                ap.add(t.getId());

                ap.addAll(t.getParentIds());
                kripke.addAtomicPropositions(t.getParentIds());
                kripke.addAtomicProposition(t.getId());


            }
            State s2 = new State(getMarkingString(m), ap);
            kripke.addState(s2);
            kripke.addInitial(s2);

            for (Transition t : e)
                for (int[] mn : t.fire(m))
                    convertMarking(mn, s2);
        }

        return kripke;
    }


    public void convertMarking(int[] m, State s) {
        List<List<Transition>> enabled = getTransitionSets(m);

        for (List<Transition> e : enabled) {
//			List<Transition> e = getEnabledTransitions(kripkeModel);    //test
            TreeSet<String> ap = new TreeSet<String>(new StringComparator());
            for (Transition t : e) {
                ap.add(t.getId());
                ap.addAll(t.getParentIds());
                kripke.addAtomicPropositions(t.getParentIds());
                kripke.addAtomicProposition(t.getId());
            }
            State s2 = new State(getMarkingString(m), ap);

            State s3 = kripke.getStates().ceiling(s2);
            if (s2.equals(s3))
                s2 = s3;
            else
                kripke.addState(s2);

            s.addNext(s2);
            s2.addPrevious(s);

            if (s2 != s3) {
//				System.out.println("Found state #" + kripke.getStateCount());

                if (e.isEmpty()) {
                    s2.addNext(s2);
                    s2.addPrevious(s2);
                }
                for (Transition t : e) {
                    int[] mc = Arrays.copyOf(m, m.length);
                    for (int[] mn : t.fire(mc)) {
                        convertMarking(mn, s2);
                    }
                }
            }
/*			else
            {
				float f = (float)dupeCount / kripke.getStateCount();
				System.out.println("Found duplicate #" + ++dupeCount + "(" + f + ")");
			}
*/
        }
    }

    public int stutterOptimize() {
        List<State> remove = new ArrayList<State>();
        Iterator<State> i = kripke.getStates().iterator();

        while (i.hasNext()) {
            State s = i.next();
            Set<State> duplicates = new HashSet<State>();

            for (State n : s.getNextStates())
                if (s != n)
                    if (s.APequals(n))
                        duplicates.add(n);

            Set<State> previous = new HashSet<State>(s.getPreviousStates());
            Set<State> next = new HashSet<State>(s.getNextStates());

            for (State n : duplicates) {
                previous.addAll(n.getPreviousStates());
                next.addAll(n.getNextStates());
                n.getPreviousStates().clear();
                n.getNextStates().clear();
                //System.out.println("Stutter "+s.toString()+" to "+n.toString());
            }

            if (!duplicates.isEmpty()) {
                previous.removeAll(duplicates);
                previous.remove(s);

                next.removeAll(duplicates);
                next.remove(s);

                s.getPreviousStates().clear();
                s.getNextStates().clear();

                s.getPreviousStates().addAll(previous);
                s.getNextStates().addAll(next);

                remove.addAll(duplicates);

                for (State prev : previous) {
                    prev.getNextStates().removeAll(duplicates);
                    prev.addNext(s);
                }

                for (State x : next) {
                    x.getPreviousStates().removeAll(duplicates);
                    x.addPrevious(s);
                }
            }
        }
        kripke.getStates().removeAll(remove);
        kripke.setStutterOptimizedStates(remove);
        return remove.size();
    }

    public void propositionOptimize(Set<String> AP) {
        kripke.setPropositionOptimized(AP);
        for (State s : kripke.getStates())
            s.removeAP(AP);

        kripke.getAtomicPropositions().removeAll(AP);
    }

    private List<List<Transition>> getTransitionSets(int[] m) {
        ArrayList<List<Transition>> enabledSets = new ArrayList<List<Transition>>();
        List<Transition> enabled = getEnabledTransitions(m);
        int[] allFireMarking = new int[m.length];

        for (Transition t : enabled)
            for (BindingElement p : t.getInPairs())
                allFireMarking[p.getId()] = allFireMarking[p.getId()] + p.getWeight();

        boolean empty = true;
        for (int i = 0; i < m.length; i++) {
            allFireMarking[i] = allFireMarking[i] - m[i];
            empty = empty && allFireMarking[i] <= 0;
        }

        if (empty)
            enabledSets.add(enabled);
        else {
            HashSet<Transition> exclusives = new HashSet<Transition>();
            for (Transition t : enabled)
                for (BindingElement p : t.getInPairs())
                    if (allFireMarking[p.getId()] > 0)
                        exclusives.add(t);

            enabled.removeAll(exclusives);
            for (List<Transition> e : getEnabledSets(new ArrayList<Transition>(exclusives), m)) {
                e.addAll(enabled);
                enabledSets.add(e);
            }
        }

        return enabledSets;
    }

    private List<Transition> getEnabledTransitions(int[] m) {
        ArrayList<Transition> enabled = new ArrayList<Transition>();
        List<Transition> transitions = cpn.getTransitions();
        for (Transition t : transitions) {

            if (t.isEnabled(m))
                enabled.add(t);
        }
        return enabled;
    }

    private List<List<Transition>> getEnabledSets(List<Transition> exclusives, int[] m) {
        ArrayList<List<Transition>> enabledSets = new ArrayList<List<Transition>>();
        ArrayList<Integer> masks = new ArrayList<Integer>();

        for (int bitMask = 0b01; bitMask < Math.pow(2, exclusives.size()); bitMask++) {
            int[] marking = Arrays.copyOf(m, m.length);

            int i = 0;
            boolean enabled = true;
            while (i < exclusives.size() && enabled) {
                if (((bitMask >> i) & 0b1) == 0b1) {
                    enabled = (exclusives.get(i)).isEnabled(marking);
                    if (enabled)
                        (exclusives.get(i)).fireIn(marking); //don't need different outputs of Transition
                }
                i++;
            }
            if (enabled)
                masks.add(new Integer(bitMask));
        }

        for (int i = 0; i < masks.size(); i++) {
            int j = 0;
            boolean subset = false;
            while (j < masks.size() && !subset) {
                if (i != j)
                    subset = ((masks.get(i) & masks.get(j)) == masks.get(i));
                j++;
            }
            if (!subset) {
                ArrayList<Transition> enabledSet = new ArrayList<Transition>();
                for (int k = 0; k < exclusives.size(); k++)
                    if (((masks.get(i) >> k) & 1) == 1)
                        enabledSet.add(exclusives.get(k));

                enabledSets.add(enabledSet);
            }
        }
        return enabledSets;
    }

    private String getMarkingString(int[] m) {
        String s = "";
        for (int i = 0; i < m.length; i++)
            if (m[i] > 0)
                s = s + "+" + m[i] + "p" + i;
        return (s.length() > 0 ? s.substring(1) : "");
    }
}
