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

package org.catacombae.storage.ps;

import java.util.LinkedList;
import org.catacombae.io.ReadableRandomAccessStream;
import org.catacombae.io.RuntimeIOException;
import org.catacombae.storage.io.DataLocator;

/**
 * A detector for known partition systems (see PartitionSystemType for a list
 * of known partition systems).
 *
 * @author Erik Larsson
 */
public class PartitionSystemDetector {

    /**
     * Runs a partition system detection test on <code>psStream</code> to
     * determine what partition system is present. As the detection engines are
     * user defined and may return false positives, a list of all positive
     * detection results is returned. It is up to the caller to sort out any
     * false positives.
     *
     * @param inputDataLocator a DataLocator pointing to the data to probe.
     * @return a list of matching partition systems. If no matches were found,
     * this list will be empty (0 elements).
     */
    public static PartitionSystemType[] detectPartitionSystem(
            DataLocator inputDataLocator) {
        ReadableRandomAccessStream dlStream = inputDataLocator.createReadOnlyFile();
        PartitionSystemType[] result = detectPartitionSystem(dlStream);
        dlStream.close();
        return result;
    }

    /**
     * Runs a partition system detection test on <code>psStream</code> to
     * determine what partition system is present. As the detection engines are
     * user defined and may return false positives, a list of all positive
     * detection results is returned. It is up to the caller to sort out any
     * false positives.
     *
     * @param psStream the stream to probe for known partition systems.
     * @return a list of matching partition systems. If no matches were found,
     * this list will be empty (0 elements).
     */
    public static PartitionSystemType[] detectPartitionSystem(
            ReadableRandomAccessStream psStream) {
        long len;
        try {
            len = psStream.length();
        } catch(RuntimeIOException e) {
            len = -1;
        }
        return detectPartitionSystem(psStream, 0, len);
    }

    /**
     * Runs a partition system detection test on <code>psStream</code> to
     * determine what partition system is present. As the detection engines are
     * user defined and may return false positives, a list of all positive
     * detection results is returned. It is up to the caller to sort out any
     * false positives.
     *
     * @param psStream the stream to probe for known partition systems.
     * @param off offset in the stream to start probing at.
     * @param len the length of the data area to probe for partition systems, or
     * -1 if the length isn't currently known.
     * @return a list of matching partition systems. If no matches were found,
     * this list will be empty (0 elements).
     */
    public static PartitionSystemType[] detectPartitionSystem(
            ReadableRandomAccessStream psStream, long off, long len) {

        LinkedList<PartitionSystemType> result =
                new LinkedList<PartitionSystemType>();

        for(PartitionSystemType type : PartitionSystemType.values()) {
            PartitionSystemHandlerFactory fact =
                    type.createDefaultHandlerFactory();

            if(fact != null) {
                if(fact.getRecognizer().detect(psStream, off, len))
                    result.add(type);
            }
        }

        return result.toArray(new PartitionSystemType[result.size()]);
    }
}
