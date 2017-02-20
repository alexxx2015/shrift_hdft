import org.apache.commons.lang3.StringUtils;

public class Test {

	public static void modulo(int i) {
		int f = 6;
		int z = f & i;
		System.out.println(z);
	}

	public static void main(String[] args) {
		int o1 = 22, o2 = 14, res = o1 ^ o2;
		
//		convert to binary representation
		String restr = Integer.toBinaryString(res);
		String restrRev = new StringBuilder(restr).reverse().toString();

		String o1str = Integer.toBinaryString(o1);
		String o1strRev = new StringBuilder(o1str).reverse().toString();
		
		String o2str = Integer.toBinaryString(o2);
		String o2strRev = new StringBuilder(o2str).reverse().toString();
		
//		count the number of ones
		int count = StringUtils.countMatches(restr, "1");
		int ones1 = StringUtils.countMatches(o1str, "1");
		int ones2 = StringUtils.countMatches(o2str, "1");
		
		System.out.println("RES: '"+restr+"' , "+count+" , '"+restrRev+"'");
		System.out.println("O1   : '"+o1str+"' , "+ones1+" , '"+o1strRev+"'");
		System.out.println("O2   : '"+o2str+"' , "+ones2+" , '"+o2strRev+"'");
		
		int[] matches = new int[]{0,0};
		for(int i = 0; i < restrRev.length(); i++){
			if(o1strRev.length() > i && restrRev.charAt(i) == o1strRev.charAt(i)){
				matches[0]++;
			}
			if(o2strRev.length() > i && restrRev.charAt(i) == o2strRev.charAt(i)){
				matches[1]++;
			}
		}
		double o1cut = (double)matches[0]/o1str.length();
		double o2cut = (double)matches[1]/o2str.length();
		System.out.println("MATCHES: "+matches[0]+" , "+matches[1]);
		System.out.println("DEC: "+o1cut+" , "+o2cut);
		
		if (count >= 0) {
			
//			diff = 1 - (double) count / (getByteSize(o2) * 8);
		}
	}
	
	public static void shift(int j){
		int shiftpos = Integer.MAX_VALUE-1;
		int k = j >> shiftpos;
		System.out.println(k);
	}
	
	public static boolean not(boolean a){
		boolean ret = !a;
		return ret;
	}
	
	public static int neg(int a){
		return ~a;
	}

}
