
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.beans.PropertyChangeEvent;
import static java.lang.Character.toUpperCase;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import javax.swing.JButton;

public class Teacher_dashboard extends javax.swing.JFrame {

    public Teacher_dashboard(Teacher t, Lms l) {
        teacher=t;
        lms=l;
        initComponents();
        setHomeValues();
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/icons8_e_64px.png")));
    }
    
    private void updateColor(JButton jb){
        Color c=new Color(233,135,33);
        Color c1=new Color(255,153,0);
        jButton1.setBackground(c);
        jButton4.setBackground(c);
        jButton5.setBackground(c);
        jButton6.setBackground(c);
        jButton7.setBackground(c);
        if(jb!=null){
            jb.setBackground(c1);
        }
    }
    
    public final void setHomeValues(){
        nameF.setText(teacher.getF_name()+" "+teacher.getL_name());
        idF.setText(teacher.getEmail());
        emailF.setText(teacher.getEmail());
        departmentF.setText(teacher.getDepartment());
        rankF.setText(teacher.getRank());
        dateofbirthF.setText(teacher.getDateS());
    }
     //--------------------------------------------------------------------------------------
    private void initAttendancePanel(){
        attendanceComboBox.removeAllItems();
        for (Section s: teacher.sections){
            attendanceComboBox.addItem(s.toString() + " " +s.getCourse().getC_name());
        }
        dateChooser.setDate(new java.util.Date());
    }
    //-------------------------------------------------------------------------------------
    //doesnt work
    private void getEvaluationTypes()
    {
        marksComboBoxEval.removeAllItems();
        int index = marksComboBoxSec.getSelectedIndex();
        if(index!=-1){
            Section s= teacher.sections.get(index);
            //Evaluation e = s.evaluations.get(index);
            System.out.println(s);
            if (s!=null){
                for(Evaluation e: s.evaluations)
                {
                    System.out.println(e);
                    marksComboBoxEval.addItem(e.getName());
                }
            }
        }
    }
    private void initMarksPanel(){
        marksComboBoxSec.removeAllItems();
        for (Section s: teacher.sections){
            marksComboBoxSec.addItem(s.toString()+ " " +s.getCourse().getC_name());
        }
    }
    //--------------------------------------------------------------------------------------
    private void initEvaluationPanel() {
       evalComboBox.removeAllItems();
        for (Section s: teacher.sections){
            evalComboBox.addItem(s.toString()+ " " +s.getCourse().getC_name());
        }
    }
    //--------------------------------------------------------------------------------------
    
    private void initMarksSheetPanel() {
        marksheetComboBoxSec.removeAllItems();
        for (Section s: teacher.sections){
            marksheetComboBoxSec.addItem(s.toString()+ " " +s.getCourse().getC_name());
        }
    }
    //--------------------------------------------------------------------------------------
    private void initadd_eval_Panel(){
        addEvalComboBoxSec.removeAllItems();
        for (Section s: teacher.sections){
            addEvalComboBoxSec.addItem(s.toString()+ " " +s.getCourse().getC_name());
        }
    }
    
    private void populateMarksTable(){
        int secIndex=marksComboBoxSec.getSelectedIndex();
        int evaluationIndex=marksComboBoxEval.getSelectedIndex();
        
        if(secIndex==-1 || evaluationIndex==-1){
            return;
        }
        
        Evaluation e=teacher.sections.get(secIndex).evaluations.get(evaluationIndex);
        
        DefaultTableModel model = (DefaultTableModel)marks_Table.getModel();
        model.setRowCount(0);

        DefaultTableCellRenderer centerRenderer=new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for(int x=0;x<4;x++){
            marks_Table.getColumnModel().getColumn(x).setCellRenderer(centerRenderer);
        }

        for(Marks m:e.marks){
            Object[] row={m.getRegisteration().getStudent().getRoll_no(),m.getRegisteration().getStudent().getFullName()
            ,m,m.getScore()};
            model.addRow(row);
        }
    }
    
