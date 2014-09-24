package de.tum.in.i22.uc.pdp.core.operators.comparison;

public class EqualsComparisonOperator extends GenericComparisonOperator {
	@Override
	public boolean compare(String parameter1, String parameter2) {
		return (parameter1 != null) && (parameter1.equals(parameter2));
	}
}