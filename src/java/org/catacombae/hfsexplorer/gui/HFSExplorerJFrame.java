/*-
 * Copyright (C) 2015 Erik Larsson
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

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import org.catacombae.hfsexplorer.Java6Util;
import org.catacombae.hfsexplorer.Resources;

/**
 * Base class for all HFSExplorer windows, setting up common properties such as
 * the window icon.
 *
 * @author Erik Larsson
 */
public class HFSExplorerJFrame extends JFrame implements Resources {
    private static final ImageIcon[] WINDOW_ICONS = {
        new ImageIcon(APPLICATION_ICON_16),
        new ImageIcon(APPLICATION_ICON_32),
        new ImageIcon(APPLICATION_ICON_48),
    };

    public HFSExplorerJFrame(String title) {
        super(title);

        if(Java6Util.isJava6OrHigher()) {
            Java6Util.setIconImages(WINDOW_ICONS, this);
        }
        else {
            setIconImage(WINDOW_ICONS[0].getImage());
        }
    }
}
