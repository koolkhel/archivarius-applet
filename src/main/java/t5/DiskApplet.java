// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 27.02.2010 21:48:03
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   DiskApplet.java

package t5;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.*;

public class DiskApplet extends JApplet {
    private static final long serialVersionUID = 0x98f9d38650765af5L;
    JButton openButton;
    JProgressBar progressBar;

    public DiskApplet() {
        openButton = null;
        progressBar = null;
    }

    public void init() {
        super.init();
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                setupAndRunGui();
            }
        });
    }

    public void setupAndRunGui() {
        setSize(89, 33);
        openButton = new JButton("Найти файлы");
        openButton.setSize(89, 33);
        openButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(1);
                if (0 == fileChooser.showOpenDialog(null)) {
                    progressBar = new JProgressBar(0, 100);
                    progressBar.setValue(0);
                    progressBar.setIndeterminate(true);
                    getContentPane().remove(openButton);
                    getContentPane().add(progressBar, "Center");
                    getContentPane().validate();
                    sendData(fileChooser.getSelectedFile());
                }
            }
        });
        getContentPane().add(openButton, "Center");
        setVisible(true);
    }

    public void start() {
    }

    void sendData(File file) {
        Task task = new Task();
        task.setMaster(this);
        task.setFile(file);
        task.start();
    }
}