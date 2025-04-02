package com.coreyd97.stepper.step.view;

import com.coreyd97.stepper.util.dialog.VariableCreationDialog;
import com.coreyd97.stepper.variable.PostExecutionStepVariable;
import com.coreyd97.stepper.variable.RegexVariable;
import com.coreyd97.stepper.variable.StepVariable;
import com.coreyd97.stepper.variable.VariableManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PostExecVariablePanel extends VariablePanel {

    public PostExecVariablePanel(VariableManager variableManager){
        super("Post-Execution Variables", variableManager);
    }

    @Override
    void createVariableTable() {
        this.variableTable = new PostExecutionVariableTable(this.variableManager);
        variableTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    JPopupMenu popupMenu = new JPopupMenu();
                    JMenuItem getValue = new JMenuItem("get selected value");

                    getValue.addActionListener(actionEvent -> {
                        int selectedRow = variableTable.getSelectedRow();
                            PostExecutionStepVariable variable = variableManager.getPostExecutionVariables().get(selectedRow);
                            String value = variable.getValue();
                            String identifier = variable.getIdentifier();
                            String condition = variable.getConditionText();

                            // JOptionPane.showMessageDialog(variableTable, "ident: " + identifier + "\ncon: " + condition + "\nval: " + value, "Variable Value", JOptionPane.INFORMATION_MESSAGE);

                            // convert to StepVariable
                            PostExecutionStepVariable newVariable = new RegexVariable(identifier);
                            newVariable.setIdentifier(identifier);
                            newVariable.setCondition(condition);
                            variableManager.addVariable(newVariable);
                    });
                    popupMenu.add(getValue);
                    popupMenu.show(variableTable, e.getX(), e.getY());
                }
            }
        });

    }

    @Override
    void handleAddVariableEvent() {
        VariableCreationDialog dialog = new VariableCreationDialog((Frame) SwingUtilities.getWindowAncestor(this),
                "New Variable", VariableCreationDialog.VariableType.REGEX);
        StepVariable variable = dialog.run();
        if(variable != null) {
            this.variableManager.addVariable(variable);
        }
    }

    @Override
    void handleDeleteVariableEvent() {
        if(this.variableTable.getSelectedRow() >= 0) {
            StepVariable variable = this.variableManager.getPostExecutionVariables().get(this.variableTable.getSelectedRow());
            this.variableManager.removeVariable(variable);
        }
    }
}
