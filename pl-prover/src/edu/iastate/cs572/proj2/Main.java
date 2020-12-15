package edu.iastate.cs572.proj2;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * Main class
 *
 * @author okhobian
 */
public class Main
{
    static boolean useGui = true;

    public static void main(String[] args)
    {

        if (useGui)
        {
            SwingUtilities.invokeLater(new Runnable()
            {
                public void run()
                {
                    JFrame frame = new GUI("PL Prover");
                    frame.setSize(600, 800);
                    frame.setLocationRelativeTo(null);
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setVisible(true);
                }
            });
        }
        else
        {
            /* Define file name from /src folder */
            String fileName = "test0.txt";

            /* Read from given file */
            String input = Utils.readFile(fileName);

            /* Instantiate new Prover */
            Prover prover = new Prover();

            /* Parse file input to prover */
            prover.parseSentences(input);

            /* Resolve the PL-Logic */
            String result = prover.resolve();

            /* Print out the result */
            System.out.println(result);
        }
    }
}