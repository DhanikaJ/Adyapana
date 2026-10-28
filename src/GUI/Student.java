package GUI;

/**
 *
 * @author Dhanika
 */
import Model.MySQL;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public final class Student extends javax.swing.JPanel {

    private SideBar sidebar;
    private HashMap<String, String> statusMap = new HashMap<>();
    private String selectedAddressId;

    /**
     * Creates new form Scheduling
     */
    public Student(SideBar sidebar) {
        initComponents();
        this.sidebar = sidebar;
        loadStatus();
        loadStatusfilter();
        generateStudentId();
        jRadioButton1.setSelected(true);
        loadStudentCards("");

        jTextField8.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filterStudentCards();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filterStudentCards();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filterStudentCards();
            }
        });

    }

    public void setSelectedAddressId(String addressId) {
        this.selectedAddressId = addressId; // Store the selected address ID
        jTextField7.setText(addressId); // Optional: Show ID for confirmation
    }

    private void loadStatus() {

        try {

            ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `status`");

            Vector<String> vector = new Vector<>();
            vector.add("Select");

            while (resultSet.next()) {
                vector.add(resultSet.getString("status"));
                statusMap.put(resultSet.getString("status"), resultSet.getString("id"));
            }

            DefaultComboBoxModel model = new DefaultComboBoxModel(vector);
            jComboBox1.setModel(model);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadStatusfilter() {

        try {

            ResultSet resultSet = MySQL.executeSearch("SELECT * FROM `status`");

            Vector<String> vector = new Vector<>();
            vector.add("Select");

            while (resultSet.next()) {
                vector.add(resultSet.getString("status"));
                statusMap.put(resultSet.getString("status"), resultSet.getString("id"));
            }

            DefaultComboBoxModel model = new DefaultComboBoxModel(vector);
            jComboBox2.setModel(model);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void generateStudentId() {
        try {
            // Query to get the maximum studentId
            String query = "SELECT MAX(studentId) AS maxId FROM student";
            ResultSet rs = MySQL.executeSearch(query);

            String nextId = "1"; // Default ID if no entries exist
            if (rs.next() && rs.getString("maxId") != null) {
                int maxId = Integer.parseInt(rs.getString("maxId"));
                nextId = String.valueOf(maxId + 1); // Increment the ID
            }

            // Set the generated ID to jTextField1
            jTextField1.setText(nextId);
            jTextField1.setEditable(false); // Make the field non-editable
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error generating student ID!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createAccount() {
        try {
            // Collect inputs from the form
            String studentId = jTextField1.getText();
            String fName = jTextField2.getText().trim();
            String lName = jTextField3.getText().trim();
            String nic = jTextField4.getText().trim();
            String email = jTextField5.getText().trim();
            String mobile = jTextField6.getText().trim();
            String enrollmentDate = java.time.LocalDate.now().toString();
            String statusId = "1"; // Default active status
            String dob = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            dob = sdf.format(jDateChooser1.getDate());
            String addre = jTextField7.getText();

            if (studentId.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your ID", "Error", JOptionPane.WARNING_MESSAGE);
            } else if (fName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your First Name", "Error", JOptionPane.WARNING_MESSAGE);
            } else if (lName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your Last Name", "Error", JOptionPane.WARNING_MESSAGE);
            } else if (nic.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select your NIC", "Error", JOptionPane.WARNING_MESSAGE);
            } else if (jDateChooser1.getDate() != null) {
                JOptionPane.showMessageDialog(this, "Please select your Date of Birth", "Error", JOptionPane.WARNING_MESSAGE);
            } else if (email.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your Mobile number", "Error", JOptionPane.WARNING_MESSAGE);
            } else if (!email.matches("^(?=.{1,64}@)[A-Za-z0-9\\+_-]+(\\.[A-za-z0-9\\+_-]+)*@[^-][A-za-z0-9\\+-]+(\\.[A-za-z0-9\\+-]+)*(\\.[A-Za-z]{2,})$")) {
                JOptionPane.showMessageDialog(this, "Please enter a valid Email", "Error", JOptionPane.WARNING_MESSAGE);
            } else if (mobile.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter your Mobile number", "Error", JOptionPane.WARNING_MESSAGE);
            } else if (!mobile.matches("^07[01245678]{1}[0-9]{7}$")) {
                JOptionPane.showMessageDialog(this, "Please enter a valid Mobile number", "Error", JOptionPane.WARNING_MESSAGE);
            } else if (buttonGroup1.getSelection() == null) {
                JOptionPane.showMessageDialog(this, "Please select your Gender", "Error", JOptionPane.WARNING_MESSAGE);
            } else if (statusId.equals("Select Status")) {
                JOptionPane.showMessageDialog(this, "Please select your Status", "Error", JOptionPane.WARNING_MESSAGE);
            } else if (addre.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select your Address", "Error", JOptionPane.WARNING_MESSAGE);
            } else {

            }

            String genderId = buttonGroup1.getSelection().getActionCommand();

            // Check email and mobile uniqueness
            String checkQuery = "SELECT * FROM student WHERE email = '" + email + "' OR mobile = '" + mobile + "'";
            ResultSet rs = MySQL.executeSearch(checkQuery);
            if (rs.next()) {
                JOptionPane.showMessageDialog(this, "Email or Mobile already exists!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Insert student into database
            String insertQuery = "INSERT INTO student (studentId, fName, lName, nic, dob, email, mobile, address_id, gender_id, enrollmentDate, Status_id) "
                    + "VALUES ('" + studentId + "', '" + fName + "', '" + lName + "', '" + nic + "', '" + dob + "', '" + email + "', '" + mobile + "', '" + selectedAddressId + "', '" + genderId + "', '" + enrollmentDate + "', '" + statusId + "')";

            MySQL.executeIUD(insertQuery);

            // Success Message
            JOptionPane.showMessageDialog(this, "Student account created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadStudentCards(""); // Refresh student list in GUI
            clearAllFields(); // Reset form inputs
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error while creating the account: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel2 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextField4 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jTextField5 = new javax.swing.JTextField();
        jTextField6 = new javax.swing.JTextField();
        jRadioButton1 = new javax.swing.JRadioButton();
        jLabel8 = new javax.swing.JLabel();
        jRadioButton2 = new javax.swing.JRadioButton();
        jLabel9 = new javax.swing.JLabel();
        jTextField7 = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jLabel10 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jTextField8 = new javax.swing.JTextField();
        jComboBox2 = new javax.swing.JComboBox<>();
        jLabel12 = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(1620, 920));
        setLayout(new java.awt.BorderLayout(5, 5));

        jPanel2.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanel2.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jPanel2.setPreferredSize(new java.awt.Dimension(1400, 781));

        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel1.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel1.setText("ID");

        jTextField1.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jLabel2.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel2.setText("First Name");

        jTextField2.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jLabel3.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel3.setText("Last Name");

        jTextField3.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jLabel4.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel4.setText("NIC");

        jTextField4.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jLabel5.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel5.setText("DOB");

        jDateChooser1.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jLabel6.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel6.setText("Email");

        jLabel7.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel7.setText("Mobile");

        jTextField5.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jTextField6.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jRadioButton1.setText("Male");
        jRadioButton1.setActionCommand("1");

        jLabel8.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel8.setText("Gender");

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jRadioButton2.setText("Female");
        jRadioButton2.setActionCommand("2");

        jLabel9.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel9.setText("Address");

        jTextField7.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N

        jButton1.setFont(new java.awt.Font("Poppins", 1, 14)); // NOI18N
        jButton1.setText("SELECT");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setBackground(new java.awt.Color(255, 186, 5));
        jButton2.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jButton2.setText("CREATE ACCOUNT");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jButton3.setText("EDIT ACCOUNT");
        jButton3.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(255, 186, 5), 3, true));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(0, 0, 0));
        jButton4.setFont(new java.awt.Font("Poppins", 1, 18)); // NOI18N
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setText("CLEAR ALL");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jLabel10.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jLabel10.setText("Status");

        jComboBox1.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addGap(73, 73, 73)
                        .addComponent(jRadioButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addComponent(jRadioButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel4)
                            .addGap(60, 60, 60)
                            .addComponent(jTextField4))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel3)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jTextField3))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel5)
                            .addGap(55, 55, 55)
                            .addComponent(jDateChooser1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel2)
                                .addComponent(jLabel1))
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jTextField2, javax.swing.GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE)
                                .addComponent(jTextField1)))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel6)
                            .addGap(48, 48, 48)
                            .addComponent(jTextField5, javax.swing.GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(jLabel7)
                            .addGap(39, 39, 39)
                            .addComponent(jTextField6, javax.swing.GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel9)
                                .addComponent(jLabel10))
                            .addGap(29, 29, 29)
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jTextField7)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(71, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 289, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(69, 69, 69))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(121, 121, 121))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 41, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jRadioButton1)
                    .addComponent(jRadioButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 34, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 36, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(jTextField7, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
                .addComponent(jButton2)
                .addGap(18, 18, 18)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton4)
                .addGap(20, 20, 20))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        add(jPanel2, java.awt.BorderLayout.CENTER);

        jPanel4.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanel4.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jPanel4.setPreferredSize(new java.awt.Dimension(1168, 752));

        jScrollPane2.setAutoscrolls(true);
        jScrollPane2.setFont(new java.awt.Font("Poppins", 0, 12)); // NOI18N
        jScrollPane2.setHorizontalScrollBar(null);

        jPanel3.setAutoscrolls(true);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1503, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 872, Short.MAX_VALUE)
        );

        jScrollPane2.setViewportView(jPanel3);

        jLabel11.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        jLabel11.setText("SEARCH STUDENT ");

        jTextField8.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField8KeyReleased(evt);
            }
        });

        jComboBox2.setFont(new java.awt.Font("Poppins", 0, 14)); // NOI18N
        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox2ActionPerformed(evt);
            }
        });

        jLabel12.setFont(new java.awt.Font("Poppins", 0, 18)); // NOI18N
        jLabel12.setText("Status");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addComponent(jLabel11)
                .addGap(27, 27, 27)
                .addComponent(jTextField8, javax.swing.GroupLayout.DEFAULT_SIZE, 324, Short.MAX_VALUE)
                .addGap(141, 141, 141)
                .addComponent(jLabel12)
                .addGap(18, 18, 18)
                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 248, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(150, 150, 150))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1136, Short.MAX_VALUE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel11)
                        .addComponent(jTextField8, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel12)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2)
                .addContainerGap())
        );

        add(jPanel4, java.awt.BorderLayout.EAST);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        AddressView address = new AddressView(null, true, this, null);
        address.setVisible(true);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        createAccount();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        try {
            // Collect inputs from the form
            String studentId = jTextField1.getText();
            String fName = jTextField2.getText().trim();
            String lName = jTextField3.getText().trim();
            String nic = jTextField4.getText().trim();
            String email = jTextField5.getText().trim();
            String mobile = jTextField6.getText().trim();
            String addressId = jTextField7.getText().trim(); // Address ID
            String genderId = buttonGroup1.getSelection().getActionCommand(); // Male=1, Female=2
            String statusName = (String) jComboBox1.getSelectedItem(); // Status name

            // Validate mandatory fields
            if (fName.isEmpty() || email.isEmpty() || mobile.isEmpty() || addressId.isEmpty() || statusName.equals("Select")) {
                JOptionPane.showMessageDialog(this, "Please fill in all mandatory fields!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Validate the selected status
            String statusId = statusMap.get(statusName); // Get status ID from the map
            if (statusId == null) {
                JOptionPane.showMessageDialog(this, "Invalid status selected!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Validate Date of Birth
            String dob = null;
            if (jDateChooser1.getDate() != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                dob = sdf.format(jDateChooser1.getDate());
            } else {
                JOptionPane.showMessageDialog(this, "Please select a valid date of birth!", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Update student in the database
            String updateQuery = "UPDATE student SET "
                    + "fName = '" + fName + "', "
                    + "lName = '" + lName + "', "
                    + "nic = '" + nic + "', "
                    + "dob = '" + dob + "', "
                    + "email = '" + email + "', "
                    + "mobile = '" + mobile + "', "
                    + "address_id = '" + addressId + "', "
                    + "gender_id = '" + genderId + "', "
                    + "status_id = '" + statusId + "' "
                    + "WHERE studentId = '" + studentId + "'";
            MySQL.executeIUD(updateQuery);

            // Success Message
            JOptionPane.showMessageDialog(this, "Student account updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            loadStudentCards(""); // Refresh student list in the GUI
            jButton2.setEnabled(true);
            clearAllFields();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error while updating the account: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        clearAllFields();
        jButton2.setEnabled(true);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jTextField8KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField8KeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField8KeyReleased

    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
        loadStudentCards("");
    }//GEN-LAST:event_jComboBox2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    private javax.swing.JTextField jTextField8;
    // End of variables declaration//GEN-END:variables

    private void clearAllFields() {
        jTextField1.setText("");
        jTextField2.setText("");
        jTextField3.setText("");
        jTextField4.setText("");
        jDateChooser1.setDate(null);
        jTextField5.setText("");
        jTextField6.setText("");
        if (jComboBox1.getItemCount() > 0) {
            jComboBox1.setSelectedIndex(0);
        }
        buttonGroup1.clearSelection();
        jTextField7.setText("");
        generateStudentId();
    }

    private void loadStudentCards(String searchQuery) {
        String selectedStatus = (String) jComboBox2.getSelectedItem();

        // If "Select" or no status is chosen, treat it as no status filter
        boolean statusFilterApplied = selectedStatus != null && !selectedStatus.equals("Select");

        try {
            // Modify the query based on the search input
            String query = "SELECT * FROM student";
            // Combine filters dynamically
            if (!searchQuery.isEmpty() || statusFilterApplied) {
                query += " WHERE ";
                if (!searchQuery.isEmpty()) {
                    query += "(LOWER(fName) LIKE '%" + searchQuery + "%' "
                            + "OR LOWER(lName) LIKE '%" + searchQuery + "%' "
                            + "OR LOWER(email) LIKE '%" + searchQuery + "%' "
                            + "OR LOWER(mobile) LIKE '%" + searchQuery + "%' "
                            + "OR studentId LIKE '%" + searchQuery + "%')";
                }
                if (!searchQuery.isEmpty() && statusFilterApplied) {
                    query += " AND ";
                }
                if (statusFilterApplied) {
                    query += "status_id = (SELECT id FROM status WHERE status = '" + selectedStatus + "')";
                }
            }

            ResultSet rs = MySQL.executeSearch(query);

            jPanel3.removeAll();

            int x = 10;
            int y = 40;
            int cardWidth = 300;
            int cardHeight = 250;
            int horizontalGap = 20;
            int verticalGap = 20;
            int panelWidth = jPanel3.getWidth();

            while (rs.next()) {
                String studentId = rs.getString("studentId");
                String fName = rs.getString("fName");
                String lName = rs.getString("lName");
                String email = rs.getString("email");
                String nic = rs.getString("nic");
                String mobile = rs.getString("mobile");
                String status = rs.getString("status_id");
                String address = rs.getString("address_id");
                String enrollmentdate = rs.getString("enrollmentDate");

                JPanel card = new JPanel();
                card.setBounds(x, y, cardWidth, cardHeight);
                card.setLayout(null);
                card.setBackground(Color.WHITE);
                card.setBorder(BorderFactory.createLineBorder(Color.BLACK));

                JLabel idLabel = new JLabel("ID : " + studentId);
                idLabel.setFont(new Font("Poppins", Font.BOLD, 14));
                idLabel.setBounds(10, 10, cardWidth - 20, 20);
                card.add(idLabel);

                JLabel nameLabel = new JLabel("Name : " + fName + " " + lName);
                nameLabel.setFont(new Font("Poppins", Font.PLAIN, 14));
                nameLabel.setBounds(10, 40, cardWidth - 20, 20);
                card.add(nameLabel);

                JLabel emailLabel = new JLabel("Email : " + email);
                emailLabel.setFont(new Font("Poppins", Font.PLAIN, 14));
                emailLabel.setBounds(10, 70, cardWidth - 20, 20);
                card.add(emailLabel);

                JLabel nicLabel = new JLabel("NIC: " + nic);
                nicLabel.setFont(new Font("Poppins", Font.PLAIN, 14));
                nicLabel.setBounds(10, 100, cardWidth - 20, 20);
                card.add(nicLabel);

                JLabel mobileLabel = new JLabel("Mobile : " + mobile);
                mobileLabel.setFont(new Font("Poppins", Font.PLAIN, 14));
                mobileLabel.setBounds(10, 130, cardWidth - 20, 20);
                card.add(mobileLabel);

                JLabel statusLabel = new JLabel("Status : " + status);
                statusLabel.setFont(new Font("Poppins", Font.PLAIN, 14));
                statusLabel.setBounds(10, 160, cardWidth - 20, 20);
                card.add(statusLabel);

                JLabel addressLabel = new JLabel("Address ID : " + address);
                addressLabel.setFont(new Font("Poppins", Font.PLAIN, 14));
                addressLabel.setBounds(10, 190, cardWidth - 20, 20);
                card.add(addressLabel);

                JLabel enrollmentdateLabel = new JLabel("Enrollment Date : " + enrollmentdate);
                enrollmentdateLabel.setFont(new Font("Poppins", Font.PLAIN, 14));
                enrollmentdateLabel.setBounds(10, 220, cardWidth - 20, 20);
                card.add(enrollmentdateLabel);

                card.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 2) { // Double-click detected
                            try {
                                // Load the student's data
                                String query = "SELECT * FROM student WHERE studentId = '" + studentId + "'";
                                ResultSet rs = MySQL.executeSearch(query);

                                if (rs.next()) {
                                    jTextField1.setText(rs.getString("studentId"));
                                    jTextField2.setText(rs.getString("fName"));
                                    jTextField3.setText(rs.getString("lName"));
                                    jTextField4.setText(rs.getString("nic"));
                                    jDateChooser1.setDate(rs.getDate("dob"));
                                    jTextField5.setText(rs.getString("email"));
                                    jTextField6.setText(rs.getString("mobile"));
                                    jTextField7.setText(rs.getString("address_id"));
                                    buttonGroup1.setSelected(rs.getString("gender_id").equals("1") ? jRadioButton1.getModel() : jRadioButton2.getModel(), true);

                                    // Set status
                                    String statusId = rs.getString("status_id");
                                    String statusName = null;

                                    // Find status name from the map
                                    for (Map.Entry<String, String> entry : statusMap.entrySet()) {
                                        if (entry.getValue().equals(statusId)) {
                                            statusName = entry.getKey();
                                            break;
                                        }
                                    }

                                    if (statusName != null) {
                                        jComboBox1.setSelectedItem(statusName); // Select the status in the combo box
                                    } else {
                                        jComboBox1.setSelectedIndex(0); // Default to "Select" if no match found
                                    }
                                    jButton2.setEnabled(false);
                                } else {
                                    JOptionPane.showMessageDialog(jPanel3, "Student data not found!", "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                JOptionPane.showMessageDialog(jPanel3, "Error loading student data!", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                });

                jPanel3.add(card);

                x += cardWidth + horizontalGap;

                if (x + cardWidth > panelWidth) {
                    x = 10;
                    y += cardHeight + verticalGap;
                }
            }

            int requiredHeight = y + cardHeight + verticalGap;
            jPanel3.setPreferredSize(new Dimension(panelWidth, requiredHeight));
            jPanel3.revalidate();
            jPanel3.repaint();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading student cards!", "Error", JOptionPane.ERROR_MESSAGE);
        }

    }

    private void filterStudentCards() {
        String searchQuery = jTextField8.getText().trim().toLowerCase();

        // If the search field is empty, load all cards
        if (searchQuery.isEmpty()) {
            loadStudentCards(""); // Load all cards
        } else {
            loadStudentCards(searchQuery); // Filter cards based on the search query
        }
    }

}
