package scv3.utils;

public class Combo {
    public char[] array;
    public int index = 0;
    public int lines = 0;
    public int length = 0;
    public int colon = -1;
    public int passLength;
    public int userLength;
    public boolean shouldCheck = false;

    public Combo() {
        array = new char[300];
    }

    public void append(char c) {
        if (c == '\n') {
            return;
        }
        if (c == ':') {
            colon = index;
        }
        if (c == '\r') {
            length = index;
            lines++;

            if (colon != -1) {
                shouldCheck = true;
            }

            index = 0;
            return;
        }

        if (index > 299) {
            return;
        }

        array[index] = c;

        index++;
    }

    public boolean isFound() {
        if (shouldCheck) {
            shouldCheck = false;
            passLength = length - colon - 1;
            userLength = colon;
            colon = -1;
            return true;
        } else {
            return false;
        }
    }

    public char charAt(int j) {
        return array[j];
    }
}
