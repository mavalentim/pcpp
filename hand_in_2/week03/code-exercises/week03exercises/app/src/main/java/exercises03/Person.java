package exercises03;

public class Person {
    private final long id; // cannot be changed in the future, so final
    private String name; // cannot be final
    private int zipCode;
    private String address;
    public static int nofpersons;

    Person() {
        if (nofpersons == 0) {
            id = 0;
        } else {
            id = nofpersons;
        }
    }

}
