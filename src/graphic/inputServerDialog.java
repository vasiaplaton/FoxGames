package graphic;

import game.*;

import javax.swing.*;
import java.awt.event.*;

class inputServerDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOKtoExist;
    private JButton buttonCancel;
    private JTextField hostnameField;
    private JComboBox<Side> comboBoxSIde;
    private JTextField existingRoomID;
    private JButton buttonOKtoNew;
    private JTextField portField;


    public inputServerDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonCancel);

        buttonOKtoExist.addActionListener(e -> {
            params = new ServerParams(hostnameField.getText(), Integer.parseInt(portField.getText()), true,
                    Integer.parseInt(existingRoomID.getText()), null);
            onCancel();
        });

        buttonOKtoNew.addActionListener(e -> {
            params = new ServerParams(hostnameField.getText(), Integer.parseInt(portField.getText()), true,
                    null, (Side) comboBoxSIde.getSelectedItem());
            onCancel();
        });

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });
    }


    public record ServerParams(String host, int port, boolean toExist, Integer roomId, Side side) {}

    private ServerParams params;

    public ServerParams getParams() {
        return params;
    }

    public static void main(String[] args) {
        inputServerDialog dialog = new inputServerDialog();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    private void onCancel() {
        dispose();
    }

    private void createUIComponents() {
        comboBoxSIde = new JComboBox<>(Side.values());
        // TODO: place custom component creation code here
    }
}
