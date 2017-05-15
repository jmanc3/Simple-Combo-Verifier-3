package scv3.utils;

import scv3.controller.Controller;

import java.util.regex.Pattern;

public class Verifier {
    private boolean passNeedsMinLength;
    private boolean passNeedsMaxLength;
    private boolean userNeedsMinLength;
    private boolean userNeedsMaxLength;

    private int userMin;
    private int userMax;
    private int passMin;
    private int passMax;

    private boolean userCapToggle;
    private boolean userNumToggle;
    private boolean userMinToggle;
    private boolean userSpecialToggle;
    private boolean passCapToggle;
    private boolean passNumToggle;
    private boolean passMinToggle;
    private boolean passSpecialToggle;

    private boolean userCustomToggle;
    private boolean passCustomToggle;

    private Pattern user = null;
    private Pattern pass = null;

    public Verifier(Controller controller) {
        userNeedsMinLength = controller.userMinLengthToggle.isSelected();
        userNeedsMaxLength = controller.userMaxLengthToggle.isSelected();
        passNeedsMinLength = controller.passMinLengthToggle.isSelected();
        passNeedsMaxLength = controller.passMaxLengthToggle.isSelected();

        if (!controller.userMinField.getText().isEmpty()) {
            userMin = Integer.parseInt(controller.userMinField.getText());
        }
        if (!controller.userMaxField.getText().isEmpty()) {
            userMax = Integer.parseInt(controller.userMaxField.getText());
        }
        if (!controller.passMinField.getText().isEmpty()) {
            passMin = Integer.parseInt(controller.passMinField.getText());
        }
        if (!controller.passMaxField.getText().isEmpty()) {
            passMax = Integer.parseInt(controller.passMaxField.getText());
        }
        if (controller.userCustomToggle.isSelected()) {
            user = Pattern.compile(controller.userCustomField.getText());
        }
        if (controller.passCustomToggle.isSelected()) {
            pass = Pattern.compile(controller.passCustomField.getText());
        }

        passCustomToggle = controller.passCustomToggle.isSelected();
        userCustomToggle = controller.userCustomToggle.isSelected();

        userCapToggle = controller.userCapToggle.isSelected();
        userNumToggle = controller.userNumToggle.isSelected();
        userSpecialToggle = controller.userSpecialToggle.isSelected();
        userMinToggle = controller.userMinToggle.isSelected();

        passCapToggle = controller.passCapToggle.isSelected();
        passNumToggle = controller.passNumToggle.isSelected();
        passSpecialToggle = controller.passSpecialToggle.isSelected();
        passMinToggle = controller.passMinToggle.isSelected();
    }

    public boolean userFailToggles(Combo combo) {
        boolean hasCap = !userCapToggle;
        boolean hasNum = !userNumToggle;
        boolean hasSpe = !userSpecialToggle;
        boolean hasMin = !userMinToggle;

        for (int j = 0; j < combo.userLength; j++) {
            char z = combo.charAt(j);

            if (Character.isLowerCase(z)) {
                hasMin = true;
            } else if (Character.isUpperCase(z)) {
                hasCap = true;
            } else if (Character.isDigit(z)) {
                hasNum = true;
            } else if (!Character.isLetterOrDigit(z)) {
                hasSpe = true;
            }

            if (hasSpe && hasCap && hasNum && hasMin) {
                return false;
            }
        }

        return true;
    }

    public boolean passFailToggles(Combo combo) {
        boolean hasCap = !passCapToggle;
        boolean hasNum = !passNumToggle;
        boolean hasSpe = !passSpecialToggle;
        boolean hasMin = !passMinToggle;

        for (int j = combo.userLength + 1; j < combo.length; j++) {
            char z = combo.charAt(j);

            if (Character.isLowerCase(z)) {
                hasMin = true;
            } else if (Character.isUpperCase(z)) {
                hasCap = true;
            } else if (Character.isDigit(z)) {
                hasNum = true;
            } else if (!Character.isLetterOrDigit(z)) {
                hasSpe = true;
            }

            if (hasSpe && hasCap && hasNum && hasMin) {
                return false;
            }
        }

        return true;
    }

    public boolean userWrongLength(int size) {
        if (userNeedsMinLength) {
            if (size < userMin) {
                return true;
            }
        }
        if (userNeedsMaxLength) {
            if (size > userMax) {
                return true;
            }
        }

        return false;
    }

    public boolean passWrongLength(int size) {
        if (passNeedsMinLength) {
            if (size < passMin) {
                return true;
            }
        }
        if (passNeedsMaxLength) {
            if (size > passMax) {
                return true;
            }
        }

        return false;
    }

    public boolean userFailCustom(Combo combo, String s) {
        if (userCustomToggle) {
            if (!user.matcher(s.subSequence(0, combo.userLength - 1)).find()) {
                return true;
            }
        }
        return false;
    }

    public boolean passFailCustom(Combo combo, String s) {
        if (passCustomToggle) {
            if (!pass.matcher(s.subSequence(combo.userLength + 1, combo.length)).find()) {
                return true;
            }
        }
        return false;
    }
}
