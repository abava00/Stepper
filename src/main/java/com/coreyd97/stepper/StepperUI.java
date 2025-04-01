package com.coreyd97.stepper;

import burp.ITab;
import com.coreyd97.BurpExtenderUtilities.CustomTabComponent;
import com.coreyd97.BurpExtenderUtilities.PopOutPanel;
import com.coreyd97.stepper.sequencemanager.listener.StepSequenceListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.coreyd97.stepper.about.view.AboutPanel;
import com.coreyd97.stepper.preferences.view.OptionsPanel;
import com.coreyd97.stepper.sequence.StepSequence;
import com.coreyd97.stepper.sequence.view.StepSequenceTab;
import com.coreyd97.stepper.sequencemanager.SequenceManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

public class StepperUI implements ITab {

    private final SequenceManager sequenceManager;
    private final JTabbedPane tabbedPane;
    private final PopOutPanel popOutPanel;
    private final HashMap<StepSequence, StepSequenceTab> managerTabMap;

    public StepperUI(SequenceManager sequenceManager){
        this.sequenceManager = sequenceManager;
        this.managerTabMap = new HashMap<>();

        this.tabbedPane = new JTabbedPane();
        CustomTabComponent addSequenceTabComponent = new CustomTabComponent( "Add Sequence");
        //Add mouse listener to "Add Sequence" tab
        addSequenceTabComponent.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if(SwingUtilities.isLeftMouseButton(e)) {
                    sequenceManager.addStepSequence(new StepSequence());
                }
            }
        });
        this.tabbedPane.addTab("Add Sequence", null);
        this.tabbedPane.setTabComponentAt(0, addSequenceTabComponent);

        this.tabbedPane.addTab("Preferences", new OptionsPanel(this.sequenceManager));
        this.tabbedPane.addTab("About", new AboutPanel());
        this.popOutPanel = new PopOutPanel(this.tabbedPane, "Stepper");

        //Add tabs for existing sequences
        for (StepSequence sequence : this.sequenceManager.getSequences()) {
            addTabForSequence(sequence);
        }

        if(this.sequenceManager.getSequences().size() == 0){
            this.tabbedPane.setSelectedIndex(2); //View about page if no sequences
        }

        //Listen for tab additions and removals
        this.sequenceManager.addStepSequenceListener(new StepSequenceListener() {
            @Override
            public void onStepSequenceAdded(StepSequence sequence) {
                addTabForSequence(sequence);
            }

            @Override
            public void onStepSequenceRemoved(StepSequence sequence) {
                removeTabForSequence(sequence);
            }
        });
    }

    private void addTabForSequence(StepSequence sequence){
        StepSequenceTab tab = new StepSequenceTab(sequence);
        managerTabMap.put(sequence, tab);
        int newTabLocation = this.tabbedPane.getTabCount()-3;
        this.tabbedPane.insertTab("", null, tab, null, newTabLocation);

        Consumer<String> onTitleChange = sequence::setTitle;

        Consumer<Void> onRemoveClicked = aVoid -> {
            int result = JOptionPane.showConfirmDialog(tabbedPane, "Are you sure you want to remove this sequence? (" + sequence.getTitle() + ")", "Remove Sequence", JOptionPane.YES_NO_OPTION);
            if(result == JOptionPane.YES_OPTION) {
                this.sequenceManager.removeStepSequence(sequence);
            }
        };

        CustomTabComponent tabComponent = new CustomTabComponent( newTabLocation-1,
                sequence.getTitle(), false,
                true, onTitleChange, true, onRemoveClicked);


        tabComponent.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if(SwingUtilities.isRightMouseButton(e)){
                    JPopupMenu popupMenu = new JPopupMenu();
                    JMenuItem copyStep = new JMenuItem("Copy Step Sequence");

                    copyStep.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            int componentIndex = tabbedPane.indexOfComponent(tab);
                            StepSequence currentSeq = sequenceManager.getSequences().get(componentIndex);
                            Gson gson = Stepper.getGsonProvider().getGson();
                            String str = "[" + gson.toJson(currentSeq, new TypeToken<StepSequence>(){}.getType()) + "]";
                            ArrayList<StepSequence> newSeq = Stepper.getGsonProvider().getGson().fromJson(str, new TypeToken<ArrayList<StepSequence>>(){}.getType());
                            newSeq.get(0).setTitle(currentSeq.getTitle() + "_copy");
                            sequenceManager.addStepSequence(newSeq.get(0));
                        }

                    });

                    popupMenu.add(copyStep);
                    popupMenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });


        this.tabbedPane.setTabComponentAt(newTabLocation, tabComponent);
        this.tabbedPane.setSelectedIndex(newTabLocation);
    }

    private void removeTabForSequence(StepSequence sequence){
        StepSequenceTab stepSequenceTab = this.getTabForStepManager(sequence);
        int removedIndex = this.tabbedPane.indexOfComponent(stepSequenceTab);
        this.tabbedPane.remove(stepSequenceTab);
        this.managerTabMap.remove(sequence);

        if(removedIndex == 0 && this.managerTabMap.size() == 0){ //If we removed the leftmost tab and have no other tabs
            this.tabbedPane.setSelectedIndex(2); //View the about tab
        }else if(removedIndex == this.tabbedPane.getTabCount() - 3 && this.managerTabMap.size() > 0) {
            //If we removed the rightmost tab, but still have other tabs, move to a different tab instead
            this.tabbedPane.setSelectedIndex(removedIndex-1);
        }
    }

    public StepSequenceTab getTabForStepManager(StepSequence manager){
        return this.managerTabMap.get(manager);
    }

    public StepSequenceTab getSelectedStepSet(){
        if(!getUiComponent().isVisible()) return null;
        Component selectedStepSet = this.tabbedPane.getSelectedComponent();
        if(selectedStepSet instanceof StepSequenceTab)
            return (StepSequenceTab) selectedStepSet;
        else
            return null;
    }

    @Override
    public String getTabCaption() {
        return "Stepper";
    }

    @Override
    public Component getUiComponent() {
        return this.popOutPanel;
    }
}
