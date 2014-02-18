
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.*;
import java.net.*;
import java.util.Arrays;



public class baseClient {

    public static void main(String[] args) {
        JFrame frame = new JFrame("baseClient");
        frame.setContentPane(new baseClient().mains);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private JButton startButton;
    private JFormattedTextField ipAddress;
    private JTextArea outputWindow;
    private JFormattedTextField fileName;
    private JRadioButton clientSelect;
    private JRadioButton serverSelect;
    private JPanel mains;

    String IPaddress = "localhost"; //this needs to be the ip of the server router


    private void createUIComponents() {

    }

    public baseClient() {
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (serverSelect.isSelected()) {
                        ServerSocket Socket = new ServerSocket(6666); //port here can be anything as long as server router knows what it is

                        /*
                            We might want to consider adding a timeout feature to the server as it will hang forever waiting for a connection...
                            we could also spawn a server thread to handle everything so as not to lock the gui. we can worry about this
                            when the rest is functioning.
                        */

                        while(serverSelect.isSelected()) //servers run loop forever
                        {
                                //accept new connections to the socket (blocks forever)
                                outputWindow.append("Waiting for client on port  :" + Socket.getLocalPort() + "...\n");
                                Socket connectionSocket = Socket.accept();
                                outputWindow.append("Client connected on port    :" + Socket.getLocalPort() + "...\n");
                                //create streams
                                BufferedReader receive = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
                                DataOutputStream send = new DataOutputStream(connectionSocket.getOutputStream());
                                //get a new message from the socket
                                String message = receive.readLine();
                                //modify the sentence
                                String modifiedMsg = message.toUpperCase() + '\n';
                                //send modified message back
                                send.writeBytes(modifiedMsg);
                        }

                    }
                    if (clientSelect.isSelected())
                    {
                        /*
                            the following code will connect to the server router and then open a file for sending the data
                         */

                        Socket clientSocket = new Socket(IPaddress, 6666); //port needs to be serverrouters port
                        //network output stream
                        DataOutputStream send = new DataOutputStream(clientSocket.getOutputStream());
                        //network input stream
                        BufferedReader receive = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        //get a lowercase message from the user (no verification or anything)
                        String message = fileName.getText();

                        InputStream file = new FileInputStream(fileName.getText());
                        BufferedReader reader = new BufferedReader(new InputStreamReader(file));
                        String line = null;


                        /*
                            in the while loop below we need to add the statistics for average length of each line we send
                            the average round trip time of each message.
                        */

                        while ((line = reader.readLine()) != null)      //loop to end of file sending every line
                        {
                            outputWindow.append("Sending TCP: " + line + "\n"); //we may want to only output statistics if its a long file
                            //convert to bytes and write to stream
                            send.writeBytes(line + '\n');
                            //receive the message back from the server
                            String modifiedMsg = receive.readLine();
                            outputWindow.append("Server TCP: " + modifiedMsg + "\n");
                            //we are done here close the socket!

                        }
                        clientSocket.close(); //we are done here
                    }
                } catch (IOException h) {
                }
            }
        });


    }





//nothing below here needs editing its all auto generated GUI data by intellij






    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mains = new JPanel();
        mains.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(4, 3, new Insets(0, 0, 0, 0), -1, -1));
        mains.setMinimumSize(new Dimension(443, 500));
        mains.setPreferredSize(new Dimension(443, 500));
        fileName = new JFormattedTextField();
        fileName.setEditable(true);
        fileName.setText("");
        fileName.setToolTipText("Enter a string of lowercase text");
        mains.add(fileName, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), new Dimension(500, 40), 0, false));
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        mains.add(panel1, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 3, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        outputWindow = new JTextArea();
        outputWindow.setEditable(false);
        panel1.add(outputWindow, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(400, 300), new Dimension(400, 300), new Dimension(400, 300), 0, false));
        ipAddress = new JFormattedTextField();
        ipAddress.setText("000.000.000.000");
        ipAddress.setToolTipText("Enter Server IP address (v4)");
        mains.add(ipAddress, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), new Dimension(200, 40), 0, false));
        clientSelect = new JRadioButton();
        clientSelect.setActionCommand("RadioButton");
        clientSelect.setLabel("UDP");
        clientSelect.setSelected(false);
        clientSelect.setText("UDP");
        mains.add(clientSelect, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(80, 40), new Dimension(80, 40), new Dimension(80, 40), 0, false));
        serverSelect = new JRadioButton();
        serverSelect.setLabel("TCP");
        serverSelect.setSelected(true);
        serverSelect.setText("TCP");
        mains.add(serverSelect, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(80, 40), new Dimension(80, 40), new Dimension(80, 40), 0, false));
        final JLabel label1 = new JLabel();
        label1.setText("IP Address");
        mains.add(label1, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        startButton = new JButton();
        startButton.setText("Send");
        startButton.setToolTipText("Click to send message");
        mains.add(startButton, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(80, 30), new Dimension(80, 30), new Dimension(80, 30), 0, false));
        ButtonGroup buttonGroup;
        buttonGroup = new ButtonGroup();
        buttonGroup.add(clientSelect);
        buttonGroup.add(serverSelect);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mains;
    }
}
