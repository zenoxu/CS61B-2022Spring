/** P2Pattern class
 *  @author Josh Hug & Vivant Sakore
 */

public class P2Pattern {
    /* Pattern to match a valid date of the form MM/DD/YYYY. Eg: 9/22/2019 */
    public static String P1 = "(([0-9])|([1][0-2])|([0][0-9]))" +
            "/([0-2][0-9]|[3][0-1]|[0-9])/\\d{4}"; //FIXME: Add your regex here

    /** Pattern to match 61b notation for literal IntLists. */
    public static String P2 = "^[(]([0-9]+,\\s+)+[0-9]+[)]"; //FIXME: Add your regex here

    /* Pattern to match a valid domain name. Eg: www.support.facebook-login.com */
    public static String P3 = "^(([a-zA-Z]|[a-zA-Z]([a-zA-Z]|-){0,}[a-zA-Z])[.])+[a-z]{2,6}$"; //FIXME: Add your regex here

    /* Pattern to match a valid java variable name. Eg: _child13$ */
    public static String P4 = "[a-zA-Z_$]([a-zA-Z_$1-9]){0,}"; //FIXME: Add your regex here

    /* Pattern to match a valid IPv4 address. Eg: 127.0.0.1 */
    private static String regex = "[0-9]{1,2}|[0-1][0-9][0-9]|[2][0-5][0-5]";
    private static String _result = String.format("(%s)\\.(%s)\\.(%s)\\.(%s)", regex, regex, regex, regex);
    public static String P5 = _result;; //FIXME: Add your regex here

}
