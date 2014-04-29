package de.tum.in.i22.uc.pip.extensions.statebased;

import com.google.common.base.Objects;

import de.tum.in.i22.uc.cm.settings.Settings;
import de.tum.in.i22.uc.pip.core.ifm.IInformationFlowModel;
import de.tum.in.i22.uc.pip.interfaces.EStateBasedFormula;
import de.tum.in.i22.uc.pip.interfaces.IStateBasedPredicate;

public abstract class StateBasedPredicate implements IStateBasedPredicate {
	private final String _predicate;

	private final static String SEPARATOR1 = Settings.getInstance().getSeparator1();
	protected final static String SEPARATOR2 = Settings.getInstance().getSeparator2();

	protected final IInformationFlowModel _informationFlowModel;


	public StateBasedPredicate(String predicate, IInformationFlowModel informationFlowModel) {
		_predicate = predicate;
		_informationFlowModel = informationFlowModel;
	}

	public static IStateBasedPredicate create(String predicate, IInformationFlowModel ifm) throws InvalidStateBasedFormula {
		IStateBasedPredicate spredicate = null;

		InvalidStateBasedFormula exc = new InvalidStateBasedFormula("Predicate {" + predicate + "} is invalid.");

		String[] st = predicate.split(StateBasedPredicate.SEPARATOR1);
		EStateBasedFormula pred = EStateBasedFormula.from(st[0]);

		if (st.length == 0 || pred == null) {
			throw exc;
		}

		switch (pred) {
			case IS_COMBINED_WITH:
				if (st.length >= 3)
					spredicate = new IsCombinedWith(predicate, st[1], st[2], ifm);
				break;
			case IS_NOT_IN:
				if (st.length >= 3)
					spredicate = new IsNotIn(predicate, st[1], st[2], ifm);
				break;
			case IS_ONLY_IN:
				if (st.length >= 3)
					spredicate = new IsOnlyIn(predicate, st[1], st[2], ifm);
				break;
			default:
				throw exc;
		}

		if (spredicate == null) {
			throw exc;
		}

		return spredicate;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("_predicate", _predicate)
				.toString();
	}
}
