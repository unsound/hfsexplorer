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
public abstract class CommonBTIndexNode <K extends CommonBTKey<K>> extends CommonBTNode<CommonBTIndexRecord<K>> {
    
    protected CommonBTIndexNode(byte[] data, int offset, int nodeSize, FSType type) {
        super(data, offset, nodeSize, type);
    }

    /*
    public CommonBTIndexRecord<K>[] getIndexRecords() {
        
        CommonBTIndexRecord<K>[] res = new CommonBTIndexRecord[ic.records.];
        
        for(int i = 0; i < res.length; ++i) {
            CommonBTRecord rec = ic.records[i];
            if(rec instanceof CommonBTIndexRecord)
                res[i] = (CommonBTIndexRecord)rec;
            else
                throw new RuntimeException("Invalid record type: " + rec.getClass());
        }

        return res;
    }
    */
}
