
import javafx.scene.input.KeyCode;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.Enumeration;


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
    private JRadioButton routerSelect;
    private JPanel pnlClientParameters;
    private JLabel lblDestinationIP;
    private JTextField txtClientDestinationIP;
    private JLabel lblTextFile;
    private JLabel lblRouterName;
    private JTextField txtRouterName;
    private JLabel lblRouterPortNumber;
    private JTextField txtRouterPortNumber;
    private JTextField txtServerDestinationIP;
    private JTextField txtMyHostname;

    String IPaddress = "localhost"; //this needs to be the ip of the server router


    private void createUIComponents() {
    }

    public baseClient() {
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int portNumber;
                    String portString = txtRouterPortNumber.getText();

                    if (portString.matches("-{0,1}[0-9]+")) {
                        portNumber = Integer.parseInt(portString);

                        if (portNumber < 0 || portNumber >= Math.pow(2, 16)) {
                            JOptionPane.showMessageDialog(null, "Port Number must be between 0-65535!", "Invalid Input!", JOptionPane.ERROR_MESSAGE);
                        } else if (portNumber > 0 && portNumber < Math.pow(2, 10)) {
                            JOptionPane.showMessageDialog(null, "You shouldn't pick a well known port (i.e., 1-1023),", "Well Known Ports", JOptionPane.INFORMATION_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Port Number must be a number between 0-65535!", "Invalid Input!", JOptionPane.ERROR_MESSAGE);
                    }

                    if (routerSelect.isSelected()) {
                    } else if (serverSelect.isSelected()) {
                        ServerSocket Socket = new ServerSocket(6666); //port here can be anything as long as server router knows what it is

                        /*
                            We might want to consider adding a timeout feature to the server as it will hang forever waiting for a connection...
                            we could also spawn a server thread to handle everything so as not to lock the gui. we can worry about this
                            when the rest is functioning.
                        */

                        while (serverSelect.isSelected()) //servers run loop forever
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

                    } else if (clientSelect.isSelected()) {
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

        ItemListener select = new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    if (e.getItem() == routerSelect) {
                        txtRouterName.setEnabled(false);
                    } else if (e.getItem() == serverSelect) {
                        txtServerDestinationIP.setEnabled(true);
                    } else if (e.getItem() == clientSelect) {
                        txtClientDestinationIP.setEnabled(true);
                        fileName.setEnabled(true);
                    }
                } else {
                    if (e.getItem() == routerSelect) {
                        txtRouterName.setEnabled(true);
                        txtRouterName.setText("");
                    } else if (e.getItem() == serverSelect) {
                        txtServerDestinationIP.setEnabled(false);
                    } else if (e.getItem() == clientSelect) {
                        txtClientDestinationIP.setEnabled(false);
                        fileName.setEnabled(false);
                    }
                }
            }
        };

        routerSelect.addItemListener(select);
        serverSelect.addItemListener(select);
        clientSelect.addItemListener(select);

        KeyListener enter = new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    startButton.doClick();
                }
            }
        };

        FocusListener focus = new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                ((JTextField) e.getSource()).selectAll();
            }

            @Override
            public void focusLost(FocusEvent e) {

            }
        };

        txtRouterName.addKeyListener(enter);
        txtRouterPortNumber.addKeyListener(enter);
        txtServerDestinationIP.addKeyListener(enter);
        txtClientDestinationIP.addKeyListener(enter);
        fileName.addKeyListener(enter);

        txtRouterName.addFocusListener(focus);
        txtRouterPortNumber.addFocusListener(focus);
        txtServerDestinationIP.addFocusListener(focus);
        txtClientDestinationIP.addFocusListener(focus);
        fileName.addFocusListener(focus);

        try {
            txtMyHostname.setText(InetAddress.getLocalHost().getHostName());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        routerSelect.setSelected(true);
    }

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
        mains.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(7, 2, new Insets(0, 0, 0, 0), -1, -1));
        mains.setDoubleBuffered(true);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        mains.add(panel1, new com.intellij.uiDesigner.core.GridConstraints(6, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, true));
        panel1.setBorder(BorderFactory.createTitledBorder("Output"));
        outputWindow = new JTextArea();
        outputWindow.setEditable(false);
        panel1.add(outputWindow, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(150, 400), null, 0, false));
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 3, new Insets(0, 0, 0, 0), -1, -1));
        panel2.setAutoscrolls(false);
        mains.add(panel2, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel2.setBorder(BorderFactory.createTitledBorder("Node Type"));
        clientSelect = new JRadioButton();
        clientSelect.setActionCommand("RadioButton");
        clientSelect.setLabel("Client");
        clientSelect.setSelected(false);
        clientSelect.setText("Client");
        panel2.add(clientSelect, new com.intellij.uiDesigner.core.GridConstraints(0, 2, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(120, 40), new Dimension(120, 40), new Dimension(120, 40), 0, false));
        routerSelect = new JRadioButton();
        routerSelect.setLabel("Router");
        routerSelect.setSelected(false);
        routerSelect.setText("Router");
        panel2.add(routerSelect, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(120, 40), new Dimension(120, 40), new Dimension(120, 40), 0, false));
        serverSelect = new JRadioButton();
        serverSelect.setLabel("Server");
        serverSelect.setSelected(false);
        serverSelect.setText("Server");
        panel2.add(serverSelect, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(120, 40), new Dimension(120, 40), new Dimension(120, 40), 0, false));
        startButton = new JButton();
        startButton.setHideActionText(true);
        startButton.setHorizontalAlignment(0);
        startButton.setText("Start");
        startButton.setToolTipText("Click to start program");
        mains.add(startButton, new com.intellij.uiDesigner.core.GridConstraints(5, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, new Dimension(80, 30), new Dimension(80, 30), new Dimension(80, 30), 0, false));
        pnlClientParameters = new JPanel();
        pnlClientParameters.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        pnlClientParameters.setEnabled(true);
        pnlClientParameters.setFocusable(true);
        mains.add(pnlClientParameters, new com.intellij.uiDesigner.core.GridConstraints(4, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        pnlClientParameters.setBorder(BorderFactory.createTitledBorder("Client Parameters"));
        lblDestinationIP = new JLabel();
        lblDestinationIP.setText("Destination IP:");
        pnlClientParameters.add(lblDestinationIP, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtClientDestinationIP = new JTextField();
        txtClientDestinationIP.setEnabled(false);
        pnlClientParameters.add(txtClientDestinationIP, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        lblTextFile = new JLabel();
        lblTextFile.setText("Text File:");
        pnlClientParameters.add(lblTextFile, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        fileName = new JFormattedTextField();
        fileName.setEditable(true);
        fileName.setEnabled(false);
        fileName.setText("");
        fileName.setToolTipText("Enter the name of a text file inthe same directory");
        pnlClientParameters.add(fileName, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(2, 2, new Insets(0, 0, 0, 0), -1, -1));
        mains.add(panel3, new com.intellij.uiDesigner.core.GridConstraints(2, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel3.setBorder(BorderFactory.createTitledBorder("Router Parameters"));
        lblRouterName = new JLabel();
        lblRouterName.setText("Router Name:");
        panel3.add(lblRouterName, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtRouterName = new JTextField();
        panel3.add(txtRouterName, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        lblRouterPortNumber = new JLabel();
        lblRouterPortNumber.setText("Router Port Number:");
        panel3.add(lblRouterPortNumber, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtRouterPortNumber = new JTextField();
        txtRouterPortNumber.setText("5555");
        panel3.add(txtRouterPortNumber, new com.intellij.uiDesigner.core.GridConstraints(1, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        mains.add(panel4, new com.intellij.uiDesigner.core.GridConstraints(3, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel4.setBorder(BorderFactory.createTitledBorder("Server Parameters"));
        final JLabel label1 = new JLabel();
        label1.setText("Destination IP:");
        panel4.add(label1, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtServerDestinationIP = new JTextField();
        txtServerDestinationIP.setEnabled(false);
        panel4.add(txtServerDestinationIP, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        final JPanel panel5 = new JPanel();
        panel5.setLayout(new com.intellij.uiDesigner.core.GridLayoutManager(1, 2, new Insets(0, 0, 0, 0), -1, -1));
        mains.add(panel5, new com.intellij.uiDesigner.core.GridConstraints(1, 0, 1, 2, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_CENTER, com.intellij.uiDesigner.core.GridConstraints.FILL_BOTH, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_SHRINK | com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        panel5.setBorder(BorderFactory.createTitledBorder("Localhost Information"));
        final JLabel label2 = new JLabel();
        label2.setText("My Hostname:");
        panel5.add(label2, new com.intellij.uiDesigner.core.GridConstraints(0, 0, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_NONE, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        txtMyHostname = new JTextField();
        txtMyHostname.setEnabled(false);
        panel5.add(txtMyHostname, new com.intellij.uiDesigner.core.GridConstraints(0, 1, 1, 1, com.intellij.uiDesigner.core.GridConstraints.ANCHOR_WEST, com.intellij.uiDesigner.core.GridConstraints.FILL_HORIZONTAL, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_WANT_GROW, com.intellij.uiDesigner.core.GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(150, -1), null, 0, false));
        ButtonGroup buttonGroup;
        buttonGroup = new ButtonGroup();
        buttonGroup.add(clientSelect);
        buttonGroup.add(serverSelect);
        buttonGroup.add(routerSelect);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mains;
    }


//nothing below here needs editing its all auto generated GUI data by intellij


}
