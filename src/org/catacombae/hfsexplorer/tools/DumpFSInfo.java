/*-
 * Copyright (C) 2006 Erik Larsson
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

package org.catacombae.hfsexplorer.tools;
import java.io.File;
import java.io.FileOutputStream;
import java.util.LinkedList;
import org.catacombae.io.ReadableFileStream;
import org.catacombae.io.ReadableRandomAccessStream;
import javax.swing.JOptionPane;
import org.catacombae.hfsexplorer.GUIUtil;
import org.catacombae.hfsexplorer.PartitionSystemRecognizer;
import org.catacombae.hfsexplorer.SelectWindowsDeviceDialog;
import org.catacombae.hfsexplorer.partitioning.ApplePartitionMap;
import org.catacombae.hfsexplorer.partitioning.GUIDPartitionTable;
import org.catacombae.hfsexplorer.partitioning.MBRPartitionTable;
import org.catacombae.hfsexplorer.partitioning.Partition;
import org.catacombae.hfsexplorer.partitioning.PartitionSystem;
import org.catacombae.hfsexplorer.win32.WindowsLowLevelIO;
import org.catacombae.jparted.lib.ps.PartitionType;

public class DumpFSInfo {

    public static void main(String[] args) throws Throwable {
        try {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        /*
         * Description of look&feels:
         *   http://java.sun.com/docs/books/tutorial/uiswing/misc/plaf.html
         */
        } catch(Throwable e) {
            //It's ok. Non-critical.
        }
        try {
            dumpInfo(args);
            System.exit(0);
        } catch(Exception e) {
            GUIUtil.displayExceptionDialog(e, 25, null);
        } catch(Throwable t) {
            t.printStackTrace();
            throw t;
        }
        System.exit(1);
    }

    public static void dumpInfo(String[] args) throws Exception {
        long runTimestamp = System.currentTimeMillis();
        ReadableRandomAccessStream fsFile;
        if(WindowsLowLevelIO.isSystemSupported()) {
            if(args.length == 1) {
                fsFile = new WindowsLowLevelIO(args[0]);
            }
            else if(args.length == 0) {
                SelectWindowsDeviceDialog swdd =
                        new SelectWindowsDeviceDialog(null, true, "Select device to extract info from");
                swdd.setVisible(true);
                fsFile = swdd.getPartitionStream();
                if(fsFile == null)
                    System.exit(0);
            }
            else {
                System.out.println("Usage: java DumpFSInfo <filename>");
                System.out.println("        for reading directly from a specified file, or...");
                System.out.println("       java DumpFSInfo");
                System.out.println("        to pop up a Windows device dialog where you can choose which device to read");
                return;
            }
        }
        else {
            if(args.length == 1) {
                fsFile = new ReadableFileStream(args[0]);
            }
            else {
                System.out.println("Usage: java DumpFSInfo <filename>");
                return;
            }
        }

        LinkedList<File> generatedFiles = new LinkedList<File>();
        long fsOffset, fsLength;
        int partNum = -1;

        PartitionSystemRecognizer psr = new PartitionSystemRecognizer(fsFile);
        PartitionSystem partSys = psr.getPartitionSystem();

        if(partSys != null) {
            Partition[] partitions = partSys.getUsedPartitionEntries();
            if(partitions.length == 0) {
                // Proceed to detect file system
                fsOffset = 0;
                try {
                    fsLength = fsFile.length();
                } catch(Exception e) {
                    e.printStackTrace();
                    fsLength = -1;
                }
            }
            else {
                // Dump partition system to file(s)
                if(partSys instanceof ApplePartitionMap) {
                    ApplePartitionMap apm = (ApplePartitionMap) partSys;
                    File ddrFile = new File("fsdump-" + runTimestamp + "_ddr.dat");
                    byte[] ddrData = new byte[512];
                    FileOutputStream fos = new FileOutputStream(ddrFile);
                    fsFile.seek(0);
                    fsFile.readFully(ddrData);
                    fos.write(ddrData);
                    fos.close();
                    generatedFiles.add(ddrFile);

                    File apmFile = new File("fsdump-" + runTimestamp + "_apm.dat");
                    fos = new FileOutputStream(apmFile);
                    fos.write(apm.getData());
                    fos.close();
                    generatedFiles.add(apmFile);
                }
                else if(partSys instanceof GUIDPartitionTable) {
                    GUIDPartitionTable gpt = (GUIDPartitionTable) partSys;
                    File mbrFile = new File("fsdump-" + runTimestamp + "_protectivembr.dat");
                    byte[] mbrData = new byte[512];
                    FileOutputStream fos = new FileOutputStream(mbrFile);
                    fsFile.seek(0);
                    fsFile.readFully(mbrData);
                    fos.write(mbrData);
                    fos.close();
                    generatedFiles.add(mbrFile);

                    File gptBeginFile = new File("fsdump-" + runTimestamp + "_gptprimary.dat");
                    fos = new FileOutputStream(gptBeginFile);
                    fos.write(gpt.getPrimaryTableBytes());
                    fos.close();
                    generatedFiles.add(gptBeginFile);

                    File gptEndFile = new File("fsdump-" + runTimestamp + "_gptbackup.dat");
                    fos = new FileOutputStream(gptEndFile);
                    fos.write(gpt.getBackupTableBytes());
                    fos.close();
                    generatedFiles.add(gptEndFile);
                }
                else if(partSys instanceof MBRPartitionTable) {
                    MBRPartitionTable mbr = (MBRPartitionTable) partSys;
                    File mbrFile = new File("fsdump-" + runTimestamp + "_mbr.dat");
                    FileOutputStream fos = new FileOutputStream(mbrFile);
                    fos.write(mbr.getMasterBootRecord().getBytes());
                    fos.close();
                    generatedFiles.add(mbrFile);
                }
                else
                    throw new RuntimeException("Unknown partition system type!");

                Object selectedValue;
                int firstPreferredPartition = 0;
                for(int i = 0; i < partitions.length; ++i) {
                    Partition p = partitions[i];
                    PartitionType pt = p.getType();
                    if(pt == PartitionType.APPLE_HFS_CONTAINER || pt == PartitionType.APPLE_HFSX) {
                        firstPreferredPartition = i;
                        break;
                    }
                }
                selectedValue = JOptionPane.showInputDialog(null, "Select which partition to read",
                        "Choose " + partSys.getLongName() + " partition",
                        JOptionPane.QUESTION_MESSAGE,
                        null, partitions, partitions[firstPreferredPartition]);
                for(int i = 0; i < partitions.length; ++i) {
                    if(partitions[i] == selectedValue) {
                        partNum = i;
                        break;
                    }
                }

                if(selectedValue instanceof Partition) {
                    Partition selectedPartition = (Partition) selectedValue;
                    fsOffset = selectedPartition.getStartOffset();
                    fsLength = selectedPartition.getLength();
                }
                else
                    throw new RuntimeException("Impossible error!");
            }
        }
        else {
            fsOffset = 0;
            try {
                fsLength = fsFile.length();
            } catch(Exception e) {
                e.printStackTrace();
                fsLength = -1;
            }
        }

        // Dump the first and last 64 KiB from the partition
        byte[] buffer = new byte[65536];
        File first64File;
        File last64File;
        if(partNum == -1) {
            first64File = new File("fsdump-" + runTimestamp + "_first64.dat");
            last64File = new File("fsdump-" + runTimestamp + "_last64.dat");
        }
        else {
            first64File = new File("fsdump-" + runTimestamp + "_p" + partNum + "_first64.dat");
            last64File = new File("fsdump-" + runTimestamp + "_p" + partNum + "_last64.dat");
        }

        if(extractDataToFile(fsFile, fsOffset, first64File, 65536))
            generatedFiles.add(first64File);

        long pos = fsOffset + fsLength - buffer.length;
        if(pos > fsOffset &&
                extractDataToFile(fsFile, pos, last64File, 65536))
            generatedFiles.add(last64File);
        
        // Display result
        StringBuilder sb = new StringBuilder();
        sb.append("Dumped FS info to directory:\n    ");
        File firstFile = generatedFiles.getFirst().getAbsoluteFile();
        File firstParent = firstFile.getParentFile();
        sb.append(firstParent.getAbsolutePath());
        sb.append("\nThe following files were generated:\n    ");
        for(File f : generatedFiles)
            sb.append(f.toString() + "\n    ");

        JOptionPane.showMessageDialog(null, sb.toString(), "Result", JOptionPane.INFORMATION_MESSAGE);

    }

    private static boolean extractDataToFile(ReadableRandomAccessStream fsFile, long pos, File outFile, int dataSize) {
        try {
            byte[] buffer = new byte[dataSize];
            fsFile.seek(pos);
            int bytesRead = fsFile.read(buffer);
            FileOutputStream fileOut = new FileOutputStream(outFile);
            fileOut.write(buffer, 0, bytesRead);
            fileOut.close();

            return true;
        } catch(Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
