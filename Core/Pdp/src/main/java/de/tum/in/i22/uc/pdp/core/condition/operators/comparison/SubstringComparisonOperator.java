package de.tum.in.i22.uc.pdp.core.condition.operators.comparison;

public class SubstringComparisonOperator extends GenericComparisonOperator {
	@Override
	public boolean compare(String parameter1, String parameter2) {
		return (parameter1 != null) && (parameter2 != null) && (parameter1.indexOf(parameter2) != -1);
	}
}