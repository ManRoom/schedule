Skip to content
Search or jump to…

Pull requests
Issues
Marketplace
Explore
 
@ManRoom 
ManRoom
/
Light-schedule
0
00
Code
Issues
Pull requests
Actions
Projects
Wiki
Security
Insights
Settings
Light-schedule/Schedule
@ManRoom
ManRoom Update Schedule
Latest commit 90a9f5c on 24 Jun 2019
 History
 1 contributor
2580 lines (2105 sloc)  107 KB
  
package tkmp.schedule;

import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.*;
import java.awt.*;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import java.util.*;
import java.util.stream.Stream;

public class Main {

    public static void main(String[] args) {
        System.setProperty("file.encoding", "UTF-8");
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (Exception e) {
            e.printStackTrace();
        }

        TableDate isStart = new TableDate();
        isStart.signInFrame();
        isStart.mainFrame();
    }


    static class TableDate extends JFrame implements ActionListener {
        static final int VARIABLE_MATH_OPERATION = 1;
        private static final String ENCODING_UTF8 = "UTF-8";

        final static String FILE_OF_TABLE_BS = "DoneSchedule.txt",
                FILE_CHECK_SAVE_DATE = "CheckForFile.txt",
                FILE_OF_NAMES = "NamesList.txt",
                FILE_OF_GROUPS = "GroupsList.txt",
                FILE_OF_SUBJECTS = "SubjectsList.txt",
                FILE_OF_REPOSITORY = "Repository.txt",
                FILE_OF_USER_DATE = "UserOfDate.txt",
                FILE_OF_DATA = "data.txt",
                FILE_OF_OUT_SOURCE = "Out_source.txt";
        static JTable TableSchedule;
        static JPanel lowPanel,
                commonPanel;
        JFrame frameOfTable;

        static JFrame mainFrame;
        static JFrame authorization;
        static JTable tableOfCurrentlyChange;

        static JButton openFormSubjects,
                openFormNames,
                beginBuild,
                openFormGroups,
                viewSavedSchedule,
                addDateToFile,
                createSchedule,
                deleteDateFromFiles,
                sortOnClick,
                signIn,
                signUpAccount,
                signUp,
                printSavedTable,
                printTableOfHours,
                openCellOfTable,
                backUpOfData,
                recoveryOfDate,
                importOfTablePath,
                deleteSelectedRow,
                writeInTable,
                deleteTable,
                exportOfTables;
        static JLabel infoAboutOperation,
                infoEnter,
                infoRepeat,
                rule,
                imageBack,
                infoSI_SU;

        JScrollPane pane;
        JTextField isText,
                textForGroups,
                textForNames,
                textForSubjects;
        static JTextField rules;

        static String DataForTable = "",
                stringWrite = "",
                stringWriteSort = "",
                checkSignIn = "",
                condition,
                dateFromFile = "";

        String[] listForNames;
        String[] listForGroups;
        String[] listForSubjects;
        String line = "";

        int linesCount = 0,
                linesCountForNew = 0,
                lineCountForNames = 0,
                linesCountsForGroups = 0,
                linesCountsForSubjects = 0,
                countLinesGet = 0,
                linesCounter = 0;
        static JComboBox<String> boxNames,
                boxGroups,
                boxSubjects,
                sortBoxNames,
                sortBoxGroups;

        final String MASSAGE_ERROR_NOT_FOUND_FILE = "Отсутствует файл",
                NAME_ERROR_SUBJECTS = "Дисциплины",
                NAME_ERROR_GROUPS = "Группы",
                NAME_ERROR_NAMES = "Names",
                NAME_ERROR_TABLE = "Таблица составления расписания",
                NAME_ERROR_TABLE_OF_SAVE = "Таблица сохранения расписания",
                INFO_ABOUT_WORK = "Предварительно необходимо " +
                        "выбрать параметр для сортировки",
                ATTENTION_NOT_SELECTED_TABLE = "Предварительно необходимо " +
                        "открыть таличную часть для " +
                        "дальнейшей печати!",
                INFO_RECOVERY = "<html> Вы действительно хотите восстановить в исходное состояние? " +
                        "<br/>" +
                        "Данная процедура создаст новые базы данных для дальнейшей работы " +
                        "без сохранения данных предыдущих баз (если они имеются)" +
                        "<br/>" +
                        "Это операция необходима при возникновении проблем с" +
                        " отсутствием чтения данных </html> ",

        INFO_ABOUT_SU = "<html> Для продолжения создайте <br/> " +
                "новую учетную запись! </html>",
                INFO_ABOUT_SI = "Войдите как администратор или продолжите";

        static JPasswordField passwordUser,
                repeatPU,
                passwordUserCheck;


        final Color COLOR_BORDER_WRONG = new Color(214, 63, 17);
        final Color COLOR_BORDER_SUCCESS = new Color(63, 132, 17);

        DefaultTableModel newModelForStorage;

        static DefaultTableModel model;

        DefaultCellEditor Storage;

        Object saveTable = "";

        static JProgressBar progressBar;
        static DefaultTableModel modelOfCurrentlyChange;

        static int countRows;
        static JTextArea log;
        static JTable tableOfData_groups;
        static JTable tableOfData_names;
        static JTable tableOfData_subjects;
        static String[] daysOfWeek;
        public void showPanelCreateAccount() {
            infoAboutOperation.setVisible(true);
            signUpAccount.setVisible(true);
            passwordUser.setVisible(true);
            repeatPU.setVisible(true);
            infoEnter.setVisible(true);
            infoRepeat.setVisible(true);
            rule.setVisible(true);
            rules.setVisible(true);
        }

        public void blockAll() {
            infoAboutOperation.setVisible(false);
            signUpAccount.setVisible(false);
            passwordUser.setVisible(false);
            repeatPU.setVisible(false);
            infoEnter.setVisible(false);
            infoRepeat.setVisible(false);
            rule.setVisible(false);
            rules.setVisible(false);
        }

