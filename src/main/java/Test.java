import edu.tum.uc.jvm.declassification.Declassifier;

public class Test {
	public static void main(String[] args) {
		int i = 54;
		Object o = new Object();
		Declassifier.declassify(o);
//		System.out.println(Object.class.getName());
	}

}
