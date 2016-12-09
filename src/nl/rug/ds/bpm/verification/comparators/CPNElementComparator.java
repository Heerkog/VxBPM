package nl.rug.ds.bpm.verification.comparators;

import nl.rug.ds.bpm.verification.models.cpn.CPNElement;

import java.util.Comparator;

public class CPNElementComparator implements Comparator<CPNElement>
{
	@Override
	public int compare(CPNElement a, CPNElement b)
	{
		return a.compareTo(b);
	}
}