        public void signInFrame() {
            passwordUserCheck = new JPasswordField();
            signIn = new JButton("Войти");
            signUp = new JButton("Создать");

            try (FileReader dateFromUoD = new FileReader(FILE_OF_USER_DATE)) {
                BufferedReader begin = new BufferedReader(dateFromUoD);
                checkSignIn = begin.readLine();

                if (checkSignIn == null) {
                    signIn.setEnabled(false);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Файл не найден");
            }


            if (checkSignIn == null) {
                condition = "isEmpty";
            }

            if (checkSignIn != null) {
                condition = "notIsEmpty";
            }

            System.out.println("Current condition [" + condition + "]");

            imageBack = new JLabel(new ImageIcon());

            infoAboutOperation = new JLabel("<html> Создайте пароль для " +
                    "дальнейшего входа </html> ");

            infoEnter = new JLabel("Введите пароль");
            infoRepeat = new JLabel("Повторите пароль");
            rule = new JLabel("Роль:");

            infoAboutOperation.setFont(new Font("Verdana", Font.PLAIN, 14));
            infoRepeat.setFont(new Font("Verdana", Font.PLAIN, 13));
            infoEnter.setFont(new Font("Verdana", Font.PLAIN, 13));
            rule.setFont(new Font("Verdana", Font.PLAIN, 13));

            infoAboutOperation.setForeground(Color.BLACK);
            infoRepeat.setForeground(Color.BLACK);
            infoEnter.setForeground(Color.BLACK);
            rule.setForeground(Color.BLACK);

            rules = new JTextField("Администратор");
            rules.setEnabled(false);

            passwordUser = new JPasswordField();
            repeatPU = new JPasswordField();

            signUpAccount = new JButton("Зарегистрироваться");
            signUpAccount.addActionListener(new CheckingDate());

            blockAll();

            infoSI_SU = new JLabel();
            infoSI_SU.setFont(new Font("Verdana", Font.PLAIN, 14));
            infoSI_SU.setForeground(Color.BLACK);

            if (condition.equals("notIsEmpty")) {
                signUp.setEnabled(false);
            }

            signUp.addActionListener(new CheckingAuth());
            signIn.addActionListener(new ContinueSI());
            authorization = new JFrame("Вход в систему");

            authorization.setIconImage(Toolkit.getDefaultToolkit().getImage("src/main/java/iconOfFrame.png"));

            commonPanel = new JPanel();
            commonPanel.setLayout(null);
            Border etched = BorderFactory.createEtchedBorder();

            if (condition.equals("isEmpty")) {
                infoSI_SU.setText(INFO_ABOUT_SU);

            }

            if (condition.equals("notIsEmpty")) {
                infoSI_SU.setText(INFO_ABOUT_SI);
            }

            Border titled = BorderFactory.createTitledBorder(etched, infoSI_SU.getText());
            commonPanel.setBorder(titled);

            System.out.println("Current condition [" + condition + "]");

            passwordUserCheck.setBounds(20, 40, 230, 25);
            signIn.setBounds(20, 70, 110, 30);
            signUp.setBounds(141, 70, 110, 30);

            commonPanel.setBounds(30, 10, 300, 120);
            commonPanel.add(passwordUserCheck);
            commonPanel.add(signUp);
            commonPanel.add(signIn);


            authorization.add(commonPanel);
            authorization.add(imageBack);

            authorization.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            authorization.setSize(370, 180);
            authorization.setResizable(false);
            authorization.setLocationRelativeTo(null);
            authorization.setVisible(true);
        }

        public void mainFrame() {

            JLabel infoAboutAll = new JLabel("<html> Выберите параметр для <br> сортировки </br> </html>");
            JLabel infoAboutGroups = new JLabel("<html> по группам: </html> ");
            JLabel infoAboutNames = new JLabel("<html> по преподавателям: </html> ");

            recoveryOfDate = new JButton(new ImageIcon("src/main/java/recovery.png"));
            backUpOfData = new JButton(new ImageIcon("src/main/java/back_up.png"));
            openFormSubjects = new JButton(new ImageIcon("src/main/java/add_subjects.png"));
            openFormNames = new JButton(new ImageIcon("src/main/java/add_names.png"));
            openFormGroups = new JButton(new ImageIcon("src/main/java/add_groups.png"));
            openCellOfTable = new JButton(new ImageIcon("src/main/java/cellTable.png"));
            deleteSelectedRow = new JButton(new ImageIcon("src/main/java/delete_row.png"));
            viewSavedSchedule = new JButton("Просмотр расписания");
            printTableOfHours = new JButton(new ImageIcon("src/main/java/print.png"));
            importOfTablePath = new JButton(new ImageIcon("src/main/java/import_icon.png"));
            deleteTable = new JButton(new ImageIcon("src/main/java/delete_table.png"));

            openFormSubjects.setFocusPainted(false);
            openFormSubjects.setContentAreaFilled(false);
            openFormNames.setContentAreaFilled(false);
            openFormGroups.setContentAreaFilled(false);
            openCellOfTable.setContentAreaFilled(false);
            recoveryOfDate.setContentAreaFilled(false);
            backUpOfData.setContentAreaFilled(false);
            importOfTablePath.setContentAreaFilled(false);
            deleteSelectedRow.setContentAreaFilled(false);
            printTableOfHours.setContentAreaFilled(false);
            deleteTable.setContentAreaFilled(false);

            importOfTablePath.addActionListener(new ExportTableOfHours());
            openFormSubjects.addActionListener(new OpenFormSubject());
            openFormNames.addActionListener(new OpenFormNames());
            openFormGroups.addActionListener(new OpenFormGroups());
            viewSavedSchedule.addActionListener(new ViewSaveSchedule());
            recoveryOfDate.addActionListener(new FormOfRecovery());
            backUpOfData.addActionListener(new BackUpOfDate());
            deleteSelectedRow.addActionListener(new DeleteSelectedRow());
            printTableOfHours.addActionListener(new PrintTableOfHours());
            deleteTable.addActionListener(new ClearTable());
            deleteTable.addActionListener(new ClearTable());

            createSchedule = new JButton("Создать расписание");
            createSchedule.addActionListener(new BuildSchedule());

            addDateToFile = new JButton("Добавить");
            addDateToFile.addActionListener(new AddDate());

            sortOnClick = new JButton("Сортировать");
            sortOnClick.addActionListener(new SortDate());

            writeInTable = new JButton("Заполнить");
            writeInTable.addActionListener(new FillTable());

            mainFrame = new JFrame("Помощник в составлени расписания");
            mainFrame.setIconImage(Toolkit.getDefaultToolkit().getImage("src/main/java/iconOfFrame.png"));
            setLayout(null);

            textForGroups = new JTextField(30);
            textForNames = new JTextField(30);
            textForSubjects = new JTextField(30);

            countLinesLocalForName();
            countLinesLocalForGroups();
            countLinesLocalForObjects();

            boxNames = new JComboBox<>(listForNames);
            boxGroups = new JComboBox<>(listForGroups);
            boxSubjects = new JComboBox<>(listForSubjects);

            sortBoxNames = new JComboBox<>(listForNames);
            sortBoxGroups = new JComboBox<>(listForGroups);

            boxNames.addActionListener(this);
            boxSubjects.addActionListener(this);
            boxGroups.addActionListener(this);

            sortBoxNames.addActionListener(this);
            sortBoxGroups.addActionListener(this);

            openFormSubjects.setBounds(10, 20, 32, 32);
            openFormSubjects.setToolTipText("<html> Открыть форму для заполнения " +
                    "<br/> данными о дисциплинах </html>");

            openFormNames.setBounds(10, 80, 32, 32);
            openFormNames.setToolTipText("<html> Открыть форму для заполнения " +
                    "<br/> данными о преподавателях </html>");

            openFormGroups.setBounds(10, 140, 32, 32);
            openFormGroups.setToolTipText("<html> Открыть форму для заполнения " +
                    "<br/> данными о группах </html>");

            openCellOfTable.setBounds(10, 320, 32, 32);
            openCellOfTable.setToolTipText("<html> Открыть таблицу часов </html>");

            recoveryOfDate.setBounds(10, 380, 32, 32);
            recoveryOfDate.setToolTipText("<html> Восстановить базу данных </html>");

            backUpOfData.setBounds(10, 440, 32, 32);
            backUpOfData.setToolTipText("<html> Создание резервной копии данных </html>");

            boxSubjects.setBounds(60, 20, 150, 25);
            boxNames.setBounds(60, 60, 150, 25);
            boxGroups.setBounds(60, 100, 150, 25);

            addDateToFile.setBounds(120, 140, 90, 25);
            createSchedule.setBounds(230, 580, 140, 25);
            sortOnClick.setBounds(110, 370, 100, 25);


            log = new JTextArea();
            log.setFont(new Font("Verdana", Font.PLAIN, 11));
            log.setEnabled(false);
            infoAboutAll.setBounds(60, 210, 350, 30);
            infoAboutAll.setFont(new Font("Tahoma", Font.PLAIN, 12));

            infoAboutNames.setBounds(60, 240, 350, 25);
            infoAboutNames.setFont(new Font("Tahoma", Font.PLAIN, 12));

            infoAboutGroups.setBounds(60, 300, 350, 25);
            infoAboutGroups.setFont(new Font("Tahoma", Font.PLAIN, 12));

            sortBoxNames.setBounds(60, 270, 150, 25);
            sortBoxGroups.setBounds(60, 330, 150, 25);

            viewSavedSchedule.setBounds(393, 580, 145, 25);
            writeInTable.setBounds(560, 580, 100, 25);

            modelOfCurrentlyChange = new DefaultTableModel();
            tableOfCurrentlyChange = new JTable(modelOfCurrentlyChange);
            refreshTableOfData();

            JScrollPane scrollOfTable = new JScrollPane(tableOfCurrentlyChange, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
            tableOfCurrentlyChange.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            tableOfCurrentlyChange.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
            mainFrame.add(scrollOfTable);

            tableOfCurrentlyChange.setAutoCreateRowSorter(true);

            JPanel panelOfManagement = new JPanel();
            JPanel borderPanel = new JPanel();

            Border etched = BorderFactory.createEtchedBorder();
            Border titled = BorderFactory.createTitledBorder(etched);
            panelOfManagement.setBorder(titled);
            borderPanel.setBorder(titled);
            borderPanel.setBounds(0, 0, 1268, 705);
            panelOfManagement.add(openFormSubjects);
            panelOfManagement.add(openFormNames);
            panelOfManagement.add(openFormGroups);
            panelOfManagement.add(backUpOfData);
            panelOfManagement.add(recoveryOfDate);

            panelOfManagement.setBounds(0, 0, 50, 800);
            scrollOfTable.setBounds(230, 20, 430, 400);

            JPanel panelOfTable = new JPanel();
            panelOfTable.add(deleteSelectedRow);
            panelOfTable.add(deleteTable);
            panelOfTable.add(printTableOfHours);
          //  panelOfTable.add(importOfTablePath);
            panelOfTable.setBorder(titled);
            panelOfTable.setBounds(670, 0, 50, 800);


            String[] nameOfColumns = {"№", "Данные", "Часы"};
            for (String counter : nameOfColumns) {
                modelOfCurrentlyChange.addColumn(counter);
            }


            JScrollPane scrollOfLog = new JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            log.setLineWrap(true);

            tableOfCurrentlyChange.getColumnModel().getColumn(1).setPreferredWidth(330);
            tableOfCurrentlyChange.getColumnModel().getColumn(0).setPreferredWidth(30);
            tableOfCurrentlyChange.getColumnModel().getColumn(2).setPreferredWidth(50);
            tableOfCurrentlyChange.setRowHeight(20);
            scrollOfLog.getViewport().setBackground(Color.gray);
            scrollOfLog.getViewport().add(log);
            scrollOfLog.setBounds(230, 420, 430, 155);

            readTable(FILE_OF_OUT_SOURCE);

            mainFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            mainFrame.add(panelOfManagement);
            mainFrame.add(panelOfTable);
            mainFrame.add(writeInTable);
            mainFrame.add(scrollOfLog);
            mainFrame.add(infoAboutAll);
            mainFrame.add(infoAboutGroups);
            mainFrame.add(infoAboutNames);
            mainFrame.add(boxNames);
            mainFrame.add(boxSubjects);
            mainFrame.add(boxGroups);
            mainFrame.add(sortBoxNames);
            mainFrame.add(sortBoxGroups);
            mainFrame.add(addDateToFile);
            mainFrame.add(createSchedule);
            mainFrame.add(viewSavedSchedule);
            mainFrame.add(sortOnClick);
            mainFrame.add(borderPanel);
            mainFrame.setSize(730, 650);
            mainFrame.setResizable(false);
            mainFrame.setLocationRelativeTo(null);
            mainFrame.setVisible(false);
        }

        public void refreshTableOfData() {

            JTableHeader th = tableOfCurrentlyChange.getTableHeader();
            for (int i = 0; i < tableOfCurrentlyChange.getColumnCount(); i++) {
                TableColumn column = tableOfCurrentlyChange.getColumnModel().getColumn(i);
                int prefWidth = Math.round((float) th.getFontMetrics(th.getFont())
                        .getStringBounds(th.getTable().getColumnName(i),
                                th.getGraphics()).getWidth());
                column.setPreferredWidth(prefWidth + 260);
            }
        }

        public void viewSchedule() {

            isText = new JTextField(30);

            newModelForStorage = new DefaultTableModel();
            showDateForNew();
            TableSchedule = new JTable(newModelForStorage);
            countLinesGeneralForTable();
            refreshTable();

            TableSchedule.setVisible(true);
            frameOfTable = new JFrame("Табличное представление данных");
            frameOfTable.setIconImage(Toolkit.getDefaultToolkit().getImage("src/main/java/iconOfFrame.png"));
            lowPanel = new JPanel();


            exportOfTables = new JButton("Экспорт в Excel");
            exportOfTables.addActionListener(new ExportTables());
            beginBuild.addActionListener(new SaveForReturn());
            printSavedTable = new JButton("Печать или сохранить");
            printSavedTable.addActionListener(new PrintTable());
            lowPanel.add(exportOfTables);
            lowPanel.add(beginBuild);
            lowPanel.add(TableSchedule);
            lowPanel.add(printSavedTable);

            if (condition.equals("LimitedMode")) {
                beginBuild.setEnabled(false);
            }


            pane = new JScrollPane(TableSchedule, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
            TableSchedule.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

            frameOfTable.add(BorderLayout.CENTER, pane);
            frameOfTable.add(BorderLayout.SOUTH, lowPanel);

            frameOfTable.setSize(900, 500);
            frameOfTable.setVisible(true);

            callBoxDataOfTable();
            OutputDateForNew(FILE_OF_REPOSITORY);
            TableSchedule.getColumnModel().getColumn(0).setPreferredWidth(90);
            TableSchedule.setRowHeight(20);
        }

        public void countLinesOfTable() {

            linesCounter = 0;

            try (LineNumberReader lnr = new LineNumberReader(new FileReader(FILE_OF_OUT_SOURCE))) {
                while (null != lnr.readLine()) {
                    linesCounter++;
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Данные не определенны "
                        + NAME_ERROR_TABLE);
            }
        }

        public void countLinesGeneralForTable() {

            linesCount = 0;

            try (LineNumberReader lnr = new LineNumberReader(new FileReader(FILE_OF_TABLE_BS))) {
                while (null != lnr.readLine()) {
                    linesCount++;
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Данные не определенны "
                        + NAME_ERROR_TABLE);
            }
        }

        public void Counter() {

            countLinesGet = 0;

            try (LineNumberReader lnr = new LineNumberReader(new FileReader(FILE_OF_DATA))) {
                while (null != lnr.readLine()) {
                    countLinesGet++;
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Данные не определенны "
                        + NAME_ERROR_TABLE);
            }
        }

        public void countLinesGeneralForTableOut() {
            linesCountForNew = 0;
            try (LineNumberReader lnr = new LineNumberReader(new FileReader(FILE_OF_REPOSITORY))) {
                while (null != lnr.readLine()) {
                    linesCountForNew++;
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Данные не определенны "
                        + NAME_ERROR_TABLE_OF_SAVE);
            }
        }

        public void countLinesLocalForGroups() {

            try (LineNumberReader lnr = new LineNumberReader(new FileReader(FILE_OF_GROUPS))) {
                while (null != lnr.readLine()) {
                    linesCountsForGroups++;
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Данные не определенны "
                        + NAME_ERROR_GROUPS);
            }
            listForGroups = new String[linesCountsForGroups];

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(FILE_OF_GROUPS), Charset.forName(ENCODING_UTF8)));
                while ((line = reader.readLine()) != null) {
                    linesCountsForGroups--;
                    listForGroups[linesCountsForGroups] = line;
                }
                reader.close();
            } catch (IOException ex) {

            }

        }

