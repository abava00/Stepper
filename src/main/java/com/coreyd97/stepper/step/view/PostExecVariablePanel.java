package com.coreyd97.stepper.step.view;

import com.coreyd97.stepper.Stepper;
import com.coreyd97.stepper.util.dialog.VariableCreationDialog;
import com.coreyd97.stepper.variable.PostExecutionStepVariable;
import com.coreyd97.stepper.variable.RegexVariable;
import com.coreyd97.stepper.variable.StepVariable;
import com.coreyd97.stepper.variable.VariableManager;
import com.google.gson.Gson;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                        // int selectedRow = variableTable.getSelectedRow();
                            // PostExecutionStepVariable variable = variableManager.getPostExecutionVariables().get(selectedRow);
                            // String value = variable.getValue();
                            // String identifier = variable.getIdentifier();
                        // String condition = variable.getConditionText();

                        // get selected multiple values
                        int[] selectedRows = variableTable.getSelectedRows();

                        // export to String selected values as json
                        String json = "{\"variables\":[";

                        for (int i = 0; i < selectedRows.length; i++) {
                            int selectedRow = selectedRows[i];
                            PostExecutionStepVariable variable = variableManager.getPostExecutionVariables().get(selectedRow);
                            String value = variable.getValue();
                            String identifier = variable.getIdentifier();
                            String condition = variable.getConditionText();
                            Map<String, Object> child = new HashMap<>();
                            child.put("pattern", condition);
                            child.put("identifier", identifier);
                            child.put("type", "Regex");
                            String childJson = new Gson().toJson(child);
                            if (i != selectedRows.length - 1) {
                                json += childJson + ",";
                            }

                        }
                        json += "]}";
                        //output
                        Stepper.callbacks.issueAlert(json);

                        // for (int selectedRow : selectedRows) {
                        // Stepper.callbacks.issueAlert("selected rows: " + selectedRow);
                        //     PostExecutionStepVariable variable = variableManager.getPostExecutionVariables().get(selectedRow);
                        //     String value = variable.getValue();
                        //     String identifier = variable.getIdentifier();
                        //     String condition = variable.getConditionText();
                        //     // PostExecutionStepVariable newVariable = new RegexVariable(identifier);
                        //     // newVariable.setIdentifier(identifier);
                        //     // newVariable.setCondition(condition);
                        //     // variableManager.addVariable(newVariable);

                        //     sv.append("value: ").append(value).append("\n");
                        // }
                    });
                    popupMenu.add(getValue);
                    popupMenu.show(variableTable, e.getX(), e.getY());

                    // conver to json

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
