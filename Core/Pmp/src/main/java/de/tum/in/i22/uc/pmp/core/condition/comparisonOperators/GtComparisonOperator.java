package de.tum.in.i22.uc.pmp.core.condition.comparisonOperators;


public class GtComparisonOperator extends GenericComparisonOperator{
	public boolean compare(String parameter1, String parameter2){
		return (parameter1!=null) && (parameter2 != null) && (Integer.valueOf(parameter1) > Integer.valueOf(parameter2));
	}
}