        public void countLinesLocalForObjects() {

            try (LineNumberReader lnr = new LineNumberReader(new FileReader(FILE_OF_SUBJECTS))) {
                while (null != lnr.readLine()) {
                    linesCountsForSubjects++;
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Данные не определенны "
                        + NAME_ERROR_SUBJECTS);
            }

            listForSubjects = new String[linesCountsForSubjects];
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(FILE_OF_SUBJECTS), Charset.forName(ENCODING_UTF8)));
                while ((line = reader.readLine()) != null) {
                    linesCountsForSubjects--;
                    listForSubjects[linesCountsForSubjects] = line;
                }
                reader.close();
            } catch (Exception ex) {

            }

        }

        public void countLinesLocalForName() {

            try (LineNumberReader lnr = new LineNumberReader(new FileReader("NamesList.txt"))) {
                while (null != lnr.readLine()) {
                    lineCountForNames++;
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Данные не определенны "
                        + NAME_ERROR_NAMES);
            }
            listForNames = new String[lineCountForNames];


            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(FILE_OF_NAMES), Charset.forName(ENCODING_UTF8)));
                while ((line = reader.readLine()) != null) {
                    lineCountForNames--;
                    listForNames[lineCountForNames] = line;
                }
                reader.close();
            } catch (IOException ex) {

            }
        }

        public void readTable(String nameFile) {
            countLinesOfTable();
            int counter = 0;
            int countLinesForStop = linesCounter;
            int columnsGoTo = 0;
            int countRowFromFile = 0;
            int linesGoTo = 0;
            int column = tableOfCurrentlyChange.getColumnCount();
            int countColumn = column;

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(FILE_OF_OUT_SOURCE), Charset.forName(ENCODING_UTF8)));

                while (dateFromFile != null) {
                    linesCounter--;
                    try (Stream<String> lines = Files.lines(Paths.get(nameFile))) {
                        dateFromFile = lines.skip(linesGoTo).findFirst().get();
                        linesGoTo++;
                        counter++;
                        columnsGoTo++;
                    }

                    if (columnsGoTo == countColumn) {
                        countRowFromFile++;
                        columnsGoTo = 1;
                        counter = 1;
                    }


                    modelOfCurrentlyChange.addRow(new Object[]{linesGoTo, dateFromFile});
                    tableOfCurrentlyChange.setValueAt(dateFromFile, (counter - counter) + countRowFromFile, counter);

                }
                reader.close();
            } catch (Exception fileNotFound) {
            }
            int valueOfDeleteRow = countRowFromFile + VARIABLE_MATH_OPERATION; //+1
            while (valueOfDeleteRow <= (countLinesForStop - VARIABLE_MATH_OPERATION)) {
                valueOfDeleteRow++;
                modelOfCurrentlyChange.removeRow(countRowFromFile + VARIABLE_MATH_OPERATION); // +1
            }
            dateFromFile = "";
        }

        public void OutputDate(String nameFile) {
            countLinesGeneralForTable();

            String[] timeLessons = new String[]{"08:20 - 08:50",
                    "09:00 - 10:30",
                    "10:45 - 12:15",
                    "12:45 - 14:15",
                    "14:30 - 15:55"};

            int counter = 0;
            int countLinesForStop = linesCount;
            int columnsGoTo = 0;
            int countRowFromFile = 0;
            int linesGoTo = 0;
            int column = TableSchedule.getColumnCount();
            int countColumn = column;
            int countLessons = 0;

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(FILE_OF_TABLE_BS), Charset.forName(ENCODING_UTF8)));

                while (dateFromFile != null) {
                    linesCount--;
                    try (Stream<String> lines = Files.lines(Paths.get(nameFile))) {

                        dateFromFile = lines.skip(linesGoTo).findFirst().get(); // back method through '-'
                        linesGoTo++;
                        counter++;
                        columnsGoTo++;
                    }

                    if (columnsGoTo == countColumn) {
                        countRowFromFile++;
                        columnsGoTo = 1;
                        counter = 1;
                    }

                    if (countLessons == timeLessons.length) {
                        countLessons = 0;
                    }

                    model.addRow(new Object[]{timeLessons[countLessons], dateFromFile});
                    TableSchedule.setValueAt(dateFromFile, (counter - counter) + countRowFromFile, counter);

                    countLessons++;
                }
                reader.close();
            } catch (Exception fileNotFound) {
                // JOptionPane.showMessageDialog(null, "No provide"); // Will be exception by reason of "while" above.
            }

            int valueOfDeleteRow = countRowFromFile + VARIABLE_MATH_OPERATION; //+1
            while (valueOfDeleteRow <= (countLinesForStop - VARIABLE_MATH_OPERATION)) {
                valueOfDeleteRow++;
                model.removeRow(countRowFromFile + VARIABLE_MATH_OPERATION); // +1
            }
        }

        public void OutputDateForNew(String nameFile) {
            countLinesGeneralForTableOut();
            String[] timeLessons = new String[]{"08:20 - 08:50",
                    "09:00 - 10:30",
                    "10:45 - 12:15",
                    "12:45 - 14:15",
                    "14:30 - 16:00"};

            int counter = 0;
            int countLinesForStop = linesCountForNew;
            int ColumnsGoTo = 0;
            int CountRowFromFile = 0;
            int linesGoTo = 0;
            int column = TableSchedule.getColumnCount();
            int countColumn = column;
            int countLessons = 0;

            if (stringWriteSort.isEmpty() && condition.equals("TurnOnSort")) {
                frameOfTable.setVisible(false);
                JOptionPane.showMessageDialog(null, INFO_ABOUT_WORK);
            }

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(FILE_OF_REPOSITORY), Charset.forName(ENCODING_UTF8)));

                while (dateFromFile != null) {
                    linesCountForNew--;
                    try (Stream<String> lines = Files.lines(Paths.get(nameFile))) {
                        dateFromFile = lines.skip(linesGoTo).findFirst().get();
                        linesGoTo++;
                        counter++;
                        ColumnsGoTo++;
                    }

                    if (ColumnsGoTo == countColumn) {
                        CountRowFromFile++;
                        ColumnsGoTo = 1;
                        counter = 1;
                    }

                    if (countLessons == 3) {
                        countLessons = 0;
                    }

                    newModelForStorage.addRow(new Object[]{timeLessons[countLessons], dateFromFile});
                    TableSchedule.setValueAt(dateFromFile, (counter - counter) + CountRowFromFile, counter);
                    countLessons++;

                    if (condition.equals("TurnOnSort")) {
                        if (!stringWriteSort.isEmpty()) {

                            Object valueOf = TableSchedule.getValueAt((counter - counter) + CountRowFromFile, counter);
                            String toStringOfValue = valueOf.toString();

                            StringTokenizer skipObjectValue = new StringTokenizer(toStringOfValue);
                            String firstArgOfObject = skipObjectValue.hasMoreTokens() ? skipObjectValue.nextToken() : "";
                            String secondArgOfObject = skipObjectValue.hasMoreTokens() ? skipObjectValue.nextToken() : "";
                            String emptyValueOfObject = skipObjectValue.hasMoreTokens() ? skipObjectValue.nextToken() : "";
                            String thirdValueOfObject = skipObjectValue.hasMoreTokens() ? skipObjectValue.nextToken() : ""; // value used for search


                            StringTokenizer skipStringBoxes = new StringTokenizer(stringWriteSort);
                            String firstArg = skipStringBoxes.hasMoreTokens() ? skipStringBoxes.nextToken() : "";
                            String secondArg = skipStringBoxes.hasMoreTokens() ? skipStringBoxes.nextToken() : "";
                            String SearchValue = skipStringBoxes.hasMoreTokens() ? skipStringBoxes.nextToken() : ""; // value used for search
                            String thirdValue = skipStringBoxes.hasMoreTokens() ? skipStringBoxes.nextToken() : "";

                            if (firstArgOfObject.equals(firstArg) || thirdValueOfObject.equals(SearchValue)) {
                                TableSchedule.setValueAt(toStringOfValue, (counter - counter) + CountRowFromFile, counter);

                            } else {
                                TableSchedule.setValueAt(" ", (counter - counter) + CountRowFromFile, counter);
                            }
                        }
                    }
                }
                reader.close();

            } catch (Exception fileNotFound) {
                // JOptionPane.showMessageDialog(null, "No provide"); // Might be exception by reason of "while" above.
            }
            int valueOfDeleteRow = CountRowFromFile + VARIABLE_MATH_OPERATION;
            while (valueOfDeleteRow <= (countLinesForStop - VARIABLE_MATH_OPERATION)) {
                valueOfDeleteRow++;
                newModelForStorage.removeRow(CountRowFromFile + VARIABLE_MATH_OPERATION);
            }
            condition = "";
        }

        public void refreshTable() {
            JTableHeader th = TableSchedule.getTableHeader();
            for (int i = 0; i < TableSchedule.getColumnCount(); i++) {
                TableColumn column = TableSchedule.getColumnModel().getColumn(i);
                int prefWidth = Math.round((float) th.getFontMetrics(th.getFont())
                        .getStringBounds(th.getTable().getColumnName(i),
                                th.getGraphics()).getWidth());
                column.setPreferredWidth(prefWidth + 260);
            }
        }

        public void callBoxDataOfTable() {
            JComboBox<String> data = new JComboBox<>(new String[]{});
            data.setEditable(true);

            Storage = new DefaultCellEditor(data);
            for (int x = 1; x <= 6; x++)
                TableSchedule.getColumnModel().getColumn(x).setCellEditor(Storage);
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(FILE_OF_DATA), Charset.forName(ENCODING_UTF8)));
                while (DataForTable != null) {
                    data.addItem(DataForTable);
                    DataForTable = reader.readLine();
                }
                reader.close();
            } catch (Exception fileNotFound) {
                JOptionPane.showMessageDialog(null, MASSAGE_ERROR_NOT_FOUND_FILE);
            }
            DataForTable = "";
        }

        public void showDate() {

         /*   String[] stringsOfGroups = new String[22];
            int index = 0;
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(FILE_OF_GROUPS), Charset.forName(ENCODING_UTF8)));

                for(int i = 0; i < stringsOfGroups[index].length(); i++) {
                    System.out.println(stringsOfGroups[index] + " ");
                    stringsOfGroups[index] = reader.readLine();
                    index++;
                }
            } catch (Exception ex) {

            }*/
            countLinesLocalForGroups();
            int maxValue = listForGroups.length;
            for (String counter : listForGroups) {
               if(listForGroups.length == maxValue) {
                   maxValue--;
                   model.addColumn("Пары");
               }
                model.addColumn(counter);
            }
        }

        public void clearFileForSave() {
            try (FileWriter write = new FileWriter(FILE_OF_REPOSITORY)) {
                write.write("");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, MASSAGE_ERROR_NOT_FOUND_FILE);
            }
        }

        public void clearFile() {
            try (FileWriter write = new FileWriter(FILE_OF_OUT_SOURCE)) {
                write.write("");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, MASSAGE_ERROR_NOT_FOUND_FILE);
            }
        }

        public void showDateForNew() {
            String[] daysOfWeek = {"Пары",
                    "Понедельник",
                    "Вторник",
                    "Среда",
                    "Четверг",
                    "Пятница",
                    "Суббота"};

            for (String counter : daysOfWeek) {
                newModelForStorage.addColumn(counter);
            }
        }

        public void actionPerformed(ActionEvent event) {
            String textGroups = (String) boxGroups.getSelectedItem();
            String textObjects = (String) boxSubjects.getSelectedItem();
            String textNames = (String) boxNames.getSelectedItem();

            stringWrite = textGroups + " " + textObjects + " " + textNames;
            System.out.println(stringWrite + " ");


            String sortTextGroups = (String) sortBoxGroups.getSelectedItem();
            String sortTextNames = (String) sortBoxNames.getSelectedItem();

            stringWriteSort = sortTextGroups + " " + textObjects + " " + sortTextNames;
            System.out.println(stringWriteSort + " ");
        }

    }

    static public class PrintTable extends TableDate implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            try {
                TableSchedule.print(JTable.PrintMode.FIT_WIDTH);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ATTENTION_NOT_SELECTED_TABLE);

            }
        }
    }

    static public class PrintTableOfHours extends TableDate implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            try {
                tableOfCurrentlyChange.print(JTable.PrintMode.FIT_WIDTH);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, ATTENTION_NOT_SELECTED_TABLE);

            }
        }
    }

    static class AddDate extends Main.TableDate implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            countLinesGeneralForTable();
            System.out.print("adding line is --> [" + stringWrite + "]\n");
            countRows = tableOfCurrentlyChange.getRowCount();

            modelOfCurrentlyChange.addRow(new Object[]{countRows + VARIABLE_MATH_OPERATION, stringWrite});
        }

    }

    static class CheckingAuth extends TableDate implements ActionListener {
        static JFrame frameOfAuth;
        JPanel panelOfComponents;

        public void actionPerformed(ActionEvent e) {
            frameOfAuth = new JFrame("Окно регистрации");
            frameOfAuth.setIconImage(Toolkit.getDefaultToolkit().getImage("src/main/java/iconOfFrame.png"));

            frameOfAuth.setLayout(null);
            showPanelCreateAccount();
            frameOfAuth.add(signUpAccount);
            frameOfAuth.add(passwordUser);
            frameOfAuth.add(infoEnter);
            frameOfAuth.add(infoRepeat);
            frameOfAuth.add(rule);
            frameOfAuth.add(rules);
            frameOfAuth.add(repeatPU);

            String getNameOfLabel = infoAboutOperation.getText();
            panelOfComponents = new JPanel();
            panelOfComponents.setLayout(null);
            panelOfComponents.setBounds(10, 10, 280, 290);
            Border etched = BorderFactory.createEtchedBorder();
            Border titled = BorderFactory.createTitledBorder(etched, getNameOfLabel);
            panelOfComponents.setBorder(titled);

            infoAboutOperation.setBounds(10, 10, 500, 25);
            infoEnter.setBounds(35, 45, 150, 25);
            passwordUser.setBounds(35, 75, 225, 25);
            infoRepeat.setBounds(35, 105, 150, 25);
            repeatPU.setBounds(35, 135, 225, 25);
            rule.setBounds(35, 165, 225, 25);
            rules.setBounds(35, 195, 225, 30);
            signUpAccount.setBounds(35, 240, 150, 30);

            frameOfAuth.setSize(320, 350);
            frameOfAuth.add(panelOfComponents);
            frameOfAuth.setResizable(false);
            frameOfAuth.setLocationRelativeTo(null);
            frameOfAuth.setVisible(true);
        }
    }

    static class ContinueSI extends TableDate implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            beginBuild = new JButton("Сохранить");
            String localCheck = passwordUserCheck.getText();

            if (localCheck.equals(checkSignIn)) {
                condition = "AccessAllow";
                authorization.dispose();
                mainFrame.setVisible(true);
                passwordUserCheck.setBorder(BorderFactory.createLineBorder(COLOR_BORDER_SUCCESS));
                log.append("Текущий пользователь: [Администратор].\n");
            } else
                condition = "NoAccess";

            System.out.println("Current condition [" + condition + "]");


            if (localCheck.isEmpty()) {
                condition = "LimitedMode";
                createSchedule.setEnabled(false);
                openFormSubjects.setEnabled(false);
                openFormGroups.setEnabled(false);
                openFormNames.setEnabled(false);
                addDateToFile.setEnabled(false);
                beginBuild.setEnabled(false);
                boxNames.setEnabled(false);
                boxGroups.setEnabled(false);
                boxSubjects.setEnabled(false);
                backUpOfData.setEnabled(false);
                recoveryOfDate.setEnabled(false);
                writeInTable.setEnabled(false);
                deleteSelectedRow.setEnabled(false);
                deleteTable.setEnabled(false);

                authorization.dispose(); // using this method for clear RAM
                mainFrame.setVisible(true);
                log.append("Текущий пользователь: [Преподаватель].\n");

                System.out.println("Current condition [" + condition + "]");
            }

            if (!localCheck.equals(checkSignIn) & !localCheck.isEmpty()) {
                passwordUserCheck.setBorder(BorderFactory.createLineBorder(COLOR_BORDER_WRONG));
            }


        }
    }

    static class CheckingDate extends CheckingAuth implements ActionListener {
        public void actionPerformed(ActionEvent e) {

            String passOriginal = passwordUser.getText();
            String passRepeat = repeatPU.getText();

            if (passOriginal.equals(passRepeat)) {
                if (passOriginal.isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Поля должны быть заполненны!");
                    return;
                }

                passwordUser.setBorder(BorderFactory.createLineBorder(COLOR_BORDER_SUCCESS));
                repeatPU.setBorder(BorderFactory.createLineBorder(COLOR_BORDER_SUCCESS));
                System.out.print("Success! Access is allowed\n");
                checkSignIn = passOriginal;
                passwordUser.setText("");
                repeatPU.setText("");

                try (FileWriter saveUserDate = new FileWriter(FILE_OF_USER_DATE)) {
                    saveUserDate.write(passOriginal + "\n");

                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Данные не определенны");
                }
                signIn.setEnabled(true);
                frameOfAuth.dispose();
            } else {
                passwordUser.setBorder(BorderFactory.createLineBorder(COLOR_BORDER_WRONG));
                repeatPU.setBorder(BorderFactory.createLineBorder(COLOR_BORDER_WRONG));
                System.out.print("Error, values [" + passOriginal + "]" +
                        " & " +
                        "[" + passRepeat + "] don`t coincidence\n");
                passwordUser.setText("");
                repeatPU.setText("");

            }
        }
    }

    static class OpenFormSubject extends TableDate implements ActionListener {

        JFrame FrameForSubjects;
        JButton addDate;
        JButton importOfData;
        JPanel panelOfComponents;

        static JTextField textForSubjects;
        static String save;
        static JLabel labelInfoFrame;

        static DefaultTableModel modelSubjects;
        static String read;
        String getTextFromTF;

        public void actionPerformed(ActionEvent event) {
            modelSubjects = new DefaultTableModel();
            tableOfData_subjects = new JTable(modelSubjects);
            modelSubjects.addColumn("Список сохраненных дисциплин: ");

            tableOfData_subjects.getColumnModel().getColumn(0).setPreferredWidth(480);
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(FILE_OF_SUBJECTS), Charset.forName("UTF-8")));
                while ((read = reader.readLine()) != null) {
                    modelSubjects.addRow(new Object[]{read});
                }
                reader.close();
            } catch (IOException ex) {

            }
            labelInfoFrame = new JLabel("Запишите дисциплину для сохранения");
            addDate = new JButton("Записать");

            textForSubjects = new JTextField();

            labelInfoFrame.setFont(new Font("Tahoma", Font.PLAIN, 13));
            panelOfComponents = new JPanel();
            panelOfComponents.setLayout(null);

            labelInfoFrame.setBounds(5, 20, 250, 20);
            textForSubjects.setBounds(5, 50, 250, 28);
            addDate.setBounds(270, 50, 125, 28);

            importOfData = new JButton(new ImageIcon("src/main/java/import_icon.png"));
            deleteDateFromFiles = new JButton(new ImageIcon("src/main/java/delete_all.png"));
            deleteDateFromFiles.setBounds(410, 50, 32, 32);
            importOfData.setBounds(450, 50, 32, 32);
            deleteDateFromFiles.setContentAreaFilled(false);
            importOfData.setContentAreaFilled(false);
            deleteDateFromFiles.setToolTipText("<html> Удалить данные </html>");
            deleteDateFromFiles.addActionListener(new DeleteDataOfSubjects());
            importOfData.addActionListener(new ImportOfSubjects());
            panelOfComponents.add(importOfData);
            panelOfComponents.add(labelInfoFrame);
            panelOfComponents.add(textForSubjects);
            panelOfComponents.add(addDate);
            panelOfComponents.add(deleteDateFromFiles);

            FrameForSubjects = new JFrame("Добавить дисциплину");
            FrameForSubjects.setIconImage(Toolkit.getDefaultToolkit().getImage("src/main/java/iconOfFrame.png"));
            JScrollPane paneOfTable = new JScrollPane(tableOfData_subjects, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            tableOfData_subjects.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            panelOfComponents.add(paneOfTable);
            paneOfTable.setBounds(5, 100, 480, 185);
            FrameForSubjects.add(panelOfComponents);
            FrameForSubjects.setSize(510, 330);

            FrameForSubjects.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            FrameForSubjects.setLocationRelativeTo(null);
            FrameForSubjects.setResizable(false);
            FrameForSubjects.setVisible(true);
            tableOfData_subjects.setAutoCreateRowSorter(true);
            addDate.addActionListener(new SaveSubjects());
        }

        public void writeDate(String dataObjects) {
            getTextFromTF = textForSubjects.getText();
            try {
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(FILE_OF_SUBJECTS, true), "UTF8"));
                bw.write(dataObjects + "\n");
                bw.flush();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, MASSAGE_ERROR_NOT_FOUND_FILE +
                        " " + NAME_ERROR_SUBJECTS);
            }
            countLinesLocalForObjects();
            readTable();
        }

        public void readTable() {

            modelSubjects.addRow(new Object[]{getTextFromTF});

        }
    }

    static class OpenFormNames extends TableDate implements ActionListener {
        JFrame frameForNames;
        JButton addDate;
        JPanel panelOfComponents;
        JButton importOfData;
        static JTextField textForNames;
        static String save;
        static JLabel labelInfo;
        static DefaultTableModel modelNames;
        static String read;
        String getTextFromTF;

        public void actionPerformed(ActionEvent event) {

            modelNames = new DefaultTableModel();
            tableOfData_names = new JTable(modelNames);
            modelNames.addColumn("Список сохраненных имен");
            tableOfData_names.getColumnModel().getColumn(0).setPreferredWidth(480);
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(FILE_OF_NAMES), Charset.forName("UTF-8")));
                while ((read = reader.readLine()) != null) {
                    modelNames.addRow(new Object[]{read});
                }
                reader.close();
            } catch (IOException ex) {

            }

            importOfData = new JButton(new ImageIcon("src/main/java/import_icon.png"));
            importOfData.setBounds(450, 50, 32, 32);
            importOfData.setContentAreaFilled(false);
            importOfData.addActionListener(new ImportOfNames());
            labelInfo = new JLabel("Запишите имя для сохранения");
            addDate = new JButton("Записать");
            textForNames = new JTextField();


            panelOfComponents = new JPanel();

            panelOfComponents.setLayout(null);

            labelInfo.setFont(new Font("Tahoma", Font.PLAIN, 13));

            labelInfo.setBounds(5, 20, 225, 20);
            textForNames.setBounds(5, 50, 250, 28);
            addDate.setBounds(270, 50, 125, 28);


            deleteDateFromFiles = new JButton(new ImageIcon("src/main/java/delete_all.png"));
            deleteDateFromFiles.setBounds(410, 50, 32, 32);
            deleteDateFromFiles.setContentAreaFilled(false);
            deleteDateFromFiles.setToolTipText("<html> Удалить данные </html>");
            deleteDateFromFiles.addActionListener(new DeleteDataOfNames());

            panelOfComponents.add(importOfData);
            panelOfComponents.add(textForNames);
            panelOfComponents.add(labelInfo);
            panelOfComponents.add(addDate);
            panelOfComponents.add(deleteDateFromFiles);

            frameForNames = new JFrame("Добавить имя");
            frameForNames.setIconImage(Toolkit.getDefaultToolkit().getImage("src/main/java/iconOfFrame.png"));
            JScrollPane paneOfTable = new JScrollPane(tableOfData_names, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            tableOfData_names.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            panelOfComponents.add(paneOfTable);
            paneOfTable.setBounds(5, 100, 480, 185);

            frameForNames.add(panelOfComponents);
            frameForNames.setSize(510, 330);
            frameForNames.setVisible(true);

            tableOfData_names.setAutoCreateRowSorter(true);
            frameForNames.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            addDate.addActionListener(new SaveNames());
            frameForNames.setLocationRelativeTo(null);
            frameForNames.setResizable(false);

        }

        public void writeDate(String dataNames) {
            getTextFromTF = textForNames.getText();
            try {
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(FILE_OF_NAMES, true), "UTF8"));
                bw.write(dataNames + "\n");
                bw.flush();
            } catch (Exception fileNotFound) {
                JOptionPane.showMessageDialog(null, MASSAGE_ERROR_NOT_FOUND_FILE +
                        " " + NAME_ERROR_NAMES);
            }
            countLinesLocalForName();
            readTable();
        }

        public void readTable() {

            modelNames.addRow(new Object[]{getTextFromTF});

        }
    }

    static class OpenFormGroups extends TableDate implements ActionListener {
        JFrame FrameForGroups;
        JButton addDate;
        JPanel panelOfComponents;
        JButton importOfData;

        static JTextField textForGroups;
        static String save;
        static JLabel labelInfo;

        static DefaultTableModel modelGroups;
        static String read;
        String getTextFromTF;

        public void actionPerformed(ActionEvent event) {
            modelGroups = new DefaultTableModel();
            modelGroups.addColumn("Список сохраненных групп: ");

            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(FILE_OF_GROUPS), Charset.forName("UTF-8")));
                while ((read = reader.readLine()) != null) {
                    modelGroups.addRow(new Object[]{read});
                }
                reader.close();
            } catch (IOException ex) {

            }

            tableOfData_groups = new JTable(modelGroups);
            tableOfData_groups.getColumnModel().getColumn(0).setPreferredWidth(480);

            labelInfo = new JLabel("Запишите группу для сохранения");

            addDate = new JButton("Записать");

            textForGroups = new JTextField();
            importOfData = new JButton(new ImageIcon("src/main/java/import_icon.png"));
            importOfData.setBounds(450, 50, 32, 32);
            importOfData.setContentAreaFilled(false);
            importOfData.addActionListener(new ImportOfGroups());
            labelInfo.setFont(new Font("Tahoma", Font.PLAIN, 13));
            panelOfComponents = new JPanel();
            panelOfComponents.setLayout(null);

            panelOfComponents.add(labelInfo);
            panelOfComponents.add(textForGroups);
            panelOfComponents.add(importOfData);
            panelOfComponents.add(addDate);
            panelOfComponents.add(tableOfData_groups);

            deleteDateFromFiles = new JButton(new ImageIcon("src/main/java/delete_all.png"));
            deleteDateFromFiles.setBounds(410, 50, 32, 32);
            deleteDateFromFiles.setContentAreaFilled(false);
            deleteDateFromFiles.setToolTipText("<html> Удалить данные </html>");
            deleteDateFromFiles.addActionListener(new DeleteDataOfGroups());

            JScrollPane paneOfTable = new JScrollPane(tableOfData_groups, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            tableOfData_groups.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            panelOfComponents.add(paneOfTable);
            paneOfTable.setBounds(5, 100, 480, 185);
            labelInfo.setBounds(5, 20, 225, 20);
            textForGroups.setBounds(5, 50, 250, 28);
            addDate.setBounds(270, 50, 125, 28);


            FrameForGroups = new JFrame("Добавить группу в список");
            FrameForGroups.setIconImage(Toolkit.getDefaultToolkit().getImage("src/main/java/iconOfFrame.png"));

            FrameForGroups.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            FrameForGroups.add(deleteDateFromFiles);
            FrameForGroups.setSize(510, 330);
            FrameForGroups.add(panelOfComponents);
            FrameForGroups.setLocationRelativeTo(null);
            FrameForGroups.setResizable(false);
            FrameForGroups.setVisible(true);

            addDate.addActionListener(new SaveGroups());

            tableOfData_groups.setAutoCreateRowSorter(true);
        }

        public void readTable() {
            modelGroups.addRow(new Object[]{getTextFromTF});
        }

        public void writeDate(String dataGroups) {
            getTextFromTF = textForGroups.getText();
            try {
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(FILE_OF_GROUPS, true), "UTF8"));
                bw.write(dataGroups + "\n");
                bw.flush();
            } catch (Exception fileNotFound) {
                JOptionPane.showMessageDialog(null, MASSAGE_ERROR_NOT_FOUND_FILE +
                        " " + NAME_ERROR_GROUPS);
            }

            countLinesLocalForGroups();
            readTable();
        }
    }

    static class BuildSchedule extends TableDate implements ActionListener {
        int indexOfReader = 0;
        float valueOfNumber = 0f;

        public void actionPerformed(ActionEvent event) {

            clearFile();
            checkParameters();
            countValue();
            Counter();
            saveTable();

            String[] arrayOfStrings = new String[countLinesGet];
            String read;
            try {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(FILE_OF_DATA), Charset.forName("UTF-8")));

                while ((read = reader.readLine()) != null) {

                    arrayOfStrings[indexOfReader] = read;
                    indexOfReader++;
                    if (indexOfReader == countLinesGet) {
                        indexOfReader = 0;
                        break;
                    }

                }
                reader.close();
            } catch (IOException ex) {

            }

            int counterWriteEmptyValues = 0;
            int valueCheck = 0;
            int counterGoTo = 0;
            int valueOfMiddle = countLinesGet / 2;

            try {
                BufferedWriter bufferWriter = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(FILE_OF_TABLE_BS), "UTF8"));
                while (counterWriteEmptyValues != 6) {
                    bufferWriter.write(" " + "\n");
                    counterWriteEmptyValues++;
                }

                int backReaderCount = (countLinesGet / 2);

                for (int counterCell = ((countLinesGet + VARIABLE_MATH_OPERATION) / 2); counterCell <= countLinesGet; counterCell++) {

                    counterGoTo = counterGoTo + 2;
                    bufferWriter.write(arrayOfStrings[counterCell] + "\n");

                    backReaderCount--;
                    bufferWriter.write(arrayOfStrings[backReaderCount] + "\n");

                    if (counterGoTo == 18) {
                        for (int z = 0; z < 6; z++) {
                            bufferWriter.write(" " + "\n");
                        }
                        counterGoTo = 0;
                    }
                    valueCheck++;
                    if (valueCheck == valueOfMiddle) {
                        if (countLinesGet % 2 != 0) {
                            bufferWriter.write(arrayOfStrings[((countLinesGet + VARIABLE_MATH_OPERATION) / 2)] + "\n");
                        }
                        valueCheck++;
                    }
                    if (backReaderCount == 0) {
                        break;
                    }

                }
                bufferWriter.flush();
            } catch (Exception ex) {

            }

            isText = new JTextField(30);

            model = new DefaultTableModel();
            showDate();
            TableSchedule = new JTable(model);

            countLinesGeneralForTable();
            refreshTable();
            TableSchedule.setVisible(true);

            frameOfTable = new JFrame("Созданное расписание");
            frameOfTable.setIconImage(Toolkit.getDefaultToolkit().getImage("src/main/java/iconOfFrame.png"));

            lowPanel = new JPanel();
            exportOfTables = new JButton("Экспорт в Excel");
            beginBuild = new JButton("Сохранить");
            viewSavedSchedule = new JButton("Просмотр");
            printSavedTable = new JButton("Печать или сохранить");
            exportOfTables.addActionListener(new ExportTables());
            printSavedTable.addActionListener(new PrintTable());
            viewSavedSchedule.addActionListener(new ViewSaveSchedule());
            beginBuild.addActionListener(new SaveForReturn());

            lowPanel.add(exportOfTables);
            lowPanel.add(TableSchedule);
            lowPanel.add(printSavedTable);
            lowPanel.add(beginBuild, BoxLayout.Y_AXIS);
            lowPanel.add(viewSavedSchedule);

            frameOfTable.add(BorderLayout.SOUTH, lowPanel);
            OutputDate(FILE_OF_TABLE_BS);

            callBoxDataOfTable();

            pane = new JScrollPane(TableSchedule, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                    ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
            TableSchedule.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

            frameOfTable.add(BorderLayout.CENTER, pane);

            frameOfTable.setSize(900, 500);
            frameOfTable.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            frameOfTable.setVisible(true);

            TableSchedule.getColumnModel().getColumn(0).setPreferredWidth(90);
            TableSchedule.setRowHeight(20);

        }

        public void saveTable() {
            String writeData;
            for (int j = 0; j < tableOfCurrentlyChange.getRowCount(); j++) {
                for (int i = 1; i < tableOfCurrentlyChange.getColumnCount(); i++) {
                    writeData = tableOfCurrentlyChange.getValueAt(j, i).toString();
                    try {
                        BufferedWriter writingData = new BufferedWriter(new OutputStreamWriter(
                                new FileOutputStream(FILE_OF_OUT_SOURCE, true), "UTF8"));
                        writingData.write(writeData + "\n");
                        writingData.flush();
                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(null, "Файл не найден");

                    }
                }
            }
        }

        public void countValue() {

            int rowsOfTable = tableOfCurrentlyChange.getRowCount();
            String getValueColumns;
            try {
                BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
                        new FileOutputStream(FILE_OF_DATA), "UTF8"));
                for (int countRows = 0; countRows < rowsOfTable; countRows++) {
                    float valueOfNumber = Float.parseFloat(tableOfCurrentlyChange.getValueAt(countRows, 2).toString());
                    getValueColumns = tableOfCurrentlyChange.getValueAt(countRows, 1).toString();
                    valueOfNumber = valueOfNumber / 2;
                    for (int counter = 0; counter < valueOfNumber; counter++) {
                        bw.write(getValueColumns + "\n");
                    }
                }
                bw.close();
            } catch (Exception ex) {

            }
        }


        public void checkParameters() {
            int attention = 0;
            int errors = 0;
            int countAttention = 0;
            int countErrors = 0;
            int rowsOfTable = tableOfCurrentlyChange.getRowCount();
            for (int countRows = 0; countRows < rowsOfTable; countRows++) {

                try {
                    valueOfNumber = Float.parseFloat(tableOfCurrentlyChange.getValueAt(countRows, 2).toString());
                } catch (Exception ex) {
                    countErrors++;
                    log.append("Ошибка: Колонки не должны быть пусты (" + (countRows + VARIABLE_MATH_OPERATION) + " - й номер колонки).\n");
                    errors = +countErrors;
                }
                if (valueOfNumber == 0) {
                    log.append("Недопустимое значение часов: [" + valueOfNumber + "].\n");
                    errors++;
                }
                if (valueOfNumber % 2 != 0) {
                    countAttention++;
                    log.append("Предупреждение: Некорректное значение часов в строке " + (countRows + VARIABLE_MATH_OPERATION) + ";\n" +
                            "Может быть заменено на [" + (valueOfNumber + VARIABLE_MATH_OPERATION) + "] или " +
                            "[" + (valueOfNumber - VARIABLE_MATH_OPERATION) + "]" +
                            " (Автоматически заменено на " + (valueOfNumber - VARIABLE_MATH_OPERATION) + ").\n");
                    attention = +countAttention;
                }
            }
            log.append("\nОшибок: " + errors + "; Предупреждений: " + attention + ";\n\n");

        }
    }

    static class BackUpOfDate extends TableDate implements ActionListener {
        static JFrame frameOfBackUp;

        public void actionPerformed(ActionEvent event) {
            frameOfBackUp = new JFrame("Создание резервной копии");
            frameOfBackUp.setLayout(null);
            frameOfBackUp.setIconImage(Toolkit.getDefaultToolkit().getImage("src/main/java/iconOfFrame.png"));
            JLabel iconDownload = new JLabel(new ImageIcon("src/main/java/download.png"));
            JLabel iconUnload = new JLabel(new ImageIcon("src/main/java/unload.png"));
            JLabel interaction = new JLabel(new ImageIcon("src/main/java/interaction.png"));

            JButton uploadDataOfBackUp = new JButton("Выгрузить"); // output from program
            JButton downloadDataOfBackUp = new JButton("Сохранить"); // input to program

            uploadDataOfBackUp.addActionListener(new Unload());
            downloadDataOfBackUp.addActionListener(new Download());

            interaction.setBounds(200, 15, 100, 100);
            iconDownload.setBounds(345, 15, 100, 100);
            iconUnload.setBounds(50, 15, 100, 100);

            uploadDataOfBackUp.setBounds(50, 120, 100, 30);
            downloadDataOfBackUp.setBounds(345, 120, 100, 30);

            progressBar = new JProgressBar();
            progressBar.setBounds(0, 180, 483, 30);

            frameOfBackUp.add(progressBar);

            frameOfBackUp.add(uploadDataOfBackUp);
            frameOfBackUp.add(interaction);
            frameOfBackUp.add(downloadDataOfBackUp);

            frameOfBackUp.add(iconDownload);
            frameOfBackUp.add(iconUnload);

            frameOfBackUp.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
            frameOfBackUp.setSize(490, 250);
            frameOfBackUp.setResizable(false);
            frameOfBackUp.setLocationRelativeTo(null);
            frameOfBackUp.setVisible(true);
        }
    }

    static class Unload extends BackUpOfDate implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            String[] listFiles_Storage = {"Repository",
                    "DoneSchedule",
                    "NamesList",
                    "GroupsList",
                    "SubjectsList",
                    "Out_source"};


            progressBar.setStringPainted(true);
            progressBar.setMinimum(0);
            progressBar.setMaximum(100);

            for (int i = 0; i <= 100; i++) {
                progressBar.setValue(i);
            }
            try {

                Unload("[" + listFiles_Storage[0] + "].txt", FILE_OF_REPOSITORY);
                Unload("[" + listFiles_Storage[1] + "].txt", FILE_OF_TABLE_BS);
                Unload("[" + listFiles_Storage[2] + "].txt", FILE_OF_NAMES);
                Unload("[" + listFiles_Storage[3] + "].txt", FILE_OF_GROUPS);
                Unload("[" + listFiles_Storage[4] + "].txt", FILE_OF_SUBJECTS);
                Unload("[" + listFiles_Storage[5] + "].txt", FILE_OF_OUT_SOURCE);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Не удалось завершить операцию! ");
                progressBar.setVisible(false);
            }
            JOptionPane.showMessageDialog(null, "Данные выгружены!");
            frameOfBackUp.dispose();
        }
    }

    static class Download extends BackUpOfDate implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            String[] listFiles_Storage = {"Repository",
                    "DoneSchedule",
                    "NamesList",
                    "GroupsList",
                    "SubjectsList",
                    "Out_source"};

            progressBar.setStringPainted(true);
            progressBar.setMinimum(0);
            progressBar.setMaximum(100);

            for (int i = 0; i <= 100; i++) {
                progressBar.setValue(i);
            }

            try {
                Unload(FILE_OF_REPOSITORY, "[" + listFiles_Storage[0] + "].txt");
                Unload(FILE_OF_TABLE_BS, "[" + listFiles_Storage[1] + "].txt");
                Unload(FILE_OF_NAMES, "[" + listFiles_Storage[2] + "].txt");
                Unload(FILE_OF_GROUPS, "[" + listFiles_Storage[3] + "].txt");
                Unload(FILE_OF_SUBJECTS, "[" + listFiles_Storage[4] + "].txt");
                Unload(FILE_OF_OUT_SOURCE, "[" + listFiles_Storage[5] + "].txt");

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Не удалось завершить операцию! ");
                progressBar.setVisible(false);
            }

            JOptionPane.showMessageDialog(null, "Данные выгружены." +
                    "Необходимо перезапустить программу.");

            frameOfBackUp.dispose();
        }
    }

    public static void Unload(String sourcePath, String destinationPath) throws IOException { // method for back up
        Files.copy(Paths.get(sourcePath), new FileOutputStream(destinationPath));
    }

    static class DeleteSelectedRow extends TableDate implements ActionListener {
        public void actionPerformed(ActionEvent event) {

            if (tableOfCurrentlyChange.getSelectedRow() != -VARIABLE_MATH_OPERATION) {
                modelOfCurrentlyChange.removeRow(tableOfCurrentlyChange.getSelectedRow());
            }
        }
    }

    static class FormOfRecovery extends TableDate implements ActionListener {
        static JProgressBar progressOfRecovery;
        static JFrame frameOfRecovery;

        public void actionPerformed(ActionEvent event) {

            frameOfRecovery = new JFrame("Восстановление");
            frameOfRecovery.setLayout(null);
            frameOfRecovery.setIconImage(Toolkit.getDefaultToolkit().getImage("src/main/java/iconOfFrame.png"));
            ImageIcon iconCreateNewData = new ImageIcon("src/main/java/add_new_data.png");
            JLabel labelOfIcon = new JLabel(iconCreateNewData);

            JButton confirmation = new JButton("Да");
            JButton renouncement = new JButton("Нет");

            confirmation.addActionListener(new RecoveryOfData());
            renouncement.addActionListener(new CancelOperation());

            JLabel infoCreateNewData = new JLabel(INFO_RECOVERY);

            labelOfIcon.setBounds(10, 10, 100, 100);
            infoCreateNewData.setBounds(130, 10, 300, 130);

            confirmation.setBounds(130, 150, 100, 30);
            renouncement.setBounds(250, 150, 100, 30);

            progressOfRecovery = new JProgressBar();
            progressOfRecovery.setBounds(0, 232, 483, 30);

            frameOfRecovery.add(progressOfRecovery);
            frameOfRecovery.add(labelOfIcon);
            frameOfRecovery.add(confirmation);
            frameOfRecovery.add(renouncement);
            frameOfRecovery.add(infoCreateNewData);

            frameOfRecovery.setSize(490, 300);
            frameOfRecovery.setResizable(false);
            frameOfRecovery.setLocationRelativeTo(null);
            frameOfRecovery.setVisible(true);

        }
    }

    static class CancelOperation extends FormOfRecovery implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            JOptionPane.showMessageDialog(null, "Операция отменена!");
            frameOfRecovery.dispose();
        }
    }

    static class RecoveryOfData extends FormOfRecovery implements ActionListener {
        public void actionPerformed(ActionEvent event) {

            String[] listFiles = {"Repository.txt",
                    "DoneSchedule.txt",
                    "NamesList.txt",
                    "GroupsList.txt",
                    "SubjectsList.txt",
                    "Check.txt",
                    "TableOfHours.txt"};

            progressOfRecovery.setStringPainted(true);
            progressOfRecovery.setMinimum(0);
            progressOfRecovery.setMaximum(100);

            for (int i = 0; i <= 100; i++) {
                progressOfRecovery.setValue(i);
            }

            for (String createFiles : listFiles) {
                try (FileWriter createFile = new FileWriter(createFiles)) {
                    createFile.write("");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null, "Операция не может быть завершена");
                }
            }
            JOptionPane.showMessageDialog(null, "Восстановление завершено!");
            frameOfRecovery.dispose();
        }
    }

    static class ExportTableOfHours extends TableDate implements ActionListener {
        public void actionPerformed(ActionEvent event) {

            String nameFile = "";

            JFileChooser fileOpen = new JFileChooser();
            int ret = fileOpen.showDialog(null, "Выбрать файл");
            if (ret == JFileChooser.APPROVE_OPTION) {
                File file = fileOpen.getSelectedFile();
                nameFile = file.getAbsolutePath();
                System.out.println(nameFile);
            }

            Workbook workbook = new HSSFWorkbook();
            Sheet sheet = workbook.createSheet("Таблица расписания");
            Row row = sheet.createRow(0);
            Cell cell;
            for (int i = 0; i < tableOfCurrentlyChange.getModel().getColumnCount(); i++) {
                cell = row.createCell(i);
                cell.setCellValue(tableOfCurrentlyChange.getModel().getColumnName(i));
            }
            for (int i = 0; i < tableOfCurrentlyChange.getModel().getRowCount(); i++) {
                row = sheet.createRow(i + 1);
                for (int j = 0; j < tableOfCurrentlyChange.getModel().getColumnCount(); j++) {
                    cell = row.createCell(j);
                    if (j == 2 || j == 3 || j == 6) {
                        cell.setCellValue(Integer.valueOf(tableOfCurrentlyChange.getModel()
                                .getValueAt(i, j).toString()));
                    } else {
                        cell.setCellValue(tableOfCurrentlyChange.getModel().getValueAt(i, j).toString());
                    }
                    sheet.autoSizeColumn(j);
                }
            }
            try (FileOutputStream fileExcel = new FileOutputStream(nameFile)) {
                workbook.write(fileExcel);
                log.append("Экспорт данных завершен!\n" +
                        "Данные сохраненны в [" + nameFile + "] \n");
            } catch (IOException e) {
                log.append("Не удалось завершить экспорт данных!\n");
            }
        }
    }

    static class ExportTables extends TableDate implements ActionListener {
        public void actionPerformed(ActionEvent event) {

            String nameFile = "";

            JFileChooser fileOpen = new JFileChooser();
            int ret = fileOpen.showDialog(null, "Выбрать файл");
            if (ret == JFileChooser.APPROVE_OPTION) {
                File file = fileOpen.getSelectedFile();
                nameFile = file.getAbsolutePath();
                System.out.println(nameFile);
            }

            Workbook workbook = new HSSFWorkbook();
            Sheet sheet = workbook.createSheet("Таблица расписания");
            Row row = sheet.createRow(0);
            Cell cell;
            for (int i = 0; i < TableSchedule.getModel().getColumnCount(); i++) {
                cell = row.createCell(i);
                cell.setCellValue(TableSchedule.getModel().getColumnName(i));
            }
            for (int i = 0; i < TableSchedule.getModel().getRowCount(); i++) {
                row = sheet.createRow(i + 1);
                for (int j = 0; j < TableSchedule.getModel().getColumnCount(); j++) {
                    cell = row.createCell(j);
                    cell.setCellValue(String.valueOf(TableSchedule.getModel().getValueAt(i, j)));
                    sheet.autoSizeColumn(j);
                }
            }
            try (FileOutputStream fileExcel = new FileOutputStream(nameFile)) {
                workbook.write(fileExcel);
                log.append("Экспорт данных завершен!\n" +
                        "Данные сохраненны в [" + nameFile + "] \n");
            } catch (IOException e) {
                log.append("Не удалось завершить экспорт данных!\n");
            }
        }
    }

    static class SaveForReturn extends TableDate implements ActionListener {
        public void actionPerformed(ActionEvent event) {

            String checkForAvailability = "";
            try (FileReader read = new FileReader(FILE_CHECK_SAVE_DATE)) {
                BufferedReader buffer = new BufferedReader(read);
                checkForAvailability = buffer.readLine();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Данные не могут быть записаны");
            }

            if (checkForAvailability != null) {
                Object[] options = {"Да",
                        "Нет"};
                int userChoice = JOptionPane.showOptionDialog(frameOfTable,
                        "Желаете перезаписать предыдущие данные?",
                        "Сохранение",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[1]);

                if (userChoice == 0) {
                    saveData();
                } else {
                    JOptionPane.showMessageDialog(null, "Сохранение отменено!");
                    log.append("Сохранение отменено!\n");
                }
            }

            String data = "[----------- data isn`t empty -----------]";

            try (FileWriter write = new FileWriter(FILE_CHECK_SAVE_DATE)) {
                write.write(data + "\n");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Данные не могут быть записаны");
                log.append("Ошибка при сохранении расписания!\n");
            }

            if (checkForAvailability == null) {
                saveData();
            }

        }

        public void saveData() {
            clearFileForSave();

            int column = TableSchedule.getColumnCount();
            int row = TableSchedule.getRowCount();

            for (int x = 0; x < row; x++) {
                for (int y = 1; y < column; y++) {
                    saveTable = TableSchedule.getValueAt(x, y);
                    try {
                        BufferedWriter writingData = new BufferedWriter(new OutputStreamWriter(
                                new FileOutputStream(FILE_OF_REPOSITORY, true), "UTF8"));
                        writingData.write(saveTable + "\n");
                        writingData.flush();

                    } catch (IOException e) {
                        JOptionPane.showMessageDialog(null, "Файл не найден");
                        log.append("Таблица не сохранена!");
                    }
                }
            }
            JOptionPane.showMessageDialog(null, "Сохранение завершено!");
            log.append("Таблица сохранена!\n");
        }
    }

    static class SortDate extends TableDate implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            condition = "TurnOnSort";
            viewSchedule();
        }
    }

    static class ViewSaveSchedule extends TableDate implements ActionListener {

        public void actionPerformed(ActionEvent event) {
            viewSchedule();
        }

    }

    static class FillTable extends TableDate implements ActionListener { ///// here change
        ArrayList<String> arrayRepeatedGroups;
        ArrayList<String> arrayRepeatedSubjects;
        ArrayList<String> arrayRepeatedNames;
        Object dataOfColumnB;
        Object dataOfColumnL;
        Object dataOfColumnO;
        public void actionPerformed(ActionEvent event) {
            countLinesLocalForName();
            countLinesLocalForObjects();
            countLinesLocalForGroups();

            String nameFile = "";

            arrayRepeatedGroups = new ArrayList<>();
            arrayRepeatedSubjects = new ArrayList<>();
            arrayRepeatedNames = new ArrayList<>();
            JFileChooser fileOpen = new JFileChooser();

            int ret = fileOpen.showDialog(null, "Выбрать файл");
            if (ret == JFileChooser.APPROVE_OPTION) {
                File file = fileOpen.getSelectedFile();
                nameFile = file.getAbsolutePath();
                System.out.println(nameFile);
            }

            try {
                File file = new File(nameFile);
                Workbook wb = WorkbookFactory.create(file);


                Object hours;
                int count = 1;
                int indexForColumn = 10;
                while (indexForColumn != 270) {


                    dataOfColumnB = wb.getSheetAt(5).getRow(indexForColumn - 1).
                            getCell(CellReference.convertColStringToIndex("B"));
                    dataOfColumnL = wb.getSheetAt(5).getRow(indexForColumn - 1).
                            getCell(CellReference.convertColStringToIndex("L"));
                    dataOfColumnO = wb.getSheetAt(5).getRow(indexForColumn - 1).
                            getCell(CellReference.convertColStringToIndex("O"));
                    hours = wb.getSheetAt(5).getRow(indexForColumn - 1).
                            getCell(CellReference.convertColStringToIndex("D"));


                    writeDataGroups(dataOfColumnO.toString());
                    writeDataSubjects(dataOfColumnB.toString());
                    writeDataNames(dataOfColumnL.toString());

                    indexForColumn++;

                    arrayRepeatedGroups.add(dataOfColumnO.toString());
                    arrayRepeatedSubjects.add(dataOfColumnB.toString());
                    arrayRepeatedNames.add(dataOfColumnL.toString());

                    System.out.println("[" + dataOfColumnB + " " + dataOfColumnL + " " + dataOfColumnO + "]");
                    Object wordBuild = dataOfColumnO + " " + dataOfColumnB + " " + dataOfColumnL;
                    modelOfCurrentlyChange.addRow(new Object[]{count, wordBuild,hours});
                    count++;
                }
                System.out.println("****************************************************************");



            } catch (Exception e) {
                System.out.println("exception");
                e.printStackTrace();
            }


        }

        public void writeDataGroups(String groups) {

            try (FileWriter write = new FileWriter(FILE_OF_GROUPS)) {


                System.out.println("begin removing duplicated...");
                HashSet hs = new HashSet();
                hs.addAll(arrayRepeatedGroups);
                arrayRepeatedGroups.clear();
                arrayRepeatedGroups.addAll(hs);
                for (int i = 0; i < arrayRepeatedGroups.size(); i ++) {
                    System.out.println("end removing duplicated: \n" + arrayRepeatedGroups.get(i));
                    groups = arrayRepeatedGroups.get(i);
                    write.write(groups + "\n");
                }

            } catch (IOException e) {
            }
            countLinesLocalForGroups();
            boxGroups.removeAllItems();
            sortBoxGroups.removeAllItems();
            for(String counter : listForGroups) {
                boxGroups.addItem(counter);
                sortBoxGroups.addItem(counter);
            }

        }

        public void writeDataSubjects(String subjects) {

            try (FileWriter write = new FileWriter(FILE_OF_SUBJECTS)) {


                System.out.println("begin removing duplicated...");
                HashSet hs = new HashSet();
                hs.addAll(arrayRepeatedSubjects);
                arrayRepeatedSubjects.clear();
                arrayRepeatedSubjects.addAll(hs);
                for (int i = 0; i < arrayRepeatedSubjects.size(); i ++) {
                    System.out.println("end removing duplicated: \n" + arrayRepeatedSubjects.get(i));
                    subjects = arrayRepeatedSubjects.get(i);
                    write.write(subjects + "\n");
                }

            } catch (IOException e) {
            }
            countLinesLocalForObjects();
            boxSubjects.removeAllItems();
            for(String counter : listForSubjects) {
                boxSubjects.addItem(counter);
            }

        }
        public void writeDataNames(String names) {

            try (FileWriter write = new FileWriter(FILE_OF_NAMES)) {


                System.out.println("begin removing duplicated...");
                HashSet hs = new HashSet();
                hs.addAll(arrayRepeatedNames);
                arrayRepeatedNames.clear();
                arrayRepeatedNames.addAll(hs);
                for (int i = 0; i < arrayRepeatedNames.size(); i ++) {
                    System.out.println("end removing duplicated: \n" + arrayRepeatedNames.get(i));
                    names = arrayRepeatedNames.get(i);
                    write.write(names + "\n");
                }

            } catch (IOException e) {
            }
            countLinesLocalForName();
            boxNames.removeAllItems();
            sortBoxNames.removeAllItems();
            for(String counter : listForNames) {
                boxNames.addItem(counter);
                sortBoxNames.addItem(counter);
            }

        }
    }
    static class ImportOfSubjects extends OpenFormSubject implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            String nameFile = "";
            ArrayList<String> arrayRepeated = new ArrayList<>();
            JFileChooser fileOpen = new JFileChooser();
            int ret = fileOpen.showDialog(null, "Выбрать файл");
            if (ret == JFileChooser.APPROVE_OPTION) {
                File file = fileOpen.getSelectedFile();
                nameFile = file.getAbsolutePath();
                System.out.println(nameFile);
            }

            try {
                File file = new File(nameFile);
                Workbook wb = WorkbookFactory.create(file);
                Object dataOfColumn;
                int indexForColumn = 10;
                while (indexForColumn != 270) {
                    dataOfColumn = wb.getSheetAt(5).getRow(indexForColumn - 1).
                            getCell(CellReference.convertColStringToIndex("B"));
                    indexForColumn++;
                    arrayRepeated.add(dataOfColumn.toString());
                    System.out.println("[" + dataOfColumn + "]");

                }
                System.out.println("****************************************************************");

                System.out.println("begin removing duplicated...");
                HashSet hs = new HashSet();
                hs.addAll(arrayRepeated);
                arrayRepeated.clear();
                arrayRepeated.addAll(hs);
                for (int i = 0; i < arrayRepeated.size(); i ++) {
                    System.out.println("end removing duplicated: \n" + arrayRepeated.get(i));
                    dataOfColumn = arrayRepeated.get(i);
                    modelSubjects.addRow(new Object[]{dataOfColumn});
                    writeDate(arrayRepeated.get(i));
                }

            } catch (Exception e) {
                System.out.println("exception");
                e.printStackTrace();
            }

            boxSubjects.removeAllItems();
            for(String counter : listForSubjects) {
                boxSubjects.addItem(counter);
            }

        }

    }
    static class ImportOfGroups extends OpenFormGroups implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            String nameFile = "";

            ArrayList<String> arrayRepeated = new ArrayList<>();
            JFileChooser fileOpen = new JFileChooser();
            int ret = fileOpen.showDialog(null, "Выбрать файл");
            if (ret == JFileChooser.APPROVE_OPTION) {
                File file = fileOpen.getSelectedFile();
                nameFile = file.getAbsolutePath();
                System.out.println(nameFile);
            }

            try {
                File file = new File(nameFile);
                Workbook wb = WorkbookFactory.create(file);
                Object dataOfColumn;
                int indexForColumn = 10;
                while (indexForColumn != 270) {
                    dataOfColumn = wb.getSheetAt(5).getRow(indexForColumn - 1).
                            getCell(CellReference.convertColStringToIndex("O"));
                    indexForColumn++;
                    arrayRepeated.add(dataOfColumn.toString());
                    System.out.println("[" + dataOfColumn + "]");

                }
                System.out.println("****************************************************************");

                System.out.println("begin removing duplicated...");
                HashSet hs = new HashSet();
                hs.addAll(arrayRepeated);
                arrayRepeated.clear();
                arrayRepeated.addAll(hs);
                for (int i = 0; i < arrayRepeated.size(); i ++) {
                    System.out.println("end removing duplicated: \n" + arrayRepeated.get(i));
                    dataOfColumn = arrayRepeated.get(i);
                    modelGroups.addRow(new Object[]{dataOfColumn});
                    writeDate(arrayRepeated.get(i));
                }

            } catch (Exception e) {
                System.out.println("exception");
                e.printStackTrace();
            }

            boxGroups.removeAllItems();
            sortBoxGroups.removeAllItems();
            for(String counter : listForGroups) {
                boxGroups.addItem(counter);
                sortBoxGroups.addItem(counter);
            }
        }

    }
    static class ImportOfNames extends OpenFormNames implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            String nameFile = "";
            ArrayList<String> arrayRepeated = new ArrayList<>();
            JFileChooser fileOpen = new JFileChooser();
            int ret = fileOpen.showDialog(null, "Выбрать файл");
            if (ret == JFileChooser.APPROVE_OPTION) {
                File file = fileOpen.getSelectedFile();
                nameFile = file.getAbsolutePath();
                System.out.println(nameFile);
            }

            try {
                File file = new File(nameFile);
                Workbook wb = WorkbookFactory.create(file);
                Object dataOfColumn;
                int indexForColumn = 10;

                while (indexForColumn != 270) {

                    dataOfColumn = wb.getSheetAt(5).getRow(indexForColumn - 1).
                                   getCell(CellReference.convertColStringToIndex("L"));

                    indexForColumn++;
                    arrayRepeated.add(dataOfColumn.toString());
                    System.out.println("[" + dataOfColumn + "]" + indexForColumn);

                }
                System.out.println("****************************************************************");

                System.out.println("begin removing duplicated...");
                HashSet hs = new HashSet();
                hs.addAll(arrayRepeated);
                arrayRepeated.clear();
                arrayRepeated.addAll(hs);
                for (int i = 0; i < arrayRepeated.size(); i ++) {
                    System.out.println("end removing duplicated: \n" + arrayRepeated.get(i));
                    dataOfColumn = arrayRepeated.get(i);
                    modelNames.addRow(new Object[]{dataOfColumn});
                    writeDate(arrayRepeated.get(i));
                }

            } catch (Exception e) {
                System.out.println("exception");
                e.printStackTrace();
            }

            boxNames.removeAllItems();
            sortBoxNames.removeAllItems();
            for(String counter : listForNames) {
                boxNames.addItem(counter);
                sortBoxNames.addItem(counter);
            }
        }

    }
    static class ClearTable extends TableDate implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            modelOfCurrentlyChange.setRowCount(0);
        }
    }
}

