/*-
 * Copyright (C) 2014 Erik Larsson
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

package org.catacombae.storage.fs.hfscommon;

import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogFileRecord;
import org.catacombae.hfs.types.hfscommon.CommonHFSCatalogLeafRecord;
import org.catacombae.storage.fs.FSFile;

/**
 * @author <a href="http://www.catacombae.org/" target="_top">Erik Larsson</a>
 */
public class HFSCommonFSFile extends HFSCommonAbstractFile implements FSFile {
    protected HFSCommonFSFile(HFSCommonFileSystemHandler parent,
            CommonHFSCatalogFileRecord fileRecord)
    {
        super(parent, fileRecord);
    }

    protected HFSCommonFSFile(HFSCommonFileSystemHandler parent,
            CommonHFSCatalogLeafRecord hardLinkRecord,
            CommonHFSCatalogFileRecord fileRecord)
    {
        super(parent, hardLinkRecord, fileRecord);
    }
}
