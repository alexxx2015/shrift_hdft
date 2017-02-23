/**
 * Created by xtarx on 23/02/2017.
 */
package edu.tum.uc.jvm.checker;
import java.util.*;
import javax.servlet.http.HttpServletRequest;

public class RequestCheck {

    static HashMap hm = new HashMap();

    public static void parse_field(String str) {
        try {
            str = str.substring(0, str.length() - 1);
            String[] entries = str.split("##");


            for (String entry : entries) {
                String[] parts = entry.split("!&!");
                hm.put(parts[0], parts[1]);
            }
        } catch (Exception e) {

        }

    }

    public static String hashmap() {

        String str="";
        Set set = hm.entrySet();
        Iterator i = set.iterator();
        // Display elements
        while (i.hasNext()) {
            Map.Entry me = (Map.Entry) i.next();
            str+=(me.getKey() + ": ");
            str+=(me.getValue());
            str+="/n";
        }
        return str;

    }



    public static void parseObject(Object o) {

        if (o instanceof HttpServletRequest) {

            HttpServletRequest request = (HttpServletRequest) o;

            String protected_fields = request.getParameter("uc_protected_fields");

            if (protected_fields != null) {
                parse_field(protected_fields);
            }

        } else {

            System.out.println("NOO");

        }

    }



    public static boolean containsField(String key) {
        if (hm.containsKey(key))
            return true;

        return false;
    }

    public static String containsFieldWithPolicy(String key) {

        if (hm.containsKey(key))
            return hm.get(key).toString();

        return null;

    }



    public static void main(String[] args) {

        String protected_fields = "first_name!&!policy1##last_name!&!policy2##age!&!policy3#";


        parseObject(protected_fields);

        System.out.println(containsFieldWithPolicy("string"));

        System.out.println(containsFieldWithPolicy("age"));


    }


}
