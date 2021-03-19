
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.util.Date;
import java.awt.*;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;

public class Student_dashboard extends javax.swing.JFrame {

    public Student_dashboard(Student s,Lms l) {
        student=s;
        lms=l;
        initComponents();
        setHomeValues();
        alreadyRegFlag=false;
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/icons/icons8_e_64px.png")));
    }
    
    private void setHomeValues(){
        nameF.setText(student.getF_name()+" "+student.getL_name());
        emailF.setText(student.getEmail());
        rollnoF.setText(student.getRoll_no());
        semesterF.setText(Integer.toString(student.getC_semester()));
        batchF.setText(Integer.toString(student.getBatch()));
        dateofbirthF.setText(student.getDateS());
        disciplineF.setText(student.getDiscipline());
    }
    
    private boolean initRegPanel(){
        courseComboBox.removeAllItems();
        Date today=new Date();
        if(today.compareTo(lms.getRegStart())>=0 && today.compareTo(lms.getRegEnd())<=0){
            coursesOffered=lms.getRegData(student);
            coursesOffered.forEach(c -> {
                courseComboBox.addItem(c.getC_name());
            });
            return true;
        }
        else{
            regPeriodL.setText("Registeration Period: Not Active");
            jButton1.setEnabled(false);
            courseComboBox.setEnabled(false);
            jPanel7.setBackground(Color.red);
            return false;
        }
    }
    
    private void initSemesterPanel(){
        semesterComboBox.removeAllItems();
        for (Semester s: student.semesters){
            semesterComboBox.addItem(s.toString());
        }
    }
    
    private void initMarks(){
        marksComboBox.removeAllItems();
        if(student.semesters.size()<1){
            return;
        }
        Semester s=student.semesters.get(student.semesters.size()-1);
        if(student.getC_semester()!=s.getSemester_no()){
            return;
        }
        for(Registeration r: s.registerations){
            marksComboBox.addItem(r.getSection().getCourse().getC_name());
        }
    }
    
    private void populateRegTable(){
        alreadyRegFlag=false;
        jButton1.setEnabled(true);
        int index=courseComboBox.getSelectedIndex();
        if (index!=-1){
            Course course=coursesOffered.get(index);
            List<Section> sec=course.sections;
            DefaultTableModel model = (DefaultTableModel)sectionTable.getModel();
            model.setRowCount(0);

            DefaultTableCellRenderer centerRenderer=new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            for(int x=0;x<5;x++){
                if(x!=1){
                    sectionTable.getColumnModel().getColumn(x).setCellRenderer(centerRenderer);
                }
            }

            for(Section s:sec){
                if(s.isOffered()==false){
                    continue;
                }
                else if(s.alreadyRegistered(student)){
                    alreadyRegFlag=true;
                    jButton1.setEnabled(false);
                    Object[] row={course.getC_code(),course.getC_name(),course.getCrhr(),s,s.getTeacher().getFullName(),true};
                    model.addRow(row);
                }
                else{
                    Object[] row={course.getC_code(),course.getC_name(),course.getCrhr(),s,s.getTeacher().getFullName(),false};
                    model.addRow(row);
                }
            }
        }
    }
    
    private void populateTranscriptTable(){
        int index=semesterComboBox.getSelectedIndex();
        if(index!=-1){
            Semester s=student.semesters.get(index);
            System.out.println(s);
            if (s!=null){
                crAtt.setText(Float.toString(s.get_crdeits_attempted()));
                crErnd.setText(Float.toString(s.get_credits_earned()));
                s.cal_sgpa();
                student.calculate_CGPA();
                sgpa.setText(Double.toString(s.getSGPA()));
                cgpa.setText(Double.toString(s.getCGPA()));
            }
            
            DefaultTableModel model = (DefaultTableModel)transcriptTable.getModel();
            model.setRowCount(0);

            DefaultTableCellRenderer centerRenderer=new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            for(int x=1;x<5;x++){
                transcriptTable.getColumnModel().getColumn(x).setCellRenderer(centerRenderer);
            }

            for(Registeration r:s.registerations){
                Object[] row={r.getSection().getCourse().getC_name(),r.getSection().getS_name(),r.getSection().getCourse().getCrhr()
                        ,r.getGrade(),Semester.grade_to_double(r.getGrade())};
                model.addRow(row);
            }
        }
    }
    
    private void populateDropTable(){
        dropB.setEnabled(true);
        withdrawB.setEnabled(true);
        Date today=new Date();
        boolean drop=true;
        boolean withdraw=true;
        
        if(today.compareTo(lms.getRegEnd())>0){
            drop=false;
            dropB.setEnabled(false);
            if(today.compareTo(lms.getWithdrawDeadline())>0){
                withdraw=false;
                withdrawB.setEnabled(false);
                dropL.setText("Withdraw Deadline Passed!");
                jPanel6.setBackground(Color.red);
            }
            else{
                dropL.setText("Withdraw Deadline: "+lms.getWithdrawDeadline());
            }
        }
        else if(today.compareTo(lms.getRegStart())<0){
            drop=false;
            dropB.setEnabled(false);
            withdrawB.setEnabled(false);
            dropL.setText("Add/Drop Period: Inactive");
            jPanel6.setBackground(Color.red);
        }
        else{
            withdrawB.setEnabled(false);
        }
        
        if(drop==true || withdraw==true){
            if(student.semesters.size()==0){
                dropB.setEnabled(false);
                return;
            }
            
            Semester sem=student.semesters.get(student.semesters.size()-1);
            
            if(sem.getSemester_no()!=student.getC_semester()){
                dropB.setEnabled(false);
                return;
            }
            
            if(sem.registerations.size()==0 || sem.allWithdrawn()){
                dropB.setEnabled(false);
            }
            
            DefaultTableModel model = (DefaultTableModel)dropTable.getModel();
            model.setRowCount(0);

            DefaultTableCellRenderer centerRenderer=new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(JLabel.CENTER);
            for(int x=0;x<5;x++){
                if(x!=1){
                    dropTable.getColumnModel().getColumn(x).setCellRenderer(centerRenderer);
                }
            }

            for(Registeration r:sem.registerations){
                if(r.getGrade().compareTo("W")!=0 && r.getGrade().compareTo("w")!=0){
                    Object[] row={r.getSection().getCourse().getC_code(),r.getSection().getCourse().getC_name(),
                    r.getSection().getCourse().getCrhr(),r.getSection().getS_name(),r.getSection().getTeacher().getFullName(),false};
                    model.addRow(row);
                }
            }
        }
    }
    
