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

package org.catacombae.jparted.lib.ps.gpt;

import org.catacombae.jparted.lib.DataLocator;
import org.catacombae.jparted.lib.ps.PartitionSystemRecognizer;
import org.catacombae.jparted.lib.ps.PartitionSystemHandler;
import org.catacombae.jparted.lib.ps.PartitionSystemHandlerFactory;
import org.catacombae.jparted.lib.ps.PartitionSystemImplementationInfo;

/**
 *
 * @author erik
 */
public class GPTHandlerFactory extends PartitionSystemHandlerFactory {

    private static final GPTRecognizer recognizer = new GPTRecognizer();

    @Override
    public PartitionSystemHandler createHandler(DataLocator data) {
        return new GPTHandler(data);
    }

    @Override
    public PartitionSystemImplementationInfo getInfo() {
        return new PartitionSystemImplementationInfo("GUID Partition Table",
                "Catacombae GPT PS Handler", "1.0", "Catacombae");
    }

    @Override
    public PartitionSystemRecognizer getRecognizer() {
        return recognizer;
    }
}
