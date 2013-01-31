package enums;

/**
 * enum class for test
 */

public enum EnumTest {
    TEST1("This is a test value 1"), TEST2("This is a test value 2");

    private String msg;

    private EnumTest(String msg) {
        this.msg = msg;
    }
    
    @Override
    public String toString() {
        return this.msg;
    }
}