    private void populateMarksTable(){
        int index=marksComboBox.getSelectedIndex();
        if(index==-1){
            return;
        }
        
        if(student.semesters.size()<1){
            return;
        }
        Semester s=student.semesters.get(student.semesters.size()-1);
        Registeration r=s.registerations.get(index);
        
        DefaultTableModel model = (DefaultTableModel)marksTable.getModel();
        model.setRowCount(0);

        DefaultTableCellRenderer centerRenderer=new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for(int x=1;x<7;x++){
            marksTable.getColumnModel().getColumn(x).setCellRenderer(centerRenderer);
        }
        
        for(Marks m:r.marks){
            Evaluation e=m.getEvaluation();
            e.calculateStats();
            Object[] row={e.getName(),e.getWeightage(),m.getScore(),e.getTotal_marks(),e.getAvg(),e.getMin(),e.getMax()};
            model.addRow(row);
        }
        
    }
    private void initAttendance() {
        attendanceComboBox.removeAllItems();
        if(student.semesters.size()<1){
            return;
        }
        Semester s=student.semesters.get(student.semesters.size()-1);
        if(student.getC_semester()!=s.getSemester_no()){
            return;
        }
        for(Registeration r: s.registerations){
            attendanceComboBox.addItem(r.getSection().getCourse().getC_name());
        }
    }
    private void populateAttendanceTable(){
        int index=attendanceComboBox.getSelectedIndex();
        if(index==-1){
            return;
        }
        
        if(student.semesters.size()<1){
            return;
        }
        Semester s=student.semesters.get(student.semesters.size()-1);
        Registeration r=s.registerations.get(index);
        
        DefaultTableModel model = (DefaultTableModel)attendanceTable.getModel();
        model.setRowCount(0);

        DefaultTableCellRenderer centerRenderer=new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for(int x=0;x<4;x++){
            attendanceTable.getColumnModel().getColumn(x).setCellRenderer(centerRenderer);
        }
        int i=1;
        for(Attendance a:r.attendance){
            
            Object[] row={i,a.getDateAsString(),a.getDuration(),a.getPresence()};
            model.addRow(row);
            i++;
        }
    }
    private Section getSelectedSection(){
        DefaultTableModel model = (DefaultTableModel)sectionTable.getModel();
        for(int x=0;x<model.getRowCount();x++){
            if((boolean)model.getValueAt(x,5)==true){
                return (Section)model.getValueAt(x,3);
            }
        }
        return null;
    }
    
    private void addCourse(){
        Section s=getSelectedSection();
        if(s!=null){
            boolean seatsAvailable=student.registerCourse(s);
            if(seatsAvailable){
                alreadyRegFlag=true;
                jButton1.setEnabled(false);
                JOptionPane.showMessageDialog(this, "Registeration Successful!");
            }
            else if(s.alreadyObserving(student)){
                JOptionPane.showMessageDialog(this, "You are already subscribed! You will get email when seats are available!");
            }
            else{
                subscribeDialog.show();
            }
        }
    }
    
    private void dropCourse(){
        List<Registeration> reg=new ArrayList<>();
        Semester s=student.semesters.get(student.semesters.size()-1);
        DefaultTableModel model = (DefaultTableModel)dropTable.getModel();
        for(int x=0;x<model.getRowCount();x++){
            if((boolean)model.getValueAt(x,5)==true){
                Registeration r=s.findReg(model.getValueAt(x,3).toString());
                reg.add(r);
            }
        }
        for(Registeration r:reg){
            student.dropCourse(r);
        }
    }
    
    private void withdrawCourse(){
        List<Registeration> reg=new ArrayList<>();
        Semester s=student.semesters.get(student.semesters.size()-1);
        DefaultTableModel model = (DefaultTableModel)dropTable.getModel();
        for(int x=0;x<model.getRowCount();x++){
            if((boolean)model.getValueAt(x,5)==true){
                Registeration r=s.findReg(model.getValueAt(x,3).toString());
                reg.add(r);
            }
        }
        for(Registeration r:reg){
            student.withdrawCourse(r);
        }
    }
    
    private void updateProgressBar(){
        Semester s=student.semesters.get(student.semesters.size()-1);
        if(student.getC_semester()!=s.getSemester_no()){
            return;
        }
        int index=attendanceComboBox.getSelectedIndex();
        if(index==-1){
            return;
        }
        Registeration r=s.registerations.get(index);
        float percentage=r.attendancePercentage();
        
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        progressBar.setValue((int) percentage);
        if(percentage>=80){
            Color c=new Color(0,102,0);
            progressBar.setForeground(c);
        }
        else{
            progressBar.setForeground(Color.red);
        }
        progressBar.setStringPainted(true);
    }
    
