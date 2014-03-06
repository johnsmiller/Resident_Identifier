package com.sjsurha.resident_identifier;


import java.awt.Dimension;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;

import javax.swing.JOptionPane;



/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Empty Main Class
 * @author John Miller
 * @version .7 - Initial Deployment
 */
public class main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        Dimension frameSize = new Dimension(550, 300);
        ViewerController temp = null;
        try {
            temp = new ViewerController();
            Runtime.getRuntime().addShutdownHook(new Thread(temp));
        } catch (CEEncryptionErrorException ex) {
            Logger.getLogger(ViewerController.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, "ERROR: Decryption has failed. Most likely caused by an invalid password. Please try again. Program will now exit.", "Decryption Failure", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
        if(temp == null)
            System.exit(0);
        else{
            JFrame main_frame = new JFrame();
            main_frame.add(temp.Get_JTabbedFrame());
            main_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            main_frame.setMinimumSize(frameSize);
            main_frame.setLocationRelativeTo(null);
            main_frame.pack();
            main_frame.setVisible(true);
        }
    }
}
