/**
 * Copyright (c) 2011, University of Konstanz, Distributed Systems Group
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 * notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 * * Neither the name of the University of Konstanz nor the
 * names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.treetank.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.treetank.access.FileDatabase;
import org.treetank.access.SessionConfiguration;
import org.treetank.api.IDatabase;
import org.treetank.api.IReadTransaction;
import org.treetank.api.ISession;
import org.treetank.exception.AbsTTException;
import org.treetank.gui.view.smallmultiples.SmallMultiplesView;
import org.treetank.gui.view.sunburst.SunburstView;
import org.treetank.gui.view.text.TextView;
import org.treetank.gui.view.tree.TreeView;
import org.treetank.service.xml.serialize.XMLSerializer;
import org.treetank.service.xml.serialize.XMLSerializer.XMLSerializerBuilder;

/**
 * <h1>GUICommands</h1>
 * 
 * <p>
 * All available GUI commands.
 * </p>
 * 
 * @author Johannes Lichtenberger, University of Konstanz
 * 
 */
public enum GUICommands implements IGUICommand {

    /**
     * Open a Treetank file.
     */
    OPEN("Open TNK-File", EMenu.MENU) {
        /** {@inheritDoc} */
        @Override
        public void execute(final GUI paramGUI) {
            assert paramGUI != null;

            // Create file chooser.
            final MyActionListener mActionListener = new MyActionListener();
            final JFileChooser fc = createFileChooser(mActionListener);

            // Handle open button action.
            if (fc.showOpenDialog(paramGUI) == JFileChooser.APPROVE_OPTION) {
                final File file = fc.getSelectedFile();
                LOGWRAPPER.debug("RevNumber: " + mActionListener.getRevision());
                paramGUI.execute(file, mActionListener.getRevision());
            }
        }
    },

    /**
     * Shredder an XML-document.
     */
    SHREDDER("Shredder XML-document", EMenu.MENU) {
        /** {@inheritDoc} */
        @Override
        public void execute(final GUI paramGUI) {
            assert paramGUI != null;
            shredder(paramGUI, EShredder.NORMAL);
        }
    },

    /**
     * Update a shreddered file.
     */
    SHREDDER_UPDATE("Update shreddered file", EMenu.MENU) {
        /** {@inheritDoc} */
        @Override
        public void execute(final GUI paramGUI) {
            assert paramGUI != null;
            shredder(paramGUI, EShredder.UPDATEONLY);
        }
    },

    /**
     * Serialize a Treetank storage.
     */
    SERIALIZE("Serialize", EMenu.MENU) {
        /** {@inheritDoc} */
        @Override
        public void execute(final GUI paramGUI) {
            assert paramGUI != null;

            // Create file chooser.
            final MyActionListener mActionListener = new MyActionListener();
            final JFileChooser fc = createFileChooser(mActionListener);

            if (fc.showOpenDialog(paramGUI) == JFileChooser.APPROVE_OPTION) {
                final File source = fc.getSelectedFile();
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fc.setAcceptAllFileFilterUsed(true);

                final JFileChooser chooser = new JFileChooser();
                if (chooser.showSaveDialog(paramGUI) == JFileChooser.APPROVE_OPTION) {
                    final File target = chooser.getSelectedFile();
                    try {
                        final FileOutputStream outputStream = new FileOutputStream(target);

                        final IDatabase db = FileDatabase.openDatabase(source);
                        final ISession session = db.getSession(new SessionConfiguration.Builder().build());

                        final ExecutorService executor = Executors.newSingleThreadExecutor();
                        final XMLSerializer serializer =
                            new XMLSerializerBuilder(session, outputStream).setIndend(true).setVersions(
                                new long[] {
                                    mActionListener.getRevision()
                                }).build();
                        executor.submit(serializer);
                        executor.shutdown();
                        try {
                            executor.awaitTermination(5, TimeUnit.SECONDS);
                        } catch (final InterruptedException e) {
                            LOGWRAPPER.error(e.getMessage(), e);
                            return;
                        }

                        session.close();
                        outputStream.close();
                        JOptionPane.showMessageDialog(paramGUI, "Serializing done!");
                    } catch (final AbsTTException e) {
                        LOGWRAPPER.error(e.getMessage(), e);
                    } catch (final IOException e) {
                        LOGWRAPPER.error(e.getMessage(), e);
                    }
                }
            }
        }
    },

