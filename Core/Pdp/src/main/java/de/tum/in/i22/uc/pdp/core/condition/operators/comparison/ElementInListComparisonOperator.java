package de.tum.in.i22.uc.pdp.core.condition.operators.comparison;

import java.util.StringTokenizer;

public class ElementInListComparisonOperator extends GenericComparisonOperator {
	@Override
	public boolean compare(String parameter1, String parameter2) {
		if (parameter1 == null)
			return false;
		StringTokenizer st = new StringTokenizer(parameter1);
		while (st.hasMoreTokens()) {
			if (st.nextToken().equals(parameter2))
				return true;
		}
		return false;
	}
}