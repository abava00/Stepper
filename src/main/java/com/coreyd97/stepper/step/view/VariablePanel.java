package com.coreyd97.stepper.step.view;

import com.coreyd97.stepper.Stepper;
import com.coreyd97.stepper.variable.PostExecutionStepVariable;
import com.coreyd97.stepper.variable.VariableManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public abstract class VariablePanel extends JPanel {

    protected final VariableManager variableManager;
    protected JTable variableTable;

    VariablePanel(String title, VariableManager variableManager){
        this.setLayout(new BorderLayout());
        this.variableManager = variableManager;
        createVariableTable();

        JPanel controlPanel = new JPanel(new GridLayout(1, 0));
        JButton addVariableButton = new JButton("Add Variable");
        addVariableButton.addActionListener(actionEvent -> {
            handleAddVariableEvent();
        });

        /* I could not add a right-click menu to “PostExecutionVariableTable” so I added a right-click menu to the button. */

        addVariableButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(SwingUtilities.isRightMouseButton(e)){
                    JPopupMenu popupMenu = new JPopupMenu();
                    JMenuItem addVar = new JMenuItem("add Variables");
                    addVar.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            Stepper.callbacks.issueAlert("hell");

                            PostExecutionStepVariable variable = variableManager.getPostExecutionVariables().get(0);
                            String value = variable.getValue();
                            String identifier = variable.getIdentifier();
                            Stepper.callbacks.issueAlert("value: " + value);

                        }
                    });
                popupMenu.add(addVar);
                popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        JButton deleteSelectedVariableButton = new JButton("Delete Selected Variable");
        deleteSelectedVariableButton.addActionListener(actionEvent -> {
            handleDeleteVariableEvent();
        });

        controlPanel.add(addVariableButton);
        controlPanel.add(deleteSelectedVariableButton);

        if(title != null) {
            JLabel label = new JLabel(title);
            label.setFont(label.getFont().deriveFont(label.getFont().getSize()+4).deriveFont(Font.BOLD));
            label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 0));
            this.add(label, BorderLayout.NORTH);
        }

        this.add(new JScrollPane(this.variableTable), BorderLayout.CENTER);
        this.add(controlPanel, BorderLayout.SOUTH);

        this.setPreferredSize(new Dimension(300,150));
    }

    abstract void createVariableTable();
    abstract void handleAddVariableEvent();
    abstract void handleDeleteVariableEvent();
}