    private void updateColor(JButton jb){
        Color c=new Color(233,135,33);
        Color c1=new Color(255,153,0);
        Home.setBackground(c);
        Registeration.setBackground(c);
        Attendance.setBackground(c);
        Marks.setBackground(c);
        transcript.setBackground(c);
        drop.setBackground(c);
        if(jb!=null){
            jb.setBackground(c1);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        subscribeDialog = new javax.swing.JDialog();
        jLabel18 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        Home = new javax.swing.JButton();
        Registeration = new javax.swing.JButton();
        transcript = new javax.swing.JButton();
        Attendance = new javax.swing.JButton();
        Marks = new javax.swing.JButton();
        drop = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        homeP = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        nameF = new javax.swing.JLabel();
        emailF = new javax.swing.JLabel();
        rollnoF = new javax.swing.JLabel();
        semesterF = new javax.swing.JLabel();
        batchF = new javax.swing.JLabel();
        dateofbirthF = new javax.swing.JLabel();
        disciplineF = new javax.swing.JLabel();
        registerationP = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        sectionTable = new javax.swing.JTable();
        jPanel7 = new javax.swing.JPanel();
        regPeriodL = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel17 = new javax.swing.JLabel();
        courseComboBox = new javax.swing.JComboBox<>();
        attendanceP = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        attendanceTable = new javax.swing.JTable();
        attendanceComboBox = new javax.swing.JComboBox<>();
        jLabel24 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        marksP = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        marksComboBox = new javax.swing.JComboBox<>();
        jScrollPane4 = new javax.swing.JScrollPane();
        marksTable = new javax.swing.JTable();
        jPanel9 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        transcriptP = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        semesterComboBox = new javax.swing.JComboBox<>();
        jScrollPane1 = new javax.swing.JScrollPane();
        transcriptTable = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        crAtt = new javax.swing.JLabel();
        crErnd = new javax.swing.JLabel();
        sgpa = new javax.swing.JLabel();
        cgpa = new javax.swing.JLabel();
        dropP = new javax.swing.JPanel();
        withdrawB = new javax.swing.JButton();
        dropB = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        dropTable = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        dropL = new javax.swing.JLabel();

        subscribeDialog.setAlwaysOnTop(true);
        subscribeDialog.setBounds(new java.awt.Rectangle(0, 0, 590, 130));
        subscribeDialog.setLocationByPlatform(true);
        subscribeDialog.setModal(true);
        subscribeDialog.setResizable(false);
        final Toolkit toolkit = Toolkit.getDefaultToolkit();
        final Dimension screenSize = toolkit.getScreenSize();
        final int x = (screenSize.width - subscribeDialog.getWidth()) / 2;
        final int y = (screenSize.height - subscribeDialog.getHeight()) / 2;
        subscribeDialog.setLocation(x, y);

        jLabel18.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel18.setText("Section is full! Subscribe to this section and you will get an email when seats are available");

        jButton3.setBackground(new java.awt.Color(0, 0, 0));
        jButton3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Subscribe");
        jButton3.setBorderPainted(false);
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(0, 0, 0));
        jButton4.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setText("Close");
        jButton4.setBorderPainted(false);
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout subscribeDialogLayout = new javax.swing.GroupLayout(subscribeDialog.getContentPane());
        subscribeDialog.getContentPane().setLayout(subscribeDialogLayout);
        subscribeDialogLayout.setHorizontalGroup(
            subscribeDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(subscribeDialogLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(subscribeDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(subscribeDialogLayout.createSequentialGroup()
                        .addGap(123, 123, 123)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel18))
                .addContainerGap(26, Short.MAX_VALUE))
        );
        subscribeDialogLayout.setVerticalGroup(
            subscribeDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(subscribeDialogLayout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 33, Short.MAX_VALUE)
                .addGroup(subscribeDialogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton3)
                    .addComponent(jButton4))
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("EDGILE-Student");
        setResizable(false);

        jPanel1.setBackground(new java.awt.Color(233, 135, 33));

        jPanel2.setBackground(new java.awt.Color(0, 0, 0));

        jLabel1.setFont(new java.awt.Font("Segoe UI Black", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("EDGILE");

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8_user_50px_1.png"))); // NOI18N

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
                .addGap(316, 316, 316)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        Home.setBackground(new java.awt.Color(255, 153, 0));
        Home.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        Home.setForeground(new java.awt.Color(255, 255, 255));
        Home.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8_home_page_50px.png"))); // NOI18N
        Home.setText("Home");
        Home.setBorderPainted(false);
        Home.setDebugGraphicsOptions(javax.swing.DebugGraphics.NONE_OPTION);
        Home.setFocusPainted(false);
        Home.setFocusable(false);
        Home.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Home.setMaximumSize(new java.awt.Dimension(134, 136));
        Home.setMinimumSize(new java.awt.Dimension(134, 136));
        Home.setPreferredSize(new java.awt.Dimension(82, 136));
        Home.setRequestFocusEnabled(false);
        Home.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        Home.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                HomeActionPerformed(evt);
            }
        });

