/**
 * MFsim - Molecular Fragment DPD Simulation Environment
 * Copyright (C) 2023  Achim Zielesny (achim.zielesny@googlemail.com)
 * 
 * Source code is available at <https://github.com/zielesny/MFsim>
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
package de.gnwi.mfsim.gui.main;

import de.gnwi.mfsim.gui.control.CustomPanelImage;
import de.gnwi.mfsim.gui.control.CustomPanelSelection;
import de.gnwi.mfsim.gui.message.GuiMessage;
import de.gnwi.mfsim.model.preference.Preferences;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.JButton;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import de.gnwi.mfsim.gui.preference.GuiDefinitions;
import de.gnwi.mfsim.model.preference.ModelDefinitions;

/**
 * Main form
 *
 * @author Achim Zielesny
 */
public class MainFrame extends JFrame {

    // <editor-fold defaultstate="collapsed" desc="Private class variables">
    /**
     * GUI element
     */
    private JMenu cacheMenu;
    /**
     * GUI element
     */
    private JMenuItem showCacheMenuItem;
    /**
     * GUI element
     */
    private JMenuItem clearCacheMenuItem;
    /**
     * GUI element
     */
    private JMenu schemataMenu;
    /**
     * GUI element
     */
    private JMenuItem manageSchemataMenuItem;
    /**
     * GUI element
     */
    private JButton copySelectedJobInputsButton;
    /**
     * GUI element
     */
    private JButton browseJobResultFolderButton;
    /**
     * GUI element
     */
    private JButton viewLogfileButton;
    /**
     * GUI element
     */
    private JButton browseJobInputFolderButton;
    /**
     * GUI element
     */
    private JButton useExecutionJobForRestartButton;
    /**
     * GUI element
     */
    private JCheckBoxMenuItem particleUpdateForJobInputMenuItem;
    /**
     * GUI element
     */
    private JButton jobResultArchiveButton;
    /**
     * GUI element
     */
    private JButton jobResultArchivePreferencesEditButton;
    /**
     * GUI element
     */
    private JButton jobResultSettingsPreferencesEditButton;
    /**
     * GUI element
     */
    private JButton importJobResultButton;
    /**
     * GUI element
     */
    private JButton jobInputArchiveButton;
    /**
     * GUI element
     */
    private JMenuItem browseTutorialsMenuItem;
    /**
     * GUI element
     */
    private JMenuItem browseMFsimDataMenuItem;
    /**
     * GUI element
     */
    private JMenuItem browseMFsimJobMenuItem;
    /**
     * GUI element
     */
    private JMenuItem browseMFsimTempMenuItem;
    /**
     * GUI element
     */
    private JMenuItem browseMFsimLogMenuItem;
    /**
     * GUI element
     */
    private JMenuItem resetMFsimLogMenuItem;
    /**
     * GUI element
     */
    private JMenuItem browseMFsimSourceMenuItem;
    /**
     * GUI element
     */
    private JMenuItem browseMFsimInfoMenuItem;
    /**
     * GUI element
     */
    private JButton showProgressButton;
    /**
     * GUI element
     */
    private JButton editStepNumberButton;
    /**
     * GUI element
     */
    private SpringLayout jobResultsPanelSpringLayout;
    /**
     * GUI element
     */
    private JPanel jobResultsPanel;
    /**
     * GUI element
     */
    private SpringLayout jobInputsPanelSpringLayout;
    /**
     * GUI element
     */
    private JPanel jobInputsPanel;
    /**
     * GUI element
     */
    private JPanel jobExecutionSelectPanel;
    /**
     * GUI element
     */
    private SpringLayout jobExecutionSelectPanelSpringLayout;
    /**
     * GUI element
     */
    private JMenuItem restorePreferencesMenuItem;
    /**
     * GUI element
     */
    private JMenu preferencesMenu;
    /**
     * GUI element
     */
    private JButton cleanJobExecutionQueueButton;
    /**
     * GUI element
     */
    private JButton startJobExecutionButton;
    /**
     * GUI element
     */
    private JButton stopSelectedJobButton;
    /**
     * GUI element
     */
    private JMenuItem chooseParticleSetMenuItem;
    /**
     * GUI element
     */
    private JMenuItem formatParticleSetMenuItem;
    /**
     * GUI element
     */
    private JMenuItem duplicateParticlesMenuItem;
    /**
     * GUI element
     */
    private JMenuItem editParticlesMenuItem;
    /**
     * GUI element
     */
    private JMenu particlesMenu;
    /**
     * GUI element
     */
    private JMenu particleSetSubMenu;
    /**
     * GUI element
     */
    private JMenu rescaleRepulsionSubMenu;
    /**
     * GUI element
     */
    private JMenu particleSubMenu;
    /**
     * GUI element
     */
    private JButton viewSelectedJobResultButton;
    /**
     * GUI element
     */
    private JButton viewSelectedJobInputButton;
    /**
     * GUI element
     */
    private JButton importJobInputButton;
    /**
     * GUI element
     */
    private JButton addExecutionJobForRestartButton;
    /**
     * GUI element
     */
    private JLabel stepsForRestartInfoLabel;
    /**
     * GUI element
     */
    private CustomPanelSelection selectJobForRestartSelectionPanel;
    /**
     * GUI element
     */
    private SpringLayout selectJobForRestartPanelSpringLayout;
    /**
     * GUI element
     */
    private JButton addExecutionJobInputButton;
    /**
     * GUI element
     */
    private CustomPanelSelection selectExecutionJobInputSelectionPanel;
    /**
     * GUI element
     */
    private SpringLayout selectExecutionJobInputPanelSpringLayout;
    /**
     * GUI element
     */
    private JButton removeSelectedJobResultButton;
    /**
     * GUI element
     */
    private JButton editTemplateJobInputButton;
    /**
     * GUI element
     */
    private JButton removeSelectedJobInputButton;
    /**
     * GUI element
     */
    private JButton editExistingJobInputButton;
    /**
     * GUI element
     */
    private CustomPanelSelection jobInputsSelectionPanel;
    /**
     * GUI element
     */
    private CustomPanelSelection jobResultsSelectionPanel;
    /**
     * GUI element
     */
    private JPanel selectJobForRestartPanel;
    /**
     * GUI element
     */
    private JPanel selectExecutionJobInputPanel;
    /**
     * GUI element
     */
    private JButton editNewJobInputButton;
    /**
     * GUI element
     */
    private JMenu tutorialsMenu;
    /**
     * GUI element
     */
    private CustomPanelImage homeImagePanel;
    /**
     * GUI element
     */
    private SpringLayout homePanelSpringLayout;
    /**
     * GUI element
     */
    private JMenuItem editPreferencesMenuItem;
    /**
     * GUI element
     */
    private SpringLayout designPanelSpringLayout;
    /**
     * GUI element
     */
    private SpringLayout resultsPanelSpringLayout;
    /**
     * GUI element
     */
    private JPanel resultsPanel;
    /**
     * GUI element
     */
    private JMenuItem maximizeWindowSizeMenuItem;
    /**
     * GUI element
     */
    private JMenuItem centerMenuItem;
    /**
     * GUI element
     */
    private JList jobExecutionList;
    /**
     * GUI element
     */
    private JScrollPane jobExecutionListScrollPanel;
    /**
     * GUI element
     */
    private JMenuItem minimizeWindowSizeMenuItem;
    /**
     * GUI element
     */
    private JMenu windowMenu;
    /**
     * GUI element
     */
    private JButton positionDownInJobExecutionQueueButton;
    /**
     * GUI element
     */
    private JButton positionUpInJobExecutionQueueButton;
    /**
     * GUI element
     */
    private JButton removeJobfromJobExecutionQueueButton;
    /**
     * GUI element
     */
    private SpringLayout jobExecutionQueuePanelSpringLayout;
    /**
     * GUI element
     */
    private JPanel jobExecutionQueuePanel;
    /**
     * GUI element
     */
    private JTabbedPane selectTabbedPanel;
    /**
     * GUI element
     */
    private SpringLayout executionPanelSpringLayout;
    /**
     * GUI element
     */
    private JPanel executionPanel;
    /**
     * GUI element
     */
    private JPanel designPanel;
    /**
     * GUI element
     */
    private JPanel homePanel;
    /**
     * GUI element
     */
    private JMenuItem aboutMenuItem;
    /**
     * GUI element
     */
    private JMenu helpMenu;
    /**
     * GUI element
     */
    private JMenu mfSimDataMenu;
    /**
     * GUI element
     */
    private JMenu mfSimJobMenu;
    /**
     * GUI element
     */
    private JMenu mfSimTempMenu;
    /**
     * GUI element
     */
    private JMenu mfSimLogMenu;
    /**
     * GUI element
     */
    private JMenu mfSimSourceMenu;
    /**
     * GUI element
     */
    private JMenu mfSimInfoMenu;
    /**
     * GUI element
     */
    private JMenuItem exitMenuItem;
    /**
     * GUI element
     */
    private JMenu applicationMenu;
    /**
     * GUI element
     */
    private JMenuBar menuBar;
    /**
     * GUI element
     */
    private JLabel statusInformationLabel;
    /**
     * GUI element
     */
    private JTabbedPane mainTabbedPanel;
    /**
     * GUI element
     */
    private JPanel statusPanel;
    /**
     * GUI element
     */
    private TitledBorder jobInputsPanelTitledBorder;
    /**
     * GUI element
     */
    private TitledBorder jobResultsPanelTitledBorder;
    /**
     * GUI element
     */
    private JMenuItem incrementProbeParticlesMenuItem;
    /**
     * GUI element
     */
    private JMenuItem backboneRepulsionMenuItem;
    /**
     * GUI element
     */
    private JMenuItem removeParticlesMenuItem;
    /**
     * GUI element
     */
    private JMenuItem morphParticlesMenuItem;
    /**
     * GUI element
     */
    private JMenuItem rescaleVminMenuItem;
    /**
     * GUI element
     */
    private JMenuItem rescaleRepulsionIndividualMenuItem;
    /**
     * GUI element
     */
    private JMenuItem rescaleRepulsionGlobalMenuItem;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Private controller class variable">
    /**
     * Controller for this view
     */
    private MainFrameController mainFrameController;
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Serial version UID">
    /**
     * The serialVersionUID is a universal version identifier for a Serializable
     * class. Deserialization uses this number to ensure that a loaded class
     * corresponds exactly to a serialized object. If no match is found, then an
     * InvalidClassException is thrown.
     */
    static final long serialVersionUID = 1000000000000000000L;

    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="MainFrame constructor">
    /**
     * Main form constructor
     */
    public MainFrame() {
        super();
        // <editor-fold defaultstate="collapsed" desc="Instantiate main frame controller">
        mainFrameController = new MainFrameController(this);
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="MainFrame itself">
        this.setName(GuiMessage.get("MainFrame.name")); 
        this.setResizable(true);
        this.setMinimumSize(new Dimension(ModelDefinitions.MINIMUM_FRAME_WIDTH, ModelDefinitions.MINIMUM_FRAME_HEIGHT));
        this.setSize(ModelDefinitions.MINIMUM_FRAME_WIDTH, 921);
        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        addComponentListener(new ComponentAdapter() {

            public void componentResized(final ComponentEvent e) {
                MainFrame.this.mainFrameController.checkFrameSize();
            }

        });
        addWindowListener(new WindowAdapter() {

            public void windowClosing(final WindowEvent e) {
                MainFrame.this.mainFrameController.exit();
            }

        });
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="menuBar">
        {
            this.menuBar = new JMenuBar();
            setJMenuBar(this.menuBar);

            // <editor-fold defaultstate="collapsed" desc="applicationMenu">
            {
                this.applicationMenu = new JMenu();
                this.applicationMenu.setText(GuiMessage.get("MainFrame.applicationMenu.text")); 
                this.menuBar.add(this.applicationMenu);

                // <editor-fold defaultstate="collapsed" desc="preferencesMenu">
                {
                    this.preferencesMenu = new JMenu();
                    this.preferencesMenu.setText(GuiMessage.get("MainFrame.preferencesMenu.text")); 
                    this.applicationMenu.add(this.preferencesMenu);

                    // <editor-fold defaultstate="collapsed" desc="editPreferencesMenuItem">
                    {
                        this.editPreferencesMenuItem = new JMenuItem();
                        this.editPreferencesMenuItem.addActionListener(new ActionListener() {

                            public void actionPerformed(final ActionEvent e) {
                                MainFrame.this.mainFrameController.displayEditPreferencesDialog();
                            }

                        });
                        this.editPreferencesMenuItem.setText(GuiMessage.get("MainFrame.editPreferencesMenuItem.text")); 
                        this.preferencesMenu.add(this.editPreferencesMenuItem);
                    }

                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="restorePreferencesMenuItem">
                    {
                        this.restorePreferencesMenuItem = new JMenuItem();
                        this.restorePreferencesMenuItem.addActionListener(new ActionListener() {

                            public void actionPerformed(final ActionEvent e) {
                                MainFrame.this.mainFrameController.setDefaultPreferences();
                            }

                        });
                        this.restorePreferencesMenuItem.setText(GuiMessage.get("MainFrame.restorePreferencesMenuItem.text")); 
                        this.preferencesMenu.add(this.restorePreferencesMenuItem);
                    }

                    // </editor-fold>
                }

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="schemataMenu">
                {
                    this.schemataMenu = new JMenu();
                    this.schemataMenu.setText(GuiMessage.get("MainFrame.schemataMenu.text")); 
                    this.applicationMenu.add(this.schemataMenu);
                    // <editor-fold defaultstate="collapsed" desc="manageSchemataMenuItem">
                    {
                        this.manageSchemataMenuItem = new JMenuItem();
                        this.manageSchemataMenuItem.addActionListener(new ActionListener() {

                            public void actionPerformed(final ActionEvent e) {
                                MainFrame.this.mainFrameController.manageSchemata();
                            }

                        });
                        this.manageSchemataMenuItem.setText(GuiMessage.get("MainFrame.manageSchemataMenuItem.text")); 
                        this.schemataMenu.add(this.manageSchemataMenuItem);
                    }

                    // </editor-fold>
                }

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="cacheMenu">
                {
                    this.cacheMenu = new JMenu();
                    this.cacheMenu.setText(GuiMessage.get("MainFrame.cacheMenu.text")); 
                    this.applicationMenu.add(this.cacheMenu);
                    // <editor-fold defaultstate="collapsed" desc="showCacheMenuItem">
                    {
                        this.showCacheMenuItem = new JMenuItem();
                        this.showCacheMenuItem.addActionListener(new ActionListener() {

                            public void actionPerformed(final ActionEvent e) {
                                MainFrame.this.mainFrameController.showCache();
                            }

                        });
                        this.showCacheMenuItem.setText(GuiMessage.get("MainFrame.showCacheMenuItem.text")); 
                        this.cacheMenu.add(this.showCacheMenuItem);
                    }

                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="clearCacheMenuItem">
                    {
                        this.clearCacheMenuItem = new JMenuItem();
                        this.clearCacheMenuItem.addActionListener(new ActionListener() {

                            public void actionPerformed(final ActionEvent e) {
                                MainFrame.this.mainFrameController.clearCache();
                            }

                        });
                        this.clearCacheMenuItem.setText(GuiMessage.get("MainFrame.clearCacheMenuItem.text")); 
                        this.cacheMenu.add(this.clearCacheMenuItem);
                    }

                    // </editor-fold>
                }

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Separator">
                {
                    this.applicationMenu.addSeparator();
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="exitMenuItem">
                {
                    this.exitMenuItem = new JMenuItem();
                    this.exitMenuItem.addActionListener(new ActionListener() {

                        public void actionPerformed(final ActionEvent e) {
                            MainFrame.this.mainFrameController.exit();
                        }

                    });
                    this.exitMenuItem.setText(GuiMessage.get("MainFrame.exitMenuItem.text")); 
                    this.applicationMenu.add(this.exitMenuItem);
                }

                // </editor-fold>
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="particlesMenu">
            {
                this.particlesMenu = new JMenu();
                this.particlesMenu.setText(GuiMessage.get("MainFrame.particlesMenu.text")); 
                this.menuBar.add(this.particlesMenu);

                // <editor-fold defaultstate="collapsed" desc="particleSetSubMenu">
                {
                    this.particleSetSubMenu = new JMenu();
                    this.particleSetSubMenu.setText(GuiMessage.get("MainFrame.particleSetSubMenu.text")); 
                    this.particlesMenu.add(this.particleSetSubMenu);
                    
                    // <editor-fold defaultstate="collapsed" desc="chooseParticleSetMenuItem">
                    {
                        this.chooseParticleSetMenuItem = new JMenuItem();
                        this.chooseParticleSetMenuItem.addActionListener(new ActionListener() {

                            public void actionPerformed(ActionEvent e) {
                                MainFrame.this.mainFrameController.displayChooseParticleSetDialog();
                            }

                        });
                        this.chooseParticleSetMenuItem.setText(GuiMessage.get("MainFrame.chooseParticleSetMenuItem.text")); 
                        this.particleSetSubMenu.add(this.chooseParticleSetMenuItem);
                    }
                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="formatParticleSetMenuItem">
                    {
                        this.formatParticleSetMenuItem = new JMenuItem();
                        this.formatParticleSetMenuItem.addActionListener(new ActionListener() {

                            public void actionPerformed(ActionEvent e) {
                                MainFrame.this.mainFrameController.displayFormatParticleSetDialog();
                            }

                        });
                        this.formatParticleSetMenuItem.setText(GuiMessage.get("MainFrame.formatParticleSetMenuItem.text")); 
                        this.particleSetSubMenu.add(this.formatParticleSetMenuItem);
                    }
                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="rescaleVminMenuItem">
                    {
                        this.rescaleVminMenuItem = new JMenuItem();
                        this.rescaleVminMenuItem.addActionListener(new ActionListener() {

                            public void actionPerformed(ActionEvent e) {
                                MainFrame.this.mainFrameController.displayRescaleVminDialog();
                            }

                        });
                        this.rescaleVminMenuItem.setText(GuiMessage.get("MainFrame.rescaleVminMenuItem.text")); 
                        this.particleSetSubMenu.add(this.rescaleVminMenuItem);
                    }
                    // </editor-fold>
                    {
                        this.rescaleRepulsionSubMenu = new JMenu();
                        this.rescaleRepulsionSubMenu.setText(GuiMessage.get("MainFrame.rescaleRepulsionSubMenu.text")); 
                        this.particleSetSubMenu.add(this.rescaleRepulsionSubMenu);

                        // <editor-fold defaultstate="collapsed" desc="rescaleRepulsionIndividualMenuItem">
                        {
                            this.rescaleRepulsionIndividualMenuItem = new JMenuItem();
                            this.rescaleRepulsionIndividualMenuItem.addActionListener(new ActionListener() {

                                public void actionPerformed(ActionEvent e) {
                                    MainFrame.this.mainFrameController.displayRescaleRepulsionsDialog(true);
                                }

                            });
                            this.rescaleRepulsionIndividualMenuItem.setText(GuiMessage.get("MainFrame.rescaleRepulsionIndividualMenuItem.text")); 
                            this.rescaleRepulsionSubMenu.add(this.rescaleRepulsionIndividualMenuItem);
                        }
                        // </editor-fold>
                        // <editor-fold defaultstate="collapsed" desc="rescaleRepulsionGlobalMenuItem">
                        {
                            this.rescaleRepulsionGlobalMenuItem = new JMenuItem();
                            this.rescaleRepulsionGlobalMenuItem.addActionListener(new ActionListener() {

                                public void actionPerformed(ActionEvent e) {
                                    MainFrame.this.mainFrameController.displayRescaleRepulsionsDialog(false);
                                }

                            });
                            this.rescaleRepulsionGlobalMenuItem.setText(GuiMessage.get("MainFrame.rescaleRepulsionGlobalMenuItem.text")); 
                            this.rescaleRepulsionSubMenu.add(this.rescaleRepulsionGlobalMenuItem);
                        }
                        // </editor-fold>
                    }
                    // <editor-fold defaultstate="collapsed" desc="editParticlesMenuItem">
                    {
                        this.editParticlesMenuItem = new JMenuItem();
                        this.editParticlesMenuItem.addActionListener(new ActionListener() {

                            public void actionPerformed(ActionEvent e) {
                                MainFrame.this.mainFrameController.displayEditParticlesDialog();
                            }

                        });
                        this.editParticlesMenuItem.setText(GuiMessage.get("MainFrame.editParticlesMenuItem.text")); 
                        this.particleSetSubMenu.add(this.editParticlesMenuItem);
                    }
                    // </editor-fold>
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="particleSubMenu">
                {
                    this.particleSubMenu = new JMenu();
                    this.particleSubMenu.setText(GuiMessage.get("MainFrame.particleSubMenu.text")); 
                    this.particlesMenu.add(this.particleSubMenu);
                    
                    // <editor-fold defaultstate="collapsed" desc="duplicateParticlesMenuItem">
                    {
                        this.duplicateParticlesMenuItem = new JMenuItem();
                        this.duplicateParticlesMenuItem.addActionListener(new ActionListener() {

                            public void actionPerformed(ActionEvent e) {
                                MainFrame.this.mainFrameController.displayDuplicateParticlesDialog();
                            }

                        });
                        this.duplicateParticlesMenuItem.setText(GuiMessage.get("MainFrame.duplicateParticlesMenuItem.text")); 
                        this.particleSubMenu.add(this.duplicateParticlesMenuItem);
                    }
                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="removeParticlesMenuItem">
                    {
                        this.removeParticlesMenuItem = new JMenuItem();
                        this.removeParticlesMenuItem.addActionListener(new ActionListener() {

                            public void actionPerformed(ActionEvent e) {
                                MainFrame.this.mainFrameController.displayRemoveParticlesDialog();
                            }

                        });
                        this.removeParticlesMenuItem.setText(GuiMessage.get("MainFrame.removeParticlesMenuItem.text")); 
                        this.particleSubMenu.add(this.removeParticlesMenuItem);
                    }
                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="morphParticlesMenuItem">
                    {
                        this.morphParticlesMenuItem = new JMenuItem();
                        this.morphParticlesMenuItem.addActionListener(new ActionListener() {

                            public void actionPerformed(ActionEvent e) {
                                MainFrame.this.mainFrameController.displayMorphParticleDialog();
                            }

                        });
                        this.morphParticlesMenuItem.setText(GuiMessage.get("MainFrame.morphParticleMenuItem.text")); 
                        this.particleSubMenu.add(this.morphParticlesMenuItem);
                    }
                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="incrementProbeParticlesMenuItem">
                    {
                        this.incrementProbeParticlesMenuItem = new JMenuItem();
                        this.incrementProbeParticlesMenuItem.addActionListener(new ActionListener() {

                            public void actionPerformed(ActionEvent e) {
                                MainFrame.this.mainFrameController.displayIncrementProbeParticlesDialog();
                            }

                        });
                        this.incrementProbeParticlesMenuItem.setText(GuiMessage.get("MainFrame.incrementProbeParticlesMenuItem.text")); 
                        this.particleSubMenu.add(this.incrementProbeParticlesMenuItem);
                    }
                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="backboneRepulsionMenuItem">
                    {
                        this.backboneRepulsionMenuItem = new JMenuItem();
                        this.backboneRepulsionMenuItem.addActionListener(new ActionListener() {

                            public void actionPerformed(ActionEvent e) {
                                MainFrame.this.mainFrameController.displayBackboneRepulsionDialog();
                            }

                        });
                        this.backboneRepulsionMenuItem.setText(GuiMessage.get("MainFrame.backboneRepulsionMenuItem.text")); 
                        this.particleSubMenu.add(this.backboneRepulsionMenuItem);
                    }
                    // </editor-fold>
                }                
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Separator">
                {
                    this.particlesMenu.addSeparator();
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="particleUpdateForJobInputMenuItem">
                {
                    this.particleUpdateForJobInputMenuItem = new JCheckBoxMenuItem();
                    this.particleUpdateForJobInputMenuItem.addItemListener(new ItemListener() {

                        public void itemStateChanged(ItemEvent e) {
                            Preferences.getInstance().setParticleUpdateForJobInput(MainFrame.this.particleUpdateForJobInputMenuItem.isSelected());
                        }

                    });
                    this.particleUpdateForJobInputMenuItem.setText(GuiMessage.get("MainFrame.particleUpdateForJobInputMenuItem.text")); 
                    this.particlesMenu.add(this.particleUpdateForJobInputMenuItem);
                }
                // </editor-fold>
            }
            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="windowMenu">
            {
                this.windowMenu = new JMenu();
                this.windowMenu.setText(GuiMessage.get("MainFrame.windowMenu.text")); 
                this.menuBar.add(this.windowMenu);

                // <editor-fold defaultstate="collapsed" desc="centerMenuItem">
                {
                    this.centerMenuItem = new JMenuItem();
                    this.centerMenuItem.addActionListener(new ActionListener() {

                        public void actionPerformed(final ActionEvent e) {
                            MainFrame.this.mainFrameController.centerFrame();
                        }

                    });
                    this.centerMenuItem.setText(GuiMessage.get("MainFrame.centerMenuItem.text")); 
                    this.windowMenu.add(this.centerMenuItem);
                }

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="minimizeWindowSizeMenuItem">
                {
                    this.minimizeWindowSizeMenuItem = new JMenuItem();
                    this.minimizeWindowSizeMenuItem.addActionListener(new ActionListener() {

                        public void actionPerformed(final ActionEvent e) {
                            MainFrame.this.mainFrameController.setDefaultFrameSize();
                        }

                    });
                    this.minimizeWindowSizeMenuItem.setText(GuiMessage.get("MainFrame.minimizeWindowSizeMenuItem.text")); 
                    this.windowMenu.add(this.minimizeWindowSizeMenuItem);
                }

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="maximizeWindowSizeMenuItem">
                {
                    this.maximizeWindowSizeMenuItem = new JMenuItem();
                    this.maximizeWindowSizeMenuItem.addActionListener(new ActionListener() {

                        public void actionPerformed(final ActionEvent e) {
                            MainFrame.this.mainFrameController.fitFrameToScreen();
                        }

                    });
                    this.maximizeWindowSizeMenuItem.setText(GuiMessage.get("MainFrame.maximizeWindowSizeMenuItem.text")); 
                    this.windowMenu.add(this.maximizeWindowSizeMenuItem);
                }

                // </editor-fold>
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="helpMenu">
            {
                this.helpMenu = new JMenu();
                this.helpMenu.setText(GuiMessage.get("MainFrame.helpMenu.text")); 
                this.menuBar.add(this.helpMenu);
                // <editor-fold defaultstate="collapsed" desc="tutorialsMenu">
                {
                    this.tutorialsMenu = new JMenu();
                    this.tutorialsMenu.setText(GuiMessage.get("MainFrame.tutorialsMenu.text")); 
                    this.helpMenu.add(this.tutorialsMenu);
                    {
                        this.browseTutorialsMenuItem = new JMenuItem();
                        this.browseTutorialsMenuItem.addActionListener(new ActionListener() {

                            public void actionPerformed(final ActionEvent e) {
                                MainFrame.this.mainFrameController.browseTutorialsDirectory();
                            }

                        });
                        this.browseTutorialsMenuItem.setText(GuiMessage.get("MainFrame.browseTutorialsMenuItem.text")); 
                        this.tutorialsMenu.add(this.browseTutorialsMenuItem);
                    }
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Separator">
                {
                    this.helpMenu.addSeparator();
                }

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="mfSimInfoMenu">
                {
                    this.mfSimInfoMenu = new JMenu();
                    this.mfSimInfoMenu.setText(GuiMessage.get("MainFrame.MFsimInfoMenu.text")); 
                    this.helpMenu.add(this.mfSimInfoMenu);
                    {
                        this.browseMFsimInfoMenuItem = new JMenuItem();
                        this.browseMFsimInfoMenuItem.addActionListener(new ActionListener() {

                            public void actionPerformed(final ActionEvent e) {
                                MainFrame.this.mainFrameController.browseMfsimInfoDirectory();
                            }

                        });
                        this.browseMFsimInfoMenuItem.setText(GuiMessage.get("MainFrame.browseMFsimInfoMenuItem.text")); 
                        this.mfSimInfoMenu.add(this.browseMFsimInfoMenuItem);
                    }
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="mfSimSourceMenu">
                {
                    this.mfSimSourceMenu = new JMenu();
                    this.mfSimSourceMenu.setText(GuiMessage.get("MainFrame.MFsimSourceMenu.text")); 
                    this.helpMenu.add(this.mfSimSourceMenu);
                    {
                        this.browseMFsimSourceMenuItem = new JMenuItem();
                        this.browseMFsimSourceMenuItem.addActionListener(new ActionListener() {

                            public void actionPerformed(final ActionEvent e) {
                                MainFrame.this.mainFrameController.browseMfsimSourceDirectory();
                            }

                        });
                        this.browseMFsimSourceMenuItem.setText(GuiMessage.get("MainFrame.browseMFsimSourceMenuItem.text")); 
                        this.mfSimSourceMenu.add(this.browseMFsimSourceMenuItem);
                    }
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="mfSimDataMenu">
                {
                    this.mfSimDataMenu = new JMenu();
                    this.mfSimDataMenu.setText(GuiMessage.get("MainFrame.MFsimDataMenu.text")); 
                    this.helpMenu.add(this.mfSimDataMenu);
                    {
                        this.browseMFsimDataMenuItem = new JMenuItem();
                        this.browseMFsimDataMenuItem.addActionListener(new ActionListener() {

                            public void actionPerformed(final ActionEvent e) {
                                MainFrame.this.mainFrameController.browseMFsimDataDirectory();
                            }

                        });
                        this.browseMFsimDataMenuItem.setText(GuiMessage.get("MainFrame.browseMFsimDataMenuItem.text")); 
                        this.mfSimDataMenu.add(this.browseMFsimDataMenuItem);
                    }
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="mfSimJobMenu">
                {
                    this.mfSimJobMenu = new JMenu();
                    this.mfSimJobMenu.setText(GuiMessage.get("MainFrame.MFsimJobMenu.text")); 
                    this.helpMenu.add(this.mfSimJobMenu);
                    {
                        this.browseMFsimJobMenuItem = new JMenuItem();
                        this.browseMFsimJobMenuItem.addActionListener(new ActionListener() {

                            public void actionPerformed(final ActionEvent e) {
                                MainFrame.this.mainFrameController.browseMFsimJobDirectory();
                            }

                        });
                        this.browseMFsimJobMenuItem.setText(GuiMessage.get("MainFrame.browseMFsimJobMenuItem.text")); 
                        this.mfSimJobMenu.add(this.browseMFsimJobMenuItem);
                    }
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="mfSimTempMenu">
                {
                    this.mfSimTempMenu = new JMenu();
                    this.mfSimTempMenu.setText(GuiMessage.get("MainFrame.MFsimTempMenu.text")); 
                    this.helpMenu.add(this.mfSimTempMenu);
                    {
                        this.browseMFsimTempMenuItem = new JMenuItem();
                        this.browseMFsimTempMenuItem.addActionListener(new ActionListener() {

                            public void actionPerformed(final ActionEvent e) {
                                MainFrame.this.mainFrameController.browseMFsimTempDirectory();
                            }

                        });
                        this.browseMFsimTempMenuItem.setText(GuiMessage.get("MainFrame.browseMFsimTempMenuItem.text")); 
                        this.mfSimTempMenu.add(this.browseMFsimTempMenuItem);
                    }
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Separator">
                {
                    this.helpMenu.addSeparator();
                }

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="mfSimLogMenu">
                {
                    this.mfSimLogMenu = new JMenu();
                    this.mfSimLogMenu.setText(GuiMessage.get("MainFrame.MFsimLogMenu.text")); 
                    this.helpMenu.add(this.mfSimLogMenu);
                    {
                        this.browseMFsimLogMenuItem = new JMenuItem();
                        this.browseMFsimLogMenuItem.addActionListener(new ActionListener() {

                            public void actionPerformed(final ActionEvent e) {
                                MainFrame.this.mainFrameController.browseMFsimLogfile();
                            }

                        });
                        this.browseMFsimLogMenuItem.setText(GuiMessage.get("MainFrame.browseMFsimLogMenuItem.text")); 
                        this.mfSimLogMenu.add(this.browseMFsimLogMenuItem);
                    }
                    {
                        this.resetMFsimLogMenuItem = new JMenuItem();
                        this.resetMFsimLogMenuItem.addActionListener(new ActionListener() {

                            public void actionPerformed(final ActionEvent e) {
                                MainFrame.this.mainFrameController.resetLogfile();
                            }

                        });
                        this.resetMFsimLogMenuItem.setText(GuiMessage.get("MainFrame.resetMFsimLogMenuItem.text")); 
                        this.mfSimLogMenu.add(this.resetMFsimLogMenuItem);
                    }
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="Separator">
                {
                    this.helpMenu.addSeparator();
                }

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="aboutMenuItem">
                {
                    this.aboutMenuItem = new JMenuItem();
                    this.helpMenu.add(this.aboutMenuItem);
                    this.aboutMenuItem.addActionListener(new ActionListener() {

                        public void actionPerformed(final ActionEvent e) {
                            MainFrame.this.mainFrameController.aboutApplication();
                        }

                    });
                    this.aboutMenuItem.setText(GuiMessage.get("MainFrame.aboutMenuItem.text")); 
                }

                // </editor-fold>
            }
            // </editor-fold>
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="mainTabbedPanel">
        {
            this.mainTabbedPanel = new JTabbedPane();
            this.mainTabbedPanel.addChangeListener(new ChangeListener() {

                public void stateChanged(final ChangeEvent e) {
                    MainFrame.this.mainFrameController.reactOnMainTabChange();
                }

            });
            this.getContentPane().add(this.mainTabbedPanel, BorderLayout.CENTER);
            // <editor-fold defaultstate="collapsed" desc="homePanel">
            {
                this.homePanel = new JPanel();
                this.homePanelSpringLayout = new SpringLayout();
                this.homePanel.setLayout(this.homePanelSpringLayout);
                this.homePanel.setToolTipText(GuiMessage.get("MainFrame.homePanel.toolTipText")); 
                this.mainTabbedPanel.addTab(GuiMessage.get("MainFrame.mainTabbedPanel.Tab.Home"), null, this.homePanel, GuiMessage.get("MainFrame.mainTabbedPanel.Tab.Home.ToolTip")); 

                // <editor-fold defaultstate="collapsed" desc="homeImagePanel">
                {
                    this.homeImagePanel = new CustomPanelImage();
                    this.homeImagePanel.setToolTipText(GuiMessage.get("MainFrame.homeImagePanel.toolTipText")); 
                    this.homeImagePanel.addMouseListener(new MouseAdapter() {

                        public void mouseClicked(final MouseEvent e) {
                            if (e.getClickCount() == 2) {
                                MainFrame.this.mainFrameController.browseGitHubRepository();
                            }
                        }

                    });
                    this.homePanel.add(this.homeImagePanel);
                    this.homePanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.homeImagePanel, -5, SpringLayout.SOUTH, this.homePanel);
                    this.homePanelSpringLayout.putConstraint(SpringLayout.NORTH, this.homeImagePanel, 5, SpringLayout.NORTH, this.homePanel);
                    this.homePanelSpringLayout.putConstraint(SpringLayout.EAST, this.homeImagePanel, -5, SpringLayout.EAST, this.homePanel);
                    this.homePanelSpringLayout.putConstraint(SpringLayout.WEST, this.homeImagePanel, 5, SpringLayout.WEST, this.homePanel);
                }

                // </editor-fold>
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="designPanel">
            {
                this.designPanel = new JPanel();
                this.designPanel.setToolTipText(GuiMessage.get("MainFrame.designPanel.toolTipText")); 
                this.designPanelSpringLayout = new SpringLayout();
                this.designPanel.setLayout(this.designPanelSpringLayout);
                this.mainTabbedPanel.addTab(GuiMessage.get("MainFrame.mainTabbedPanel.Tab.Design"), null, this.designPanel, GuiMessage.get("MainFrame.mainTabbedPanel.Tab.Design.ToolTip")); 
                // <editor-fold defaultstate="collapsed" desc="jobInputsPanel">
                {
                    this.jobInputsPanel = new JPanel();
                    this.jobInputsPanelTitledBorder = 
                        new TitledBorder(
                            new BevelBorder(BevelBorder.RAISED),
                            GuiMessage.get("MainFrame.jobInputsPanelTitledBorder.title"), 
                            TitledBorder.DEFAULT_JUSTIFICATION, 
                            TitledBorder.DEFAULT_POSITION, 
                            null, 
                            GuiDefinitions.PANEL_TITLE_COLOR
                        );
                    this.jobInputsPanel.setBorder(this.jobInputsPanelTitledBorder);
                    this.jobInputsPanelSpringLayout = new SpringLayout();
                    this.jobInputsPanel.setLayout(this.jobInputsPanelSpringLayout);
                    this.designPanel.add(this.jobInputsPanel);
                    this.designPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.jobInputsPanel, -50, SpringLayout.SOUTH, this.designPanel);
                    this.designPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.jobInputsPanel, 10, SpringLayout.NORTH, this.designPanel);
                    this.designPanelSpringLayout.putConstraint(SpringLayout.EAST, this.jobInputsPanel, -10, SpringLayout.EAST, this.designPanel);
                    this.designPanelSpringLayout.putConstraint(SpringLayout.WEST, this.jobInputsPanel, 10, SpringLayout.WEST, this.designPanel);
                    // <editor-fold defaultstate="collapsed" desc="jobInputsSelectionPanel">
                    {
                        this.jobInputsSelectionPanel = new CustomPanelSelection();
                        this.jobInputsSelectionPanel.setToolTipText(GuiMessage.get("MainFrame.jobInputsSelectionPanel.toolTipText")); 
                        this.jobInputsSelectionPanel.getFilterPanel().setToolTipText(GuiMessage.get("MainFrame.jobInputFilter.toolTipText")); 
                        this.jobInputsSelectionPanel.getList().setToolTipText(GuiMessage.get("MainFrame.availableJobInputList.toolTipText")); 
                        this.jobInputsSelectionPanel.getFilterPanel().getClearButton().setToolTipText(GuiMessage.get("MainFrame.jobInputFilter.clear.toolTipText"));  
                        this.jobInputsSelectionPanel.getFilterPanel().getEditButton().setToolTipText(GuiMessage.get("MainFrame.jobInputFilter.edit.toolTipText"));  
                        this.jobInputsSelectionPanel.getFilterPanel().getClearButton().addActionListener(new ActionListener() {

                            public void actionPerformed(ActionEvent e) {
                                MainFrame.this.mainFrameController.clearJobInputFilter();
                            }

                        });
                        this.jobInputsSelectionPanel.getFilterPanel().getEditButton().addActionListener(new ActionListener() {

                            public void actionPerformed(ActionEvent e) {
                                MainFrame.this.mainFrameController.editJobInputFilter();
                            }

                        });
                        this.jobInputsSelectionPanel.getList().setVisibleRowCount(-1);
                        this.jobInputsSelectionPanel.getList().addMouseListener(new MouseAdapter() {

                            public void mouseClicked(MouseEvent e) {
                                if (e.getClickCount() == 2) {
                                    // Double click: View existing job input
                                    MainFrame.this.mainFrameController.viewSelectedJobInput();
                                }
                            }

                        });
                        this.jobInputsPanel.add(this.jobInputsSelectionPanel);
                        this.jobInputsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.jobInputsSelectionPanel, -10, SpringLayout.EAST, this.jobInputsPanel);
                        this.jobInputsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.jobInputsSelectionPanel, 10, SpringLayout.WEST, this.jobInputsPanel);
                        this.jobInputsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.jobInputsSelectionPanel, -10, SpringLayout.SOUTH, this.jobInputsPanel);
                        this.jobInputsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.jobInputsSelectionPanel, 10, SpringLayout.NORTH, this.jobInputsPanel);
                    }
                    // </editor-fold>
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="editNewJobInputButton">
                {
                    this.editNewJobInputButton = new JButton();
                    this.editNewJobInputButton.setMnemonic(KeyEvent.VK_N);
                    this.editNewJobInputButton.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            MainFrame.this.mainFrameController.createNewJobInput();
                        }

                    });
                    this.editNewJobInputButton.setToolTipText(GuiMessage.get("MainFrame.editNewJobInputButton.toolTipText")); 
                    this.editNewJobInputButton.setText(GuiMessage.get("MainFrame.editNewJobInputButton.text")); 
                    this.designPanel.add(this.editNewJobInputButton);
                    this.designPanelSpringLayout.putConstraint(SpringLayout.EAST, this.editNewJobInputButton, 190, SpringLayout.WEST, this.designPanel);
                    this.designPanelSpringLayout.putConstraint(SpringLayout.WEST, this.editNewJobInputButton, 110, SpringLayout.WEST, this.designPanel);
                    this.designPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.editNewJobInputButton, -10, SpringLayout.SOUTH, this.designPanel);
                    this.designPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.editNewJobInputButton, -45, SpringLayout.SOUTH, this.designPanel);
                }

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="importJobInputButton">
                {
                    this.importJobInputButton = new JButton();
                    this.importJobInputButton.setMnemonic(KeyEvent.VK_I);
                    this.importJobInputButton.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            MainFrame.this.mainFrameController.importJobInputs();
                        }

                    });
                    this.importJobInputButton.setToolTipText(GuiMessage.get("MainFrame.importJobInputButton.toolTipText")); 
                    this.importJobInputButton.setText(GuiMessage.get("MainFrame.importJobInputButton.text")); 
                    this.designPanel.add(this.importJobInputButton);
                    this.designPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.importJobInputButton, -10, SpringLayout.SOUTH, this.designPanel);
                    this.designPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.importJobInputButton, -45, SpringLayout.SOUTH, this.designPanel);
                    this.designPanelSpringLayout.putConstraint(SpringLayout.EAST, this.importJobInputButton, 450, SpringLayout.WEST, this.designPanel);
                    this.designPanelSpringLayout.putConstraint(SpringLayout.WEST, this.importJobInputButton, 370, SpringLayout.WEST, this.designPanel);
                }

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="editExistingJobInputButton">
                {
                    this.editExistingJobInputButton = new JButton();
                    this.editExistingJobInputButton.setMnemonic(KeyEvent.VK_E);
                    this.editExistingJobInputButton.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            MainFrame.this.mainFrameController.selectExistingJobInputForEdit();
                        }

                    });
                    this.editExistingJobInputButton.setToolTipText(GuiMessage.get("MainFrame.editExistingJobInputButton.toolTipText")); 
                    this.editExistingJobInputButton.setText(GuiMessage.get("MainFrame.editExistingJobInputButton.text")); 
                    this.designPanel.add(this.editExistingJobInputButton);
                    this.designPanelSpringLayout.putConstraint(SpringLayout.EAST, this.editExistingJobInputButton, 270, SpringLayout.WEST, this.designPanel);
                    this.designPanelSpringLayout.putConstraint(SpringLayout.WEST, this.editExistingJobInputButton, 190, SpringLayout.WEST, this.designPanel);
                    this.designPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.editExistingJobInputButton, -10, SpringLayout.SOUTH, this.designPanel);
                    this.designPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.editExistingJobInputButton, -45, SpringLayout.SOUTH, this.designPanel);
                }

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="editTemplateJobInputButton">
                {
                    this.editTemplateJobInputButton = new JButton();
                    this.editTemplateJobInputButton.setMnemonic(KeyEvent.VK_U);
                    this.editTemplateJobInputButton.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            MainFrame.this.mainFrameController.useExistingJobInputAsTemplateForEdit();
                        }

                    });
                    this.editTemplateJobInputButton.setToolTipText(GuiMessage.get("MainFrame.editTemplateJobInputButton.toolTipText")); 
                    this.editTemplateJobInputButton.setText(GuiMessage.get("MainFrame.editTemplateJobInputButton.text")); 
                    this.designPanel.add(this.editTemplateJobInputButton);
                    this.designPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.editTemplateJobInputButton, -10, SpringLayout.SOUTH, this.designPanel);
                    this.designPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.editTemplateJobInputButton, -45, SpringLayout.SOUTH, this.designPanel);
                    this.designPanelSpringLayout.putConstraint(SpringLayout.EAST, this.editTemplateJobInputButton, 350, SpringLayout.WEST, this.designPanel);
                    this.designPanelSpringLayout.putConstraint(SpringLayout.WEST, this.editTemplateJobInputButton, 270, SpringLayout.WEST, this.designPanel);
                }

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="jobInputArchiveButton">
                {
                    this.jobInputArchiveButton = new JButton();
                    this.jobInputArchiveButton.setMnemonic(KeyEvent.VK_A);
                    this.jobInputArchiveButton.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            MainFrame.this.mainFrameController.archiveJobInput();
                        }

                    });
                    this.jobInputArchiveButton.setToolTipText(GuiMessage.get("MainFrame.jobInputArchiveButton.toolTipText")); 
                    this.jobInputArchiveButton.setText(GuiMessage.get("MainFrame.jobInputArchiveButton.text")); 
                    this.designPanel.add(this.jobInputArchiveButton);
                    this.designPanelSpringLayout.putConstraint(SpringLayout.EAST, this.jobInputArchiveButton, 530, SpringLayout.WEST, this.designPanel);
                    this.designPanelSpringLayout.putConstraint(SpringLayout.WEST, this.jobInputArchiveButton, 450, SpringLayout.WEST, this.designPanel);
                    this.designPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.jobInputArchiveButton, -10, SpringLayout.SOUTH, this.designPanel);
                    this.designPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.jobInputArchiveButton, -45, SpringLayout.SOUTH, this.designPanel);
                }

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="removeSelectedJobInputButton">
                {
                    this.removeSelectedJobInputButton = new JButton();
                    this.removeSelectedJobInputButton.setMnemonic(KeyEvent.VK_R);
                    this.removeSelectedJobInputButton.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            MainFrame.this.mainFrameController.removeSelectedJobInputs();
                        }

                    });
                    this.removeSelectedJobInputButton.setToolTipText(GuiMessage.get("MainFrame.removeSelectedJobInputButton.toolTipText")); 
                    this.removeSelectedJobInputButton.setText(GuiMessage.get("MainFrame.removeSelectedJobInputButton.text")); 
                    this.designPanel.add(this.removeSelectedJobInputButton);
                    this.designPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.removeSelectedJobInputButton, -10, SpringLayout.SOUTH, this.designPanel);
                    this.designPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.removeSelectedJobInputButton, -45, SpringLayout.SOUTH, this.designPanel);
                    this.designPanelSpringLayout.putConstraint(SpringLayout.EAST, this.removeSelectedJobInputButton, -10, SpringLayout.EAST, this.designPanel);
                    this.designPanelSpringLayout.putConstraint(SpringLayout.WEST, this.removeSelectedJobInputButton, -90, SpringLayout.EAST, this.designPanel);
                }

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="viewSelectedJobInputButton">
                {
                    this.viewSelectedJobInputButton = new JButton();
                    this.viewSelectedJobInputButton.setMnemonic(KeyEvent.VK_V);
                    this.viewSelectedJobInputButton.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            MainFrame.this.mainFrameController.viewSelectedJobInput();
                        }

                    });
                    this.viewSelectedJobInputButton.setToolTipText(GuiMessage.get("MainFrame.viewSelectedJobInputButton.toolTipText")); 
                    this.viewSelectedJobInputButton.setText(GuiMessage.get("MainFrame.viewSelectedJobInputButton.text")); 
                    this.designPanel.add(this.viewSelectedJobInputButton);
                    this.designPanelSpringLayout.putConstraint(SpringLayout.EAST, this.viewSelectedJobInputButton, 90, SpringLayout.WEST, this.designPanel);
                    this.designPanelSpringLayout.putConstraint(SpringLayout.WEST, this.viewSelectedJobInputButton, 10, SpringLayout.WEST, this.designPanel);
                    this.designPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.viewSelectedJobInputButton, -10, SpringLayout.SOUTH, this.designPanel);
                    this.designPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.viewSelectedJobInputButton, -45, SpringLayout.SOUTH, this.designPanel);
                }

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="browseJobInputFolderButton">
                {
                    this.browseJobInputFolderButton = new JButton();
                    this.browseJobInputFolderButton.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            MainFrame.this.mainFrameController.browseJobInputFolder();
                        }

                    });
                    this.browseJobInputFolderButton.setToolTipText(GuiMessage.get("MainFrame.browseJobInputFolderButton.toolTipText")); 
                    this.browseJobInputFolderButton.setText(GuiMessage.get("MainFrame.browseJobInputFolderButton.text")); 
                    this.designPanel.add(browseJobInputFolderButton);
                    this.designPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.browseJobInputFolderButton, -10, SpringLayout.SOUTH, this.designPanel);
                    this.designPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.browseJobInputFolderButton, -45, SpringLayout.SOUTH, this.designPanel);
                    this.designPanelSpringLayout.putConstraint(SpringLayout.EAST, this.browseJobInputFolderButton, 630, SpringLayout.WEST, this.designPanel);
                    this.designPanelSpringLayout.putConstraint(SpringLayout.WEST, this.browseJobInputFolderButton, 550, SpringLayout.WEST, this.designPanel);
                }

                // </editor-fold>
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="executionPanel">
            {
                this.executionPanel = new JPanel();
                this.executionPanel.setToolTipText(GuiMessage.get("MainFrame.executionPanel.toolTipText")); 
                this.executionPanelSpringLayout = new SpringLayout();
                this.executionPanel.setLayout(this.executionPanelSpringLayout);
                this.mainTabbedPanel.addTab(GuiMessage.get("MainFrame.mainTabbedPanel.Tab.Execution"), null, this.executionPanel, GuiMessage.get("MainFrame.mainTabbedPanel.Tab.Execution.ToolTip")); 

                // <editor-fold defaultstate="collapsed" desc="jobExecutionQueuePanel">
                {
                    this.jobExecutionQueuePanel = new JPanel();
                    this.jobExecutionQueuePanel.setToolTipText(GuiMessage.get("MainFrame.jobExecutionQueuePanel.toolTipText")); 
                    this.jobExecutionQueuePanelSpringLayout = new SpringLayout();
                    this.jobExecutionQueuePanel.setLayout(this.jobExecutionQueuePanelSpringLayout);
                    final TitledBorder jobExecutionQueuePanelTitledBorder = new TitledBorder(new BevelBorder(BevelBorder.RAISED), GuiMessage.get("MainFrame.jobExecutionQueuePanelTitledBorder.title"), TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, null, GuiDefinitions.PANEL_TITLE_COLOR); 
                    this.jobExecutionQueuePanel.setBorder(jobExecutionQueuePanelTitledBorder); 
                    this.executionPanel.add(this.jobExecutionQueuePanel);
                    this.executionPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.jobExecutionQueuePanel, -50, SpringLayout.SOUTH, this.executionPanel);
                    executionPanelSpringLayout.putConstraint(SpringLayout.NORTH, jobExecutionQueuePanel, 10, SpringLayout.NORTH, executionPanel);
                    this.executionPanelSpringLayout.putConstraint(SpringLayout.EAST, this.jobExecutionQueuePanel, -10, SpringLayout.EAST, this.executionPanel);
                    this.executionPanelSpringLayout.putConstraint(SpringLayout.WEST, this.jobExecutionQueuePanel, 10, SpringLayout.WEST, this.executionPanel);
                    // <editor-fold defaultstate="collapsed" desc="jobExecutionSelectPanel">
                    {
                        this.jobExecutionSelectPanel = new JPanel();
                        this.jobExecutionSelectPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
                        this.jobExecutionSelectPanelSpringLayout = new SpringLayout();
                        this.jobExecutionSelectPanel.setLayout(jobExecutionSelectPanelSpringLayout);
                        this.jobExecutionQueuePanel.add(jobExecutionSelectPanel);
                        this.jobExecutionQueuePanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.jobExecutionSelectPanel, -190, SpringLayout.SOUTH, this.jobExecutionQueuePanel);
                        this.jobExecutionQueuePanelSpringLayout.putConstraint(SpringLayout.NORTH, this.jobExecutionSelectPanel, 10, SpringLayout.NORTH, this.jobExecutionQueuePanel);
                        this.jobExecutionQueuePanelSpringLayout.putConstraint(SpringLayout.EAST, this.jobExecutionSelectPanel, -10, SpringLayout.EAST, this.jobExecutionQueuePanel);
                        this.jobExecutionQueuePanelSpringLayout.putConstraint(SpringLayout.WEST, this.jobExecutionSelectPanel, 10, SpringLayout.WEST, this.jobExecutionQueuePanel);

                        // <editor-fold defaultstate="collapsed" desc="selectTabbedPanel">
                        {
                            this.selectTabbedPanel = new JTabbedPane();
                            this.jobExecutionSelectPanel.add(this.selectTabbedPanel);
                            this.jobExecutionSelectPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.selectTabbedPanel, -5, SpringLayout.SOUTH, this.jobExecutionSelectPanel);
                            this.jobExecutionSelectPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.selectTabbedPanel, 5, SpringLayout.NORTH, this.jobExecutionSelectPanel);
                            this.jobExecutionSelectPanelSpringLayout.putConstraint(SpringLayout.EAST, this.selectTabbedPanel, -5, SpringLayout.EAST, this.jobExecutionSelectPanel);
                            this.jobExecutionSelectPanelSpringLayout.putConstraint(SpringLayout.WEST, this.selectTabbedPanel, 5, SpringLayout.WEST, this.jobExecutionSelectPanel);

                            // <editor-fold defaultstate="collapsed" desc="selectExecutionJobInputPanel">
                            {
                                this.selectExecutionJobInputPanel = new JPanel();
                                this.selectExecutionJobInputPanel.setToolTipText(GuiMessage.get("MainFrame.selectExecutionJobInputPanel.toolTipText")); 
                                this.selectExecutionJobInputPanelSpringLayout = new SpringLayout();
                                this.selectExecutionJobInputPanel.setLayout(this.selectExecutionJobInputPanelSpringLayout);
                                this.selectTabbedPanel.addTab(GuiMessage.get("MainFrame.mainTabbedPanel.Tab.Execution.SelectTabbedPanel.Tab.SelectJobInputs"), null,
                                        this.selectExecutionJobInputPanel, GuiMessage.get("MainFrame.mainTabbedPanel.Tab.Execution.SelectTabbedPanel.Tab.SelectJobInputs.ToolTip")); 

                                // <editor-fold defaultstate="collapsed" desc="selectExecutionJobInputSelectionPanel">
                                {
                                    this.selectExecutionJobInputSelectionPanel = new CustomPanelSelection();
                                    this.selectExecutionJobInputSelectionPanel.getFilterPanel().getClearButton().setToolTipText(GuiMessage.get("MainFrame.jobInputsFilter.clear.toolTipText")); 
                                    this.selectExecutionJobInputSelectionPanel.getFilterPanel().getEditButton().setToolTipText(GuiMessage.get("MainFrame.jobInputsFilter.edit.toolTipText")); 
                                    this.selectExecutionJobInputSelectionPanel.getFilterPanel().getClearButton().addActionListener(new ActionListener() {

                                        public void actionPerformed(ActionEvent e) {
                                            MainFrame.this.mainFrameController.clearJobInputFilter();
                                        }

                                    });
                                    this.selectExecutionJobInputSelectionPanel.getFilterPanel().getEditButton().addActionListener(new ActionListener() {

                                        public void actionPerformed(ActionEvent e) {
                                            MainFrame.this.mainFrameController.editJobInputFilter();
                                        }

                                    });
                                    this.selectExecutionJobInputSelectionPanel.getList().addMouseListener(new MouseAdapter() {

                                        public void mouseClicked(MouseEvent e) {
                                            if (e.getClickCount() == 2) {
                                                // Double click: Add selected job input to job execution queue
                                                MainFrame.this.mainFrameController.addSelectedJobInputsToJobExecutionQueue();
                                            }
                                        }

                                    });
                                    this.selectExecutionJobInputPanel.add(this.selectExecutionJobInputSelectionPanel);
                                    this.selectExecutionJobInputPanelSpringLayout.putConstraint(SpringLayout.EAST, this.selectExecutionJobInputSelectionPanel, 0, SpringLayout.EAST,
                                            this.selectExecutionJobInputPanel);
                                    this.selectExecutionJobInputPanelSpringLayout.putConstraint(SpringLayout.WEST, this.selectExecutionJobInputSelectionPanel, 0, SpringLayout.WEST,
                                            this.selectExecutionJobInputPanel);
                                    this.selectExecutionJobInputPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.selectExecutionJobInputSelectionPanel, -40, SpringLayout.SOUTH,
                                            this.selectExecutionJobInputPanel);
                                    this.selectExecutionJobInputPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.selectExecutionJobInputSelectionPanel, 10, SpringLayout.NORTH,
                                            this.selectExecutionJobInputPanel);
                                }

                                // </editor-fold>
                                // <editor-fold defaultstate="collapsed" desc="addExecutionJobInputButton">
                                {
                                    this.addExecutionJobInputButton = new JButton();
                                    this.addExecutionJobInputButton.addActionListener(new ActionListener() {

                                        public void actionPerformed(ActionEvent e) {
                                            MainFrame.this.mainFrameController.addSelectedJobInputsToJobExecutionQueue();
                                        }

                                    });
                                    this.addExecutionJobInputButton.setToolTipText(GuiMessage.get("MainFrame.addExecutionJobInputButton.toolTipText")); 
                                    this.addExecutionJobInputButton.setText(GuiMessage.get("MainFrame.addExecutionJobInputButton.text")); 
                                    this.selectExecutionJobInputPanel.add(this.addExecutionJobInputButton);
                                    this.selectExecutionJobInputPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.addExecutionJobInputButton, 0, SpringLayout.SOUTH,
                                            this.selectExecutionJobInputPanel);
                                    this.selectExecutionJobInputPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.addExecutionJobInputButton, -35, SpringLayout.SOUTH,
                                            this.selectExecutionJobInputPanel);
                                    this.selectExecutionJobInputPanelSpringLayout.putConstraint(SpringLayout.EAST, this.addExecutionJobInputButton, 80, SpringLayout.WEST,
                                            this.selectExecutionJobInputPanel);
                                    this.selectExecutionJobInputPanelSpringLayout.putConstraint(SpringLayout.WEST, this.addExecutionJobInputButton, 0, SpringLayout.WEST,
                                            this.selectExecutionJobInputPanel);
                                }

                                // </editor-fold>
                            }

                            // </editor-fold>
                            // <editor-fold defaultstate="collapsed" desc="selectJobForRestartPanel">
                            {
                                this.selectJobForRestartPanel = new JPanel();
                                this.selectJobForRestartPanel.setToolTipText(GuiMessage.get("MainFrame.selectJobForRestartPanel.toolTipText")); 
                                this.selectJobForRestartPanelSpringLayout = new SpringLayout();
                                this.selectJobForRestartPanel.setLayout(this.selectJobForRestartPanelSpringLayout);
                                this.selectTabbedPanel.addTab(GuiMessage.get("MainFrame.mainTabbedPanel.Tab.Execution.SelectTabbedPanel.Tab.SelectJobForRestart"), null,
                                        this.selectJobForRestartPanel, GuiMessage.get("MainFrame.mainTabbedPanel.Tab.Execution.SelectTabbedPanel.Tab.SelectJobForRestart.ToolTip")); 

                                // <editor-fold defaultstate="collapsed" desc="selectJobForRestartSelectionPanel">
                                {
                                    this.selectJobForRestartSelectionPanel = new CustomPanelSelection();
                                    this.selectJobForRestartSelectionPanel.getFilterPanel().getClearButton().setToolTipText(GuiMessage.get("MainFrame.jobResultFilter.clear.toolTipText"));  
                                    this.selectJobForRestartSelectionPanel.getFilterPanel().getEditButton().setToolTipText(GuiMessage.get("MainFrame.jobResultFilter.edit.toolTipText"));  
                                    this.selectJobForRestartSelectionPanel.getFilterPanel().getClearButton().addActionListener(new ActionListener() {

                                        public void actionPerformed(ActionEvent e) {
                                            MainFrame.this.mainFrameController.clearJobResultFilter();
                                        }

                                    });
                                    this.selectJobForRestartSelectionPanel.getFilterPanel().getEditButton().addActionListener(new ActionListener() {

                                        public void actionPerformed(ActionEvent e) {
                                            MainFrame.this.mainFrameController.editJobResultFilter();
                                        }

                                    });
                                    this.selectJobForRestartSelectionPanel.getList().addMouseListener(new MouseAdapter() {

                                        public void mouseClicked(MouseEvent e) {
                                            if (e.getClickCount() == 2) {
                                                // Double click: Add selected restart job to job execution queue
                                                MainFrame.this.mainFrameController.addSelectedRestartJobsToJobExecutionQueue();
                                            }
                                        }

                                    });
                                    this.selectJobForRestartPanel.add(this.selectJobForRestartSelectionPanel);
                                    this.selectJobForRestartPanelSpringLayout.putConstraint(SpringLayout.EAST, this.selectJobForRestartSelectionPanel, 0, SpringLayout.EAST,
                                            this.selectJobForRestartPanel);
                                    this.selectJobForRestartPanelSpringLayout.putConstraint(SpringLayout.WEST, this.selectJobForRestartSelectionPanel, 0, SpringLayout.WEST,
                                            this.selectJobForRestartPanel);
                                    this.selectJobForRestartPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.selectJobForRestartSelectionPanel, -40, SpringLayout.SOUTH,
                                            this.selectJobForRestartPanel);
                                    this.selectJobForRestartPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.selectJobForRestartSelectionPanel, 10, SpringLayout.NORTH,
                                            this.selectJobForRestartPanel);
                                }

                                // </editor-fold>
                                // <editor-fold defaultstate="collapsed" desc="stepsForRestartInfoLabel">
                                {
                                    this.stepsForRestartInfoLabel = new JLabel();
                                    this.stepsForRestartInfoLabel.setForeground(GuiDefinitions.PANEL_TITLE_COLOR);
                                    this.stepsForRestartInfoLabel.setText(GuiMessage.get("MainFrame.stepsForRestartInfoLabel.text")); 
                                    this.selectJobForRestartPanel.add(this.stepsForRestartInfoLabel);
                                    this.selectJobForRestartPanelSpringLayout.putConstraint(SpringLayout.EAST, this.stepsForRestartInfoLabel, -90, SpringLayout.EAST, this.selectJobForRestartPanel);
                                    this.selectJobForRestartPanelSpringLayout.putConstraint(SpringLayout.WEST, this.stepsForRestartInfoLabel, 170, SpringLayout.WEST, this.selectJobForRestartPanel);
                                    this.selectJobForRestartPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.stepsForRestartInfoLabel, 0, SpringLayout.SOUTH, this.selectJobForRestartPanel);
                                    this.selectJobForRestartPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.stepsForRestartInfoLabel, -35, SpringLayout.SOUTH, this.selectJobForRestartPanel);
                                }

                                // </editor-fold>
                                // <editor-fold defaultstate="collapsed" desc="addExecutionJobForRestartButton">
                                {
                                    this.addExecutionJobForRestartButton = new JButton();
                                    this.addExecutionJobForRestartButton.addActionListener(new ActionListener() {

                                        public void actionPerformed(ActionEvent e) {
                                            MainFrame.this.mainFrameController.addSelectedRestartJobsToJobExecutionQueue();
                                        }

                                    });
                                    this.addExecutionJobForRestartButton.setToolTipText(GuiMessage.get("MainFrame.addExecutionJobForRestartButton.toolTipText")); 
                                    this.addExecutionJobForRestartButton.setText(GuiMessage.get("MainFrame.addExecutionJobForRestartButton.text")); 
                                    this.selectJobForRestartPanel.add(this.addExecutionJobForRestartButton);
                                    this.selectJobForRestartPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.addExecutionJobForRestartButton, 0, SpringLayout.SOUTH,
                                            this.selectJobForRestartPanel);
                                    this.selectJobForRestartPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.addExecutionJobForRestartButton, -35, SpringLayout.SOUTH,
                                            this.selectJobForRestartPanel);
                                    this.selectJobForRestartPanelSpringLayout.putConstraint(SpringLayout.EAST, this.addExecutionJobForRestartButton, 80, SpringLayout.WEST,
                                            this.selectJobForRestartPanel);
                                    this.selectJobForRestartPanelSpringLayout.putConstraint(SpringLayout.WEST, this.addExecutionJobForRestartButton, 0, SpringLayout.WEST,
                                            this.selectJobForRestartPanel);
                                }

                                // </editor-fold>
                                // <editor-fold defaultstate="collapsed" desc="useExecutionJobForRestartButton">
                                {
                                    this.useExecutionJobForRestartButton = new JButton();
                                    this.useExecutionJobForRestartButton.addActionListener(new ActionListener() {

                                        public void actionPerformed(ActionEvent e) {
                                            MainFrame.this.mainFrameController.editSelectedRestartJobAndAddToJobExecutionQueue();
                                        }

                                    });
                                    this.useExecutionJobForRestartButton.setToolTipText(GuiMessage.get("MainFrame.useExecutionJobForRestartButton.toolTipText")); 
                                    this.useExecutionJobForRestartButton.setText(GuiMessage.get("MainFrame.useExecutionJobForRestartButton.text")); 
                                    this.selectJobForRestartPanel.add(this.useExecutionJobForRestartButton);
                                    this.selectJobForRestartPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.useExecutionJobForRestartButton, 0, SpringLayout.SOUTH,
                                            this.selectJobForRestartPanel);
                                    this.selectJobForRestartPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.useExecutionJobForRestartButton, -35, SpringLayout.SOUTH,
                                            this.selectJobForRestartPanel);
                                    this.selectJobForRestartPanelSpringLayout.putConstraint(SpringLayout.EAST, this.useExecutionJobForRestartButton, 160, SpringLayout.WEST,
                                            this.selectJobForRestartPanel);
                                    this.selectJobForRestartPanelSpringLayout.putConstraint(SpringLayout.WEST, this.useExecutionJobForRestartButton, 80, SpringLayout.WEST,
                                            this.selectJobForRestartPanel);
                                }

                                // </editor-fold>
                                // <editor-fold defaultstate="collapsed" desc="editStepNumberButton">
                                {
                                    this.editStepNumberButton = new JButton();
                                    this.editStepNumberButton.setToolTipText(GuiMessage.get("MainFrame.editStepNumberButton.toolTipText")); 
                                    this.editStepNumberButton.addActionListener(new ActionListener() {

                                        public void actionPerformed(ActionEvent e) {
                                            MainFrame.this.mainFrameController.editNumberOfAdditionalStepsForJobRestart();
                                        }

                                    });
                                    this.editStepNumberButton.setText(GuiMessage.get("MainFrame.editStepNumberButton.text")); 
                                    this.selectJobForRestartPanel.add(this.editStepNumberButton);
                                    this.selectJobForRestartPanelSpringLayout.putConstraint(SpringLayout.EAST, this.editStepNumberButton, 0, SpringLayout.EAST, this.selectJobForRestartPanel);
                                    this.selectJobForRestartPanelSpringLayout.putConstraint(SpringLayout.WEST, this.editStepNumberButton, -80, SpringLayout.EAST, this.selectJobForRestartPanel);
                                    this.selectJobForRestartPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.editStepNumberButton, 0, SpringLayout.SOUTH, this.selectJobForRestartPanel);
                                    this.selectJobForRestartPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.editStepNumberButton, -35, SpringLayout.SOUTH, this.selectJobForRestartPanel);
                                }

                                // </editor-fold>
                            }

                            // </editor-fold>
                        }

                        // </editor-fold>
                    }

                    // </editor-fold>
                    // <editor-fold defaultstate="collapsed" desc="jobListScrollPanel and jobList">
                    {
                        this.jobExecutionListScrollPanel = new JScrollPane();
                        this.jobExecutionListScrollPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
                        this.jobExecutionQueuePanel.add(this.jobExecutionListScrollPanel);
                        jobExecutionQueuePanelSpringLayout.putConstraint(SpringLayout.SOUTH, jobExecutionListScrollPanel, -10, SpringLayout.SOUTH, jobExecutionQueuePanel);
                        this.jobExecutionQueuePanelSpringLayout.putConstraint(SpringLayout.NORTH, this.jobExecutionListScrollPanel, -180, SpringLayout.SOUTH, this.jobExecutionQueuePanel);
                        this.jobExecutionQueuePanelSpringLayout.putConstraint(SpringLayout.EAST, this.jobExecutionListScrollPanel, -10, SpringLayout.EAST, this.jobExecutionQueuePanel);
                        this.jobExecutionQueuePanelSpringLayout.putConstraint(SpringLayout.WEST, this.jobExecutionListScrollPanel, 10, SpringLayout.WEST, this.jobExecutionQueuePanel);
                        {
                            this.jobExecutionList = new JList();
                            this.jobExecutionList.setToolTipText(GuiMessage.get("MainFrame.jobExecutionList.toolTipText")); 
                            this.jobExecutionList.addListSelectionListener(new ListSelectionListener() {

                                public void valueChanged(final ListSelectionEvent e) {
                                    // NOTE: When "if (e.getValueIsAdjusting()) {...}" is used a selection change with keys does not work. Disadvantage:
                                    // With a mouse click this change event is called twice.
                                    MainFrame.this.mainFrameController.updateJobExecutionQueueDisplay();
                                }

                            });
                            this.jobExecutionList.addMouseListener(new MouseAdapter() {

                                public void mouseClicked(MouseEvent e) {
                                    if (e.getClickCount() == 2) {
                                        // Double click: Show progress
                                        MainFrame.this.mainFrameController.showProgress();
                                    }
                                }

                            });
                            this.jobExecutionList.setVisibleRowCount(-1);
                            this.jobExecutionListScrollPanel.setViewportView(this.jobExecutionList);
                            this.jobExecutionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
                        }
                    }

                    // </editor-fold>
                }

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="removeJobFromJobExecutionQueueButton">
                {
                    this.removeJobfromJobExecutionQueueButton = new JButton();
                    this.removeJobfromJobExecutionQueueButton.setMnemonic(KeyEvent.VK_R);
                    this.removeJobfromJobExecutionQueueButton.setToolTipText(GuiMessage.get("MainFrame.removeJobfromJobExecutionQueueButton.toolTipText")); 
                    this.removeJobfromJobExecutionQueueButton.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            MainFrame.this.mainFrameController.removeSelectedJobInJobExecutionQueue();
                        }

                    });
                    this.removeJobfromJobExecutionQueueButton.setText(GuiMessage.get("MainFrame.removeJobFromJobExecutionQueueButton.text")); 
                    this.executionPanel.add(this.removeJobfromJobExecutionQueueButton);
                    this.executionPanelSpringLayout.putConstraint(SpringLayout.EAST, this.removeJobfromJobExecutionQueueButton, 270, SpringLayout.WEST, this.executionPanel);
                    this.executionPanelSpringLayout.putConstraint(SpringLayout.WEST, this.removeJobfromJobExecutionQueueButton, 190, SpringLayout.WEST, this.executionPanel);
                    this.executionPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.removeJobfromJobExecutionQueueButton, -10, SpringLayout.SOUTH, this.executionPanel);
                    this.executionPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.removeJobfromJobExecutionQueueButton, -45, SpringLayout.SOUTH, this.executionPanel);
                }

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="cleanJobExecutionQueueButton">
                {
                    this.cleanJobExecutionQueueButton = new JButton();
                    this.cleanJobExecutionQueueButton.setMnemonic(KeyEvent.VK_C);
                    this.cleanJobExecutionQueueButton.addActionListener(new ActionListener() {

                        public void actionPerformed(final ActionEvent e) {
                            MainFrame.this.mainFrameController.cleanJobExecutionQueue();
                        }

                    });
                    this.cleanJobExecutionQueueButton.setToolTipText(GuiMessage.get("MainFrame.cleanJobExecutionQueueButton.toolTipText")); 
                    this.cleanJobExecutionQueueButton.setText(GuiMessage.get("MainFrame.cleanJobExecutionQueueButton.text")); 
                    this.executionPanel.add(this.cleanJobExecutionQueueButton);
                    this.executionPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.cleanJobExecutionQueueButton, -10, SpringLayout.SOUTH, this.executionPanel);
                    this.executionPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.cleanJobExecutionQueueButton, -45, SpringLayout.SOUTH, this.executionPanel);
                    this.executionPanelSpringLayout.putConstraint(SpringLayout.EAST, this.cleanJobExecutionQueueButton, 190, SpringLayout.WEST, this.executionPanel);
                    this.executionPanelSpringLayout.putConstraint(SpringLayout.WEST, this.cleanJobExecutionQueueButton, 110, SpringLayout.WEST, this.executionPanel);
                }

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="positionUpInJobExecutionQueueButton">
                {
                    this.positionUpInJobExecutionQueueButton = new JButton();
                    this.positionUpInJobExecutionQueueButton.setMnemonic(KeyEvent.VK_U);
                    this.positionUpInJobExecutionQueueButton.setToolTipText(GuiMessage.get("MainFrame.positionUpInJobExecutionQueueButton.toolTipText")); 
                    this.positionUpInJobExecutionQueueButton.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            MainFrame.this.mainFrameController.decrementPositionInJobList();
                        }

                    });
                    this.positionUpInJobExecutionQueueButton.setText(GuiMessage.get("MainFrame.positionUpInJobExecutionQueueButton.text")); 
                    this.executionPanel.add(this.positionUpInJobExecutionQueueButton);
                    this.executionPanelSpringLayout.putConstraint(SpringLayout.EAST, this.positionUpInJobExecutionQueueButton, 370, SpringLayout.WEST, this.executionPanel);
                    this.executionPanelSpringLayout.putConstraint(SpringLayout.WEST, this.positionUpInJobExecutionQueueButton, 290, SpringLayout.WEST, this.executionPanel);
                    this.executionPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.jobExecutionListScrollPanel, 0, SpringLayout.NORTH, this.positionUpInJobExecutionQueueButton);
                    this.executionPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.jobExecutionListScrollPanel, 10, SpringLayout.NORTH, this.executionPanel);
                    this.executionPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.positionUpInJobExecutionQueueButton, -10, SpringLayout.SOUTH, this.executionPanel);
                    this.executionPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.positionUpInJobExecutionQueueButton, -45, SpringLayout.SOUTH, this.executionPanel);
                }

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="positionDownInJobExecutionQueueButton">
                {
                    this.positionDownInJobExecutionQueueButton = new JButton();
                    this.positionDownInJobExecutionQueueButton.setMnemonic(KeyEvent.VK_D);
                    this.positionDownInJobExecutionQueueButton.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                        }

                    });
                    this.positionDownInJobExecutionQueueButton.setToolTipText(GuiMessage.get("MainFrame.positionDownInJobExecutionQueueButton.toolTipText")); 
                    this.positionDownInJobExecutionQueueButton.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            MainFrame.this.mainFrameController.incrementPositionInJobList();
                        }

                    });
                    this.positionDownInJobExecutionQueueButton.setText(GuiMessage.get("MainFrame.positionDownInJobExecutionQueueButton.text")); 
                    this.executionPanel.add(this.positionDownInJobExecutionQueueButton);
                    this.executionPanelSpringLayout.putConstraint(SpringLayout.EAST, this.positionDownInJobExecutionQueueButton, 450, SpringLayout.WEST, this.executionPanel);
                    this.executionPanelSpringLayout.putConstraint(SpringLayout.WEST, this.positionDownInJobExecutionQueueButton, 370, SpringLayout.WEST, this.executionPanel);
                    this.executionPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.positionDownInJobExecutionQueueButton, -10, SpringLayout.SOUTH, this.executionPanel);
                    this.executionPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.positionDownInJobExecutionQueueButton, -45, SpringLayout.SOUTH, this.executionPanel);
                }

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="startJobExecutionButton">
                {
                    this.startJobExecutionButton = new JButton();
                    this.startJobExecutionButton.setMnemonic(KeyEvent.VK_S);
                    this.startJobExecutionButton.addActionListener(new ActionListener() {

                        public void actionPerformed(final ActionEvent e) {
                            MainFrame.this.mainFrameController.startJobsInJobExecutionQueue();
                        }

                    });
                    this.startJobExecutionButton.setToolTipText(GuiMessage.get("MainFrame.startJobExecutionButton.toolTipText")); 
                    this.startJobExecutionButton.setText(GuiMessage.get("MainFrame.startJobExecutionButton.text")); 
                    this.executionPanel.add(this.startJobExecutionButton);
                    this.executionPanelSpringLayout.putConstraint(SpringLayout.EAST, this.startJobExecutionButton, 90, SpringLayout.WEST, this.executionPanel);
                    this.executionPanelSpringLayout.putConstraint(SpringLayout.WEST, this.startJobExecutionButton, 10, SpringLayout.WEST, this.executionPanel);
                    this.executionPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.startJobExecutionButton, -10, SpringLayout.SOUTH, this.executionPanel);
                    this.executionPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.startJobExecutionButton, -45, SpringLayout.SOUTH, this.executionPanel);
                }

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="stopSelectedJobButton">
                {
                    this.stopSelectedJobButton = new JButton();
                    this.stopSelectedJobButton.setMnemonic(KeyEvent.VK_O);
                    this.stopSelectedJobButton.addActionListener(new ActionListener() {

                        public void actionPerformed(final ActionEvent e) {
                            MainFrame.this.mainFrameController.stopSelectedJobInJobExecutionQueue();
                        }

                    });
                    this.stopSelectedJobButton.setToolTipText(GuiMessage.get("MainFrame.stopSelectedJobButton.toolTipText")); 
                    this.stopSelectedJobButton.setText(GuiMessage.get("MainFrame.stopSelectedJobButton.text")); 
                    this.executionPanel.add(this.stopSelectedJobButton);
                    this.executionPanelSpringLayout.putConstraint(SpringLayout.EAST, this.stopSelectedJobButton, -10, SpringLayout.EAST, this.executionPanel);
                    this.executionPanelSpringLayout.putConstraint(SpringLayout.WEST, this.stopSelectedJobButton, -90, SpringLayout.EAST, this.executionPanel);
                    this.executionPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.stopSelectedJobButton, -10, SpringLayout.SOUTH, this.executionPanel);
                    this.executionPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.stopSelectedJobButton, -45, SpringLayout.SOUTH, this.executionPanel);
                }

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="showProgressButton">
                {
                    this.showProgressButton = new JButton();
                    this.showProgressButton.setMnemonic(KeyEvent.VK_P);
                    this.showProgressButton.addActionListener(new ActionListener() {

                        public void actionPerformed(final ActionEvent e) {
                            MainFrame.this.mainFrameController.showProgress();
                        }

                    });
                    this.showProgressButton.setToolTipText(GuiMessage.get("MainFrame.showProgressButton.toolTipText")); 
                    this.showProgressButton.setText(GuiMessage.get("MainFrame.showProgressButton.text")); 
                    this.executionPanel.add(this.showProgressButton);
                    this.executionPanelSpringLayout.putConstraint(SpringLayout.EAST, this.showProgressButton, -90, SpringLayout.EAST, this.executionPanel);
                    this.executionPanelSpringLayout.putConstraint(SpringLayout.WEST, this.showProgressButton, -170, SpringLayout.EAST, this.executionPanel);
                    this.executionPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.showProgressButton, -10, SpringLayout.SOUTH, this.executionPanel);
                    this.executionPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.showProgressButton, -45, SpringLayout.SOUTH, this.executionPanel);
                }

                // </editor-fold>
            }

            // </editor-fold>
            // <editor-fold defaultstate="collapsed" desc="resultsPanel">
            {
                this.resultsPanel = new JPanel();
                this.resultsPanel.setToolTipText(GuiMessage.get("MainFrame.resultsPanel.toolTipText")); 
                this.resultsPanelSpringLayout = new SpringLayout();
                this.resultsPanel.setLayout(this.resultsPanelSpringLayout);
                this.mainTabbedPanel.addTab(GuiMessage.get("MainFrame.mainTabbedPanel.Tab.Results"), null, this.resultsPanel, GuiMessage.get("MainFrame.mainTabbedPanel.Tab.Results.ToolTip")); 
                // <editor-fold defaultstate="collapsed" desc="jobResultsPanel">
                {
                    this.jobResultsPanel = new JPanel();
                    this.jobResultsPanelSpringLayout = new SpringLayout();
                    this.jobResultsPanel.setLayout(this.jobResultsPanelSpringLayout);
                    this.jobResultsPanelTitledBorder = 
                        new TitledBorder(
                            new BevelBorder(BevelBorder.RAISED), 
                            GuiMessage.get("MainFrame.jobResultsPanelTitledBorder.title"),
                            TitledBorder.DEFAULT_JUSTIFICATION, 
                            TitledBorder.DEFAULT_POSITION, 
                            null, 
                            GuiDefinitions.PANEL_TITLE_COLOR
                        ); 
                    this.jobResultsPanel.setBorder(this.jobResultsPanelTitledBorder);
                    this.resultsPanel.add(this.jobResultsPanel);
                    this.resultsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.jobResultsPanel, -50, SpringLayout.SOUTH, this.resultsPanel);
                    this.resultsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.jobResultsPanel, 10, SpringLayout.NORTH, this.resultsPanel);
                    this.resultsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.jobResultsPanel, -10, SpringLayout.EAST, this.resultsPanel);
                    this.resultsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.jobResultsPanel, 10, SpringLayout.WEST, this.resultsPanel);
                    // <editor-fold defaultstate="collapsed" desc="jobResultsSelectionPanel">
                    {
                        this.jobResultsSelectionPanel = new CustomPanelSelection();
                        this.jobResultsSelectionPanel.getList().setToolTipText(GuiMessage.get("MainFrame.availableJobResultList.toolTipText")); 
                        this.jobResultsSelectionPanel.setToolTipText(GuiMessage.get("MainFrame.jobResultsSelectionPanel.toolTipText")); 
                        this.jobResultsSelectionPanel.getFilterPanel().getEditButton().setToolTipText(GuiMessage.get("MainFrame.jobResultsFilter.edit.toolTipText")); 
                        this.jobResultsSelectionPanel.getFilterPanel().getClearButton().setToolTipText(GuiMessage.get("MainFrame.jobResultsFilter.clear.toolTipText")); 
                        this.jobResultsSelectionPanel.getFilterPanel().getClearButton().addActionListener(new ActionListener() {

                            public void actionPerformed(ActionEvent e) {
                                MainFrame.this.mainFrameController.clearJobResultFilter();
                            }

                        });
                        this.jobResultsSelectionPanel.getFilterPanel().getEditButton().addActionListener(new ActionListener() {

                            public void actionPerformed(ActionEvent e) {
                                MainFrame.this.mainFrameController.editJobResultFilter();
                            }

                        });
                        this.jobResultsSelectionPanel.getList().setVisibleRowCount(-1);
                        this.jobResultsSelectionPanel.getList().addMouseListener(new MouseAdapter() {

                            public void mouseClicked(MouseEvent e) {
                                if (e.getClickCount() == 2) {
                                    // Double click: Edit existing job input
                                    MainFrame.this.mainFrameController.viewSelectedJobResult();
                                }
                            }

                        });
                        this.jobResultsPanel.add(this.jobResultsSelectionPanel);
                        this.jobResultsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.jobResultsSelectionPanel, -10, SpringLayout.EAST, this.jobResultsPanel);
                        this.jobResultsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.jobResultsSelectionPanel, 10, SpringLayout.WEST, this.jobResultsPanel);
                        this.jobResultsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.jobResultsSelectionPanel, -10, SpringLayout.SOUTH, this.jobResultsPanel);
                        this.jobResultsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.jobResultsSelectionPanel, 10, SpringLayout.NORTH, this.jobResultsPanel);
                    }
                    // </editor-fold>
                }

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="viewSelectedJobResultButton">
                {
                    this.viewSelectedJobResultButton = new JButton();
                    this.viewSelectedJobResultButton.setMnemonic(KeyEvent.VK_V);
                    this.viewSelectedJobResultButton.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            MainFrame.this.mainFrameController.viewSelectedJobResult();
                        }

                    });
                    this.viewSelectedJobResultButton.setToolTipText(GuiMessage.get("MainFrame.viewSelectedJobResultButton.toolTipText")); 
                    this.viewSelectedJobResultButton.setText(GuiMessage.get("MainFrame.viewSelectedJobResultButton.text")); 
                    this.resultsPanel.add(this.viewSelectedJobResultButton);
                    this.resultsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.viewSelectedJobResultButton, -10, SpringLayout.SOUTH, this.resultsPanel);
                    this.resultsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.viewSelectedJobResultButton, -45, SpringLayout.SOUTH, this.resultsPanel);
                    this.resultsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.viewSelectedJobResultButton, 90, SpringLayout.WEST, this.resultsPanel);
                    this.resultsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.viewSelectedJobResultButton, 10, SpringLayout.WEST, this.resultsPanel);
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="jobResultSettingsPreferencesEditButton">
                {
                    this.jobResultSettingsPreferencesEditButton = new JButton();
                    this.jobResultSettingsPreferencesEditButton.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            MainFrame.this.mainFrameController.editJobResultSettingsPreferences();
                        }

                    });
                    this.jobResultSettingsPreferencesEditButton.setToolTipText(GuiMessage.get("MainFrame.jobResultSettingsPreferencesEditButton.toolTipText")); 
                    this.jobResultSettingsPreferencesEditButton.setText(GuiMessage.get("MainFrame.jobResultSettingsPreferencesEditButton.text")); 
                    this.resultsPanel.add(this.jobResultSettingsPreferencesEditButton);
                    this.resultsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.jobResultSettingsPreferencesEditButton, -10, SpringLayout.SOUTH, this.resultsPanel);
                    this.resultsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.jobResultSettingsPreferencesEditButton, -45, SpringLayout.SOUTH, this.resultsPanel);
                    this.resultsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.jobResultSettingsPreferencesEditButton, 150, SpringLayout.WEST, this.resultsPanel);
                    this.resultsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.jobResultSettingsPreferencesEditButton, 90, SpringLayout.WEST, this.resultsPanel);
                }

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="importJobResultButton">
                {
                    this.importJobResultButton = new JButton();
                    this.importJobResultButton.setMnemonic(KeyEvent.VK_I);
                    this.importJobResultButton.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            MainFrame.this.mainFrameController.importJobResultsFromArchiveZipFiles();
                        }

                    });
                    this.importJobResultButton.setToolTipText(GuiMessage.get("MainFrame.importJobResultButton.toolTipText")); 
                    this.importJobResultButton.setText(GuiMessage.get("MainFrame.importJobResultButton.text")); 
                    this.resultsPanel.add(this.importJobResultButton);
                    this.resultsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.importJobResultButton, -10, SpringLayout.SOUTH, this.resultsPanel);
                    this.resultsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.importJobResultButton, -45, SpringLayout.SOUTH, this.resultsPanel);
                    this.resultsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.importJobResultButton, 250, SpringLayout.WEST, this.resultsPanel);
                    this.resultsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.importJobResultButton, 170, SpringLayout.WEST, this.resultsPanel);
                }

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="jobResultArchiveButton">
                {
                    this.jobResultArchiveButton = new JButton();
                    this.jobResultArchiveButton.setMnemonic(KeyEvent.VK_A);
                    this.jobResultArchiveButton.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            MainFrame.this.mainFrameController.archiveJobResult();
                        }

                    });
                    this.jobResultArchiveButton.setToolTipText(GuiMessage.get("MainFrame.jobResultArchiveButton.toolTipText")); 
                    this.jobResultArchiveButton.setText(GuiMessage.get("MainFrame.jobResultArchiveButton.text")); 
                    this.resultsPanel.add(this.jobResultArchiveButton);
                    this.resultsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.jobResultArchiveButton, -10, SpringLayout.SOUTH, this.resultsPanel);
                    this.resultsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.jobResultArchiveButton, -45, SpringLayout.SOUTH, this.resultsPanel);
                    this.resultsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.jobResultArchiveButton, 350, SpringLayout.WEST, this.resultsPanel);
                    this.resultsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.jobResultArchiveButton, 270, SpringLayout.WEST, this.resultsPanel);
                }

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="jobResultArchivePreferencesEditButton">
                {
                    this.jobResultArchivePreferencesEditButton = new JButton();
                    this.jobResultArchivePreferencesEditButton.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            MainFrame.this.mainFrameController.editJobResultArchivePreferences();
                        }

                    });
                    this.jobResultArchivePreferencesEditButton.setToolTipText(GuiMessage.get("MainFrame.jobResultArchivePreferencesEditButton.toolTipText")); 
                    this.jobResultArchivePreferencesEditButton.setText(GuiMessage.get("MainFrame.jobResultArchivePreferencesEditButton.text")); 
                    this.resultsPanel.add(this.jobResultArchivePreferencesEditButton);
                    this.resultsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.jobResultArchivePreferencesEditButton, -10, SpringLayout.SOUTH, this.resultsPanel);
                    this.resultsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.jobResultArchivePreferencesEditButton, -45, SpringLayout.SOUTH, this.resultsPanel);
                    this.resultsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.jobResultArchivePreferencesEditButton, 410, SpringLayout.WEST, this.resultsPanel);
                    this.resultsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.jobResultArchivePreferencesEditButton, 350, SpringLayout.WEST, this.resultsPanel);
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="copySelectedJobInputsButton">
                {
                    this.copySelectedJobInputsButton = new JButton();
                    this.copySelectedJobInputsButton.addActionListener(new ActionListener() {

                        public void actionPerformed(final ActionEvent e) {
                            MainFrame.this.mainFrameController.copySelectedJobInputs();
                        }

                    });
                    this.copySelectedJobInputsButton.setToolTipText(GuiMessage.get("MainFrame.copySelectedJobInputsButton.toolTipText")); 
                    this.copySelectedJobInputsButton.setText(GuiMessage.get("MainFrame.copySelectedJobInputsButton.text")); 
                    this.resultsPanel.add(this.copySelectedJobInputsButton);
                    this.resultsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.copySelectedJobInputsButton, -10, SpringLayout.SOUTH, this.resultsPanel);
                    this.resultsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.copySelectedJobInputsButton, -45, SpringLayout.SOUTH, this.resultsPanel);
                    this.resultsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.copySelectedJobInputsButton, 510, SpringLayout.WEST, this.resultsPanel);
                    this.resultsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.copySelectedJobInputsButton, 430, SpringLayout.WEST, this.resultsPanel);
                }

                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="browseJobResultFolderButton">
                {
                    this.browseJobResultFolderButton = new JButton();
                    this.browseJobResultFolderButton.addActionListener(new ActionListener() {

                        public void actionPerformed(final ActionEvent e) {
                            MainFrame.this.mainFrameController.browseJobResultFolder();
                        }

                    });
                    this.browseJobResultFolderButton.setToolTipText(GuiMessage.get("MainFrame.browseJobResultFolderButton.toolTipText")); 
                    this.browseJobResultFolderButton.setText(GuiMessage.get("MainFrame.browseJobResultFolderButton.text")); 
                    this.resultsPanel.add(this.browseJobResultFolderButton);
                    this.resultsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.browseJobResultFolderButton, -10, SpringLayout.SOUTH, this.resultsPanel);
                    this.resultsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.browseJobResultFolderButton, -45, SpringLayout.SOUTH, this.resultsPanel);
                    this.resultsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.browseJobResultFolderButton, 590, SpringLayout.WEST, this.resultsPanel);
                    this.resultsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.browseJobResultFolderButton, 510, SpringLayout.WEST, this.resultsPanel);
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="viewLogfileButton">
                {
                    this.viewLogfileButton = new JButton();
                    this.viewLogfileButton.addActionListener(new ActionListener() {

                        public void actionPerformed(final ActionEvent e) {
                            MainFrame.this.mainFrameController.viewLogfile();
                        }

                    });
                    this.viewLogfileButton.setToolTipText(GuiMessage.get("MainFrame.viewLogfileButton.toolTipText")); 
                    this.viewLogfileButton.setText(GuiMessage.get("MainFrame.viewLogfileButton.text")); 
                    this.resultsPanel.add(this.viewLogfileButton);
                    this.resultsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.viewLogfileButton, -10, SpringLayout.SOUTH, this.resultsPanel);
                    this.resultsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.viewLogfileButton, -45, SpringLayout.SOUTH, this.resultsPanel);
                    this.resultsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.viewLogfileButton, 670, SpringLayout.WEST, this.resultsPanel);
                    this.resultsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.viewLogfileButton, 590, SpringLayout.WEST, this.resultsPanel);
                }
                // </editor-fold>
                // <editor-fold defaultstate="collapsed" desc="removeSelectedJobResultButton">
                {
                    this.removeSelectedJobResultButton = new JButton();
                    this.removeSelectedJobResultButton.setMnemonic(KeyEvent.VK_R);
                    this.removeSelectedJobResultButton.addActionListener(new ActionListener() {

                        public void actionPerformed(ActionEvent e) {
                            MainFrame.this.mainFrameController.removeSelectedJobResults();
                        }

                    });
                    this.removeSelectedJobResultButton.setToolTipText(GuiMessage.get("MainFrame.removeSelectedJobResultButton.toolTipText")); 
                    this.removeSelectedJobResultButton.setText(GuiMessage.get("MainFrame.removeSelectedJobResultButton.text")); 
                    this.resultsPanel.add(this.removeSelectedJobResultButton);
                    this.resultsPanelSpringLayout.putConstraint(SpringLayout.EAST, this.removeSelectedJobResultButton, -10, SpringLayout.EAST, this.resultsPanel);
                    this.resultsPanelSpringLayout.putConstraint(SpringLayout.WEST, this.removeSelectedJobResultButton, -90, SpringLayout.EAST, this.resultsPanel);
                    this.resultsPanelSpringLayout.putConstraint(SpringLayout.SOUTH, this.removeSelectedJobResultButton, -10, SpringLayout.SOUTH, this.resultsPanel);
                    this.resultsPanelSpringLayout.putConstraint(SpringLayout.NORTH, this.removeSelectedJobResultButton, -45, SpringLayout.SOUTH, this.resultsPanel);
                }
                // </editor-fold>
            }

            // </editor-fold>
        }
        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="statusPanel">
        {
            this.statusPanel = new JPanel();
            this.statusPanel.setLayout(new BorderLayout());
            this.statusPanel.setMinimumSize(new Dimension(0, 35));
            this.statusPanel.setToolTipText(GuiMessage.get("MainFrame.statusPanel.toolTipText")); 
            this.statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
            this.statusPanel.setPreferredSize(new Dimension(0, 35));
            this.statusPanel.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        MainFrame.this.mainFrameController.editDirectories();
                    }
                }
            });
            getContentPane().add(this.statusPanel, BorderLayout.SOUTH);
            // <editor-fold defaultstate="collapsed" desc="statusInformationLabel">
            {
                this.statusInformationLabel = new JLabel();
                this.statusInformationLabel.setHorizontalAlignment(SwingConstants.CENTER);
                this.statusInformationLabel.setOpaque(true);
                this.statusInformationLabel.setText(GuiMessage.get("MainFrame.statusInformationLabel.text")); 
                this.statusPanel.add(this.statusInformationLabel, BorderLayout.CENTER);
            }
            // </editor-fold>
        }

        // </editor-fold>
        // <editor-fold defaultstate="collapsed" desc="Initialize (MUST be LAST command)">
        MainFrame.this.mainFrameController.initialize();
        // </editor-fold>
    }
    // </editor-fold>
    //
    // <editor-fold defaultstate="collapsed" desc="Public exposed fields">
    /**
     * GUI element
     * 
     * @return GUI element
     */
    public JList getJobExecutionList() {
        return this.jobExecutionList;
    }

    /**
     * GUI element
     * 
     * @return GUI element
     */
    public JTabbedPane getMainTabbedPanel() {
        return this.mainTabbedPanel;
    }

    /**
     * GUI element
     * 
     * @return GUI element
     */
    public JTabbedPane getSelectTabbedPanel() {
        return this.selectTabbedPanel;
    }

    /**
     * GUI element
     * 
     * @return GUI element
     */
    public JPanel getJobExecutionQueuePanel() {
        return this.jobExecutionQueuePanel;
    }

    /**
     * GUI element
     * 
     * @return GUI element
     */
    public JButton getPositionUpInJobExecutionQueueButton() {
        return this.positionUpInJobExecutionQueueButton;
    }

    /**
     * GUI element
     * 
     * @return GUI element
     */
    public JButton getPositionDownInJobExecutionQueueButton() {
        return this.positionDownInJobExecutionQueueButton;
    }

    /**
     * GUI element
     * 
     * @return GUI element
     */
    public JButton getRemoveJobfromJobExecutionQueueButton() {
        return this.removeJobfromJobExecutionQueueButton;
    }

    /**
     * GUI element
     * 
     * @return GUI element
     */
    public JLabel getStatusInformationLabel() {
        return this.statusInformationLabel;
    }

    /**
     * GUI element
     * 
     * @return GUI element
     */
    public JMenu getPreferencesMenu() {
        return this.preferencesMenu;
    }

    /**
     * GUI element
     * 
     * @return GUI element
     */
    public JMenu geMFsimJobMenu() {
        return this.mfSimJobMenu;
    }

    /**
     * GUI element
     * 
     * @return GUI element
     */
    public CustomPanelImage getHomeImagePanel() {
        return this.homeImagePanel;
    }

    /**
     * GUI element
     * 
     * @return GUI element
     */
    public CustomPanelSelection getSelectJobResultPanel() {
        return this.jobResultsSelectionPanel;
    }

    /**
     * GUI element
     * 
     * @return GUI element
     */
    public CustomPanelSelection getJobInputsSelectionPanel() {
        return this.jobInputsSelectionPanel;
    }

    /**
     * GUI element
     * 
     * @return GUI element
     */
    public JButton getEditExistingJobInputButton() {
        return this.editExistingJobInputButton;
    }

    /**
     * GUI element
     * 
     * @return GUI element
     */
    public JButton getEditTemplateJobInputButton() {
        return this.editTemplateJobInputButton;
    }

    /**
     * GUI element
     * 
     * @return GUI element
     */
    public JButton getRemoveSelectedJobInputButton() {
        return this.removeSelectedJobInputButton;
    }

    /**
     * GUI element
     * 
     * @return GUI element
     */
    public CustomPanelSelection getSelectExecutionJobInputSelectionPanel() {
        return this.selectExecutionJobInputSelectionPanel;
    }

    /**
     * GUI element
     * 
     * @return GUI element
     */
    public CustomPanelSelection getSelectJobForRestartSelectionPanel() {
        return this.selectJobForRestartSelectionPanel;
    }

    /**
     * GUI element
     * 
     * @return GUI element
     */
    public JButton getAddExecutionJobInputButton() {
        return this.addExecutionJobInputButton;
    }

    /**
     * GUI element
     * 
     * @return GUI element
     */
    public JButton getAddExecutionJobForRestartButton() {
        return this.addExecutionJobForRestartButton;
    }

    /**
     * GUI element
     * 
     * @return GUI element
     */
    public JButton getRemoveSelectedJobResultButton() {
        return this.removeSelectedJobResultButton;
    }

    /**
     * GUI element
     * 
     * @return GUI element
     */
    public JButton getImportJobInputButton() {
        return this.importJobInputButton;
    }

    /**
     * GUI element
     * 
     * @return GUI element
     */
    public JButton getViewSelectedJobInputButton() {
        return this.viewSelectedJobInputButton;
    }

    /**
     * GUI element
     * 
     * @return GUI element
     */
    public JButton getViewSelectedJobResultButton() {
        return this.viewSelectedJobResultButton;
    }

    /**
     * GUI element
     * 
     * @return GUI element
     */
    public JButton getStartJobExecutionButton() {
        return this.startJobExecutionButton;
    }

    /**
     * GUI element
     * 
     * @return GUI element
     */
    public JButton getStopSelectedJobButton() {
        return this.stopSelectedJobButton;
    }

    /**
     * GUI element
     * 
     * @return GUI element
     */
    public JButton getCleanJobExecutionQueueButton() {
        return this.cleanJobExecutionQueueButton;
    }

    /**
     * GUI element
     * 
     * @return GUI element
     */
    public JButton getEditNewJobInputButton() {
        return this.editNewJobInputButton;
    }

    /**
     * GUI element
     * 
     * @return GUI element
     */
    public JButton getEditStepNumberButton() {
        return this.editStepNumberButton;
    }

    /**
     * GUI element
     * 
     * @return GUI element
     */
    public JLabel getStepsForRestartInfoLabel() {
        return this.stepsForRestartInfoLabel;
    }

    /**
     * GUI element
     * 
     * @return GUI element
     */
    public JButton getShowProgressButton() {
        return showProgressButton;
    }

    /**
     * GUI element
     * 
     * @return GUI element
     */
    public JButton getJobInputArchiveButton() {
        return jobInputArchiveButton;
    }

    /**
     * GUI element
     * 
     * @return GUI element
     */
    public JButton getImportJobResultButton() {
        return importJobResultButton;
    }

    /**
     * GUI element
     * 
     * @return GUI element
     */
    public JButton getJobResultArchiveButton() {
        return jobResultArchiveButton;
    }

    /**
     * GUI element
     * 
     * @return GUI element
     */
    public JButton getJobResultArchivePreferencesEditButton() {
        return jobResultArchivePreferencesEditButton;
    }

    /**
     * GUI element
     * 
     * @return GUI element
     */
    public JButton getJobResultSettingsPreferencesEditButton() {
        return jobResultSettingsPreferencesEditButton;
    }

    /**
     * GUI element
     * 
     * @return GUI element
     */
    public JCheckBoxMenuItem getParticleUpdateForJobInputMenuItem() {
        return particleUpdateForJobInputMenuItem;
    }

    /**
     * GUI element
     * 
     * @return GUI element
     */
    public JButton getBrowseJobInputFolderButton() {
        return browseJobInputFolderButton;
    }

    /**
     * GUI element
     * 
     * @return GUI element
     */
    public JButton getBrowseJobResultFolderButton() {
        return browseJobResultFolderButton;
    }

    /**
     * GUI element
     * 
     * @return GUI element
     */
    public JButton getViewLogfileButton() {
        return viewLogfileButton;
    }

    /**
     * GUI element
     * 
     * @return GUI element
     */
    public TitledBorder getJobInputsPanelTitledBorder() {
        return this.jobInputsPanelTitledBorder;
    }

    /**
     * GUI element
     * 
     * @return GUI element
     */
    public JPanel getJobInputsPanel() {
        return this.jobInputsPanel;
    }

    /**
     * GUI element
     * 
     * @return GUI element
     */
    public JButton getCopySelectedJobInputsButton() {
        return copySelectedJobInputsButton;
    }
    // </editor-fold>

}
