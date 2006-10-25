import java.util.regex.Pattern;

public class TestRegexp {
    public static void main(String[] args) {
	String regexp = args[0];
	String testString = args[1];
	
	System.out.println(Pattern.matches(regexp, testString));
    }
}
