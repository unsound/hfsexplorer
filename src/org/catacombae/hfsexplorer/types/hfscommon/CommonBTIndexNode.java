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

package org.catacombae.hfsexplorer.types.hfscommon;

/**
 *
 * @author erik
 */
public abstract class CommonBTIndexNode extends CommonBTNode {
    
    protected CommonBTIndexNode(byte[] data, int offset, int nodeSize, FSType type) {
	super(data, offset, nodeSize, type);
        
        validate();
    }

    /**
     * Runs a validation test on the node data, and throws a RuntimeException if
     * the data is invalid. Check the message of the exception for more details.
     *
     * @throws RuntimeException if the some of the data in the fields is
     * invalid.
     */
    private void validate() throws RuntimeException {
        for(CommonBTRecord rec : ic.records) {
            if(!(rec instanceof CommonBTIndexRecord))
                throw new RuntimeException("Invalid record type: " + rec.getClass());
        }
    }

    public CommonBTIndexRecord[] getIndexRecords() {
        CommonBTIndexRecord[] res = new CommonBTIndexRecord[ic.records.length];
        
        for(int i = 0; i < res.length; ++i) {
            CommonBTRecord rec = ic.records[i];
            if(rec instanceof CommonBTIndexRecord)
                res[i] = (CommonBTIndexRecord)rec;
            else
                throw new RuntimeException("Invalid record type: " + rec.getClass());
        }

        return res;
    }
}