        Registeration.setBackground(new java.awt.Color(233, 135, 33));
        Registeration.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        Registeration.setForeground(new java.awt.Color(255, 255, 255));
        Registeration.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8_schedule_50px.png"))); // NOI18N
        Registeration.setText("Registeration");
        Registeration.setBorderPainted(false);
        Registeration.setFocusPainted(false);
        Registeration.setFocusable(false);
        Registeration.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Registeration.setRequestFocusEnabled(false);
        Registeration.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        Registeration.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RegisterationActionPerformed(evt);
            }
        });

        transcript.setBackground(new java.awt.Color(233, 135, 33));
        transcript.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        transcript.setForeground(new java.awt.Color(255, 255, 255));
        transcript.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8_folder_50px.png"))); // NOI18N
        transcript.setText("Transcript");
        transcript.setBorderPainted(false);
        transcript.setFocusPainted(false);
        transcript.setFocusable(false);
        transcript.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        transcript.setRequestFocusEnabled(false);
        transcript.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        transcript.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                transcriptActionPerformed(evt);
            }
        });

        Attendance.setBackground(new java.awt.Color(233, 135, 33));
        Attendance.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        Attendance.setForeground(new java.awt.Color(255, 255, 255));
        Attendance.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8_test_passed_50px.png"))); // NOI18N
        Attendance.setText("Attendance");
        Attendance.setBorderPainted(false);
        Attendance.setFocusPainted(false);
        Attendance.setFocusable(false);
        Attendance.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Attendance.setRequestFocusEnabled(false);
        Attendance.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        Attendance.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                AttendanceActionPerformed(evt);
            }
        });

        Marks.setBackground(new java.awt.Color(233, 135, 33));
        Marks.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        Marks.setForeground(new java.awt.Color(255, 255, 255));
        Marks.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8_report_card_50px.png"))); // NOI18N
        Marks.setText("Marks");
        Marks.setBorderPainted(false);
        Marks.setFocusPainted(false);
        Marks.setFocusable(false);
        Marks.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        Marks.setRequestFocusEnabled(false);
        Marks.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        Marks.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                MarksActionPerformed(evt);
            }
        });

        drop.setBackground(new java.awt.Color(233, 135, 33));
        drop.setFont(new java.awt.Font("Tahoma", 1, 13)); // NOI18N
        drop.setForeground(new java.awt.Color(255, 255, 255));
        drop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8_form_50px.png"))); // NOI18N
        drop.setText("Drop/Withdraw");
        drop.setBorderPainted(false);
        drop.setFocusPainted(false);
        drop.setFocusable(false);
        drop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        drop.setRequestFocusEnabled(false);
        drop.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        drop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dropActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(Home, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(Registeration, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(Attendance, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(Marks, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(transcript, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(drop)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(1, 1, 1)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(drop)
                    .addComponent(Attendance, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(Marks, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(transcript, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(Registeration, javax.swing.GroupLayout.Alignment.TRAILING)))
            .addComponent(Home, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        jPanel4.setLayout(new java.awt.CardLayout());

        jPanel3.setBackground(new java.awt.Color(248, 216, 169));

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(51, 51, 51));
        jLabel4.setIcon(new javax.swing.ImageIcon(getClass().getResource("/home_icons/icons8_name_tag_16px.png"))); // NOI18N
        jLabel4.setText("Name:");
        jLabel4.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel6.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(51, 51, 51));
        jLabel6.setIcon(new javax.swing.ImageIcon(getClass().getResource("/home_icons/icons8_identity_theft_16px.png"))); // NOI18N
        jLabel6.setText("Roll Number:");
        jLabel6.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel8.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(51, 51, 51));
        jLabel8.setIcon(new javax.swing.ImageIcon(getClass().getResource("/home_icons/icons8_exam_16px.png"))); // NOI18N
        jLabel8.setText("Semester:");
        jLabel8.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(51, 51, 51));
        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/home_icons/icons8_batch_assign_16px_1.png"))); // NOI18N
        jLabel10.setText("Batch:");
        jLabel10.setVerticalAlignment(javax.swing.SwingConstants.TOP);

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(51, 51, 51));
        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/home_icons/icons8_department_16px.png"))); // NOI18N
        jLabel12.setText("Discipline");
        jLabel12.setToolTipText("");
        jLabel12.setVerticalAlignment(javax.swing.SwingConstants.TOP);

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

        nameF.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        nameF.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        nameF.setText("jLabel16");

        emailF.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        emailF.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        emailF.setText("jLabel16");

        rollnoF.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        rollnoF.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        rollnoF.setText("jLabel16");

        semesterF.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        semesterF.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        semesterF.setText("jLabel16");

        batchF.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        batchF.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        batchF.setText("jLabel16");

        dateofbirthF.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        dateofbirthF.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        dateofbirthF.setText("jLabel16");

        disciplineF.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        disciplineF.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        disciplineF.setText("jLabel16");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(nameF, javax.swing.GroupLayout.PREFERRED_SIZE, 419, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(249, 249, 249))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel12)
                                    .addComponent(jLabel14)
                                    .addComponent(jLabel10)
                                    .addComponent(jLabel8)
                                    .addComponent(jLabel6))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 99, Short.MAX_VALUE)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(rollnoF, javax.swing.GroupLayout.PREFERRED_SIZE, 427, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(semesterF, javax.swing.GroupLayout.PREFERRED_SIZE, 415, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(batchF, javax.swing.GroupLayout.PREFERRED_SIZE, 496, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(dateofbirthF, javax.swing.GroupLayout.PREFERRED_SIZE, 420, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(disciplineF, javax.swing.GroupLayout.PREFERRED_SIZE, 382, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                                .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 76, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(emailF, javax.swing.GroupLayout.PREFERRED_SIZE, 427, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(69, 69, 69)))
                        .addGap(172, 172, 172))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(nameF))
                .addGap(36, 36, 36)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(emailF))
                .addGap(36, 36, 36)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(rollnoF))
                .addGap(36, 36, 36)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(semesterF))
                .addGap(36, 36, 36)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(batchF))
                .addGap(36, 36, 36)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dateofbirthF))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 45, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(disciplineF))
                .addGap(105, 105, 105))
        );

        javax.swing.GroupLayout homePLayout = new javax.swing.GroupLayout(homeP);
        homeP.setLayout(homePLayout);
        homePLayout.setHorizontalGroup(
            homePLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 946, Short.MAX_VALUE)
            .addGroup(homePLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(homePLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );
        homePLayout.setVerticalGroup(
            homePLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 524, Short.MAX_VALUE)
            .addGroup(homePLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(homePLayout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        jPanel4.add(homeP, "card2");

        registerationP.setBackground(new java.awt.Color(255, 255, 255));

        sectionTable.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        sectionTable.getTableHeader().setFont(new java.awt.Font("Tahoma",Font.BOLD,14));
        sectionTable.getTableHeader().setBackground(java.awt.Color.BLACK);
        sectionTable.getTableHeader().setForeground(java.awt.Color.WHITE);
        sectionTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Course Code", "Course Name", "Credit Hours", "Section", "Teacher", "Register"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                if(alreadyRegFlag){
                    return false;
                }
                return canEdit [columnIndex];
            }
        });
        sectionTable.setFocusable(false);
        sectionTable.setGridColor(new java.awt.Color(255, 153, 0));
        sectionTable.setRowHeight(26);
        sectionTable.setSelectionBackground(new java.awt.Color(0, 0, 0));
        sectionTable.setSelectionForeground(Color.white);
        sectionTable.setShowGrid(false);
        sectionTable.setShowHorizontalLines(true);
        sectionTable.getTableHeader().setReorderingAllowed(false);
        sectionTable.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                sectionTablePropertyChange(evt);
            }
        });
        jScrollPane2.setViewportView(sectionTable);

        jPanel7.setBackground(new java.awt.Color(0, 0, 0));
        jPanel7.setForeground(new java.awt.Color(255, 255, 255));

        regPeriodL.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        regPeriodL.setForeground(new java.awt.Color(255, 255, 255));
        regPeriodL.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        regPeriodL.setText("Registeration Period: Active");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(181, 181, 181)
                .addComponent(regPeriodL, javax.swing.GroupLayout.PREFERRED_SIZE, 568, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(regPeriodL, javax.swing.GroupLayout.DEFAULT_SIZE, 40, Short.MAX_VALUE)
                .addContainerGap())
        );

        jButton1.setBackground(new java.awt.Color(0, 0, 0));
        jButton1.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jButton1.setForeground(new java.awt.Color(255, 255, 255));
        jButton1.setText("Register");
        jButton1.setBorderPainted(false);
        jButton1.setFocusPainted(false);
        jButton1.setFocusable(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel17.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel17.setText("Select Course");

        courseComboBox.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        courseComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                courseComboBoxItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout registerationPLayout = new javax.swing.GroupLayout(registerationP);
        registerationP.setLayout(registerationPLayout);
        registerationPLayout.setHorizontalGroup(
            registerationPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 946, Short.MAX_VALUE)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, registerationPLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(courseComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        registerationPLayout.setVerticalGroup(
            registerationPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, registerationPLayout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(registerationPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 38, Short.MAX_VALUE)
                    .addComponent(jLabel17)
                    .addComponent(courseComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 407, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel4.add(registerationP, "card2");

        attendanceP.setBackground(new java.awt.Color(255, 255, 255));
        attendanceP.setForeground(new java.awt.Color(255, 255, 255));

        jLabel27.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel27.setText("Attendance Percentage%");

        attendanceTable.getTableHeader().setFont(new java.awt.Font("Tahoma",Font.BOLD,14));
        attendanceTable.getTableHeader().setBackground(java.awt.Color.BLACK);
        attendanceTable.getTableHeader().setForeground(java.awt.Color.WHITE);
        attendanceTable.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        attendanceTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Lecture", "Date", "Duration", "Presence"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        attendanceTable.setFocusable(false);
        attendanceTable.setGridColor(new java.awt.Color(255, 153, 0));
        attendanceTable.setRowHeight(26);
        attendanceTable.setSelectionBackground(new java.awt.Color(0, 0, 0));
        attendanceTable.setSelectionForeground(Color.white);
        attendanceTable.setShowVerticalLines(false);
        attendanceTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane5.setViewportView(attendanceTable);
        if (attendanceTable.getColumnModel().getColumnCount() > 0) {
            attendanceTable.getColumnModel().getColumn(0).setResizable(false);
            attendanceTable.getColumnModel().getColumn(1).setResizable(false);
            attendanceTable.getColumnModel().getColumn(2).setResizable(false);
            attendanceTable.getColumnModel().getColumn(3).setResizable(false);
        }

        attendanceComboBox.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        attendanceComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        attendanceComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                attendanceComboBoxItemStateChanged(evt);
            }
        });
        attendanceComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                attendanceComboBoxActionPerformed(evt);
            }
        });

        jLabel24.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel24.setText("Select Course:");

        jPanel8.setBackground(new java.awt.Color(0, 0, 0));

        jLabel25.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(255, 255, 255));
        jLabel25.setText("Attendance");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(438, 438, 438)
                .addComponent(jLabel25)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel25)
                .addContainerGap())
        );

        progressBar.setBackground(new java.awt.Color(255, 255, 255));
        progressBar.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        progressBar.setForeground(new java.awt.Color(51, 51, 255));
        progressBar.setToolTipText("");
        progressBar.setStringPainted(true);

        javax.swing.GroupLayout attendancePLayout = new javax.swing.GroupLayout(attendanceP);
        attendanceP.setLayout(attendancePLayout);
        attendancePLayout.setHorizontalGroup(
            attendancePLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5)
            .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(attendancePLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel24)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(attendanceComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 257, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 42, Short.MAX_VALUE)
                .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, 317, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        attendancePLayout.setVerticalGroup(
            attendancePLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, attendancePLayout.createSequentialGroup()
                .addGap(0, 41, Short.MAX_VALUE)
                .addGroup(attendancePLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(progressBar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(attendancePLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(attendanceComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel27)))
                .addGap(26, 26, 26)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 384, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel4.add(attendanceP, "card4");

        marksP.setBackground(new java.awt.Color(255, 255, 255));

        jLabel23.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel23.setText("Select Course:");

        marksComboBox.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        marksComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                marksComboBoxItemStateChanged(evt);
            }
        });

        marksTable.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        marksTable.getTableHeader().setFont(new java.awt.Font("Tahoma",Font.BOLD,14));
        marksTable.getTableHeader().setBackground(java.awt.Color.BLACK);
        marksTable.getTableHeader().setForeground(java.awt.Color.WHITE);
        marksTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Evaluation", "Weightage", "Obtained Marks", "Total Marks", "Average", "Minimum", "Maximum"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        marksTable.setGridColor(new java.awt.Color(255, 153, 0));
        marksTable.setRowHeight(26);
        marksTable.setSelectionBackground(new java.awt.Color(0, 0, 0));
        marksTable.setSelectionForeground(Color.white);
        marksTable.setShowVerticalLines(false);
        marksTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane4.setViewportView(marksTable);
        if (marksTable.getColumnModel().getColumnCount() > 0) {
            marksTable.getColumnModel().getColumn(0).setResizable(false);
            marksTable.getColumnModel().getColumn(0).setPreferredWidth(150);
            marksTable.getColumnModel().getColumn(1).setResizable(false);
            marksTable.getColumnModel().getColumn(1).setPreferredWidth(5);
            marksTable.getColumnModel().getColumn(2).setResizable(false);
            marksTable.getColumnModel().getColumn(2).setPreferredWidth(5);
            marksTable.getColumnModel().getColumn(3).setResizable(false);
            marksTable.getColumnModel().getColumn(3).setPreferredWidth(5);
            marksTable.getColumnModel().getColumn(4).setResizable(false);
            marksTable.getColumnModel().getColumn(4).setPreferredWidth(5);
            marksTable.getColumnModel().getColumn(5).setResizable(false);
            marksTable.getColumnModel().getColumn(5).setPreferredWidth(5);
            marksTable.getColumnModel().getColumn(6).setResizable(false);
            marksTable.getColumnModel().getColumn(6).setPreferredWidth(5);
        }

        jPanel9.setBackground(new java.awt.Color(0, 0, 0));

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Marks");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addGap(435, 435, 435))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addContainerGap())
        );

        javax.swing.GroupLayout marksPLayout = new javax.swing.GroupLayout(marksP);
        marksP.setLayout(marksPLayout);
        marksPLayout.setHorizontalGroup(
            marksPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(marksPLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel23)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(marksComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 946, Short.MAX_VALUE)
            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        marksPLayout.setVerticalGroup(
            marksPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(marksPLayout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(marksPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(marksComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE))
        );

        jPanel4.add(marksP, "card5");

        transcriptP.setBackground(new java.awt.Color(255, 255, 255));

        jLabel16.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel16.setText("Select Semester:");

        semesterComboBox.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        semesterComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                semesterComboBoxItemStateChanged(evt);
            }
        });

        transcriptTable.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        transcriptTable.getTableHeader().setFont(new java.awt.Font("Tahoma",Font.BOLD,14));
        transcriptTable.getTableHeader().setBackground(java.awt.Color.BLACK);
        transcriptTable.getTableHeader().setForeground(java.awt.Color.WHITE);
        transcriptTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Course", "Section", "Credit Hr", "Grade", "Points"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        transcriptTable.setGridColor(new java.awt.Color(255, 153, 0));
        transcriptTable.setRowHeight(26);
        transcriptTable.setSelectionBackground(new java.awt.Color(0, 0, 0));
        transcriptTable.setShowGrid(false);
        transcriptTable.setShowHorizontalLines(true);
        transcriptTable.getTableHeader().setResizingAllowed(false);
        transcriptTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(transcriptTable);
        transcriptTable.setSelectionForeground(Color.white);
        if (transcriptTable.getColumnModel().getColumnCount() > 0) {
            transcriptTable.getColumnModel().getColumn(0).setResizable(false);
            transcriptTable.getColumnModel().getColumn(0).setPreferredWidth(150);
            transcriptTable.getColumnModel().getColumn(1).setResizable(false);
            transcriptTable.getColumnModel().getColumn(1).setPreferredWidth(5);
            transcriptTable.getColumnModel().getColumn(2).setResizable(false);
            transcriptTable.getColumnModel().getColumn(2).setPreferredWidth(3);
            transcriptTable.getColumnModel().getColumn(3).setResizable(false);
            transcriptTable.getColumnModel().getColumn(3).setPreferredWidth(3);
            transcriptTable.getColumnModel().getColumn(4).setResizable(false);
            transcriptTable.getColumnModel().getColumn(4).setPreferredWidth(4);
        }

        jPanel5.setBackground(new java.awt.Color(0, 0, 0));

        jLabel19.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setText("Credits Attempted:");

        jLabel20.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setText("Credits Earned:");

        jLabel21.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setText("SGPA:");

        jLabel22.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(255, 255, 255));
        jLabel22.setText("CGPA:");

        crAtt.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        crAtt.setForeground(new java.awt.Color(255, 255, 255));
        crAtt.setText("jLabel23");

        crErnd.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        crErnd.setForeground(new java.awt.Color(255, 255, 255));
        crErnd.setText("jLabel24");

        sgpa.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        sgpa.setForeground(new java.awt.Color(255, 255, 255));
        sgpa.setText("jLabel25");

        cgpa.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cgpa.setForeground(new java.awt.Color(255, 255, 255));
        cgpa.setText("jLabel26");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(51, 51, 51)
                .addComponent(jLabel19)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(crAtt, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel20)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(crErnd, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sgpa, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel22)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cgpa, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(jLabel20)
                    .addComponent(jLabel21)
                    .addComponent(jLabel22)
                    .addComponent(crAtt)
                    .addComponent(crErnd)
                    .addComponent(sgpa)
                    .addComponent(cgpa))
                .addContainerGap(44, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout transcriptPLayout = new javax.swing.GroupLayout(transcriptP);
        transcriptP.setLayout(transcriptPLayout);
        transcriptPLayout.setHorizontalGroup(
            transcriptPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(transcriptPLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(semesterComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 238, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(573, Short.MAX_VALUE))
            .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        transcriptPLayout.setVerticalGroup(
            transcriptPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(transcriptPLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(transcriptPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(semesterComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 351, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jPanel4.add(transcriptP, "card6");

        dropP.setBackground(new java.awt.Color(255, 255, 255));

        withdrawB.setBackground(new java.awt.Color(0, 0, 0));
        withdrawB.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        withdrawB.setForeground(new java.awt.Color(255, 255, 255));
        withdrawB.setText("Withdraw");
        withdrawB.setBorderPainted(false);
        withdrawB.setFocusPainted(false);
        withdrawB.setFocusable(false);
        withdrawB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                withdrawBActionPerformed(evt);
            }
        });

        dropB.setBackground(new java.awt.Color(0, 0, 0));
        dropB.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        dropB.setForeground(new java.awt.Color(255, 255, 255));
        dropB.setText("Drop");
        dropB.setBorderPainted(false);
        dropB.setFocusPainted(false);
        dropB.setFocusable(false);
        dropB.setMaximumSize(new java.awt.Dimension(99, 25));
        dropB.setMinimumSize(new java.awt.Dimension(99, 25));
        dropB.setPreferredSize(new java.awt.Dimension(99, 25));
        dropB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dropBActionPerformed(evt);
            }
        });

        dropTable.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        dropTable.getTableHeader().setFont(new java.awt.Font("Tahoma",Font.BOLD,14));
        dropTable.getTableHeader().setBackground(java.awt.Color.BLACK);
        dropTable.getTableHeader().setForeground(java.awt.Color.WHITE);
        dropTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Course Code", "Course Name", "Credit Hours", "Section", "Teacher", "Drop/Withdraw"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        dropTable.setGridColor(new java.awt.Color(255, 153, 0));
        dropTable.setRowHeight(26);
        dropTable.setSelectionBackground(new java.awt.Color(0, 0, 0));
        dropTable.setShowVerticalLines(false);
        dropTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(dropTable);
        dropTable.setSelectionForeground(Color.white);
        if (dropTable.getColumnModel().getColumnCount() > 0) {
            dropTable.getColumnModel().getColumn(0).setResizable(false);
            dropTable.getColumnModel().getColumn(0).setPreferredWidth(5);
            dropTable.getColumnModel().getColumn(1).setResizable(false);
            dropTable.getColumnModel().getColumn(1).setPreferredWidth(150);
            dropTable.getColumnModel().getColumn(2).setResizable(false);
            dropTable.getColumnModel().getColumn(2).setPreferredWidth(5);
            dropTable.getColumnModel().getColumn(3).setResizable(false);
            dropTable.getColumnModel().getColumn(3).setPreferredWidth(5);
            dropTable.getColumnModel().getColumn(4).setResizable(false);
            dropTable.getColumnModel().getColumn(4).setPreferredWidth(5);
            dropTable.getColumnModel().getColumn(5).setResizable(false);
            dropTable.getColumnModel().getColumn(5).setPreferredWidth(3);
        }

        jPanel6.setBackground(new java.awt.Color(0, 0, 0));
        jPanel6.setForeground(new java.awt.Color(255, 255, 255));
        jPanel6.setPreferredSize(new java.awt.Dimension(910, 62));

        dropL.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        dropL.setForeground(new java.awt.Color(255, 255, 255));
        dropL.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        dropL.setText("Add/Drop Period: Active");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(dropL, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(dropL)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout dropPLayout = new javax.swing.GroupLayout(dropP);
        dropP.setLayout(dropPLayout);
        dropPLayout.setHorizontalGroup(
            dropPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dropPLayout.createSequentialGroup()
                .addContainerGap(440, Short.MAX_VALUE)
                .addComponent(withdrawB, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(dropB, javax.swing.GroupLayout.PREFERRED_SIZE, 239, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 946, Short.MAX_VALUE)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        dropPLayout.setVerticalGroup(
            dropPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dropPLayout.createSequentialGroup()
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(dropPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(dropB, javax.swing.GroupLayout.DEFAULT_SIZE, 45, Short.MAX_VALUE)
                    .addComponent(withdrawB, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 381, Short.MAX_VALUE))
        );

        jPanel4.add(dropP, "card7");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(4, 4, 4)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void HomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_HomeActionPerformed
        jPanel4.removeAll();
        jPanel4.add(homeP);
        jPanel4.repaint();
        jPanel4.revalidate();
        updateColor(Home);
    }//GEN-LAST:event_HomeActionPerformed

    private void AttendanceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_AttendanceActionPerformed
        jPanel4.removeAll();
        jPanel4.add(attendanceP);
        jPanel4.repaint();
        jPanel4.revalidate();
        initAttendance();
        updateColor(Attendance);
    }//GEN-LAST:event_AttendanceActionPerformed

    private void MarksActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_MarksActionPerformed
        jPanel4.removeAll();
        jPanel4.add(marksP);
        jPanel4.repaint();
        jPanel4.revalidate();
        initMarks();
        updateColor(Marks);
    }//GEN-LAST:event_MarksActionPerformed

    private void dropActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dropActionPerformed
        jPanel4.removeAll();
        jPanel4.add(dropP);
        jPanel4.repaint();
        jPanel4.revalidate();
        populateDropTable();
        updateColor(drop);
    }//GEN-LAST:event_dropActionPerformed

    private void RegisterationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RegisterationActionPerformed
        boolean regperiod=initRegPanel();
        jPanel4.removeAll();
        jPanel4.add(registerationP);
        jPanel4.repaint();
        jPanel4.revalidate();
        if(regperiod){populateRegTable();}
        updateColor(Registeration);
    }//GEN-LAST:event_RegisterationActionPerformed

    private void transcriptActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_transcriptActionPerformed
        jPanel4.removeAll();
        jPanel4.add(transcriptP);
        jPanel4.repaint();
        jPanel4.revalidate();
        initSemesterPanel();
        updateColor(transcript);
    }//GEN-LAST:event_transcriptActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        addCourse();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void courseComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_courseComboBoxItemStateChanged
        populateRegTable();
    }//GEN-LAST:event_courseComboBoxItemStateChanged

    private void sectionTablePropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_sectionTablePropertyChange
        int row=sectionTable.getSelectedRow();
        DefaultTableModel model = (DefaultTableModel)sectionTable.getModel();
        for(int x=0;x<model.getRowCount();x++){
            if(x!=row){
                model.setValueAt(false, x, 5);
            }
        }
    }//GEN-LAST:event_sectionTablePropertyChange

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        subscribeDialog.setVisible(false);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        subscribeDialog.setVisible(false);
        getSelectedSection().addToSubscriberList(student);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void semesterComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_semesterComboBoxItemStateChanged
        populateTranscriptTable();
    }//GEN-LAST:event_semesterComboBoxItemStateChanged

    private void withdrawBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_withdrawBActionPerformed
        withdrawCourse();
        populateDropTable();
    }//GEN-LAST:event_withdrawBActionPerformed

    private void dropBActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dropBActionPerformed
        dropCourse();
        populateDropTable();
    }//GEN-LAST:event_dropBActionPerformed

    private void marksComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_marksComboBoxItemStateChanged
        populateMarksTable();
    }//GEN-LAST:event_marksComboBoxItemStateChanged

    private void attendanceComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_attendanceComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_attendanceComboBoxActionPerformed

    private void attendanceComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_attendanceComboBoxItemStateChanged
        // TODO add your handling code here:
        populateAttendanceTable();
        updateProgressBar();
    }//GEN-LAST:event_attendanceComboBoxItemStateChanged

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        lms.start2(this);
    }//GEN-LAST:event_jButton2ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Attendance;
    private javax.swing.JButton Home;
    private javax.swing.JButton Marks;
    private javax.swing.JButton Registeration;
    private javax.swing.JComboBox<String> attendanceComboBox;
    private javax.swing.JPanel attendanceP;
    private javax.swing.JTable attendanceTable;
    private javax.swing.JLabel batchF;
    private javax.swing.JLabel cgpa;
    private javax.swing.JComboBox<String> courseComboBox;
    private javax.swing.JLabel crAtt;
    private javax.swing.JLabel crErnd;
    private javax.swing.JLabel dateofbirthF;
    private javax.swing.JLabel disciplineF;
    private javax.swing.JButton drop;
    private javax.swing.JButton dropB;
    private javax.swing.JLabel dropL;
    private javax.swing.JPanel dropP;
    private javax.swing.JTable dropTable;
    private javax.swing.JLabel emailF;
    private javax.swing.JPanel homeP;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
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
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
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
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JComboBox<String> marksComboBox;
    private javax.swing.JPanel marksP;
    private javax.swing.JTable marksTable;
    private javax.swing.JLabel nameF;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel regPeriodL;
    private javax.swing.JPanel registerationP;
    private javax.swing.JLabel rollnoF;
    private javax.swing.JTable sectionTable;
    private javax.swing.JComboBox<String> semesterComboBox;
    private javax.swing.JLabel semesterF;
    private javax.swing.JLabel sgpa;
    private javax.swing.JDialog subscribeDialog;
    private javax.swing.JButton transcript;
    private javax.swing.JPanel transcriptP;
    private javax.swing.JTable transcriptTable;
    private javax.swing.JButton withdrawB;
    // End of variables declaration//GEN-END:variables
    private final Student student;
    private final Lms lms;
    private List<Course> coursesOffered;
    private boolean alreadyRegFlag;
}