import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class CPUAllocator extends JFrame implements ActionListener {

    JTable intabel;
    JScrollPane scroll;
    JButton calculate, add, btnDelete, clear;
    JComboBox<String> comboBox;
    String[] algos = {"FCFS","SJF", "Priority Non-Premtive"};
    List<Process> list;

    CPUAlgos algorithm;

    Object[][] deft = {{"P1", 5, 1, 0}, {"P2", 3, 4, 0}, {"P3", 6, 2, 0}, {"P4", 0, 2, 0}, {"P5", 4, 3, 0}};
    DefaultTableModel m1 = new DefaultTableModel(deft, new String[]{"Pid", "AT", "BT"});
    DefaultTableModel m2 = new DefaultTableModel(deft, new String[]{"Pid", "AT", "BT", "Priority"});

    public CPUAllocator() {
        setLayout(null);
        setResizable(false);

        list = new ArrayList<>();

        add = new JButton("Add");
        add.setFont(new Font("Arial", Font.BOLD, 18));
        add.setBounds(400, 30, 100, 28);
        add.addActionListener(this);
        add(add);

        btnDelete = new JButton("Delete");
        btnDelete.setFont(new Font("Arial", Font.BOLD, 18));
        btnDelete.setBounds(520, 30, 100, 28);
        btnDelete.addActionListener(this);
        add(btnDelete);

        clear = new JButton("Clear");
        clear.setFont(new Font("Arial", Font.BOLD, 18));
        clear.setBounds(640, 30, 100, 28);
        clear.addActionListener(this);
        add(clear);

        calculate = new JButton("Calculate");
        calculate.setFont(new Font("Arial", Font.BOLD, 18));
        calculate.setBounds(760, 30, 130, 28);
        calculate.addActionListener(this);
        add(calculate);

        scroll = new JScrollPane();
        intabel = new JTable();
        intabel.setAutoCreateRowSorter(true);
        intabel.setFont(new Font("Calibri", 0, 24));
        intabel.setModel(m1);
        intabel.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
        intabel.setRowHeight(30);
        intabel.setRowMargin(2);
        intabel.setSelectionBackground(new java.awt.Color(85, 242, 73));
        intabel.setSurrendersFocusOnKeystroke(true);
        tablePreference(intabel);
        scroll.setViewportView(intabel);
        scroll.setBounds(100, 100, 800, 800);
        add(scroll);

        comboBox = new JComboBox<>(algos);
        comboBox.setFont(new Font("Arial", Font.PLAIN, 18));
        comboBox.setBounds(150, 30, 200, 28);
        comboBox.addActionListener(e -> {
            changeTableAccordingToAlgo();
        });
        add(comboBox);

        setTitle("CPU Scheduling");
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void changeTableAccordingToAlgo() {
        int id = comboBox.getSelectedIndex();

        if (id == 2) {
            intabel.setModel(m2);
        } else {
            intabel.setModel(m1);
        }
  
    }

    public void tablePreference(JTable table) {
        JTableHeader tj = table.getTableHeader();
        tj.setFont(new Font("Segoe UI", Font.BOLD, 26));
        tj.setOpaque(false);
        tj.setBackground(new Color(32, 136, 203));
        tj.setForeground(new Color(255, 255, 255));

        DefaultTableCellRenderer renderer = (DefaultTableCellRenderer) tj.getDefaultRenderer();
        renderer.setHorizontalAlignment(JLabel.CENTER);

        DefaultTableCellRenderer cellRenderer = new DefaultTableCellRenderer();
        cellRenderer.setHorizontalAlignment(JLabel.CENTER);
        int column = table.getColumnCount();
        for (int i = 0; i < column; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
        }

        table.setAutoCreateRowSorter(true);
        table.setFont(new java.awt.Font("Calibri", 0, 24));
        table.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        table.setRowHeight(30);
        table.setRowMargin(2);
        table.setSelectionBackground(new java.awt.Color(102, 255, 51));
        table.setSurrendersFocusOnKeystroke(true);
    }

    public static void main(String[] args) {
        new CPUAllocator();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        DefaultTableModel model = (DefaultTableModel) intabel.getModel();
        try {
            if (e.getSource() == add) {
                model.addRow(new Object[]{});
            } else if (e.getSource() == btnDelete) {
                model.removeRow(model.getRowCount() - 1);
            } else if (e.getSource() == clear) {
                model.setRowCount(1);
                for (int i = 0; i < model.getColumnCount(); i++)
                    model.setValueAt(null, 0, i);
            } else if (e.getSource() == calculate) {
                checkValidate();
                int index = comboBox.getSelectedIndex();
                switch (index) {
                    case 0:
                        algorithm = new FCFS(list);
                        break;
                    case 1:
                        algorithm = new SJF(list);
                        break;
                    case 2:
                        algorithm = new PriorityNP(list);
                        break;
                  
                    default:
                        break;
                }
                algorithm.schedule();
                
                displayResults();

            }
        } catch (Exception ex) {
        } finally {
            intabel.setModel(model);
        }
        intabel.requestFocusInWindow();
    }

    private void checkValidate() {
        int row = intabel.getRowCount();
        int column = intabel.getColumnCount();

        try {
            String str;
            list.clear();
            for (int i = 0; i < row; i++) {
                for (int j = 1; j < column; j++) {
                    str = intabel.getValueAt(i, j).toString();
                    if (!str.matches("[0-9]+")) {
                        throw new Exception();
                    }
                }
            }

            for (int i = 0; i < row; i++) {
                int arr[] = new int[3];
                String name = intabel.getValueAt(i, 0).toString();
                for (int j = 1; j < column; j++) {
                    str = intabel.getValueAt(i, j).toString();
                    arr[j - 1] = Integer.parseInt(str);
                }
                list.add(new Process(i, name, arr[0], arr[1], arr[2]));
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Please Enter Valid Integer in Table", "Error Box",
                    JOptionPane.ERROR_MESSAGE);
        }

    }

    private void displayResults() {
        JFrame ouput = new JFrame("Results");
        ouput.setSize(400, 300);
        ouput.setLocationRelativeTo(null);
        ouput.setLayout(new GridLayout(3, 1));

        JLabel gantchart = new JLabel("Gantt Chart:\n" + Process.Gantt);
        ouput.add(gantchart);

        JLabel avgTat = new JLabel("Average Turnaround Time: " + Process.AvgTat);
        ouput.add(avgTat);

        JLabel avgWt = new JLabel("Average Waiting Time: " + Process.AvgWt);
        ouput.add(avgWt);

        ouput.setVisible(true);
    }
}
