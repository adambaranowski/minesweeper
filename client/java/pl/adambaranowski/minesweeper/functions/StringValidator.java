package pl.adambaranowski.minesweeper.functions;

public class StringValidator {
    private static StringValidator INSTANCE;

    private StringValidator() {
    }

    public static StringValidator getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new StringValidator();
        }
        return INSTANCE;
    }


    public boolean checkTextCorrectness(String text) {
        if (text.contains("~") || text.length() < 3) {
            return false;
        }
        return true;
    }
}
