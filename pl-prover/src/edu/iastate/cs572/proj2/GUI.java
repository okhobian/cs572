package edu.iastate.cs572.proj2;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * GUI class
 *
 * @author okhobian
 */
public class GUI extends JFrame
{
    JButton selectFileBtn;
    JButton resolveFileBtn;
    JTextArea resultText;
    File file;

    public GUI(String title)
    {
        // set title
        super(title);
        getContentPane().setLayout(new BorderLayout());

        // init text & btn
        resultText = new JTextArea();
        resultText.setEditable(false);
        selectFileBtn = new JButton("Select File");
        resolveFileBtn = new JButton("Resolve File");

        // set borders
        Border border = BorderFactory.createLineBorder(Color.BLACK);

        resultText.setBorder(BorderFactory.createCompoundBorder(border,
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        // set panel
        JPanel subPanel = new JPanel();
        subPanel.setLayout(new BorderLayout());
        subPanel.add(selectFileBtn, BorderLayout.WEST);
        subPanel.add(resolveFileBtn, BorderLayout.EAST);

        // set font
        Font font = new Font("Monospaced", Font.BOLD, 18);
        resultText.setFont(font);

        // init scrollable text
        JScrollPane scroll = new JScrollPane (resultText);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // set container
        Container container = getContentPane();
        container.add(subPanel, BorderLayout.NORTH);
        container.add(scroll, BorderLayout.CENTER);

        // btn listener
        selectFileBtn.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("txt file", "txt");
                chooser.setFileFilter(filter);
                File workingDirectory = new File(System.getProperty("user.dir"));
                chooser.setCurrentDirectory(workingDirectory);
                int returnVal = chooser.showOpenDialog(null);
                if(returnVal == JFileChooser.APPROVE_OPTION)
                {
                    file = chooser.getSelectedFile();
                    resultText.setText("File Selected: " + file.getName());
                }

            }
        });

        resolveFileBtn.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(file != null)
                    resolveFile();
            }
        });
    }

    private void resolveFile()
    {
        /* Read from given file */
        String input = Utils.parseFile(file);

        /* Instantiate new Prover */
        Prover prover = new Prover();

        /* Parse file input to prover */
        prover.parseSentences(input);

        /* Resolve the PL-Logic */
        String result = prover.resolve();

        /* Print out the result */
        resultText.setText(result);
    }

}
