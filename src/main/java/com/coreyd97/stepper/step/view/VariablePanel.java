package com.coreyd97.stepper.step.view;

import com.coreyd97.stepper.variable.PostExecutionStepVariable;
import com.coreyd97.stepper.variable.RegexVariable;
import com.coreyd97.stepper.variable.VariableManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
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

        addVariableButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(SwingUtilities.isRightMouseButton(e)){
                    JPopupMenu popupMenu = new JPopupMenu();
                    JMenuItem addVar = new JMenuItem("Set Variable From Clipboard");
                    addVar.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            String json = null;
                            addVariableEvent(json);
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

        JScrollPane scrollPane = new JScrollPane(this.variableTable);

        this.add(scrollPane, BorderLayout.CENTER);
        this.add(controlPanel, BorderLayout.SOUTH);

        this.setPreferredSize(new Dimension(300,150));

        // add event to variableTable's JViewport
        scrollPane.getViewport().addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    JPopupMenu popupMenu = new JPopupMenu();
                    JMenuItem setValue = new JMenuItem("Set Variable From Clipboard");

                    setValue.addActionListener(actionEvent -> {
                        String json = null;
                        addVariableEvent(json);
                    });
                    popupMenu.add(setValue);
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    abstract void createVariableTable();
    abstract void handleAddVariableEvent();
    abstract void handleDeleteVariableEvent();

    public void addVariableEvent(String json) {
        try {
            json = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
            // convert to json object
            Gson gson = new GsonBuilder().create();
            JsonArray variables = gson.fromJson(json, JsonObject.class).getAsJsonArray("variables");
            for(int v = 0; v < variables.size(); v++){
                JsonObject variable = variables.get(v).getAsJsonObject();
                PostExecutionStepVariable newVariable = new RegexVariable(variable.get("identifier").getAsString());
                newVariable.setCondition(variable.get("pattern").getAsString());
                variableManager.addVariable(newVariable);
            }
        } catch (UnsupportedFlavorException | java.io.IOException ex) { }

    }
}
