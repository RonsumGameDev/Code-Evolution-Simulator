// This is the Main.java file, which contains the main method and import of swing library for the implementation of GUI.

import java.awt.Color;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class Main{
    final static String s_TITLE = "Code Evolution Simulator";
    public static void main(String[] args) {
        JFrame frame = new JFrame(); //Creating JFrame object to create a window for the application. 

        frame.setTitle(s_TITLE); //Setting the title of the frame to "Code Evolution Simulator".

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //Setting the default close operation of the frame to EXIT_ON_CLOSE, which means that the application will exit when the user closes the window. (Close refers to clicking the "X" button on the top right corner of the window).

        frame.setResizable(true); //Setting the resizable property of the frame to true, which means that the user will be able to resize the window.

        frame.setSize(420, 420); //Setting the size of the frame to 420 pixels by 420 pixels.






        JButton uploadProjectBtn = new JButton("Upload your Project"); //Creates the JButton object.

        // We have created an ActionListener this is similar to eventListener in JavaScript, it listens for the click event on the button and executes the code inside the lambda function when the button is clicked.
        uploadProjectBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // This makes sure we can only select directories and not files.

            int result = chooser.showOpenDialog(frame); //This returns an integer value that indicates the user's action. If the user approves the selection, it will return JFileChooser.APPROVE_OPTION. If the user cancels the selection, it will return JFileChooser.CANCEL_OPTION. If an error occurs, it will return JFileChooser.ERROR_OPTION.
            if(result == JFileChooser.APPROVE_OPTION) {
                System.out.println("Selected : " + chooser.getSelectedFile().getAbsolutePath());
            }
            //If everything went nicely it will print the absolute path of the selected directory to the console. If the user cancels the selection or an error occurs, it will not print anything.
        });

        frame.add(uploadProjectBtn);

        ImageIcon icon = new ImageIcon("./Assets/JavaLogo.png"); //Creating an ImageIcon object with the image file "./Assets/JavaLogo.png". This will be used as the icon for the application window.

        frame.setIconImage(icon.getImage()); // Set the image icon.

        frame.getContentPane().setBackground(new Color(0xFF8EFC));
        frame.setVisible(true); //Setting the visibility of the frame to true so that it can be seen when the application is run.

    }
}