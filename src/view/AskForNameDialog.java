package view;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

public class AskForNameDialog extends JDialog {
    private JTextField          TEXT_FIELD;
    protected int               BUTTON_VALUE;
    protected JPanel            PANEL_BUTTON;
    protected JButton           CONFIRM_BUTTON;
    public static final int     CONFIRM_VALUE = 1;
    public static final int     CANCEL = 0;
    protected JButton   btnCancel;

    public AskForNameDialog(JFrame frame, String title) {
        super(frame, title, true);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        BUTTON_VALUE = CANCEL;
        CONFIRM_BUTTON = new JButton(actionConfirm);
        CONFIRM_BUTTON.setMnemonic('O');
        CONFIRM_BUTTON.setPreferredSize(new Dimension(160, 25));
        btnCancel = new JButton(actionCancel);

        KeyStroke cancelKeyStroke = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
        InputMap inputMap = btnCancel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = btnCancel.getActionMap();
        if (inputMap != null && actionMap != null) {
            inputMap.put(cancelKeyStroke, "cancel");
            actionMap.put("cancel", actionCancel);
        }
        btnCancel.setPreferredSize(new Dimension(90, 25));
        getRootPane().setDefaultButton(CONFIRM_BUTTON);
        PANEL_BUTTON = new JPanel(new FlowLayout());
        PANEL_BUTTON.add(CONFIRM_BUTTON);
        setSize(300, 150);
        TEXT_FIELD = new JTextField();
        setLayout(new BorderLayout());
        add("Center", TEXT_FIELD);
        add("South", PANEL_BUTTON);
        setResizable(false);
        setLocationRelativeTo(null);
        pack();
        setResizable(false);
    }
    public String getValue(){
        return TEXT_FIELD.getText();
    }

    protected boolean processOK() {
        return true;
    }

    protected void processCancel(){}

    public int getButtonCode()
    {
        return BUTTON_VALUE;
    }

    private AbstractAction actionConfirm = new AbstractAction("CONFIRM NAME") {
        public void actionPerformed(ActionEvent e) {
            if ( processOK() ) {
                BUTTON_VALUE = CONFIRM_VALUE;
                AskForNameDialog.this.setVisible(false);
            }
        }
    };

    private AbstractAction actionCancel = new AbstractAction("Cancel") {
        public void actionPerformed(ActionEvent e) {
            processCancel();
            BUTTON_VALUE = CANCEL;
            AskForNameDialog.this.setVisible(false);
        }
    };
}
