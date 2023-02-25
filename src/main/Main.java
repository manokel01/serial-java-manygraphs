package main;

import frame.MainFrame;

import com.fazecast.jSerialComm.SerialPort;

import frame.MainFrame;
import frame.MainFrame;

import java.awt.*;
import java.io.*;
import javax.swing.*;

import org.jfree.data.xy.XYSeries;

import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A Java swing application that opens a Serial port to an Arduino and
 * reads nine (9) input data values as CSV strings. 
 * The input data values are displayed in six (6) separate time-scaled diagram.
 * 
 * @version 0.1
 * @author manokel01
 *
 */

public class Main {

    public static void main(String[] args) throws IOException {
        //Schedule a job for the event dispatch thread:
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                MainFrame.createAndShowGUI();
            }
        });
    }
}