    /**
     * Separator.
     */
    SEPARATOR("", EMenu.SEPARATOR) {
        /** {@inheritDoc} */
        @Override
        public void execute(final GUI paramGUI) {

        }
    },

    /**
     * Close Treetank GUI.
     */
    QUIT("Quit", EMenu.MENU) {
        /** {@inheritDoc} */
        @Override
        public void execute(final GUI paramGUI) {
            assert paramGUI != null;
            paramGUI.dispose();
        }
    },

    /**
     * Show tree view.
     */
    TREE("Tree", EMenu.CHECKBOXITEM) {
        /** {@inheritDoc} */
        @Override
        public boolean selected() {
            return GUIProp.EShowViews.SHOWTREE.getValue();
        }

        /** {@inheritDoc} */
        @Override
        public void execute(final GUI paramGUI) {
            assert paramGUI != null;
            GUIProp.EShowViews.SHOWTREE.invert();
            if (!GUIProp.EShowViews.SHOWTREE.getValue()) {
                TreeView.getInstance(paramGUI.getNotifier()).dispose();
            }
            paramGUI.getViewContainer().layoutViews();
        }
    },

    /**
     * Show text view.
     */
    TEXT("Text", EMenu.CHECKBOXITEM) {
        /** {@inheritDoc} */
        @Override
        public boolean selected() {
            return GUIProp.EShowViews.SHOWTEXT.getValue();
        }

        /** {@inheritDoc} */
        @Override
        public void execute(final GUI paramGUI) {
            assert paramGUI != null;
            GUIProp.EShowViews.SHOWTEXT.invert();
            if (!GUIProp.EShowViews.SHOWTEXT.getValue()) {
                TextView.getInstance(paramGUI.getNotifier()).dispose();
            }
            paramGUI.getViewContainer().layoutViews();
        }
    },

    /**
     * Show small multiples view.
     */
    SMALLMULTIPLES("Small multiples", EMenu.CHECKBOXITEM) {
        /** {@inheritDoc} */
        @Override
        public boolean selected() {
            return GUIProp.EShowViews.SHOWSMALLMULTIPLES.getValue();
        }

        /** {@inheritDoc} */
        @Override
        public void execute(final GUI paramGUI) {
            assert paramGUI != null;
            GUIProp.EShowViews.SHOWSMALLMULTIPLES.invert();
            if (!GUIProp.EShowViews.SHOWSMALLMULTIPLES.getValue()) {
                SmallMultiplesView.getInstance(paramGUI.getNotifier()).dispose();
            }
            paramGUI.getViewContainer().layoutViews();
        }
    },

    /**
     * Show sunburst view.
     */
    SUNBURST("Sunburst", EMenu.CHECKBOXITEM) {
        /** {@inheritDoc} */
        @Override
        public boolean selected() {
            return GUIProp.EShowViews.SHOWSUNBURST.getValue();
        }

        /** {@inheritDoc} */
        @Override
        public void execute(final GUI paramGUI) {
            assert paramGUI != null;
            GUIProp.EShowViews.SHOWSUNBURST.invert();
            if (!GUIProp.EShowViews.SHOWSUNBURST.getValue()) {
                SunburstView.getInstance(paramGUI.getNotifier()).dispose();
            }
            paramGUI.getViewContainer().layoutViews();
        }
    };

    /** Logger. */
    private static final Logger LOGWRAPPER = LoggerFactory.getLogger(GUICommands.class);

    /** Description of command. */
    private final String mDesc;

    /** Determines menu entry type. */
    private final EMenu mType;

