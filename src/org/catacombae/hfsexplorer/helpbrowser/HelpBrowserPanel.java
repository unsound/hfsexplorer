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

package org.catacombae.hfsexplorer.helpbrowser;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * A simple implemetation of a HTML help browser through Java's standard API.
 *
 * @author <a href="http://hem.bredband.net/catacombae">Erik Larsson</a>
 */
public class HelpBrowserPanel extends javax.swing.JPanel {
    private static final String TEST_HOME =
            "http://hem.bredband.net/catacombae/hfsx.html";
    private final URL homePage;
    private URL currentPage = null;
    private LinkedList<URL> history = new LinkedList<URL>();
    
    /**
     * Creates new HelpBrowserPanel.
     * 
     * @param iHomePage the start page URL of this help browser.
     */
    public HelpBrowserPanel(URL iHomePage) {
        this.homePage = iHomePage;
        initComponents();
        htmlView.setEditable(false);
        htmlView.addHyperlinkListener(new HyperlinkListener() {
            @Override
            public void hyperlinkUpdate(HyperlinkEvent e) {
                if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    URL url = e.getURL();
                    if(url != null)
                        goToPage(url);
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goBack();
            }
        });
        homeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goHome();
            }
        });
        
        goHome();
    }

    protected void goHome() {
        goToPage(homePage);
    }
    
    protected void goBack() {
        try {
            URL previousPage = history.getLast();
            htmlView.setPage(previousPage);
            currentPage = previousPage;
            history.removeLast();
        } catch(NoSuchElementException ex) {
        } catch(IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING,
                    "Could not load page " + currentPage, ex);
        }
    }
    protected void goToPage(final URL iUrl) {
        try {
            //System.out.println("setCurrentPage(" + iUrl + ");");
            if(currentPage != null && iUrl.equals(currentPage)) {
                //System.out.println("  Refreshing page.");
                htmlView.setPage(currentPage);
            }
            else {
                //System.out.println("  Before:");
                //System.out.println("    currentPage == " + currentPage);
                //{
                //    int i = 0;
                //    for(URL curUrl : history) {
                //        System.out.println("    history[" + i++ + "]: " + curUrl);
                //    }
                //}
                if(currentPage != null)
                    history.addLast(currentPage);
                htmlView.setPage(iUrl);
                currentPage = iUrl;
                //System.out.println("  After:");
                //System.out.println("    currentPage == " + currentPage);
                //{
                //    int i = 0;
                //    for(URL curUrl : history) {
                //        System.out.println("    history[" + i++ + "]: " + curUrl);
                //    }
                //}
            }
        } catch(IOException ex) {
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING,
                    "Could not load page " + iUrl, ex);
        }
    }
    
    public static void showHelpBrowserWindow(String iTitle, URL iHomePage) {
        JFrame f = new JFrame(iTitle);
        f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JPanel helpBrowserPanel = new HelpBrowserPanel(iHomePage);
        //helpBrowserPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        f.add(helpBrowserPanel);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        backButton = new javax.swing.JButton();
        homeButton = new javax.swing.JButton();
        htmlViewScroller = new javax.swing.JScrollPane();
        htmlView = new javax.swing.JTextPane();

        backButton.setText("Back");

        homeButton.setText("Home");

        htmlViewScroller.setViewportView(htmlView);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(backButton)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(homeButton)
                .addContainerGap(486, Short.MAX_VALUE))
            .add(htmlViewScroller, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 616, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(backButton)
                    .add(homeButton))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(htmlViewScroller, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton backButton;
    private javax.swing.JButton homeButton;
    private javax.swing.JTextPane htmlView;
    private javax.swing.JScrollPane htmlViewScroller;
    // End of variables declaration//GEN-END:variables

    
    /*
    public static void main(String[] args) throws MalformedURLException {
        try {
	    javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());

            // Description of look&feels:
	    //   http://java.sun.com/docs/books/tutorial/uiswing/misc/plaf.html
	}
	catch(Exception e) {
	    //It's ok. Non-critical.
	}

        JFrame f = new JFrame("Help viewer");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel helpBrowserPanel = new HelpBrowserPanel(new URL(TEST_HOME));
        //helpBrowserPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        f.add(helpBrowserPanel);
        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }
    */
}
