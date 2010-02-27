// Decompiled by DJ v3.10.10.93 Copyright 2007 Atanas Neshkov  Date: 27.02.2010 21:48:03
// Home Page: http://members.fortunecity.com/neshkov/dj.html  http://www.neshkov.com/dj.html - Check often for new version!
// Decompiler options: packimports(3) 
// Source File Name:   DiskApplet.java

package t5;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.*;
import java.net.URL;
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
        openButton = new JButton("Open dir");
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

    private class Task extends Thread {

        private File file;
        private DiskApplet master;

        Task() {
        }

        public void run() {
            try {
                List<File> files = getFileListing(file);
                File temp = File.createTempFile("diskApplet", null);
                PrintWriter pw = new PrintWriter(new FileOutputStream(temp));
                long len = file.getAbsolutePath().length();
                for (File file : files) {
                    pw.println(file.getAbsolutePath());
                    pw.println(file.length());
                }
                pw.flush();
                pw.close();
                JOptionPane.showMessageDialog(master, master.getParameter("uploadUrl"));
                JOptionPane.showMessageDialog(master, master.getParameter("redirectUrl"));
                InputStream is = ClientHttpRequest.post(new URL(master.getParameter("uploadUrl")), "files", temp);
                is.close();
                temp.deleteOnExit();
                try {
                    master.getAppletContext().showDocument(new URL(master.getParameter("redirectUrl")), "_top");
                } catch (Exception e) {
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Попытка деления на букву О");
                e.printStackTrace();
            }
        }

        public File getFile() {
            return file;
        }

        public void setFile(File file) {
            this.file = file;
        }

        public List getFileListing(File aStartingDir)
                throws FileNotFoundException {
            validateDirectory(aStartingDir);
            List result = getFileListingNoSort(aStartingDir);
            Collections.sort(result);
            return result;
        }

        private List getFileListingNoSort(File aStartingDir)
                throws FileNotFoundException {
            List result = new ArrayList();
            File filesAndDirs[] = aStartingDir.listFiles();
            List filesDirs = Arrays.asList(filesAndDirs);
            Iterator i$ = filesDirs.iterator();
            do {
                if (!i$.hasNext())
                    break;
                File file = (File) i$.next();
                result.add(file);
                if (!file.isFile()) {
                    List deeperList = getFileListingNoSort(file);
                    result.addAll(deeperList);
                }
            } while (true);
            return result;
        }

        private void validateDirectory(File aDirectory)
                throws FileNotFoundException {
            if (aDirectory == null)
                throw new IllegalArgumentException("Directory should not be null.");
            if (!aDirectory.exists())
                throw new FileNotFoundException((new StringBuilder()).append("Directory does not exist: ").append(aDirectory).toString());
            if (!aDirectory.isDirectory())
                throw new IllegalArgumentException((new StringBuilder()).append("Is not a directory: ").append(aDirectory).toString());
            if (!aDirectory.canRead())
                throw new IllegalArgumentException((new StringBuilder()).append("Directory cannot be read: ").append(aDirectory).toString());
        }

        public DiskApplet getMaster() {
            return master;
        }

        public void setMaster(DiskApplet master) {
            this.master = master;
        }
    }
}