    /**
     * Constructor.
     * 
     * @param paramDesc
     *            Description of command
     * @param paramType
     *            Determines if menu item is checked or not
     */
    GUICommands(final String paramDesc, final EMenu paramType) {
        assert paramDesc != null;
        assert paramType != null;
        mDesc = paramDesc;
        mType = paramType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String desc() {
        return mDesc;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EMenu type() {
        return mType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean selected() {
        throw new IllegalStateException("May not be invoked on this command!");
    }

    /**
     * Create a directory file chooser with the possibility to select a specific revision.
     * 
     * @param paramActionListener
     *            {@link MyActionListener} instance
     * @return {@link JFileChooser} instance
     */
    private static JFileChooser createFileChooser(final MyActionListener paramActionListener) {
        // Action listener.
        final MyActionListener mActionListener = paramActionListener;

        // Create a file chooser.
        final JFileChooser fc = new JFileChooser();
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setAcceptAllFileFilterUsed(false);

        // Create new panel etc.pp. for choosing the revision at the bottom of the frame.
        final JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        final JComboBox cb = new JComboBox();
        cb.addActionListener(mActionListener);

        panel.add(cb, BorderLayout.SOUTH);
        fc.setAccessory(panel);

        final PropertyChangeListener changeListener = new PropertyChangeListener() {
            @Override
            public void propertyChange(final PropertyChangeEvent paramEvent) {
                assert paramEvent != null;
                assert paramEvent.getSource() instanceof JFileChooser;

                // Get last revision number from TT-storage.
                final JFileChooser fileChooser = (JFileChooser)paramEvent.getSource();
                final File tmpDir = fileChooser.getSelectedFile();
                long revNumber = 0;

                if (tmpDir != null) {
                    // Remove items first.
                    cb.removeActionListener(mActionListener);
                    cb.removeAllItems();

                    // A directory is in focus.
                    boolean error = false;

                    try {
                        final IDatabase db = FileDatabase.openDatabase(tmpDir);
                        final IReadTransaction rtx =
                            db.getSession(new SessionConfiguration.Builder().build()).beginReadTransaction();
                        revNumber = rtx.getRevisionNumber();
                        rtx.close();
                    } catch (final AbsTTException e) {
                        // Selected directory is not a Treetank storage.
                        error = true;
                    }

                    if (!error) {
                        // Create items, which are used as available revisions.
                        for (long i = 0; i <= revNumber; i++) {
                            cb.addItem(i);
                        }
                    }

                    cb.addActionListener(mActionListener);
                }
            }
        };
        fc.addPropertyChangeListener(changeListener);

        return fc;
    }

    /**
     * Shredder or shredder into.
     * 
     * @param paramGUI
     *            Main GUI frame
     * @param paramShredding
     *            Determines which shredder to use
     */
    private static void shredder(final GUI paramGUI, final EShredder paramShredding) {
        assert paramGUI != null;
        assert paramShredding != null;

        // Create a file chooser.
        final JFileChooser fc = new JFileChooser();
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileFilter(new XMLFileFilter());

        if (fc.showOpenDialog(paramGUI) == JFileChooser.APPROVE_OPTION) {
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            final File source = fc.getSelectedFile();

            if (fc.showSaveDialog(paramGUI) == JFileChooser.APPROVE_OPTION) {
                fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                final File target = fc.getSelectedFile();

                paramShredding.shred(source, target);
                
                try {
                    final IDatabase database = FileDatabase.openDatabase(target);
                    final ISession session = database.getSession(new SessionConfiguration.Builder().build());
                    final IReadTransaction rtx = session.beginReadTransaction();
                    final long rev = rtx.getRevisionNumber();
                    rtx.close();
                    session.close();
                    paramGUI.execute(target, rev);
                } catch (final AbsTTException e) {
                    LOGWRAPPER.error(e.getMessage(), e);
                }
            }
        }
    }

    /** Action listener to listen for the selection of a revision. */
    private static final class MyActionListener implements ActionListener {
        /** Selected revision. */
        private transient long mRevision;

        /** {@inheritDoc} */
        @Override
        public void actionPerformed(final ActionEvent paramEvent) {
            assert paramEvent != null;
            assert paramEvent.getSource() instanceof JComboBox;
            final JComboBox cb = (JComboBox)paramEvent.getSource();
            if (cb.getSelectedItem() != null) {
                mRevision = (Long)cb.getSelectedItem();
            }
        };

        /**
         * Get selected revision number.
         * 
         * @return the Revision
         */
        long getRevision() {
            return mRevision;
        }
    }
}