class SaveGroups extends Main.OpenFormGroups implements ActionListener {
    public void actionPerformed(ActionEvent event) {
        save = textForGroups.getText();
        labelInfo.setForeground(COLOR_BORDER_SUCCESS);
        if(save.isEmpty()) {
            labelInfo.setText("Поле не должно быть пустым!");
            labelInfo.setForeground(COLOR_BORDER_WRONG);
            return;
        }
        else
            labelInfo.setText("Группа '" + textForGroups.getText() + "' сохранена");
        writeDate(save);
        log.append("Данные группы " + save + " добавлены!\n");
        textForGroups.setText("");

        sortBoxGroups.removeAllItems();
        boxGroups.removeAllItems();

        for(String counter : listForGroups) {
            boxGroups.addItem(counter);
            sortBoxGroups.addItem(counter);
        }
    }

}
class SaveNames extends Main.OpenFormNames implements ActionListener {
    public void actionPerformed(ActionEvent event) {
        save = textForNames.getText();

        labelInfo.setForeground(COLOR_BORDER_SUCCESS);

        if(save.isEmpty()) {
            labelInfo.setText("Поле не должно быть пустым!");
            labelInfo.setForeground(COLOR_BORDER_WRONG);
            return;
        }
        else
        labelInfo.setText("ФИО '" + textForNames.getText() + "' сохраненно");
        log.append("Данные преподавателя " + save + " сохраненны!\n");
        writeDate(save);
        textForNames.setText("");


        sortBoxNames.removeAllItems();
        boxNames.removeAllItems();
        for(String counter : listForNames) {
            boxNames.addItem(counter);
            sortBoxNames.addItem(counter);
        }
    }
}
class SaveSubjects extends Main.OpenFormSubject implements ActionListener { /// save date for objects
    public void actionPerformed(ActionEvent event) {
        save = textForSubjects.getText();
        labelInfoFrame.setForeground(COLOR_BORDER_SUCCESS);
        if(save.isEmpty()) {
            labelInfoFrame.setText("Поле не должно быть пустым!");
            labelInfoFrame.setForeground(COLOR_BORDER_WRONG);

            return;
        }
        else
            labelInfoFrame.setText("Дисциплина '" + textForSubjects.getText() + "' сохранена");
        log.append("Данные дисциплины " + save + " сохраненны!\n");
        writeDate(save);
        textForSubjects.setText("");

        boxSubjects.removeAllItems();
        for(String counter : listForSubjects) {
            boxSubjects.addItem(counter);
        }
    }
}

