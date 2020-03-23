package dev.shroysha.widgets.mp3rename.view;

import dev.shroysha.widgets.mp3rename.App;
import org.farng.mp3.AbstractMP3Tag;
import org.farng.mp3.MP3File;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;


public class Mp3RenameFrame extends JFrame {

    private final ArrayList<MP3File> filesToRename = new ArrayList<>();
    private JTable toRenameTable;

    public Mp3RenameFrame() {
        super("MP3 Renamer");
        init();
    }

    public static void main(String[] args) {
        Mp3RenameFrame frame = new Mp3RenameFrame();
        frame.setVisible(true);
    }

    private void init() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        JLabel isConvertingLabel = new JLabel("blah");

        JButton addButton = new JButton("Add Songs");
        addButton.addActionListener(ae -> addFiles());

        JButton removeButton = new JButton("Remove Selected Songs");
        removeButton.addActionListener(ae -> removeFiles());

        toRenameTable = new JTable();

        toRenameTable.setBorder(new EmptyBorder(10, 0, 10, 0));
        JProgressBar progressBar = new JProgressBar(0, 100);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBorder(new EtchedBorder());
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);

        JPanel progressPanel = new JPanel(new BorderLayout());
        progressPanel.add(isConvertingLabel, BorderLayout.NORTH);
        progressPanel.add(progressBar, BorderLayout.SOUTH);

        contentPanel.add(buttonPanel, BorderLayout.NORTH);
        contentPanel.add(toRenameTable, BorderLayout.CENTER);
        contentPanel.add(progressPanel, BorderLayout.SOUTH);

        updateTable();

        this.add(contentPanel, BorderLayout.CENTER);
        this.setSize(400, 300);
    }

    public void startRenaming() {
        Queue<MP3File> files = new LinkedList<>(filesToRename);
        Exception[] exs = App.renameMP3s(files);
        showExceptions(exs);
    }

    private void showExceptions(Exception[] exs) {
        if (exs.length != 0) {
            JLabel label = new JLabel("There was an error loading these songs.");

            JList<Exception> errorList = new JList<>(exs);
            JScrollPane scroller = new JScrollPane(errorList);

            final JDialog dialog = new JDialog(this, "Problems", true);
            dialog.setLayout(new BorderLayout());

            JButton button = new JButton("Okay");
            button.addActionListener(e -> dialog.dispose());

            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(Color.white);
            panel.setBorder(new javax.swing.border.EmptyBorder(10, 10, 10, 10));
            panel.add(label, BorderLayout.NORTH);
            panel.add(scroller, BorderLayout.CENTER);
            panel.add(button, BorderLayout.SOUTH);
            dialog.add(panel);

            dialog.getRootPane().setDefaultButton(button);

            dialog.pack();
            dialog.setVisible(true);

            for (Exception ex : exs) {
                ex.printStackTrace(System.err);
            }
        }
    }


    private void addFiles() {
        File[] files = App.openFiles(this);

        ArrayList<Exception> exs = new ArrayList<>();

        for (File file : files) {
            try {
                MP3File next = new MP3File(file);

                if (!filesToRename.contains(next))
                    filesToRename.add(new MP3File(file));
            } catch (Exception ex) {
                exs.add(ex);
            }
        }

        updateTable();
        showExceptions(exs.toArray(new Exception[0]));
    }

    public void removeFiles() {
        int[] toRemove = toRenameTable.getSelectedRows();
        removeFiles(toRemove);
        updateTable();
    }

    private void removeFiles(int[] toRemove) {
        for (int i = 0; i < toRemove.length; i++) {
            filesToRename.remove(toRemove[i] - i);
        }
    }

    private void updateTable() {
        String[] columnNames = {"Song", "Artist", "Album", "Track Number"};
        Object[][] data = new Object[filesToRename.size()][columnNames.length];

        for (int i = 0; i < filesToRename.size(); i++) {
            MP3File next = filesToRename.get(i);
            AbstractMP3Tag tag = App.getTag(next);
            assert tag != null;
            data[i][0] = tag.getSongTitle();
            data[i][1] = tag.getLeadArtist();
            data[i][2] = tag.getAlbumTitle();
            try {
                data[i][3] = tag.getTrackNumberOnAlbum();
            } catch (Exception ex) {
                data[i][3] = "";
            }
        }

        TableModel model = new DefaultTableModel(data, columnNames);
        toRenameTable.setRowSelectionAllowed(true);

        toRenameTable.setModel(model);
    }
}
