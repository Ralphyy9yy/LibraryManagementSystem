/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author Ralph Giann Suquib
 */
public final class IssueBookForm extends javax.swing.JFrame {

    /**
     * Creates new form IssueBookForm
     */
    public IssueBookForm() {
        initComponents();
        loadMembers();
        loadBooks();
       displayIssuedBooks();
    }
    
    private void loadMembers() {
    try {
        Class.forName("com.mysql.cj.jdbc.Driver");
         con1 = DriverManager.getConnection("jdbc:mysql://localhost/librarydb", "root", "");

        insert = con1.prepareStatement("SELECT member_id, name FROM members");
        ResultSet rs = insert.executeQuery();

        jComboBox1.removeAllItems(); // Clear previous items

        while (rs.next()) {
            String member = rs.getInt("member_id") + " - " + rs.getString("name");
            jComboBox1.addItem(member);
        }

        rs.close();
        insert.close();
        con1.close();
    } catch (ClassNotFoundException | SQLException ex) {
        JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
    
    private void loadBooks(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con1 = DriverManager.getConnection("jdbc:mysql://localhost/librarydb","root","");
            insert = con1.prepareStatement("SELECT book_id,title from books where status = 1");
            ResultSet rs = insert.executeQuery();
            jComboBox2.removeAllItems();
            while(rs.next()){
                String books = rs.getInt("book_id") + " - " + rs.getString("title");
                jComboBox2.addItem(books);
            }
            rs.close();
            insert.close();
          con1.close();
            
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(IssueBookForm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void issueBook(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
             con1 = DriverManager.getConnection("jdbc:mysql://localhost/librarydb","root","");
              if (jComboBox1.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a member!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Check if a book is selected
        if (jComboBox2.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Please select a book!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
             String members = jComboBox1.getSelectedItem().toString();
             String book = jComboBox2.getSelectedItem().toString();
             if(members.isEmpty() || book.isEmpty()){
                 JOptionPane.showMessageDialog(this, "Members and book should not be empty!","Error",JOptionPane.ERROR_MESSAGE);
                 return;
             }
             int memberID = Integer.parseInt(members.split(" - ")[0]);
             int bookID = Integer.parseInt(book.split(" - ")[0]);
             insert = con1.prepareStatement("INSERT INTO issuedBooks(member_id,book_id,issue_date) VALUES(?,?,NOW())");
             insert.setInt(1, memberID);
             insert.setInt(2,bookID);
             insert.executeUpdate();
             
             insert = con1.prepareStatement("UPDATE books set status = 'Issued' where book_id = ?");
             insert.setInt(1,bookID);
             insert.executeUpdate();
             
             JOptionPane.showMessageDialog(this, "Book Issued Successfully");
             con1.close();
             loadBooks();
             displayIssuedBooks();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(IssueBookForm.class.getName()).log(Level.SEVERE, null, ex);
        }
           
        
    }
    
    public void returnBook(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con1 = DriverManager.getConnection("jdbc:mysql://localhost/librarydb", "root", "");
            int selectedRow = jTable2.getSelectedRow();
            if(selectedRow == -1){
                JOptionPane.showMessageDialog(this, "Select a book to return");
                return;
            }
            int issueID =Integer.parseInt(jTable2.getValueAt(selectedRow,0).toString());
            int bookID =Integer.parseInt(jTable2.getValueAt(selectedRow,2).toString());
           
           
           


            

        insert = con1.prepareStatement("DELETE FROM issuedBooks where issued_id = ?");
        insert.setInt(1, issueID);
        insert.executeUpdate();
        
        insert = con1.prepareStatement("UPDATE books set status = 'Available' where book_id = ?");
        insert.setInt(1, bookID);
        insert.executeUpdate();
        
       JOptionPane.showMessageDialog(this, "Book returned Successfully!");
       con1.close();
       
       loadBooks();
       displayIssuedBooks();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(IssueBookForm.class.getName()).log(Level.SEVERE, null, ex);
             JOptionPane.showMessageDialog(this, "Error returning book: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
         
    }
    
    public void displayIssuedBooks(){
        try {
            
            Class.forName("com.mysql.cj.jdbc.Driver");
             con1 = DriverManager.getConnection("jdbc:mysql://localhost/librarydb", "root", "");
             insert = con1.prepareStatement("SELECT ib.issued_id, m.name, b.title, b.book_id, ib.issue_date "
        + "FROM issuedBooks ib "
        + "JOIN members m ON ib.member_id = m.member_id "
        + "JOIN books b ON ib.book_id = b.book_id");
             

              
              ResultSet rs = insert.executeQuery();
               DefaultTableModel df = (DefaultTableModel)jTable2.getModel();
                
               df.setRowCount(0);
                
             while(rs.next()){
                 Object[] row  = {
                     rs.getInt("issued_id"),
                     rs.getString("name"),
                      rs.getInt("book_id"),
                     rs.getString("title"),
                    
                     rs.getString("issue_date")
                 };
                 df.addRow(row);
             }
               
               
             con1.close();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(IssueBookForm.class.getName()).log(Level.SEVERE, null, ex);
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

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel1.setText("Issue & Return Books");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", " " }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jLabel2.setText("Select Member");

        jLabel3.setText("Select Book");

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox2ActionPerformed(evt);
            }
        });

        jButton1.setText("Issue Book");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Return Book");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Back");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "IssuedID", "MEMBER NAME", "bookID", "BOOK TITLE", "ISSUE DATE"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTable2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(130, 130, 130))
            .addGroup(layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(57, 57, 57)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jComboBox1, 0, 76, Short.MAX_VALUE)
                            .addComponent(jComboBox2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(84, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jLabel1)
                .addGap(32, 32, 32)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(31, 31, 31)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2)
                    .addComponent(jButton3))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    Connection con1;
    PreparedStatement insert;
    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed

           
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
       
        issueBook();

      
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
      returnBook();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
       DashboardForm dash = new DashboardForm();
       dash.setVisible(true);
       this.dispose();
    }//GEN-LAST:event_jButton3ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(IssueBookForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(IssueBookForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(IssueBookForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(IssueBookForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new IssueBookForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    // End of variables declaration//GEN-END:variables
}
