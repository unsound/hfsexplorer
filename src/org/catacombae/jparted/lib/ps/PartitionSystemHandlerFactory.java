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

package org.catacombae.jparted.lib.ps;

import org.catacombae.jparted.lib.DataLocator;

/**
 * A subclass of PartitionSystemHandlerFactory must always have a empty
 * constructor in order to be used as such.
 * @author Erik Larsson
 */
public abstract class PartitionSystemHandlerFactory {
    public abstract PartitionSystemRecognizer getRecognizer();
    public abstract PartitionSystemHandler createHandler(DataLocator partitionData);
    //public abstract PartitionSystemRecognizer createDetector(DataLocator partitionData);
    public abstract PartitionSystemImplementationInfo getInfo();
}
