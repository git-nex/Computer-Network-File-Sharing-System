package client;
import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

/**
 * Client Class. Connects to the server using socket programming. 
 * Client is able to upload, download and query the server's list of files.
 * Additional feature: password-protected file upload.
 * @authors Aayush Verma (BT19CSE012 IIITN) , Ansh Garewal (BT19CSE001 IIITN).
 */
public class Client extends javax.swing.JFrame {
    private Socket socket;
    private String fileName;
    private PrintStream output;
    private final String fileNotFound = "404 Not Found";
    private BufferedReader serverInput;
    private boolean connected = false;
    private DataInputStream clientData;
    private final String fileDirectorySplit;
    private final DefaultListModel<String> listModel;
    private String downloadfileName;
    private String password;
    
    /**
     * Creates new form Client
     * @throws java.io.IOException
     */
    public Client() throws IOException {
        initComponents();
        setTitle("Client"); 
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        socket = null;
        output = null;
        serverInput = null;
        
        outputTextArea.setFocusable(false);
        outputTextArea.setEditable(false);
        queryButton.setEnabled(false);
        uploadButton.setEnabled(false);
        downloadButton.setEnabled(false);
        listModel = new DefaultListModel<>();
        
        String os = System.getProperty("os.name");
        if (os.startsWith("Win")) fileDirectorySplit = "\\";
        else fileDirectorySplit = "/";
        
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent ev) {    
                int exit = JOptionPane.showConfirmDialog(null, "Are you sure you want to exit?", "Exit", JOptionPane.YES_NO_OPTION);
                if (exit == 0){
                    try {
                        if (connected){
                            output.println("4");
                            output.close();
                            serverInput.close();
                            clientData.close();
                            socket.close();
                        }
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(null, "Something went wrong.", "Error", JOptionPane.ERROR_MESSAGE);
                        dispose();
                        System.exit(0);
                    }
                    dispose();
                    System.exit(0);
                }      
            }
        });
        
        list.getSelectionModel().addListSelectionListener(e ->{
            try {    
                downloadfileName = list.getSelectedValue().toString();  
                downloadButton.setEnabled(true);
            } catch (Exception ex) {  }
        
        });
        
        list.addFocusListener(new FocusAdapter(){
            @Override
            public void focusLost(FocusEvent e){
                list = (JList) e.getComponent();
                list.clearSelection();
            }
        });
    }

    
    @SuppressWarnings("unchecked")
    
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        socketPanel = new javax.swing.JPanel();
        ipLabel = new javax.swing.JLabel();
        ipInput = new javax.swing.JTextField();
        portLabel = new javax.swing.JLabel();
        portInput = new javax.swing.JTextField();
        connectButton = new javax.swing.JButton();
        cmdPanel = new javax.swing.JPanel();
        serverPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        outputTextArea = new javax.swing.JTextArea();
        serverLabel = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        list = new javax.swing.JList();
        queryButton = new javax.swing.JButton();
        uploadButton = new javax.swing.JButton();
        downloadButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 0, 0));
        jPanel1.setForeground(new java.awt.Color(255, 153, 153));

        socketPanel.setBackground(new java.awt.Color(255, 204, 204));

        ipLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); 
        ipLabel.setText("IP Address:");

        portLabel.setFont(new java.awt.Font("Tahoma", 0, 12)); 
        portLabel.setText("Port:");

        connectButton.setBackground(new java.awt.Color(204, 0, 102));
        connectButton.setFont(new java.awt.Font("Tahoma", 0, 12)); 
        connectButton.setForeground(new java.awt.Color(255, 255, 255));
        connectButton.setText("Connect");
        connectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout socketPanelLayout = new javax.swing.GroupLayout(socketPanel);
        socketPanel.setLayout(socketPanelLayout);
        socketPanelLayout.setHorizontalGroup(
            socketPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(socketPanelLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(ipLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ipInput, javax.swing.GroupLayout.PREFERRED_SIZE, 156, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(portLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(portInput, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(connectButton, javax.swing.GroupLayout.PREFERRED_SIZE, 109, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(30, Short.MAX_VALUE))
        );
        socketPanelLayout.setVerticalGroup(
            socketPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, socketPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(socketPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ipLabel)
                    .addComponent(ipInput, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(portLabel)
                    .addComponent(portInput, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(connectButton, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10))
        );

        cmdPanel.setBackground(new java.awt.Color(255, 153, 153));

        serverPanel.setBackground(new java.awt.Color(255, 153, 153));

        outputTextArea.setColumns(20);
        outputTextArea.setRows(5);
        jScrollPane1.setViewportView(outputTextArea);

        serverLabel.setFont(new java.awt.Font("Arial Black", 0, 14)); 
        serverLabel.setText("Server Responses:");

        list.setFont(new java.awt.Font("Arial", 0, 12)); 
        list.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        list.setSelectionBackground(new java.awt.Color(102, 204, 255));
        jScrollPane2.setViewportView(list);

        queryButton.setBackground(new java.awt.Color(153, 0, 51));
        queryButton.setFont(new java.awt.Font("Tahoma", 0, 12));
        queryButton.setForeground(new java.awt.Color(255, 255, 255));
        queryButton.setText("Get list of server's files");
        queryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                queryButtonActionPerformed(evt);
            }
        });

        uploadButton.setFont(new java.awt.Font("Tahoma", 0, 12)); 
        uploadButton.setText("Upload File");
        uploadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                uploadButtonActionPerformed(evt);
            }
        });

        downloadButton.setFont(new java.awt.Font("Tahoma", 0, 12)); 
        downloadButton.setText("Download File");
        downloadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                downloadButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout serverPanelLayout = new javax.swing.GroupLayout(serverPanel);
        serverPanel.setLayout(serverPanelLayout);
        serverPanelLayout.setHorizontalGroup(
            serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(serverPanelLayout.createSequentialGroup()
                .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 425, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(serverLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(queryButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(serverPanelLayout.createSequentialGroup()
                        .addComponent(uploadButton, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(downloadButton, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)))
                .addContainerGap())
        );
        serverPanelLayout.setVerticalGroup(
            serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, serverPanelLayout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(serverLabel)
                    .addComponent(queryButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(serverPanelLayout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(uploadButton, javax.swing.GroupLayout.DEFAULT_SIZE, 34, Short.MAX_VALUE)
                            .addComponent(downloadButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jScrollPane1)))
        );

        javax.swing.GroupLayout cmdPanelLayout = new javax.swing.GroupLayout(cmdPanel);
        cmdPanel.setLayout(cmdPanelLayout);
        cmdPanelLayout.setHorizontalGroup(
            cmdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cmdPanelLayout.createSequentialGroup()
                .addComponent(serverPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        cmdPanelLayout.setVerticalGroup(
            cmdPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(cmdPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(serverPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(cmdPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(104, 104, 104)
                        .addComponent(socketPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(socketPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(cmdPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }

    /**
     * Connect to server on input IP Address and Port Number
     * @param evt 
     */
    private void connectButtonActionPerformed(java.awt.event.ActionEvent evt) {
        String IP = ipInput.getText().trim();
        int port;
        
        try {
            port = Integer.parseInt(portInput.getText().trim());
            socket = new Socket(IP, port);
            output = new PrintStream(socket.getOutputStream());
            serverInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            clientData = new DataInputStream(socket.getInputStream());
            
            
            outputTextArea.append(serverInput.readLine() + "\n");
            
            ipInput.setFocusable(false);
            portInput.setFocusable(false);
            connected = true;
            connectButton.setText("Connected");
            connectButton.setEnabled(false);
            queryButton.setEnabled(true);
            uploadButton.setEnabled(true);
            outputTextArea.setFocusable(true);
            socketPanel.setBackground(new java.awt.Color(255, 153, 153));
            cmdPanel.setBackground(new java.awt.Color(255, 204, 204));

        } catch (NumberFormatException | IOException ex ) {
            JOptionPane.showMessageDialog(this, "Error: " + ex, "Error Message", JOptionPane.ERROR_MESSAGE);
            try {
                Client c = new Client();
            } catch (IOException ex1) {
                JOptionPane.showMessageDialog(this, "Error: " + ex1, "Error Message", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        }
    }

    private void downloadButtonActionPerformed(java.awt.event.ActionEvent evt) {
        output.println("3");
        output.println(downloadfileName);
        String response;
        try {
            response = serverInput.readLine();
           
            if (!response.equals(fileNotFound)){
                download(downloadfileName);
                list.clearSelection();
                downloadButton.setEnabled(false);
            }
            else {
                list.clearSelection();
                outputTextArea.append(response);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex, "Error Message", JOptionPane.ERROR_MESSAGE);
        }     
    }

    private void uploadButtonActionPerformed(java.awt.event.ActionEvent evt) {
            output.println("1");
            upload();
        
    }

    private void queryActionPerformed(java.awt.event.ActionEvent evt) {
        
    }

    private void queryButtonActionPerformed(java.awt.event.ActionEvent evt) {
        output.println("2");
        String files;
        if (!listModel.isEmpty()){
            listModel.clear();
        }
        try {
            files = serverInput.readLine();
            String serverlist[] = files.split(",");
            for (String file: serverlist){
                listModel.addElement(file.trim());
            }
            list.setModel(listModel);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Could not get list of files from server", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Client.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        
        java.awt.EventQueue.invokeLater(() -> {
            try {
                new Client().setVisible(true);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Error: " + ex, "Error Message", JOptionPane.ERROR_MESSAGE);
            }
        });         
    }
    
   
    public void upload() {
        try {           
            
            JFileChooser browser = new JFileChooser();
            int response = browser.showOpenDialog(null);
            
            if (response == JFileChooser.APPROVE_OPTION){
                
                File uploadFile = browser.getSelectedFile();
                int start = uploadFile.getAbsolutePath().lastIndexOf(fileDirectorySplit);
                fileName = uploadFile.getAbsolutePath().substring(start+1);
                
                if(!uploadFile.exists()) {
                    outputTextArea.append(fileNotFound);
                    return;
                }

                byte[] bytes = new byte[(int) uploadFile.length()];

                FileInputStream fileInputStream = new FileInputStream(uploadFile);
                BufferedInputStream buffer = new BufferedInputStream(fileInputStream);

                DataInputStream dataInput = new DataInputStream(buffer);
                dataInput.readFully(bytes, 0, bytes.length);

                   
                OutputStream outputStream = socket.getOutputStream();

                
                DataOutputStream dataOutput = new DataOutputStream(outputStream);
                dataOutput.writeUTF(uploadFile.getName());
                dataOutput.writeLong(bytes.length);
                dataOutput.write(bytes, 0, bytes.length);
                dataOutput.flush();
                outputTextArea.append("\n" + fileName + " successfully uploaded to server.\n");  
                
            } else {
                outputTextArea.append("\nUpload was cancelled\n");
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error: " + e, "Error Message", JOptionPane.ERROR_MESSAGE);
            outputTextArea.append("Error: " + e +"\n");
        }
    }

    /**
     
     * @param fileName Name of the file that the client wants to download.
     * @throws java.io.FileNotFoundException
     */
    public void download(String fileName) throws FileNotFoundException, IOException {
        int bytesRead;
 
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setFileFilter(new FolderFilter());
        
        int response = chooser.showSaveDialog(null);
        
        if (response == JFileChooser.APPROVE_OPTION){
            
            fileName = clientData.readUTF();
            File folder = chooser.getSelectedFile();
            try (FileOutputStream fileOutput = new FileOutputStream(folder+ fileDirectorySplit +fileName)) {
                long size = clientData.readLong();
                byte[] buffer = new byte[1024];
                while (size > 0 && (bytesRead = clientData.read(buffer, 0, (int) Math.min(buffer.length, size))) != -1) {
                    fileOutput.write(buffer, 0, bytesRead);
                    size -= bytesRead;
                }
                fileOutput.close();
                outputTextArea.append("\n" + fileName +" received from Server.");

            }catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Error: " + e, "Error Message", JOptionPane.ERROR_MESSAGE);
            }
        }        
        else {
            outputTextArea.append("\nDownload cancelled");
        }
        downloadButton.setEnabled(false);
    }
    
    private String protectedPermission(){
        int protect = JOptionPane.showConfirmDialog(null, "Do you want to password-protect this file?", "Password Protection", JOptionPane.YES_NO_OPTION);
        int yes = JOptionPane.YES_OPTION;
        
        if (protect == yes){
            JPasswordField jpf = new JPasswordField(20);
            JOptionPane.showConfirmDialog(null, jpf, "Enter File Password", JOptionPane.OK_OPTION);
            return jpf.getText();
        }
        return null;
    }

    
    private javax.swing.JPanel cmdPanel;
    private javax.swing.JButton connectButton;
    private javax.swing.JButton downloadButton;
    private javax.swing.JTextField ipInput;
    private javax.swing.JLabel ipLabel;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JList list;
    private javax.swing.JTextArea outputTextArea;
    private javax.swing.JTextField portInput;
    private javax.swing.JLabel portLabel;
    private javax.swing.JButton queryButton;
    private javax.swing.JLabel serverLabel;
    private javax.swing.JPanel serverPanel;
    private javax.swing.JPanel socketPanel;
    private javax.swing.JButton uploadButton;

}
class FolderFilter extends javax.swing.filechooser.FileFilter {

    @Override
    public boolean accept(File f) {
        return f.isDirectory();
    }

    @Override
    public String getDescription() {
        return "File Directory";
    }
}