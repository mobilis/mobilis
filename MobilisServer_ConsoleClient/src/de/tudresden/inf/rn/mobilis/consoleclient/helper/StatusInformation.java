package de.tudresden.inf.rn.mobilis.consoleclient.helper;

import javax.swing.JOptionPane;

/**
 * @author cmdaltent
 */
public class StatusInformation {

    private StatusType type;
    private String message;

    public StatusInformation() {
        init(null, null);
    }

    public StatusInformation(final StatusType type, final String message) {
        init(type, message);
    }

    private void init(StatusType type, String message) {
        this.type = type;
        this.message = message;
    }

    public StatusType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public final int optionPaneStatus() {
        int status;

        switch (type) {
            case QUESTION: status = JOptionPane.QUESTION_MESSAGE; break;
            case INFORMATION: status = JOptionPane.INFORMATION_MESSAGE; break;
            case WARNING: status = JOptionPane.WARNING_MESSAGE; break;
            case ERROR: status = JOptionPane.ERROR_MESSAGE; break;
            default: status = -1; break;
        }

        return status;
    }
}
