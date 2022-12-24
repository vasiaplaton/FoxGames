package graphic;

import players.local.LocalGameType;

import javax.swing.*;
import java.awt.event.*;

public class chooseGameDialog extends JDialog {
    private JPanel contentPane;
    private JButton foxBot;
    private JButton twoHumLoc;
    private JButton gooseBot;
    private JButton twoBotButton;
    private JButton twoHumServ;

    private LocalGameType whichTypeChoose;
    private boolean isOnline = false;

    public chooseGameDialog() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(twoHumLoc);

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        foxBot.addActionListener(e -> {
            whichTypeChoose = LocalGameType.FOX_BOT;
            onCancel();
        });

        gooseBot.addActionListener(e -> {
            whichTypeChoose = LocalGameType.GOOSE_BOT;
            onCancel();
        });

        twoHumLoc.addActionListener(e ->{
            whichTypeChoose = LocalGameType.LOCAL_GAME;
            onCancel();
        });


        twoBotButton.addActionListener(e -> {
            whichTypeChoose = LocalGameType.TWO_BOTS;
            onCancel();
        });

        twoHumServ.addActionListener(e -> {
            whichTypeChoose = null;
            isOnline = true;
            onCancel();
        });
    }

    public LocalGameType whichTypeChosen(){
        return whichTypeChoose;
    }

    public boolean isOnline() {
        return isOnline;
    }


    private void onCancel() {
        dispose();
    }
}
