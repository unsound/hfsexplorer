/*-
 * Copyright (C) 2008 Erik Larsson
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catacombae.hfsexplorer.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.SwingUtilities;
import org.catacombae.hfsexplorer.ExtractProgressMonitor.CreateDirectoryFailedAction;
import org.catacombae.hfsexplorer.ExtractProgressMonitor.CreateFileFailedAction;
import org.catacombae.hfsexplorer.ExtractProgressMonitor.DirectoryExistsAction;
import org.catacombae.hfsexplorer.ExtractProgressMonitor.ExtractProperties;
import org.catacombae.hfsexplorer.ExtractProgressMonitor.ExtractPropertiesListener;
import org.catacombae.hfsexplorer.ExtractProgressMonitor.FileExistsAction;

/**
 *
 * @author  Erik
 */
public class ExtractSettingsPanel extends javax.swing.JPanel {
    private final ButtonGroup createDirButtonGroup = new ButtonGroup();
    private final ButtonGroup createFileButtonGroup = new ButtonGroup();
    private final ButtonGroup dirExistsButtonGroup = new ButtonGroup();
    private final ButtonGroup fileExistsButtonGroup = new ButtonGroup();
    
    public ExtractSettingsPanel(final ExtractProperties p) {
        this();
        
        p.addListener(new ExtractPropertiesListener() {
            @Override
            public void propertyChanged(Object changedProperty) {
                //System.err.println("Received a propertyChanged for " + changedProperty);
                final AbstractButton theButton;
                if(changedProperty instanceof CreateDirectoryFailedAction) {
                    switch((CreateDirectoryFailedAction)changedProperty) {
                        case PROMPT_USER:
                            theButton = createDirPromptUserButton;
                            break;
                        case AUTO_RENAME:
                            theButton = createDirAutoRenameButton;
                            break;
                        case SKIP_DIRECTORY:
                            theButton = createDirSkipDirectoryButton;
                            break;
                        case CANCEL:
                            theButton = createDirCancelButton;
                            break;
                        default:
                            throw new RuntimeException("Unknown property: " + changedProperty);
                    }
                }
                else if(changedProperty instanceof CreateFileFailedAction) {
                    switch((CreateFileFailedAction)changedProperty) {
                        case PROMPT_USER:
                            theButton = createFilePromptUserButton;
                            break;
                        case SKIP_FILE:
                            theButton = createFileSkipFileButton;
                            break;
                        case AUTO_RENAME:
                            theButton = createFileAutoRenameButton;
                            break;
                        case SKIP_DIRECTORY:
                            theButton = createFileSkipDirectoryButton;
                            break;
                        case CANCEL:
                            theButton = createFileCancelButton;
                            break;
                        default:
                            throw new RuntimeException("Unknown property: " + changedProperty);
                    }
                }
                else if(changedProperty instanceof DirectoryExistsAction) {
                    switch((DirectoryExistsAction)changedProperty) {
                        case PROMPT_USER:
                            theButton = dirExistsPromptUserButton;
                            break;
                        case CONTINUE:
                            theButton = dirExistsContinueButton;
                            break;
                        case AUTO_RENAME:
                            theButton = dirExistsAutoRenameButton;
                            break;
                        case SKIP_DIRECTORY:
                            theButton = dirExistsSkipDirectoryButton;
                            break;
                        case CANCEL:
                            theButton = dirExistsCancelButton;
                            break;
                        default:
                            throw new RuntimeException("Unknown property: " + changedProperty);
                    }
                }
                else if(changedProperty instanceof FileExistsAction) {
                    switch((FileExistsAction)changedProperty) {
                        case PROMPT_USER:
                            theButton = fileExistsPromptUserButton;
                            break;
                        case OVERWRITE:
                            theButton = fileExistsOverwriteButton;
                            break;
                        case AUTO_RENAME:
                            theButton = fileExistsAutoRenameButton;
                            break;
                        case SKIP_FILE:
                            theButton = fileExistsSkipFileButton;
                            break;
                        case SKIP_DIRECTORY:
                            theButton = fileExistsSkipDirectoryButton;
                            break;
                        case CANCEL:
                            theButton = fileExistsCancelButton;
                            break;
                        default:
                            throw new RuntimeException("Unknown property: " + changedProperty);
                    }
                }
                else
                    throw new RuntimeException("Unknown property: " +
                            (changedProperty != null?changedProperty.getClass():"null"));
                
                if(theButton != null) {
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            theButton.setSelected(true);
                        }
                    });
                }
            }
        });
        
        createDirPromptUserButton.doClick();
        createFilePromptUserButton.doClick();
        dirExistsPromptUserButton.doClick();
        fileExistsPromptUserButton.doClick();

        createDirPromptUserButton.addActionListener(new CreateDirListener(createDirPromptUserButton,
                p, CreateDirectoryFailedAction.PROMPT_USER));
        createDirSkipDirectoryButton.addActionListener(new CreateDirListener(createDirSkipDirectoryButton,
                p, CreateDirectoryFailedAction.SKIP_DIRECTORY));
        createDirAutoRenameButton.addActionListener(new CreateDirListener(createDirAutoRenameButton,
                p, CreateDirectoryFailedAction.AUTO_RENAME));
        createDirCancelButton.addActionListener(new CreateDirListener(createDirCancelButton,
                p, CreateDirectoryFailedAction.CANCEL));
        
        createFilePromptUserButton.addActionListener(new CreateFileListener(createFilePromptUserButton,
                p, CreateFileFailedAction.PROMPT_USER));
        createFileSkipFileButton.addActionListener(new CreateFileListener(createFileSkipFileButton,
                p, CreateFileFailedAction.SKIP_FILE));
        createFileSkipDirectoryButton.addActionListener(new CreateFileListener(createFileSkipDirectoryButton,
                p, CreateFileFailedAction.SKIP_DIRECTORY));
        createFileAutoRenameButton.addActionListener(new CreateFileListener(createFileAutoRenameButton,
                p, CreateFileFailedAction.AUTO_RENAME));
        createFileCancelButton.addActionListener(new CreateFileListener(createFileCancelButton,
                p, CreateFileFailedAction.CANCEL));
        
        dirExistsPromptUserButton.addActionListener(new DirExistsListener(dirExistsPromptUserButton,
                p, DirectoryExistsAction.PROMPT_USER));
        dirExistsContinueButton.addActionListener(new DirExistsListener(dirExistsContinueButton,
                p, DirectoryExistsAction.CONTINUE));
        dirExistsSkipDirectoryButton.addActionListener(new DirExistsListener(dirExistsSkipDirectoryButton,
                p, DirectoryExistsAction.SKIP_DIRECTORY));
        dirExistsAutoRenameButton.addActionListener(new DirExistsListener(dirExistsAutoRenameButton,
                p, DirectoryExistsAction.AUTO_RENAME));
        dirExistsCancelButton.addActionListener(new DirExistsListener(dirExistsCancelButton,
                p, DirectoryExistsAction.CANCEL));
        
        fileExistsPromptUserButton.addActionListener(new FileExistsListener(fileExistsPromptUserButton,
                p, FileExistsAction.PROMPT_USER));
        fileExistsSkipFileButton.addActionListener(new FileExistsListener(fileExistsSkipFileButton,
                p, FileExistsAction.SKIP_FILE));
        fileExistsSkipDirectoryButton.addActionListener(new FileExistsListener(fileExistsSkipDirectoryButton,
                p, FileExistsAction.SKIP_DIRECTORY));
        fileExistsOverwriteButton.addActionListener(new FileExistsListener(fileExistsOverwriteButton,
                p, FileExistsAction.OVERWRITE));
        fileExistsAutoRenameButton.addActionListener(new FileExistsListener(fileExistsAutoRenameButton,
                p, FileExistsAction.AUTO_RENAME));
        fileExistsCancelButton.addActionListener(new FileExistsListener(fileExistsCancelButton,
                p, FileExistsAction.CANCEL));
    }
    /** Creates new form ExtractSettingsPanel */
    private ExtractSettingsPanel() {
        initComponents();
        
        createDirButtonGroup.add(createDirPromptUserButton);
        createDirButtonGroup.add(createDirSkipDirectoryButton);
        createDirButtonGroup.add(createDirAutoRenameButton);
        createDirButtonGroup.add(createDirCancelButton);
        
        createFileButtonGroup.add(createFilePromptUserButton);
        createFileButtonGroup.add(createFileSkipFileButton);
        createFileButtonGroup.add(createFileSkipDirectoryButton);
        createFileButtonGroup.add(createFileAutoRenameButton);
        createFileButtonGroup.add(createFileCancelButton);
        
        dirExistsButtonGroup.add(dirExistsPromptUserButton);
        dirExistsButtonGroup.add(dirExistsContinueButton);
        dirExistsButtonGroup.add(dirExistsSkipDirectoryButton);
        dirExistsButtonGroup.add(dirExistsAutoRenameButton);
        dirExistsButtonGroup.add(dirExistsCancelButton);

        fileExistsButtonGroup.add(fileExistsPromptUserButton);
        fileExistsButtonGroup.add(fileExistsSkipFileButton);
        fileExistsButtonGroup.add(fileExistsSkipDirectoryButton);
        fileExistsButtonGroup.add(fileExistsOverwriteButton);
        fileExistsButtonGroup.add(fileExistsAutoRenameButton);
        fileExistsButtonGroup.add(fileExistsCancelButton);
        
        quietModeBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                boolean selected = quietModeBox.isSelected();
                
                if(selected) {
                    createDirSkipDirectoryButton.doClick();
                    createFileSkipFileButton.doClick();
                    dirExistsSkipDirectoryButton.doClick();
                    fileExistsSkipFileButton.doClick();
                }
                
                List<ButtonGroup> buttonGroups = Arrays.asList(createDirButtonGroup,
                        createFileButtonGroup, dirExistsButtonGroup, fileExistsButtonGroup);
                for(ButtonGroup bg : buttonGroups) {
                    Enumeration<AbstractButton> buttonEnum = bg.getElements();
                    while(buttonEnum.hasMoreElements()) {
                        AbstractButton b = buttonEnum.nextElement();
                        b.setEnabled(!selected);
                    }
                }
            }
            
        });
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        quietModeBox = new javax.swing.JCheckBox();
        createDirPanel = new javax.swing.JPanel();
        createDirLabel = new javax.swing.JLabel();
        createDirPromptUserButton = new javax.swing.JRadioButton();
        createDirSkipDirectoryButton = new javax.swing.JRadioButton();
        createDirAutoRenameButton = new javax.swing.JRadioButton();
        createDirCancelButton = new javax.swing.JRadioButton();
        createFilePanel = new javax.swing.JPanel();
        createFileLabel = new javax.swing.JLabel();
        createFilePromptUserButton = new javax.swing.JRadioButton();
        createFileSkipFileButton = new javax.swing.JRadioButton();
        createFileSkipDirectoryButton = new javax.swing.JRadioButton();
        createFileAutoRenameButton = new javax.swing.JRadioButton();
        createFileCancelButton = new javax.swing.JRadioButton();
        dirExistsPanel = new javax.swing.JPanel();
        dirExistsLabel = new javax.swing.JLabel();
        dirExistsPromptUserButton = new javax.swing.JRadioButton();
        dirExistsContinueButton = new javax.swing.JRadioButton();
        dirExistsSkipDirectoryButton = new javax.swing.JRadioButton();
        dirExistsAutoRenameButton = new javax.swing.JRadioButton();
        dirExistsCancelButton = new javax.swing.JRadioButton();
        fileExistsPanel = new javax.swing.JPanel();
        fileExistsLabel = new javax.swing.JLabel();
        fileExistsPromptUserButton = new javax.swing.JRadioButton();
        fileExistsSkipFileButton = new javax.swing.JRadioButton();
        fileExistsSkipDirectoryButton = new javax.swing.JRadioButton();
        fileExistsOverwriteButton = new javax.swing.JRadioButton();
        fileExistsAutoRenameButton = new javax.swing.JRadioButton();
        fileExistsCancelButton = new javax.swing.JRadioButton();

        quietModeBox.setText("Quiet mode");
        quietModeBox.setToolTipText("A non-destructive \"No questions asked\" mode");

        createDirLabel.setText("Create directory failed:");

        createDirPromptUserButton.setText("Prompt user");

        createDirSkipDirectoryButton.setText("Skip directory");

        createDirAutoRenameButton.setText("Auto-rename");

        createDirCancelButton.setText("Cancel");

        org.jdesktop.layout.GroupLayout createDirPanelLayout = new org.jdesktop.layout.GroupLayout(createDirPanel);
        createDirPanel.setLayout(createDirPanelLayout);
        createDirPanelLayout.setHorizontalGroup(
            createDirPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(createDirLabel)
            .add(createDirPanelLayout.createSequentialGroup()
                .add(10, 10, 10)
                .add(createDirPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(createDirPromptUserButton)
                    .add(createDirSkipDirectoryButton)
                    .add(createDirAutoRenameButton)
                    .add(createDirCancelButton)))
        );
        createDirPanelLayout.setVerticalGroup(
            createDirPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(createDirPanelLayout.createSequentialGroup()
                .add(createDirLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(createDirPromptUserButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(createDirSkipDirectoryButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(createDirAutoRenameButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(createDirCancelButton)
                .addContainerGap(23, Short.MAX_VALUE))
        );

        createFileLabel.setText("Create file failed:");

        createFilePromptUserButton.setText("Prompt user");

        createFileSkipFileButton.setText("Skip file");

        createFileSkipDirectoryButton.setText("Skip rest of directory");

        createFileAutoRenameButton.setText("Auto-rename");

        createFileCancelButton.setText("Cancel");

        org.jdesktop.layout.GroupLayout createFilePanelLayout = new org.jdesktop.layout.GroupLayout(createFilePanel);
        createFilePanel.setLayout(createFilePanelLayout);
        createFilePanelLayout.setHorizontalGroup(
            createFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(createFilePanelLayout.createSequentialGroup()
                .add(createFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(createFileLabel)
                    .add(createFilePanelLayout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(createFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(createFileSkipFileButton)
                            .add(createFilePromptUserButton)
                            .add(createFileSkipDirectoryButton)
                            .add(createFileAutoRenameButton)
                            .add(createFileCancelButton))))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        createFilePanelLayout.setVerticalGroup(
            createFilePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(createFilePanelLayout.createSequentialGroup()
                .add(createFileLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(createFilePromptUserButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(createFileSkipFileButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(createFileSkipDirectoryButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(createFileAutoRenameButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(createFileCancelButton))
        );

        dirExistsLabel.setText("Directory already exists:");

        dirExistsPromptUserButton.setText("Prompt user");

        dirExistsContinueButton.setText("Continue");

        dirExistsSkipDirectoryButton.setText("Skip directory");

        dirExistsAutoRenameButton.setText("Auto-rename");

        dirExistsCancelButton.setText("Cancel");

        org.jdesktop.layout.GroupLayout dirExistsPanelLayout = new org.jdesktop.layout.GroupLayout(dirExistsPanel);
        dirExistsPanel.setLayout(dirExistsPanelLayout);
        dirExistsPanelLayout.setHorizontalGroup(
            dirExistsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(dirExistsLabel)
            .add(dirExistsPanelLayout.createSequentialGroup()
                .add(10, 10, 10)
                .add(dirExistsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(dirExistsContinueButton)
                    .add(dirExistsPromptUserButton)
                    .add(dirExistsSkipDirectoryButton)
                    .add(dirExistsAutoRenameButton)
                    .add(dirExistsCancelButton)))
        );
        dirExistsPanelLayout.setVerticalGroup(
            dirExistsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(dirExistsPanelLayout.createSequentialGroup()
                .add(dirExistsLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(dirExistsPromptUserButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(dirExistsContinueButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(dirExistsSkipDirectoryButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(dirExistsAutoRenameButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(dirExistsCancelButton))
        );

        fileExistsLabel.setText("File already exists:");

        fileExistsPromptUserButton.setText("Prompt user");

        fileExistsSkipFileButton.setText("Skip file");

        fileExistsSkipDirectoryButton.setText("Skip rest of directory");

        fileExistsOverwriteButton.setText("Overwrite existing file");

        fileExistsAutoRenameButton.setText("Auto-rename");

        fileExistsCancelButton.setText("Cancel");

        org.jdesktop.layout.GroupLayout fileExistsPanelLayout = new org.jdesktop.layout.GroupLayout(fileExistsPanel);
        fileExistsPanel.setLayout(fileExistsPanelLayout);
        fileExistsPanelLayout.setHorizontalGroup(
            fileExistsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(fileExistsPanelLayout.createSequentialGroup()
                .add(fileExistsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(fileExistsLabel)
                    .add(fileExistsPanelLayout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(fileExistsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(fileExistsCancelButton)
                            .add(fileExistsAutoRenameButton)
                            .add(fileExistsSkipFileButton)
                            .add(fileExistsPromptUserButton)
                            .add(fileExistsSkipDirectoryButton)
                            .add(fileExistsOverwriteButton))))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        fileExistsPanelLayout.setVerticalGroup(
            fileExistsPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(fileExistsPanelLayout.createSequentialGroup()
                .add(fileExistsLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(fileExistsPromptUserButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(fileExistsSkipFileButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(fileExistsSkipDirectoryButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(fileExistsOverwriteButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(fileExistsAutoRenameButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(fileExistsCancelButton))
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(21, 21, 21)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(dirExistsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(createDirPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                        .add(18, 18, 18)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(fileExistsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                            .add(createFilePanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(quietModeBox))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(quietModeBox)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(createDirPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(createFilePanel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(fileExistsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(dirExistsPanel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JRadioButton createDirAutoRenameButton;
    private javax.swing.JRadioButton createDirCancelButton;
    private javax.swing.JLabel createDirLabel;
    private javax.swing.JPanel createDirPanel;
    private javax.swing.JRadioButton createDirPromptUserButton;
    private javax.swing.JRadioButton createDirSkipDirectoryButton;
    private javax.swing.JRadioButton createFileAutoRenameButton;
    private javax.swing.JRadioButton createFileCancelButton;
    private javax.swing.JLabel createFileLabel;
    private javax.swing.JPanel createFilePanel;
    private javax.swing.JRadioButton createFilePromptUserButton;
    private javax.swing.JRadioButton createFileSkipDirectoryButton;
    private javax.swing.JRadioButton createFileSkipFileButton;
    private javax.swing.JRadioButton dirExistsAutoRenameButton;
    private javax.swing.JRadioButton dirExistsCancelButton;
    private javax.swing.JRadioButton dirExistsContinueButton;
    private javax.swing.JLabel dirExistsLabel;
    private javax.swing.JPanel dirExistsPanel;
    private javax.swing.JRadioButton dirExistsPromptUserButton;
    private javax.swing.JRadioButton dirExistsSkipDirectoryButton;
    private javax.swing.JRadioButton fileExistsAutoRenameButton;
    private javax.swing.JRadioButton fileExistsCancelButton;
    private javax.swing.JLabel fileExistsLabel;
    private javax.swing.JRadioButton fileExistsOverwriteButton;
    private javax.swing.JPanel fileExistsPanel;
    private javax.swing.JRadioButton fileExistsPromptUserButton;
    private javax.swing.JRadioButton fileExistsSkipDirectoryButton;
    private javax.swing.JRadioButton fileExistsSkipFileButton;
    private javax.swing.JCheckBox quietModeBox;
    // End of variables declaration//GEN-END:variables

    private abstract class AbstractListener<A> implements ActionListener {
        protected final AbstractButton button;
        protected final ExtractProperties p;
        protected final A action;
        
        public AbstractListener(AbstractButton button, ExtractProperties p, A action) {
            this.button = button;
            this.p = p;
            this.action = action;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            if(button.isSelected()) {
                //System.err.println("Setting action " + action.getClass().getSimpleName() + "." +
                //        action);
                setAction(action);
            }
        }
        
        protected abstract void setAction(A action);
    }
    
    private class CreateDirListener extends AbstractListener<CreateDirectoryFailedAction> {
        public CreateDirListener(AbstractButton button, ExtractProperties p, CreateDirectoryFailedAction action) {
            super(button, p, action);
        }

        @Override
        protected void setAction(CreateDirectoryFailedAction action) {
            p.setCreateDirectoryFailedAction(action);
        }
    }
    
    private class CreateFileListener extends AbstractListener<CreateFileFailedAction> {
        public CreateFileListener(AbstractButton button, ExtractProperties p, CreateFileFailedAction action) {
            super(button, p, action);
        }

        @Override
        protected void setAction(CreateFileFailedAction action) {
            p.setCreateFileFailedAction(action);
        }
    }
    
    private class DirExistsListener extends AbstractListener<DirectoryExistsAction> {
        public DirExistsListener(AbstractButton button, ExtractProperties p, DirectoryExistsAction action) {
            super(button, p, action);
        }

        @Override
        protected void setAction(DirectoryExistsAction action) {
            p.setDirectoryExistsAction(action);
        }
    }
    
    private class FileExistsListener extends AbstractListener<FileExistsAction> {
        public FileExistsListener(AbstractButton button, ExtractProperties p, FileExistsAction action) {
            super(button, p, action);
        }

        @Override
        protected void setAction(FileExistsAction action) {
            p.setFileExistsAction(action);
        }
    }
    
    /*
    public static void main(String[] args) {
        JFrame jf = new JFrame("Test");
        jf.add(new ExtractSettingsPanel());
        jf.pack();
        jf.setLocationRelativeTo(null);
        jf.setVisible(true);
        jf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
    */
};
