import Toaster.Toaster;
import Utils.TextAreaCustom;
import Utils.TextFieldUsername;
import Utils.UIUtils;
import chat.chat_client;
import chat.chat_server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class ServerUI extends JFrame {

    private final Toaster toaster;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JLabel serverButton;
    private JLabel clientButton;
    private JPanel mainJPanel;
    private JLabel quitButton;
    private TextAreaCustom txtPath;
    private JLabel chooseFileButton;
    private static int passCount = 0;
    private JLabel sendButton;

    public static void main(String[] args) {

        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                new ServerUI();
            }
        });
    }

    ServerUI() {
        mainJPanel = getMainJPanel();

        addLogo(mainJPanel);

        addSeparator(mainJPanel);


        clientButton = createClientButton(mainJPanel);
        serverButton =  createServerButton(mainJPanel);
        quitButton = createQuitButton();

        mainJPanel.add(quitButton);
        mainJPanel.add(clientButton);
        mainJPanel.add(serverButton);

        this.setTitle("DarkSky");
        this.add(mainJPanel);
        this.pack();
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);
        this.toFront();

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenSize.width / 2 - getWidth() / 2, screenSize.height / 2 - getHeight() / 2);

        toaster = new Toaster(mainJPanel);

    }

    private JPanel getMainJPanel() {
        this.setUndecorated(true);

        Dimension size = new Dimension(800, 400);

        JPanel panel1 = new JPanel();
        panel1.setSize(size);
        panel1.setPreferredSize(size);
        panel1.setBackground(UIUtils.COLOR_BACKGROUND);
        panel1.setLayout(null);


        MouseAdapter ma = new MouseAdapter() {
            int lastX, lastY;

            @Override
            public void mousePressed(MouseEvent e) {
                lastX = e.getXOnScreen();
                lastY = e.getYOnScreen();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                int x = e.getXOnScreen();
                int y = e.getYOnScreen();
                setLocation(getLocationOnScreen().x + x - lastX, getLocationOnScreen().y + y - lastY);
                lastX = x;
                lastY = y;
            }
        };

        panel1.addMouseListener(ma);
        panel1.addMouseMotionListener(ma);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        return panel1;
    }

    private void addSeparator(JPanel panel1) {
        JSeparator separator1 = new JSeparator();
        separator1.setOrientation(SwingConstants.VERTICAL);
        separator1.setForeground(UIUtils.COLOR_OUTLINE);
        panel1.add(separator1);
        separator1.setBounds(310, 80, 1, 240);
    }

    private void addLogo(JPanel panel1) {
        JLabel label1 = new JLabel();
        label1.setFocusable(false);
        label1.setIcon(new ImageIcon(Objects.requireNonNull(getClass().getClassLoader().getResource("darksky.png")).getFile()));
        panel1.add(label1);
        label1.setBounds(55, 146, 200, 110);
    }

    private TextAreaCustom createTextArea(){
        txtPath = new TextAreaCustom();

        txtPath.setBounds(350, 65, 400, 250);
        txtPath.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtPath.getText().equals(UIUtils.PLACEHOLDER_TEXT_USERNAME)) {
                    txtPath.setText("");
                }
               txtPath.setForeground(Color.white);
                txtPath.setBorderColor(UIUtils.COLOR_INTERACTIVE);
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txtPath.getText().isEmpty()) {
                    txtPath.setText(UIUtils.PLACEHOLDER_TEXT_USERNAME);
                }
                txtPath.setForeground(UIUtils.COLOR_OUTLINE);
                txtPath.setBorderColor(UIUtils.COLOR_OUTLINE);
            }
        });

        return txtPath;
    }

    private JTextField createUsernameTextField() {
        TextFieldUsername usernameField = new TextFieldUsername();

        usernameField.setBounds(350, 350, 250, 44);
        usernameField.setEditable(false);


        return usernameField;
    }



    private JLabel createServerButton(JPanel mainJPanel) {
        final Color[] loginButtonColors = {UIUtils.COLOR_INTERACTIVE, Color.white};

        JLabel serverButton = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = UIUtils.get2dGraphics(g);
                super.paintComponent(g2);

                Insets insets = getInsets();
                int w = getWidth() - insets.left - insets.right;
                int h = getHeight() - insets.top - insets.bottom;
                g2.setColor(loginButtonColors[0]);
                g2.fillRoundRect(insets.left, insets.top, w, h, UIUtils.ROUNDNESS, UIUtils.ROUNDNESS);

                FontMetrics metrics = g2.getFontMetrics(UIUtils.FONT_GENERAL_UI);
                int x2 = (getWidth() - metrics.stringWidth(UIUtils.BUTTON_TEXT_SERVEROPT)) / 2;
                int y2 = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
                g2.setFont(UIUtils.FONT_GENERAL_UI);
                g2.setColor(loginButtonColors[1]);
                g2.drawString(UIUtils.BUTTON_TEXT_SERVEROPT, x2, y2);
            }
        };

        serverButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                toaster.success("Multi-Client selected");



            }

            @Override
            public void mouseEntered(MouseEvent e) {
                loginButtonColors[0] = UIUtils.COLOR_INTERACTIVE_DARKER;
                loginButtonColors[1] = UIUtils.OFFWHITE;
                serverButton.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                loginButtonColors[0] = UIUtils.COLOR_INTERACTIVE;
                loginButtonColors[1] = Color.white;
                serverButton.repaint();
            }
        });

        serverButton.setBackground(UIUtils.COLOR_BACKGROUND);
        serverButton.setBounds(423, 109, 250, 44);
        serverButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return serverButton;
    }

    private JLabel createClientButton(JPanel mainJPanel) {
        final Color[] loginButtonColors = {UIUtils.COLOR_INTERACTIVE, Color.white};

        JLabel clientButton = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = UIUtils.get2dGraphics(g);
                super.paintComponent(g2);

                Insets insets = getInsets();
                int w = getWidth() - insets.left - insets.right;
                int h = getHeight() - insets.top - insets.bottom;
                g2.setColor(loginButtonColors[0]);
                g2.fillRoundRect(insets.left, insets.top, w, h, UIUtils.ROUNDNESS, UIUtils.ROUNDNESS);

                FontMetrics metrics = g2.getFontMetrics(UIUtils.FONT_GENERAL_UI);
                int x2 = (getWidth() - metrics.stringWidth(UIUtils.BUTTON_TEXT_SEND_FILE)) / 2;
                int y2 = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
                g2.setFont(UIUtils.FONT_GENERAL_UI);
                g2.setColor(loginButtonColors[1]);
                g2.drawString(UIUtils.BUTTON_TEXT_SEND_FILE, x2, y2);
            }
        };

        clientButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                toaster.success("Send File Selected");
                sendFilePanel(mainJPanel);

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                loginButtonColors[0] = UIUtils.COLOR_INTERACTIVE_DARKER;
                loginButtonColors[1] = UIUtils.OFFWHITE;
                clientButton.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                loginButtonColors[0] = UIUtils.COLOR_INTERACTIVE;
                loginButtonColors[1] = Color.white;
                clientButton.repaint();
            }
        });

        clientButton.setBackground(UIUtils.COLOR_BACKGROUND);
        clientButton.setBounds(423, 200, 250, 44);
        clientButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return clientButton;
    }

    private JLabel createChooseFileButton(JPanel mainJPanel) {
        final Color[] loginButtonColors = {UIUtils.COLOR_INTERACTIVE, Color.white};

        JLabel fileButton = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = UIUtils.get2dGraphics(g);
                super.paintComponent(g2);

                Insets insets = getInsets();
                int w = getWidth() - insets.left - insets.right;
                int h = getHeight() - insets.top - insets.bottom;
                g2.setColor(loginButtonColors[0]);
                g2.fillRoundRect(insets.left, insets.top, w, h, UIUtils.ROUNDNESS, UIUtils.ROUNDNESS);

                FontMetrics metrics = g2.getFontMetrics(UIUtils.FONT_GENERAL_UI);
                int x2 = (getWidth() - metrics.stringWidth(UIUtils.BUTTON_TEXT_CHOOSEFILE)) / 2;
                int y2 = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
                g2.setFont(UIUtils.FONT_GENERAL_UI);
                g2.setColor(loginButtonColors[1]);
                g2.drawString(UIUtils.BUTTON_TEXT_CHOOSEFILE, x2, y2);
            }
        };

        fileButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                toaster.success("Send File Selected");
                JFileChooser pilih=new JFileChooser();
                int confirm=pilih.showOpenDialog(null);
                if(confirm==JFileChooser.APPROVE_OPTION){
                    Path path=pilih.getSelectedFile().toPath();
                    txtPath.append(path.toString());
                }

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                loginButtonColors[0] = UIUtils.COLOR_INTERACTIVE_DARKER;
                loginButtonColors[1] = UIUtils.OFFWHITE;
                fileButton.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                loginButtonColors[0] = UIUtils.COLOR_INTERACTIVE;
                loginButtonColors[1] = Color.white;
                fileButton.repaint();
            }
        });

        fileButton.setBackground(UIUtils.COLOR_BACKGROUND);
        fileButton.setBounds(40, 50, 250, 44);
        fileButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return fileButton;
    }

    private JLabel createSendFileButton(JPanel mainJPanel) {
        final Color[] loginButtonColors = {UIUtils.COLOR_INTERACTIVE, Color.white};

        JLabel sendButton = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = UIUtils.get2dGraphics(g);
                super.paintComponent(g2);

                Insets insets = getInsets();
                int w = getWidth() - insets.left - insets.right;
                int h = getHeight() - insets.top - insets.bottom;
                g2.setColor(loginButtonColors[0]);
                g2.fillRoundRect(insets.left, insets.top, w, h, UIUtils.ROUNDNESS, UIUtils.ROUNDNESS);

                FontMetrics metrics = g2.getFontMetrics(UIUtils.FONT_GENERAL_UI);
                int x2 = (getWidth() - metrics.stringWidth(UIUtils.BUTTON_TEXT_SENDFILE)) / 2;
                int y2 = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
                g2.setFont(UIUtils.FONT_GENERAL_UI);
                g2.setColor(loginButtonColors[1]);
                g2.drawString(UIUtils.BUTTON_TEXT_SENDFILE, x2, y2);
            }
        };

        sendButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {

                start();
                /*
                try {
                    ServerSocket servsock = new ServerSocket(Port);



                    System.out.println("Server started on port " + servsock.getLocalPort() + "...");

                    Socket sock = servsock.accept();

                    JOptionPane.showMessageDialog(mainJPanel,"Client " + sock.getRemoteSocketAddress() + " connected to server...");
                    usernameField.setText("Client " + sock.getRemoteSocketAddress() + " connected to server...");
                    System.out.println("Client " + sock.getRemoteSocketAddress() + " connected to server...");

                    int buffer_size=1500;
                    byte[] buffer = new byte[buffer_size];

                    usernameField.setText("Attepting to send file to client...");



                    BufferedInputStream in = new BufferedInputStream(new FileInputStream(folderFile));
                    BufferedOutputStream out = new BufferedOutputStream(sock.getOutputStream());
                    out.write(buffer,0,buffer.length);
                    out.flush();
                    usernameField.setText("Send File Success");
                    toaster.success("File sent succesfully");
                    out.close();
                    servsock.close();
                    sock.close();
                    in.close();
                    out.close();
                } catch (IOException ex){
                    System.out.println("Error : " + ex);
                }*/

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                loginButtonColors[0] = UIUtils.COLOR_INTERACTIVE_DARKER;
                loginButtonColors[1] = UIUtils.OFFWHITE;
                sendButton.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                loginButtonColors[0] = UIUtils.COLOR_INTERACTIVE;
                loginButtonColors[1] = Color.white;
               sendButton.repaint();
            }
        });

        sendButton.setBackground(UIUtils.COLOR_BACKGROUND);
        sendButton.setBounds(40, 300, 250, 44);
        sendButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return sendButton;
    }

    private void start() {

        // Use SwingWorker<Void, Void> and return null from doInBackground if
        // you don't want any final result and you don't want to update the GUI
        // as the thread goes along.
        // First argument is the thread result, returned when processing finished.
        // Second argument is the value to update the GUI with via publish() and process()
        SwingWorker<Boolean, String> worker = new SwingWorker<Boolean, String>() {

            @Override
            /*
             * Note: do not update the GUI from within doInBackground.
             */
            protected Boolean doInBackground() throws Exception {
                int Port =Integer.parseInt(JOptionPane.showInputDialog("Input Your Port : "));
                String folderFile=txtPath.getText();
                try {
                    ServerSocket servsock = new ServerSocket(Port);


                    publish("Server started on port " + servsock.getLocalPort() + "...");


                    Socket sock = servsock.accept();

                    publish("Client " + sock.getRemoteSocketAddress() + " connected to server...");

                    File myFile = new File(folderFile);
                    Path filePath = Paths.get(folderFile);
                    long fileSize = Files.size(filePath);

                    publish("Attepting to send file to client...");

                    byte[] buffer = new byte[Integer.parseInt(String.valueOf(fileSize))];

                    FileInputStream fileInputStream = new FileInputStream(myFile);
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
                    bufferedInputStream.read(buffer, 0, buffer.length);

                    OutputStream outputStream = sock.getOutputStream();
                    outputStream.write(buffer, 0, buffer.length);
                    outputStream.flush();

                    System.out.println(fileSize);

                    fileInputStream.close();
                    bufferedInputStream.close();
                    outputStream.close();

                    servsock.close();
                    sock.close();

                    return true;
                } catch (IOException ex){
                    System.out.println("Error : " + ex);
                    return false;
                }
            }

            @Override
            // This will be called if you call publish() from doInBackground()
            // Can safely update the GUI here.
            protected void process(java.util.List<String> chunks) {
                String value = chunks.get(chunks.size() - 1);
                for(String string : chunks){
                    txtPath.append("\n");
                    txtPath.append(string);
                    txtPath.append("\n");
                }

            }

            @Override
            // This is called when the thread finishes.
            // Can safely update GUI here.
            protected void done() {

                try {
                    Boolean status = get();
                    if(status){
                        usernameField.setText("File Sent Successfully");
                        toaster.success("File sent succesfully");
                    }
                    else{
                        usernameField.setText("Error: File not delievered");
                    }

                } catch (InterruptedException | ExecutionException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };

        worker.execute();
    }

    private void sendFilePanel(JPanel mainJPanel){
        setVisible(false);
        mainJPanel.remove(clientButton);
        mainJPanel.remove(serverButton);
        txtPath = createTextArea();
        chooseFileButton = createChooseFileButton(mainJPanel);
        sendButton = createSendFileButton(mainJPanel);
        usernameField = createUsernameTextField();
        mainJPanel.add(usernameField);
        mainJPanel.add(txtPath);
        mainJPanel.add(chooseFileButton);
        mainJPanel.add(sendButton);

        setVisible(true);
    }

    private JLabel createQuitButton() {
        final Color[] loginButtonColors = {UIUtils.COLOR_INTERACTIVE, Color.white};

        JLabel quitButton = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = UIUtils.get2dGraphics(g);
                super.paintComponent(g2);

                Insets insets = getInsets();
                int w = getWidth() - insets.left - insets.right;
                int h = getHeight() - insets.top - insets.bottom;
                g2.setColor(loginButtonColors[0]);
                g2.fillRoundRect(insets.left, insets.top, w, h, UIUtils.ROUNDNESS, UIUtils.ROUNDNESS);

                FontMetrics metrics = g2.getFontMetrics(UIUtils.FONT_GENERAL_UI);
                int x2 = (getWidth() - metrics.stringWidth(UIUtils.BUTTON_TEXT_LOGIN)) / 2;
                int y2 = ((getHeight() - metrics.getHeight()) / 2) + metrics.getAscent();
                g2.setFont(UIUtils.FONT_GENERAL_UI);
                g2.setColor(loginButtonColors[1]);
                g2.drawString(UIUtils.BUTTON_TEXT_QUIT, x2, y2);

            }
        };

        quitButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                System.exit(0);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                loginButtonColors[0] = UIUtils.COLOR_INTERACTIVE_DARKER;
                loginButtonColors[1] = UIUtils.OFFWHITE;
                quitButton.repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                loginButtonColors[0] = UIUtils.COLOR_INTERACTIVE;
                loginButtonColors[1] = Color.white;
                quitButton.repaint();
            }
        });

        quitButton.setBackground(UIUtils.COLOR_BACKGROUND);
        quitButton.setBounds(635, 350, 150, 44);
        quitButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return quitButton;

    }
}