    private void populateAttendanceTable(){
        newAttendance.setEnabled(false);
        int secIndex=attendanceComboBox.getSelectedIndex();
        if(secIndex==-1){
            return;
        }
        Section sec=teacher.sections.get(secIndex);
        Date d=null;
        try {
            SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
            String s=df.format(dateChooser.getDate());
            d = new SimpleDateFormat("yyyy-MM-dd").parse(s);
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        
        DefaultTableModel model = (DefaultTableModel)attendance_table.getModel();
        model.setRowCount(0);
        
        TableColumn attColumn = attendance_table.getColumnModel().getColumn(4);

        JComboBox comboBox = new JComboBox();
        comboBox.addItem("P");
        comboBox.addItem("A");
        comboBox.addItem("L");
        attColumn.setCellEditor(new DefaultCellEditor(comboBox));

        DefaultTableCellRenderer centerRenderer=new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for(int x=0;x<5;x++){
            attendance_table.getColumnModel().getColumn(x).setCellRenderer(centerRenderer);
        }
        
        boolean found=false;
        
        for(Registeration r:sec.registerations){
            Attendance a=r.getAttendance(d);
            if(a!=null){
                found=true;
                Object[] row={r,r.getStudent().getFullName(),a,a.getDuration(),a.getPresence()};
                model.addRow(row);
            }
        }
        
        if(!found){
            newAttendance.setEnabled(true);
        }
    }
    
    private void populateMarksSheetTable(){
        int secIndex=marksheetComboBoxSec.getSelectedIndex();
        if(secIndex==-1){
            return;
        }
        
        Section sec=teacher.sections.get(secIndex);
        
        DefaultTableModel model = (DefaultTableModel)marksheet_Table.getModel();
        model.setRowCount(0);
        
        TableColumn gradeColumn = marksheet_Table.getColumnModel().getColumn(3);

        JComboBox comboBox = new JComboBox();
        comboBox.addItem("-");
        comboBox.addItem("A+");
        comboBox.addItem("A");
        comboBox.addItem("A-");
        comboBox.addItem("B+");
        comboBox.addItem("B");
        comboBox.addItem("B-");
        comboBox.addItem("C+");
        comboBox.addItem("C");
        comboBox.addItem("C-");
        comboBox.addItem("D+");
        comboBox.addItem("D");
        comboBox.addItem("F");
        gradeColumn.setCellEditor(new DefaultCellEditor(comboBox));

        DefaultTableCellRenderer centerRenderer=new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for(int x=0;x<4;x++){
            marksheet_Table.getColumnModel().getColumn(x).setCellRenderer(centerRenderer);
        }
        
        for(Registeration r:sec.registerations){
            float marks=r.getTotalMarks();
            Object[] row={r,r.getStudent().getFullName(),marks,r.getGrade()};
            model.addRow(row);
        }
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        addAttendanceDialog = new javax.swing.JDialog();
        jLabel27 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        defaultAttendance = new javax.swing.JComboBox<>();
        jButton8 = new javax.swing.JButton();
        duration = new javax.swing.JComboBox<>();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jPanel6 = new javax.swing.JPanel();
        homePanel = new javax.swing.JPanel();
        homePanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        idF = new javax.swing.JLabel();
        nameF = new javax.swing.JLabel();
        emailF = new javax.swing.JLabel();
        departmentF = new javax.swing.JLabel();
        rankF = new javax.swing.JLabel();
        dateofbirthF = new javax.swing.JLabel();
        attendance_panel = new javax.swing.JPanel();
        attendanceComboBox = new javax.swing.JComboBox<>();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        attendance_table = new javax.swing.JTable();
        attendanceSave = new javax.swing.JButton();
        dateChooser = new com.toedter.calendar.JDateChooser();
        jPanel4 = new javax.swing.JPanel();
        jLabel30 = new javax.swing.JLabel();
        newAttendance = new javax.swing.JButton();
        marks_panel = new javax.swing.JPanel();
        marksSaveB = new javax.swing.JButton();
        marksComboBoxSec = new javax.swing.JComboBox<>();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        marks_Table = new javax.swing.JTable();
        marksComboBoxEval = new javax.swing.JComboBox<>();
        jPanel5 = new javax.swing.JPanel();
        jLabel31 = new javax.swing.JLabel();
        evaluation_panel = new javax.swing.JPanel();
        addEvaluationB = new javax.swing.JButton();
        evalComboBox = new javax.swing.JComboBox<>();
        jLabel23 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        evaluationTable = new javax.swing.JTable();
        jPanel7 = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        marksheet_panel = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        marksheet_Table = new javax.swing.JTable();
        jButton11 = new javax.swing.JButton();
        marksheetComboBoxSec = new javax.swing.JComboBox<>();
        jLabel24 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        add_eval_panel = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        addEvalComboBoxEval = new javax.swing.JComboBox<>();
        addEvalComboBoxSec = new javax.swing.JComboBox<>();
        evalTotalMarks = new javax.swing.JTextField();
        evalName = new javax.swing.JTextField();
        addEvalSaveB = new javax.swing.JButton();
        evalTotalWeightage = new javax.swing.JTextField();
        jPanel9 = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();

        addAttendanceDialog.setTitle("Add Attendance");
        addAttendanceDialog.setAlwaysOnTop(true);
        addAttendanceDialog.setLocation(new java.awt.Point(0, 0));
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Dimension screenSize = toolkit.getScreenSize();
        final int x = (screenSize.width - addAttendanceDialog.getWidth()) / 2;
        final int y = (screenSize.height - addAttendanceDialog.getHeight()) / 2;
        addAttendanceDialog.setLocation(x, y);
        addAttendanceDialog.setMinimumSize(new java.awt.Dimension(300, 190));
        addAttendanceDialog.setModal(true);
        addAttendanceDialog.setResizable(false);

        jLabel27.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel27.setText("Duration:");

        jLabel33.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel33.setText("Default Attendaance:");

        defaultAttendance.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        defaultAttendance.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "P", "A", "L" }));
        defaultAttendance.setSelectedIndex(1);

        jButton8.setBackground(new java.awt.Color(0, 0, 0));
        jButton8.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jButton8.setForeground(new java.awt.Color(255, 255, 255));
        jButton8.setText("Add");
        jButton8.setBorderPainted(false);
        jButton8.setFocusPainted(false);
        jButton8.setFocusable(false);
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        duration.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        duration.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1", "1.5", "2", "2.5", "3", "3.5", "4" }));
        duration.setSelectedIndex(1);

        javax.swing.GroupLayout addAttendanceDialogLayout = new javax.swing.GroupLayout(addAttendanceDialog.getContentPane());
        addAttendanceDialog.getContentPane().setLayout(addAttendanceDialogLayout);
        addAttendanceDialogLayout.setHorizontalGroup(
            addAttendanceDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(addAttendanceDialogLayout.createSequentialGroup()
                .addGroup(addAttendanceDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(addAttendanceDialogLayout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addGroup(addAttendanceDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(addAttendanceDialogLayout.createSequentialGroup()
                                .addComponent(jLabel27)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(duration, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(addAttendanceDialogLayout.createSequentialGroup()
                                .addComponent(jLabel33)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(defaultAttendance, 0, 44, Short.MAX_VALUE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, addAttendanceDialogLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        addAttendanceDialogLayout.setVerticalGroup(
            addAttendanceDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(addAttendanceDialogLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(addAttendanceDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel27)
                    .addComponent(duration, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(36, 36, 36)
                .addGroup(addAttendanceDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel33)
                    .addComponent(defaultAttendance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                .addComponent(jButton8)
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("EDGILE-Teacher");
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(233, 135, 33));

        jPanel2.setBackground(new java.awt.Color(0, 0, 0));

        jLabel1.setFont(new java.awt.Font("Segoe UI Black", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("EDGILE");

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8_classroom_50px.png"))); // NOI18N

        jButton2.setBackground(new java.awt.Color(0, 0, 0));
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8_shutdown_24px_1.png"))); // NOI18N
        jButton2.setBorderPainted(false);
        jButton2.setFocusPainted(false);
        jButton2.setFocusable(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel1)
                .addGap(301, 301, 301)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jButton1.setBackground(new java.awt.Color(255, 153, 0));
        jButton1.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8_home_page_50px.png"))); // NOI18N
        jButton1.setText("Home");
        jButton1.setBorderPainted(false);
        jButton1.setDebugGraphicsOptions(javax.swing.DebugGraphics.NONE_OPTION);
        jButton1.setFocusPainted(false);
        jButton1.setFocusable(false);
        jButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton1.setRequestFocusEnabled(false);
        jButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(233, 135, 33));
        jButton4.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8_compose_50px.png"))); // NOI18N
        jButton4.setText("Evaluations");
        jButton4.setBorderPainted(false);
        jButton4.setFocusable(false);
        jButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton4.setRequestFocusEnabled(false);
        jButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setBackground(new java.awt.Color(233, 135, 33));
        jButton5.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jButton5.setForeground(new java.awt.Color(255, 255, 255));
        jButton5.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8_test_passed_50px.png"))); // NOI18N
        jButton5.setText("Attendance");
        jButton5.setBorderPainted(false);
        jButton5.setFocusPainted(false);
        jButton5.setFocusable(false);
        jButton5.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton5.setMaximumSize(new java.awt.Dimension(82, 78));
        jButton5.setMinimumSize(new java.awt.Dimension(82, 78));
        jButton5.setPreferredSize(new java.awt.Dimension(82, 78));
        jButton5.setRequestFocusEnabled(false);
        jButton5.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setBackground(new java.awt.Color(233, 135, 33));
        jButton6.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jButton6.setForeground(new java.awt.Color(255, 255, 255));
        jButton6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8_report_card_50px.png"))); // NOI18N
        jButton6.setText("Marks");
        jButton6.setBorderPainted(false);
        jButton6.setFocusPainted(false);
        jButton6.setFocusable(false);
        jButton6.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton6.setRequestFocusEnabled(false);
        jButton6.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setBackground(new java.awt.Color(233, 135, 33));
        jButton7.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        jButton7.setForeground(new java.awt.Color(255, 255, 255));
        jButton7.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8_form_50px.png"))); // NOI18N
        jButton7.setText("Mark Sheet");
        jButton7.setBorderPainted(false);
        jButton7.setFocusPainted(false);
        jButton7.setFocusable(false);
        jButton7.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton7.setRequestFocusEnabled(false);
        jButton7.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(68, 68, 68)
                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(66, 66, 66)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(51, 51, 51)
                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel6.setPreferredSize(new java.awt.Dimension(930, 510));
        jPanel6.setLayout(new java.awt.CardLayout());

        homePanel1.setBackground(new java.awt.Color(248, 216, 169));
        homePanel1.setToolTipText("");
        homePanel1.setPreferredSize(new java.awt.Dimension(906, 488));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(51, 51, 51));
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/home_icons/icons8_name_tag_16px.png"))); // NOI18N
        jLabel4.setText("Name:");
        jLabel4.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(51, 51, 51));
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/home_icons/icons8_identity_theft_16px.png"))); // NOI18N
        jLabel6.setText("Teacher ID:");
        jLabel6.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(51, 51, 51));
        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/home_icons/icons8_small_business_16px.png"))); // NOI18N
        jLabel8.setText("Department:");
        jLabel8.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(51, 51, 51));
        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/home_icons/icons8_silver_medal_16px.png"))); // NOI18N
        jLabel10.setText("Rank:");
        jLabel10.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel14.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(51, 51, 51));
        jLabel14.setIcon(new javax.swing.ImageIcon(getClass().getResource("/home_icons/icons8_calendar_30_16px.png"))); // NOI18N
        jLabel14.setText("Date of Birth:");
        jLabel14.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel15.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(51, 51, 51));
        jLabel15.setIcon(new javax.swing.ImageIcon(getClass().getResource("/home_icons/icons8_send_email_16px.png"))); // NOI18N
        jLabel15.setText("Email:");
        jLabel15.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        idF.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        idF.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        idF.setText("jLabel16");

        nameF.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        nameF.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        nameF.setText("jLabel16");

        emailF.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        emailF.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        emailF.setText("jLabel16");

        departmentF.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        departmentF.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        departmentF.setText("jLabel16");

        rankF.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        rankF.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        rankF.setText("jLabel16");

        dateofbirthF.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        dateofbirthF.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        dateofbirthF.setText("jLabel16");

        javax.swing.GroupLayout homePanel1Layout = new javax.swing.GroupLayout(homePanel1);
        homePanel1.setLayout(homePanel1Layout);
        homePanel1Layout.setHorizontalGroup(
            homePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(homePanel1Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(homePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6)
                    .addComponent(jLabel15)
                    .addComponent(jLabel8)
                    .addComponent(jLabel10)
                    .addComponent(jLabel14)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(76, 76, 76)
                .addGroup(homePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(nameF, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dateofbirthF, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rankF, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(departmentF, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(emailF, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(idF, javax.swing.GroupLayout.PREFERRED_SIZE, 375, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(296, Short.MAX_VALUE))
        );
        homePanel1Layout.setVerticalGroup(
            homePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(homePanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(homePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nameF))
                .addGap(32, 32, 32)
                .addGroup(homePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(idF))
                .addGap(32, 32, 32)
                .addGroup(homePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(emailF))
                .addGap(32, 32, 32)
                .addGroup(homePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(departmentF))
                .addGap(32, 32, 32)
                .addGroup(homePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rankF))
                .addGap(32, 32, 32)
                .addGroup(homePanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dateofbirthF))
                .addContainerGap(179, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout homePanelLayout = new javax.swing.GroupLayout(homePanel);
        homePanel.setLayout(homePanelLayout);
        homePanelLayout.setHorizontalGroup(
            homePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 930, Short.MAX_VALUE)
            .addGroup(homePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, homePanelLayout.createSequentialGroup()
                    .addContainerGap(14, Short.MAX_VALUE)
                    .addComponent(homePanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
        );
        homePanelLayout.setVerticalGroup(
            homePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 515, Short.MAX_VALUE)
            .addGroup(homePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, homePanelLayout.createSequentialGroup()
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(homePanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jPanel6.add(homePanel, "card8");

        attendance_panel.setBackground(new java.awt.Color(255, 255, 255));

        attendanceComboBox.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        attendanceComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                attendanceComboBoxItemStateChanged(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel18.setText("Select Section");

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel19.setText("Select Date");

        attendance_table.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        attendance_table.getTableHeader().setFont(new java.awt.Font("Tahoma",Font.BOLD,14));
        attendance_table.getTableHeader().setBackground(java.awt.Color.BLACK);
        attendance_table.getTableHeader().setForeground(java.awt.Color.WHITE);
        attendance_table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Roll No", "Name", "Date", "Duration", "Attendance"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        attendance_table.setFocusable(false);
        attendance_table.setSelectionForeground(Color.white);
        attendance_table.setGridColor(new java.awt.Color(255, 153, 0));
        attendance_table.setRowHeight(26);
        attendance_table.setSelectionBackground(new java.awt.Color(0, 0, 0));
        attendance_table.setShowVerticalLines(false);
        attendance_table.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(attendance_table);
        if (attendance_table.getColumnModel().getColumnCount() > 0) {
            attendance_table.getColumnModel().getColumn(0).setResizable(false);
            attendance_table.getColumnModel().getColumn(1).setResizable(false);
            attendance_table.getColumnModel().getColumn(2).setResizable(false);
            attendance_table.getColumnModel().getColumn(3).setResizable(false);
            attendance_table.getColumnModel().getColumn(4).setResizable(false);
        }

        attendanceSave.setBackground(new java.awt.Color(0, 0, 0));
        attendanceSave.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        attendanceSave.setForeground(new java.awt.Color(255, 255, 255));
        attendanceSave.setBorderPainted(false);
        attendanceSave.setFocusPainted(false);
        attendanceSave.setFocusable(false);
        attendanceSave.setLabel("Save");
        attendanceSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                attendanceSaveActionPerformed(evt);
            }
        });

        dateChooser.setDateFormatString("yyyy-MM-dd");
        dateChooser.setDate(new java.util.Date());
        dateChooser.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        dateChooser.setMaxSelectableDate(new java.util.Date(253370750485000L));
        dateChooser.setMinSelectableDate(new java.util.Date(1577822460000L));
        dateChooser.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                dateChooserPropertyChange(evt);
            }
        });

        jPanel4.setBackground(new java.awt.Color(0, 0, 0));
        jPanel4.setForeground(new java.awt.Color(255, 255, 255));

        jLabel30.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel30.setForeground(new java.awt.Color(255, 255, 255));
        jLabel30.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel30.setText("Attendance");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel30)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        newAttendance.setBackground(new java.awt.Color(0, 0, 0));
        newAttendance.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        newAttendance.setForeground(new java.awt.Color(255, 255, 255));
        newAttendance.setText("Add New Attendance");
        newAttendance.setBorderPainted(false);
        newAttendance.setEnabled(false);
        newAttendance.setFocusPainted(false);
        newAttendance.setFocusable(false);
        newAttendance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newAttendanceActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout attendance_panelLayout = new javax.swing.GroupLayout(attendance_panel);
        attendance_panel.setLayout(attendance_panelLayout);
        attendance_panelLayout.setHorizontalGroup(
            attendance_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(attendance_panelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(attendanceComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(dateChooser, javax.swing.GroupLayout.DEFAULT_SIZE, 176, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(newAttendance, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(attendanceSave, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        attendance_panelLayout.setVerticalGroup(
            attendance_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(attendance_panelLayout.createSequentialGroup()
                .addGroup(attendance_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(attendance_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(attendance_panelLayout.createSequentialGroup()
                            .addGap(55, 55, 55)
                            .addGroup(attendance_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(attendanceComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, attendance_panelLayout.createSequentialGroup()
                            .addContainerGap()
                            .addGroup(attendance_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(attendanceSave, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(newAttendance, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(dateChooser, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 362, Short.MAX_VALUE))
        );

        jPanel6.add(attendance_panel, "card3");

        marks_panel.setBackground(new java.awt.Color(255, 255, 255));

        marksSaveB.setBackground(new java.awt.Color(0, 0, 0));
        marksSaveB.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        marksSaveB.setForeground(new java.awt.Color(255, 255, 255));
        marksSaveB.setBorderPainted(false);
        marksSaveB.setFocusPainted(false);
        marksSaveB.setFocusable(false);
        marksSaveB.setLabel("Save");
        marksSaveB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                marksSaveBActionPerformed(evt);
            }
        });

        marksComboBoxSec.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        marksComboBoxSec.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                marksComboBoxSecItemStateChanged(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel20.setText("Select Section");

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel21.setText("Select Evaluation");

        marks_Table.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        marks_Table.getTableHeader().setFont(new java.awt.Font("Tahoma",Font.BOLD,14));
        marks_Table.getTableHeader().setBackground(java.awt.Color.BLACK);
        marks_Table.getTableHeader().setForeground(java.awt.Color.WHITE);
        marks_Table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Roll No", "Name", "Evaluation", "Marks"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Float.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        marks_Table.setFocusable(false);
        marks_Table.setSelectionForeground(Color.white);
        marks_Table.setGridColor(new java.awt.Color(255, 153, 0));
        marks_Table.setRowHeight(26);
        marks_Table.setSelectionBackground(new java.awt.Color(0, 0, 0));
        marks_Table.setShowVerticalLines(false);
        marks_Table.getTableHeader().setReorderingAllowed(false);
        jScrollPane2.setViewportView(marks_Table);
        if (marks_Table.getColumnModel().getColumnCount() > 0) {
            marks_Table.getColumnModel().getColumn(0).setResizable(false);
            marks_Table.getColumnModel().getColumn(0).setPreferredWidth(100);
            marks_Table.getColumnModel().getColumn(1).setResizable(false);
            marks_Table.getColumnModel().getColumn(1).setPreferredWidth(150);
            marks_Table.getColumnModel().getColumn(2).setResizable(false);
            marks_Table.getColumnModel().getColumn(2).setPreferredWidth(100);
            marks_Table.getColumnModel().getColumn(3).setResizable(false);
            marks_Table.getColumnModel().getColumn(3).setPreferredWidth(5);
        }

        marksComboBoxEval.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        marksComboBoxEval.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                marksComboBoxEvalItemStateChanged(evt);
            }
        });

        jPanel5.setBackground(new java.awt.Color(0, 0, 0));

        jLabel31.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(255, 255, 255));
        jLabel31.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel31.setText("Upload Marks");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel31, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel31)
                .addGap(19, 19, 19))
        );

        javax.swing.GroupLayout marks_panelLayout = new javax.swing.GroupLayout(marks_panel);
        marks_panel.setLayout(marks_panelLayout);
        marks_panelLayout.setHorizontalGroup(
            marks_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(marks_panelLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(marksComboBoxSec, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(marksComboBoxEval, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 191, Short.MAX_VALUE)
                .addComponent(marksSaveB, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        marks_panelLayout.setVerticalGroup(
            marks_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(marks_panelLayout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(marks_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(marks_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(marksComboBoxSec, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel21, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(marksComboBoxEval, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(marksSaveB, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 361, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel6.add(marks_panel, "card4");

        evaluation_panel.setBackground(new java.awt.Color(255, 255, 255));

        addEvaluationB.setBackground(new java.awt.Color(0, 0, 0));
        addEvaluationB.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        addEvaluationB.setForeground(new java.awt.Color(255, 255, 255));
        addEvaluationB.setText("Add Evaluation");
        addEvaluationB.setBorderPainted(false);
        addEvaluationB.setFocusPainted(false);
        addEvaluationB.setFocusable(false);
        addEvaluationB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addEvaluationBActionPerformed(evt);
            }
        });

        evalComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                evalComboBoxItemStateChanged(evt);
            }
        });

        jLabel23.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jLabel23.setText("Select Section");

        evaluationTable.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        evaluationTable.getTableHeader().setFont(new java.awt.Font("Tahoma",Font.BOLD,14));
        evaluationTable.getTableHeader().setBackground(java.awt.Color.BLACK);
        evaluationTable.getTableHeader().setForeground(java.awt.Color.WHITE);
        evaluationTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Evaluation", "Type", "Weightage", "Total Marks"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        evaluationTable.setFocusable(false);
        evaluationTable.setSelectionForeground(Color.white);
        evaluationTable.setGridColor(new java.awt.Color(255, 153, 0));
        evaluationTable.setRowHeight(26);
        evaluationTable.setSelectionBackground(new java.awt.Color(0, 0, 0));
        evaluationTable.setShowVerticalLines(false);
        evaluationTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(evaluationTable);
        if (evaluationTable.getColumnModel().getColumnCount() > 0) {
            evaluationTable.getColumnModel().getColumn(0).setResizable(false);
            evaluationTable.getColumnModel().getColumn(0).setPreferredWidth(150);
            evaluationTable.getColumnModel().getColumn(1).setResizable(false);
            evaluationTable.getColumnModel().getColumn(1).setPreferredWidth(100);
            evaluationTable.getColumnModel().getColumn(2).setResizable(false);
            evaluationTable.getColumnModel().getColumn(2).setPreferredWidth(5);
            evaluationTable.getColumnModel().getColumn(3).setResizable(false);
            evaluationTable.getColumnModel().getColumn(3).setPreferredWidth(5);
        }

        jPanel7.setBackground(new java.awt.Color(0, 0, 0));

        jLabel32.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(255, 255, 255));
        jLabel32.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel32.setText("Evaluations");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel32)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton3.setBackground(new java.awt.Color(0, 0, 0));
        jButton3.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Save");
        jButton3.setBorderPainted(false);
        jButton3.setFocusPainted(false);
        jButton3.setFocusable(false);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout evaluation_panelLayout = new javax.swing.GroupLayout(evaluation_panel);
        evaluation_panel.setLayout(evaluation_panelLayout);
        evaluation_panelLayout.setHorizontalGroup(
            evaluation_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 930, Short.MAX_VALUE)
            .addGroup(evaluation_panelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(evalComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addEvaluationB)
                .addContainerGap())
            .addComponent(jPanel7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        evaluation_panelLayout.setVerticalGroup(
            evaluation_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(evaluation_panelLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(evaluation_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addEvaluationB, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(evalComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 390, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel6.add(evaluation_panel, "card5");

        marksheet_panel.setBackground(new java.awt.Color(255, 255, 255));

        marksheet_Table.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        marksheet_Table.getTableHeader().setFont(new java.awt.Font("Tahoma",Font.BOLD,14));
        marksheet_Table.getTableHeader().setBackground(java.awt.Color.BLACK);
        marksheet_Table.getTableHeader().setForeground(java.awt.Color.WHITE);
        marksheet_Table.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Roll No", "Name", "Total Marks", "Grade"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        marksheet_Table.setFocusable(false);
        marksheet_Table.setSelectionForeground(Color.white);
        marksheet_Table.setGridColor(new java.awt.Color(255, 153, 0));
        marksheet_Table.setRowHeight(26);
        marksheet_Table.setSelectionBackground(new java.awt.Color(0, 0, 0));
        marksheet_Table.setShowVerticalLines(false);
        marksheet_Table.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(marksheet_Table);
        if (marksheet_Table.getColumnModel().getColumnCount() > 0) {
            marksheet_Table.getColumnModel().getColumn(0).setResizable(false);
            marksheet_Table.getColumnModel().getColumn(1).setResizable(false);
            marksheet_Table.getColumnModel().getColumn(2).setResizable(false);
            marksheet_Table.getColumnModel().getColumn(3).setResizable(false);
        }

        jButton11.setBackground(new java.awt.Color(0, 0, 0));
        jButton11.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        jButton11.setForeground(new java.awt.Color(255, 255, 255));
        jButton11.setBorderPainted(false);
        jButton11.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jButton11.setDefaultCapable(false);
        jButton11.setFocusPainted(false);
        jButton11.setFocusable(false);
        jButton11.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButton11.setLabel("Save");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        marksheetComboBoxSec.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        marksheetComboBoxSec.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                marksheetComboBoxSecItemStateChanged(evt);
            }
        });

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel24.setText("Select Section");

        jPanel8.setBackground(new java.awt.Color(0, 0, 0));

        jLabel12.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel12.setText("Marks Sheet");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel12, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel12)
                .addContainerGap())
        );

        javax.swing.GroupLayout marksheet_panelLayout = new javax.swing.GroupLayout(marksheet_panel);
        marksheet_panel.setLayout(marksheet_panelLayout);
        marksheet_panelLayout.setHorizontalGroup(
            marksheet_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(marksheet_panelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel24)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(marksheetComboBoxSec, javax.swing.GroupLayout.PREFERRED_SIZE, 242, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 930, Short.MAX_VALUE)
        );
        marksheet_panelLayout.setVerticalGroup(
            marksheet_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(marksheet_panelLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(marksheet_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(marksheetComboBoxSec, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE))
        );

        jPanel6.add(marksheet_panel, "card6");

        add_eval_panel.setBackground(new java.awt.Color(255, 255, 255));

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel16.setText("Evaluation Type:");

        jLabel26.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel26.setText("Total Weightage:");

        jLabel25.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel25.setText("Total Marks:");

        jLabel28.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel28.setText("Section:");

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel17.setText("Evaluation Name:");

        addEvalComboBoxEval.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        addEvalComboBoxEval.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Quiz", "Assignment", "Sessional", "Project", "Presentation", "Classroom Participation", "Final" }));

        addEvalComboBoxSec.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        evalTotalMarks.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        evalName.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        addEvalSaveB.setBackground(new java.awt.Color(0, 0, 0));
        addEvalSaveB.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        addEvalSaveB.setForeground(new java.awt.Color(255, 255, 255));
        addEvalSaveB.setText("Save");
        addEvalSaveB.setBorderPainted(false);
        addEvalSaveB.setFocusPainted(false);
        addEvalSaveB.setFocusable(false);
        addEvalSaveB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addEvalSaveBActionPerformed(evt);
            }
        });

        evalTotalWeightage.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel28)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel26)
                                    .addComponent(jLabel25))
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGap(22, 22, 22)
                                        .addComponent(addEvalComboBoxSec, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel3Layout.createSequentialGroup()
                                        .addGap(21, 21, 21)
                                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(evalTotalWeightage, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(evalTotalMarks, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel17)
                                    .addComponent(jLabel16))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(addEvalComboBoxEval, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(evalName, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(285, 285, 285)
                        .addComponent(addEvalSaveB, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(addEvalComboBoxEval, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(40, 40, 40)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(evalName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(46, 46, 46)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel25)
                    .addComponent(evalTotalMarks, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(44, 44, 44)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel26)
                    .addComponent(evalTotalWeightage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(44, 44, 44)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(addEvalComboBoxSec, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(39, 39, 39)
                .addComponent(addEvalSaveB)
                .addContainerGap(42, Short.MAX_VALUE))
        );

        jPanel9.setBackground(new java.awt.Color(0, 0, 0));

        jLabel29.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(255, 255, 255));
        jLabel29.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel29.setText("Add Evaluation");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(15, Short.MAX_VALUE)
                .addComponent(jLabel29)
                .addContainerGap())
        );

        javax.swing.GroupLayout add_eval_panelLayout = new javax.swing.GroupLayout(add_eval_panel);
        add_eval_panel.setLayout(add_eval_panelLayout);
        add_eval_panelLayout.setHorizontalGroup(
            add_eval_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        add_eval_panelLayout.setVerticalGroup(
            add_eval_panelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(add_eval_panelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.add(add_eval_panel, "card7");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, 515, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void addEvaluationBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addEvaluationBActionPerformed
        jPanel6.removeAll();
        jPanel6.add(add_eval_panel);
        jPanel6.repaint();
        jPanel6.revalidate();
        initadd_eval_Panel();
    }//GEN-LAST:event_addEvaluationBActionPerformed

    private void marksComboBoxEvalItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_marksComboBoxEvalItemStateChanged
        populateMarksTable();
    }//GEN-LAST:event_marksComboBoxEvalItemStateChanged

    private void marksComboBoxSecItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_marksComboBoxSecItemStateChanged
        getEvaluationTypes();
    }//GEN-LAST:event_marksComboBoxSecItemStateChanged

    private void evalComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_evalComboBoxItemStateChanged
        int index=evalComboBox.getSelectedIndex();
        if(index==-1){
            return;
        }
        Section s = teacher.sections.get(index);
        
        DefaultTableModel model = (DefaultTableModel)evaluationTable.getModel();
        model.setRowCount(0);

        DefaultTableCellRenderer centerRenderer=new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for(int x=1;x<4;x++){
            evaluationTable.getColumnModel().getColumn(x).setCellRenderer(centerRenderer);
        }

        for(Evaluation e:s.evaluations){
            Object[] row={e.getName(),e.getType(),e.getWeightage(),e.getTotal_marks()};
            model.addRow(row);
        }
    }//GEN-LAST:event_evalComboBoxItemStateChanged

    private void addEvalSaveBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addEvalSaveBActionPerformed
        int index=addEvalComboBoxSec.getSelectedIndex();
        if(index==-1){
            evalName.setEnabled(false);
            evalTotalMarks.setEnabled(false);
            evalTotalWeightage.setEnabled(false);
            addEvalComboBoxEval.setEnabled(false);
            return;
        }
        Section s=teacher.sections.get(index);
        String name=evalName.getText();
        String type=addEvalComboBoxEval.getSelectedItem().toString();
        int totalMarks=Integer.parseInt(evalTotalMarks.getText());
        float totalWeightage=Float.parseFloat(evalTotalWeightage.getText());
        
        try {
            s.addEvaluation(name, type, totalWeightage, totalMarks);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        ActionEvent e=null;
        jButton4ActionPerformed(e); //calling evaluation function back
    }//GEN-LAST:event_addEvalSaveBActionPerformed

    private void marksSaveBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_marksSaveBActionPerformed
        if(marksComboBoxSec.getSelectedIndex()==-1 || marksComboBoxEval.getSelectedIndex()==-1){
            return;
        }
        
        DefaultTableModel model = (DefaultTableModel)marks_Table.getModel();
        for(int x=0;x<model.getRowCount();x++){
            Marks m=(Marks)model.getValueAt(x,2);
            float score=(float)model.getValueAt(x,3);
            m.getRegisteration().upDateMarks(m, score);
        }
    }//GEN-LAST:event_marksSaveBActionPerformed

    private void attendanceSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_attendanceSaveActionPerformed
       if(attendanceComboBox.getSelectedIndex()==-1){
            return;
        }
        
        DefaultTableModel model = (DefaultTableModel)attendance_table.getModel();
        for(int x=0;x<model.getRowCount();x++){
            Attendance a=(Attendance)model.getValueAt(x,2);
            char presence=model.getValueAt(x, 4).toString().charAt(0);
            presence=Character.toUpperCase(presence);
            float duration=(float)model.getValueAt(x,3);
            Registeration r=(Registeration)model.getValueAt(x, 0);
            
            r.updateAttendance(a, presence, duration);
        }
    }//GEN-LAST:event_attendanceSaveActionPerformed

    private void newAttendanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newAttendanceActionPerformed
        addAttendanceDialog.setVisible(true);
    }//GEN-LAST:event_newAttendanceActionPerformed

    private void dateChooserPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_dateChooserPropertyChange
        populateAttendanceTable();
    }//GEN-LAST:event_dateChooserPropertyChange

    private void attendanceComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_attendanceComboBoxItemStateChanged
        PropertyChangeEvent a=null;
        dateChooserPropertyChange(a);
    }//GEN-LAST:event_attendanceComboBoxItemStateChanged

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        int index=attendanceComboBox.getSelectedIndex();
        if(index==-1){
            return;
        }
        Section sec=teacher.sections.get(index);
        SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
        String s=df.format(dateChooser.getDate());
        float d=Float.parseFloat(duration.getSelectedItem().toString());
        char att=defaultAttendance.getSelectedItem().toString().charAt(0);
        sec.addAttendance(s,att,d);
        addAttendanceDialog.setVisible(false);
        populateAttendanceTable();
    }//GEN-LAST:event_jButton8ActionPerformed

    private void marksheetComboBoxSecItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_marksheetComboBoxSecItemStateChanged
        populateMarksSheetTable();
    }//GEN-LAST:event_marksheetComboBoxSecItemStateChanged

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        int secIndex=marksheetComboBoxSec.getSelectedIndex();
        if(secIndex==-1){
            return;
        }
        
        Section s=teacher.sections.get(secIndex);
        DefaultTableModel model = (DefaultTableModel)marksheet_Table.getModel();
        
        for(int x=0;x<model.getRowCount();x++){
            Registeration r=(Registeration)model.getValueAt(x, 0);
            String grade=model.getValueAt(x, 3).toString();
            r.assignGrade(grade);
        }
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        jPanel6.removeAll();
        jPanel6.add(marksheet_panel);
        jPanel6.repaint();
        jPanel6.revalidate();
        initMarksSheetPanel();
        updateColor(jButton7);
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        jPanel6.removeAll();
        jPanel6.add(marks_panel);
        jPanel6.repaint();
        jPanel6.revalidate();
        initMarksPanel();
        updateColor(jButton6);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        jPanel6.removeAll();
        jPanel6.add(attendance_panel);
        jPanel6.repaint();
        jPanel6.revalidate();
        initAttendancePanel();
        updateColor(jButton5);
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        jPanel6.removeAll();
        jPanel6.add(evaluation_panel);
        jPanel6.repaint();
        jPanel6.revalidate();
        initEvaluationPanel();
        updateColor(jButton4);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        jPanel6.removeAll();
        jPanel6.add(homePanel);
        jPanel6.repaint();
        jPanel6.revalidate();
        updateColor(jButton1);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        lms.start2(this);
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        if(evalComboBox.getSelectedIndex()==-1){
            return;
        }
        
        Section s=teacher.sections.get(evalComboBox.getSelectedIndex());
        DefaultTableModel model = (DefaultTableModel)evaluationTable.getModel();
        Evaluation e=null;
        float w=0;
        int m=0;
        
        for(int x=0;x<model.getRowCount();x++){
            e=s.evaluations.get(x);
            w=Float.parseFloat(model.getValueAt(x, 2).toString());
            m=Integer.parseInt(model.getValueAt(x, 3).toString());
            e.updateEvaluation(w, m);
        }
        
    }//GEN-LAST:event_jButton3ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDialog addAttendanceDialog;
    private javax.swing.JComboBox<String> addEvalComboBoxEval;
    private javax.swing.JComboBox<String> addEvalComboBoxSec;
    private javax.swing.JButton addEvalSaveB;
    private javax.swing.JButton addEvaluationB;
    private javax.swing.JPanel add_eval_panel;
    private javax.swing.JComboBox<String> attendanceComboBox;
    private javax.swing.JButton attendanceSave;
    private javax.swing.JPanel attendance_panel;
    private javax.swing.JTable attendance_table;
    private com.toedter.calendar.JDateChooser dateChooser;
    private javax.swing.JLabel dateofbirthF;
    private javax.swing.JComboBox<String> defaultAttendance;
    private javax.swing.JLabel departmentF;
    private javax.swing.JComboBox<String> duration;
    private javax.swing.JLabel emailF;
    private javax.swing.JComboBox<String> evalComboBox;
    private javax.swing.JTextField evalName;
    private javax.swing.JTextField evalTotalMarks;
    private javax.swing.JTextField evalTotalWeightage;
    private javax.swing.JTable evaluationTable;
    private javax.swing.JPanel evaluation_panel;
    private javax.swing.JPanel homePanel;
    private javax.swing.JPanel homePanel1;
    private javax.swing.JLabel idF;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JComboBox<String> marksComboBoxEval;
    private javax.swing.JComboBox<String> marksComboBoxSec;
    private javax.swing.JButton marksSaveB;
    private javax.swing.JTable marks_Table;
    private javax.swing.JPanel marks_panel;
    private javax.swing.JComboBox<String> marksheetComboBoxSec;
    private javax.swing.JTable marksheet_Table;
    private javax.swing.JPanel marksheet_panel;
    private javax.swing.JLabel nameF;
    private javax.swing.JButton newAttendance;
    private javax.swing.JLabel rankF;
    // End of variables declaration//GEN-END:variables
    private Teacher teacher;
    private Lms lms;
}