package com.coreyd97.stepper.step.view;

import com.coreyd97.stepper.util.dialog.VariableCreationDialog;
import com.coreyd97.stepper.variable.PostExecutionStepVariable;
import com.coreyd97.stepper.variable.StepVariable;
import com.coreyd97.stepper.variable.VariableManager;
import com.google.gson.Gson;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
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
                    JMenuItem getValue = new JMenuItem("Get Selected Variable");

                    getValue.addActionListener(actionEvent -> {
                        int[] selectedRows = variableTable.getSelectedRows();

                        // convert to String selected values as json
                        String json = "{\"variables\":[";
                        for (int i = 0; i < selectedRows.length; i++) {
                            int selectedRow = selectedRows[i];
                            PostExecutionStepVariable variable = variableManager.getPostExecutionVariables().get(selectedRow);
                            String identifier = variable.getIdentifier();
                            String condition = variable.getConditionText();
                            Map<String, Object> child = new HashMap<>();
                            child.put("pattern", condition);
                            child.put("identifier", identifier);
                            child.put("type", "Regex");
                            String childJson = new Gson().toJson(child);
                            if (i != selectedRows.length - 1) {
                                json += childJson + ",";
                            } else {
                                json += childJson;
                            }

                        }
                        json += "]}";
                        //output to clipboard
                        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(json), null);
                    });
                    if (variableTable.getSelectedRow() >= 0) {
                        popupMenu.add(getValue);
                        popupMenu.show(variableTable, e.getX(), e.getY());
                    }

                    // JMenuItem setValue = new JMenuItem("Set Variable From Clipboard");
                    // setValue.addActionListener(actionEvent -> {
                    //     String json = null;
                    //     addVariableEvent(json);
                    // });
                    // popupMenu.add(setValue);
                    // popupMenu.show(e.getComponent(), e.getX(), e.getY());


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
