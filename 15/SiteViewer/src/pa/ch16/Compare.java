package pa.ch16;

public class Compare {
    /*
     * Safe object comparison - return true if references point to the same
     * object, or if the references are .equals() with each other.
     */
    public static boolean same(Object one, Object two) {
        boolean oneNull = one == null;
        boolean twoNull = two == null;

        if (oneNull && twoNull) {
            return true;
        }

        if (oneNull || twoNull) {
            return false;
        }

        return (one.equals(two));
    }

    public static boolean empty(String data) {
        return ((data == null) || ("".equals(data)));
    }
}
