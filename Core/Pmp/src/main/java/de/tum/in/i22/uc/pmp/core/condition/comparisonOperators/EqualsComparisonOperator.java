package de.tum.in.i22.uc.pmp.core.condition.comparisonOperators;


public class EqualsComparisonOperator extends GenericComparisonOperator {
	public boolean compare(String parameter1, String parameter2){
		return (parameter1!=null) && (parameter1.equals(parameter2));
	}
}