class DeleteDataOfGroups extends Main.OpenFormGroups implements ActionListener {
    public void actionPerformed(ActionEvent event) {
        Object[] options = {"Да",
                            "Нет"};

        int userChoice = JOptionPane.showOptionDialog(frameOfTable,
                "Вы действительно хотите удалить все данные?",
                "Удаление данных",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);

        if(userChoice == 0) {

            try {
                FileWriter write = new FileWriter(FILE_OF_GROUPS);
                write.write("");
                write.close();
                log.append("Данные списка групп удаленны!\n");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Удаление не завершено по неизвестной причине");
            }
            JOptionPane.showMessageDialog(null, "Удаление завершено!");
            sortBoxGroups.removeAllItems();
            boxGroups.removeAllItems();
            modelGroups.setRowCount(0);
            for(String counter : listForGroups) {
                boxGroups.addItem(counter);
                sortBoxGroups.addItem(counter);
            }
        }
        else
            JOptionPane.showMessageDialog(null, "Удаление отменено!");
    }
}
class DeleteDataOfNames extends Main.OpenFormNames implements ActionListener {
    public void actionPerformed(ActionEvent event) {
        Object[] options = {"Да",
                            "Нет"};

        int userChoice = JOptionPane.showOptionDialog(frameOfTable,
                "Вы действительно хотите удалить все данные?",
                "Удаление данных",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);

        if(userChoice == 0) {

            try {
                FileWriter write = new FileWriter(FILE_OF_NAMES);
                write.write("");
                write.close();
                log.append("Данные списка преподавателей удаленны!\n");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Удаление не завершено по неизвестной причине");
            }
            JOptionPane.showMessageDialog(null, "Удаление завершено!");
            sortBoxNames.removeAllItems();
            boxNames.removeAllItems();
            modelNames.setRowCount(0);
            for(String counter : listForNames) {
                boxNames.addItem(counter);
                sortBoxNames.addItem(counter);
            }
        }
        else
            JOptionPane.showMessageDialog(null, "Удаление отменено!");
    }

}
class DeleteDataOfSubjects extends Main.OpenFormSubject implements ActionListener {
    public void actionPerformed(ActionEvent event) {
        Object[] options = {"Да",
                             "Нет"};

        int userChoice = JOptionPane.showOptionDialog(frameOfTable,
                "Вы действительно хотите удалить все данные?",
                "Удаление данных",
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);

        if(userChoice == 0) {

            try {
                FileWriter write = new FileWriter(FILE_OF_SUBJECTS);
                write.write("");
                write.close();
                log.append("Данные списка дисциплин удаленны!\n");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Удаление не завершено по неизвестной причине");
            }
            JOptionPane.showMessageDialog(null, "Удаление завершено!");

            boxSubjects.removeAllItems();
            modelSubjects.setRowCount(0);
            for(String counter : listForSubjects) {
                boxSubjects.addItem(counter);
            }
        }
        else
            JOptionPane.showMessageDialog(null, "Удаление отменено!");
    }
}
© 2020 GitHub, Inc.
Terms
Privacy
Security
Status
Help
Contact GitHub
Pricing
API
Training
Blog
About
