/*-
 * Copyright (C) 2007 Erik Larsson
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

/*
 * HFSPlusVolumeInfoPanel.java
 *
 * Created on den 11 februari 2007, 14:42
 */

package org.catacombae.hfsexplorer.gui;

import org.catacombae.hfsexplorer.Util;
import org.catacombae.hfsexplorer.types.hfsplus.HFSPlusVolumeHeader;
import org.catacombae.hfsexplorer.types.hfsplus.HFSPlusForkData;
import java.text.DateFormat;

/**
 * A panel for displaying the values in a HFSPlusVolumeHeader in a good looking
 * way.
 * @author  Erik Larsson, erik82@kth.se
 */
public class HFSPlusVolumeInfoPanel extends javax.swing.JPanel {
    private final DateFormat dti =
            DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
    
    /** Creates new form HFSPlusVolumeInfoPanel */
    public HFSPlusVolumeInfoPanel(HFSPlusVolumeHeader vh) {
        initComponents();
        setFields(vh);
    }
    
    /**
     * Sets the fields of this HFSPlusVolumeInfoPanel to the values in the structure
     * <code>vh</code>.
     * @param vh the structure containing the values to set.
     */
    public void setFields(HFSPlusVolumeHeader vh) {
	signatureField.setText("" + Util.toASCIIString(vh.getSignature()));
	versionField.setText("" + vh.getVersion());
        
	hardwareLockBox.setSelected(vh.getAttributeVolumeHardwareLock());
	volumeUnmountedBox.setSelected(vh.getAttributeVolumeUnmounted());
	sparedBlocksBox.setSelected(vh.getAttributeVolumeSparedBlocks());
	noCacheBox.setSelected(vh.getAttributeVolumeNoCacheRequired());
	volumeInconsistentBox.setSelected(
                vh.getAttributeBootVolumeInconsistent());
	idsReusedBox.setSelected(vh.getAttributeCatalogNodeIDsReused());
	journaledBox.setSelected(vh.getAttributeVolumeJournaled());
	softwareLockBox.setSelected(vh.getAttributeVolumeSoftwareLock());
        
	lastMountedVersionField.setText("" +
                Util.toASCIIString(vh.getLastMountedVersion()));
	journalInfoBlockField.setText("0x" +
                Util.toHexStringBE(vh.getJournalInfoBlock()));
	createDateField.setText("" + dti.format(vh.getCreateDateAsDate()));
	modifyDateField.setText("" + dti.format(vh.getModifyDateAsDate()));
	backupDateField.setText("" + dti.format(vh.getBackupDateAsDate()));
	checkedDateField.setText("" + dti.format(vh.getCheckedDateAsDate()));
	fileCountField.setText("" + vh.getFileCount());
	folderCountField.setText("" + vh.getFolderCount());
	blockSizeField.setText("" + vh.getBlockSize() + " bytes");
        totalBlocksField.setText("" + vh.getTotalBlocks());
	freeBlocksField.setText("" + vh.getFreeBlocks());
	nextAllocationField.setText("" + vh.getNextAllocation());
	rsrcClumpSizeField.setText("" + vh.getRsrcClumpSize() + " bytes");
	dataClumpSizeField.setText("" + vh.getDataClumpSize() + " bytes");
	nextCatalogIDField.setText("" + vh.getNextCatalogID());
	writeCountField.setText("" + vh.getWriteCount());
	encodingsBitmapField.setText("0x" +
                Util.toHexStringBE(vh.getEncodingsBitmap()));
	int[] finderInfo = vh.getFinderInfo();
	finderInfo1Field.setText("" + finderInfo[0]);
	finderInfo2Field.setText("" + finderInfo[1]);
	finderInfo3Field.setText("" + finderInfo[2]);
	finderInfo4Field.setText("" + finderInfo[3]);
	finderInfo5Field.setText("0x" + Util.toHexStringBE(finderInfo[4]));
	finderInfo6Field.setText("" + finderInfo[5]);
	finderInfo78Field.setText("0x" + Util.toHexStringBE(finderInfo[6]) +
                Util.toHexStringBE(finderInfo[7]));
        
        // Allocation file fields
        HFSPlusForkData allocationFileData = vh.getAllocationFile();
        allocationFileLogicalSizeField.setText(allocationFileData.getLogicalSize() + " bytes");
        allocationFileClumpSizeField.setText(allocationFileData.getClumpSize() + " bytes");
        allocationFileTotalBlocksField.setText("" + allocationFileData.getTotalBlocks());
        allocationFileBasicExtentCountField.setText("" + allocationFileData.getExtents().getNumExtentsInUse());
        
        // Extents file fields
        HFSPlusForkData extentsFileData = vh.getExtentsFile();
        extentsFileLogicalSizeField.setText(extentsFileData.getLogicalSize() + " bytes");
        extentsFileClumpSizeField.setText(extentsFileData.getClumpSize() + " bytes");
        extentsFileTotalBlocksField.setText("" + extentsFileData.getTotalBlocks());
        extentsFileBasicExtentCountField.setText("" + extentsFileData.getExtents().getNumExtentsInUse());
        
        // Catalog file fields
        HFSPlusForkData catalogFileData = vh.getCatalogFile();
        catalogFileLogicalSizeField.setText(catalogFileData.getLogicalSize() + " bytes");
        catalogFileClumpSizeField.setText(catalogFileData.getClumpSize() + " bytes");
        catalogFileTotalBlocksField.setText("" + catalogFileData.getTotalBlocks());
        catalogFileBasicExtentCountField.setText("" + catalogFileData.getExtents().getNumExtentsInUse());
        
        // Attributes file fields
        HFSPlusForkData attributesFileData = vh.getAttributesFile();
        attributesFileLogicalSizeField.setText(attributesFileData.getLogicalSize() + " bytes");
        attributesFileClumpSizeField.setText(attributesFileData.getClumpSize() + " bytes");
        attributesFileTotalBlocksField.setText("" + attributesFileData.getTotalBlocks());
        attributesFileBasicExtentCountField.setText("" + attributesFileData.getExtents().getNumExtentsInUse());
        
        // Startup file fields
        HFSPlusForkData startupFileData = vh.getStartupFile();
        startupFileLogicalSizeField.setText(startupFileData.getLogicalSize() + " bytes");
        startupFileClumpSizeField.setText(startupFileData.getClumpSize() + " bytes");
        startupFileTotalBlocksField.setText("" + startupFileData.getTotalBlocks());
        startupFileBasicExtentCountField.setText("" + startupFileData.getExtents().getNumExtentsInUse());
        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        signatureLabel = new javax.swing.JLabel();
        signatureField = new javax.swing.JTextField();
        versionLabel = new javax.swing.JLabel();
        versionField = new javax.swing.JTextField();
        attributesSectionLabel = new javax.swing.JLabel();
        hardwareLockBox = new javax.swing.JCheckBox();
        hardwareLockLabel = new javax.swing.JLabel();
        volumeUnmountedBox = new javax.swing.JCheckBox();
        volumeUnmountedLabel = new javax.swing.JLabel();
        sparedBlocksBox = new javax.swing.JCheckBox();
        sparedBlocksLabel = new javax.swing.JLabel();
        noCacheBox = new javax.swing.JCheckBox();
        noCacheLabel = new javax.swing.JLabel();
        volumeInconsistentBox = new javax.swing.JCheckBox();
        volumeInconsistentLabel = new javax.swing.JLabel();
        idsReusedBox = new javax.swing.JCheckBox();
        idsReusedLabel = new javax.swing.JLabel();
        journaledBox = new javax.swing.JCheckBox();
        journaledLabel = new javax.swing.JLabel();
        softwareLockBox = new javax.swing.JCheckBox();
        softwareLockLabel = new javax.swing.JLabel();
        lastMountedVersionLabel = new javax.swing.JLabel();
        lastMountedVersionField = new javax.swing.JTextField();
        journalInfoBlockLabel = new javax.swing.JLabel();
        journalInfoBlockField = new javax.swing.JTextField();
        createDateLabel = new javax.swing.JLabel();
        createDateField = new javax.swing.JTextField();
        modifyDateLabel = new javax.swing.JLabel();
        modifyDateField = new javax.swing.JTextField();
        backupDateLabel = new javax.swing.JLabel();
        backupDateField = new javax.swing.JTextField();
        checkedDateLabel = new javax.swing.JLabel();
        checkedDateField = new javax.swing.JTextField();
        fileCountLabel = new javax.swing.JLabel();
        fileCountField = new javax.swing.JTextField();
        folderCountLabel = new javax.swing.JLabel();
        folderCountField = new javax.swing.JTextField();
        blockSizeLabel = new javax.swing.JLabel();
        blockSizeField = new javax.swing.JTextField();
        totalBlocksLabel = new javax.swing.JLabel();
        totalBlocksField = new javax.swing.JTextField();
        freeBlocksLabel = new javax.swing.JLabel();
        freeBlocksField = new javax.swing.JTextField();
        nextAllocationLabel = new javax.swing.JLabel();
        nextAllocationField = new javax.swing.JTextField();
        rsrcClumpSizeLabel = new javax.swing.JLabel();
        rsrcClumpSizeField = new javax.swing.JTextField();
        dataClumpSizeLabel = new javax.swing.JLabel();
        dataClumpSizeField = new javax.swing.JTextField();
        nextCatalogIDLabel = new javax.swing.JLabel();
        nextCatalogIDField = new javax.swing.JTextField();
        writeCountLabel = new javax.swing.JLabel();
        writeCountField = new javax.swing.JTextField();
        encodingsBitmapLabel = new javax.swing.JLabel();
        encodingsBitmapField = new javax.swing.JTextField();
        finderInfoSectionLabel = new javax.swing.JLabel();
        finderInfo1Label = new javax.swing.JLabel();
        finderInfo2Label = new javax.swing.JLabel();
        finderInfo3Label = new javax.swing.JLabel();
        finderInfo4Label = new javax.swing.JLabel();
        finderInfo5Label = new javax.swing.JLabel();
        finderInfo6Label = new javax.swing.JLabel();
        finderInfo78Label = new javax.swing.JLabel();
        finderInfo1Field = new javax.swing.JTextField();
        finderInfo2Field = new javax.swing.JTextField();
        finderInfo3Field = new javax.swing.JTextField();
        finderInfo4Field = new javax.swing.JTextField();
        finderInfo5Field = new javax.swing.JTextField();
        finderInfo6Field = new javax.swing.JTextField();
        finderInfo78Field = new javax.swing.JTextField();
        allocationFileSectionLabel = new javax.swing.JLabel();
        allocationFileLogicalSizeLabel = new javax.swing.JLabel();
        allocationFileClumpSizeLabel = new javax.swing.JLabel();
        allocationFileTotalBlocksLabel = new javax.swing.JLabel();
        allocationFileLogicalSizeField = new javax.swing.JTextField();
        allocationFileClumpSizeField = new javax.swing.JTextField();
        allocationFileTotalBlocksField = new javax.swing.JTextField();
        allocationFileBasicExtentCountField = new javax.swing.JTextField();
        allocationFileBasicExtentCountLabel = new javax.swing.JLabel();
        extentsFileSectionLabel = new javax.swing.JLabel();
        extentsFileLogicalSizeLabel = new javax.swing.JLabel();
        extentsFileLogicalSizeField = new javax.swing.JTextField();
        extentsFileClumpSizeLabel = new javax.swing.JLabel();
        extentsFileClumpSizeField = new javax.swing.JTextField();
        extentsFileTotalBlocksLabel = new javax.swing.JLabel();
        extentsFileTotalBlocksField = new javax.swing.JTextField();
        extentsFileBasicExtentCountLabel = new javax.swing.JLabel();
        extentsFileBasicExtentCountField = new javax.swing.JTextField();
        catalogFileSectionLabel = new javax.swing.JLabel();
        catalogFileLogicalSizeLabel = new javax.swing.JLabel();
        catalogFileLogicalSizeField = new javax.swing.JTextField();
        catalogFileClumpSizeLabel = new javax.swing.JLabel();
        catalogFileClumpSizeField = new javax.swing.JTextField();
        catalogFileTotalBlocksLabel = new javax.swing.JLabel();
        catalogFileTotalBlocksField = new javax.swing.JTextField();
        catalogFileBasicExtentCountLabel = new javax.swing.JLabel();
        catalogFileBasicExtentCountField = new javax.swing.JTextField();
        attributesFileSectionLabel = new javax.swing.JLabel();
        attributesFileLogicalSizeLabel = new javax.swing.JLabel();
        attributesFileLogicalSizeField = new javax.swing.JTextField();
        attributesFileClumpSizeLabel = new javax.swing.JLabel();
        attributesFileClumpSizeField = new javax.swing.JTextField();
        attributesFileTotalBlocksLabel = new javax.swing.JLabel();
        attributesFileTotalBlocksField = new javax.swing.JTextField();
        attributesFileBasicExtentCountLabel = new javax.swing.JLabel();
        attributesFileBasicExtentCountField = new javax.swing.JTextField();
        startupFileSectionLabel = new javax.swing.JLabel();
        startupFileLogicalSizeLabel = new javax.swing.JLabel();
        startupFileLogicalSizeField = new javax.swing.JTextField();
        startupFileClumpSizeLabel = new javax.swing.JLabel();
        startupFileClumpSizeField = new javax.swing.JTextField();
        startupFileTotalBlocksLabel = new javax.swing.JLabel();
        startupFileTotalBlocksField = new javax.swing.JTextField();
        startupFileBasicExtentCountLabel = new javax.swing.JLabel();
        startupFileBasicExtentCountField = new javax.swing.JTextField();

        signatureLabel.setText("Volume signature:");

        signatureField.setEditable(false);
        signatureField.setText("jTextField18");
        signatureField.setBorder(null);
        signatureField.setOpaque(false);

        versionLabel.setText("File system version:");

        versionField.setEditable(false);
        versionField.setText("jTextField19");
        versionField.setBorder(null);
        versionField.setOpaque(false);

        attributesSectionLabel.setText("Attributes:");

        hardwareLockBox.setEnabled(false);
        hardwareLockBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        hardwareLockLabel.setText("Volume hardware lock");

        volumeUnmountedBox.setEnabled(false);
        volumeUnmountedBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        volumeUnmountedLabel.setText("Volume unmounted");

        sparedBlocksBox.setEnabled(false);
        sparedBlocksBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        sparedBlocksLabel.setText("Volume spared blocks");

        noCacheBox.setEnabled(false);
        noCacheBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        noCacheLabel.setText("No cache required");

        volumeInconsistentBox.setEnabled(false);
        volumeInconsistentBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        volumeInconsistentLabel.setText("Boot volume inconsistent");

        idsReusedBox.setEnabled(false);
        idsReusedBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        idsReusedLabel.setText("Catalog node IDs reused");

        journaledBox.setEnabled(false);
        journaledBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        journaledLabel.setText("Volume journaled");

        softwareLockBox.setEnabled(false);
        softwareLockBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        softwareLockLabel.setText("Volume software lock");

        lastMountedVersionLabel.setText("Last mounted version:");

        lastMountedVersionField.setEditable(false);
        lastMountedVersionField.setText("jTextField1");
        lastMountedVersionField.setBorder(null);
        lastMountedVersionField.setOpaque(false);

        journalInfoBlockLabel.setText("Journal info block ID:");

        journalInfoBlockField.setEditable(false);
        journalInfoBlockField.setText("jTextField2");
        journalInfoBlockField.setBorder(null);
        journalInfoBlockField.setOpaque(false);

        createDateLabel.setText("Date created:");

        createDateField.setEditable(false);
        createDateField.setText("jTextField3");
        createDateField.setBorder(null);
        createDateField.setOpaque(false);

        modifyDateLabel.setText("Date last modified:");

        modifyDateField.setEditable(false);
        modifyDateField.setText("jTextField4");
        modifyDateField.setBorder(null);
        modifyDateField.setOpaque(false);

        backupDateLabel.setText("Date last backuped:");

        backupDateField.setEditable(false);
        backupDateField.setText("jTextField5");
        backupDateField.setBorder(null);
        backupDateField.setOpaque(false);

        checkedDateLabel.setText("Date last checked:");

        checkedDateField.setEditable(false);
        checkedDateField.setText("jTextField6");
        checkedDateField.setBorder(null);
        checkedDateField.setOpaque(false);

        fileCountLabel.setText("File count:");

        fileCountField.setEditable(false);
        fileCountField.setText("jTextField7");
        fileCountField.setBorder(null);
        fileCountField.setOpaque(false);

        folderCountLabel.setText("Folder count:");

        folderCountField.setEditable(false);
        folderCountField.setText("jTextField8");
        folderCountField.setBorder(null);
        folderCountField.setOpaque(false);

        blockSizeLabel.setText("Block size:");

        blockSizeField.setEditable(false);
        blockSizeField.setText("jTextField9");
        blockSizeField.setBorder(null);
        blockSizeField.setOpaque(false);

        totalBlocksLabel.setText("Number of blocks:");

        totalBlocksField.setEditable(false);
        totalBlocksField.setText("jTextField10");
        totalBlocksField.setBorder(null);
        totalBlocksField.setOpaque(false);

        freeBlocksLabel.setText("Number of free blocks:");

        freeBlocksField.setEditable(false);
        freeBlocksField.setText("jTextField11");
        freeBlocksField.setBorder(null);
        freeBlocksField.setOpaque(false);

        nextAllocationLabel.setText("Start of next allocation search:");

        nextAllocationField.setEditable(false);
        nextAllocationField.setText("jTextField12");
        nextAllocationField.setBorder(null);
        nextAllocationField.setOpaque(false);

        rsrcClumpSizeLabel.setText("Resource fork default clump size:");

        rsrcClumpSizeField.setEditable(false);
        rsrcClumpSizeField.setText("jTextField13");
        rsrcClumpSizeField.setBorder(null);
        rsrcClumpSizeField.setOpaque(false);

        dataClumpSizeLabel.setText("Data fork default clump size:");

        dataClumpSizeField.setEditable(false);
        dataClumpSizeField.setText("jTextField14");
        dataClumpSizeField.setBorder(null);
        dataClumpSizeField.setOpaque(false);

        nextCatalogIDLabel.setText("Next unused catalog ID:");

        nextCatalogIDField.setEditable(false);
        nextCatalogIDField.setText("jTextField15");
        nextCatalogIDField.setBorder(null);
        nextCatalogIDField.setOpaque(false);

        writeCountLabel.setText("Write count:");

        writeCountField.setEditable(false);
        writeCountField.setText("jTextField16");
        writeCountField.setBorder(null);
        writeCountField.setOpaque(false);

        encodingsBitmapLabel.setText("Encodings bitmap:");

        encodingsBitmapField.setEditable(false);
        encodingsBitmapField.setText("jTextField17");
        encodingsBitmapField.setBorder(null);
        encodingsBitmapField.setOpaque(false);

        finderInfoSectionLabel.setText("Finder info:");

        finderInfo1Label.setText("System folder ID:");

        finderInfo2Label.setText("Startup application parent folder ID:");

        finderInfo3Label.setText("Folder ID to display at mount:");

        finderInfo4Label.setText("Legacy Mac OS system folder ID:");

        finderInfo5Label.setText("Reserved:");

        finderInfo6Label.setText("Mac OS X system folder ID:");

        finderInfo78Label.setText("Unique volume identifier:");

        finderInfo1Field.setEditable(false);
        finderInfo1Field.setText("jTextField1");
        finderInfo1Field.setBorder(null);
        finderInfo1Field.setOpaque(false);

        finderInfo2Field.setEditable(false);
        finderInfo2Field.setText("jTextField2");
        finderInfo2Field.setBorder(null);
        finderInfo2Field.setOpaque(false);

        finderInfo3Field.setEditable(false);
        finderInfo3Field.setText("jTextField3");
        finderInfo3Field.setBorder(null);
        finderInfo3Field.setOpaque(false);

        finderInfo4Field.setEditable(false);
        finderInfo4Field.setText("jTextField4");
        finderInfo4Field.setBorder(null);
        finderInfo4Field.setOpaque(false);

        finderInfo5Field.setEditable(false);
        finderInfo5Field.setText("jTextField5");
        finderInfo5Field.setBorder(null);
        finderInfo5Field.setOpaque(false);

        finderInfo6Field.setEditable(false);
        finderInfo6Field.setText("jTextField6");
        finderInfo6Field.setBorder(null);
        finderInfo6Field.setOpaque(false);

        finderInfo78Field.setEditable(false);
        finderInfo78Field.setText("jTextField7");
        finderInfo78Field.setBorder(null);
        finderInfo78Field.setOpaque(false);

        allocationFileSectionLabel.setText("Allocation file:");

        allocationFileLogicalSizeLabel.setText("Logical size:");

        allocationFileClumpSizeLabel.setText("Clump size:");

        allocationFileTotalBlocksLabel.setText("Total blocks:");

        allocationFileLogicalSizeField.setEditable(false);
        allocationFileLogicalSizeField.setText("jTextField1");
        allocationFileLogicalSizeField.setBorder(null);
        allocationFileLogicalSizeField.setOpaque(false);

        allocationFileClumpSizeField.setEditable(false);
        allocationFileClumpSizeField.setText("jTextField2");
        allocationFileClumpSizeField.setBorder(null);
        allocationFileClumpSizeField.setOpaque(false);

        allocationFileTotalBlocksField.setEditable(false);
        allocationFileTotalBlocksField.setText("jTextField3");
        allocationFileTotalBlocksField.setBorder(null);
        allocationFileTotalBlocksField.setOpaque(false);

        allocationFileBasicExtentCountField.setEditable(false);
        allocationFileBasicExtentCountField.setText("jTextField1");
        allocationFileBasicExtentCountField.setAutoscrolls(false);
        allocationFileBasicExtentCountField.setBorder(null);
        allocationFileBasicExtentCountField.setOpaque(false);

        allocationFileBasicExtentCountLabel.setText("Number of basic extents:");

        extentsFileSectionLabel.setText("Extents file:");

        extentsFileLogicalSizeLabel.setText("Logical size:");

        extentsFileLogicalSizeField.setEditable(false);
        extentsFileLogicalSizeField.setText("jTextField1");
        extentsFileLogicalSizeField.setBorder(null);
        extentsFileLogicalSizeField.setOpaque(false);

        extentsFileClumpSizeLabel.setText("Clump size:");

        extentsFileClumpSizeField.setEditable(false);
        extentsFileClumpSizeField.setText("jTextField2");
        extentsFileClumpSizeField.setBorder(null);
        extentsFileClumpSizeField.setOpaque(false);

        extentsFileTotalBlocksLabel.setText("Total blocks:");

        extentsFileTotalBlocksField.setEditable(false);
        extentsFileTotalBlocksField.setText("jTextField3");
        extentsFileTotalBlocksField.setBorder(null);
        extentsFileTotalBlocksField.setOpaque(false);

        extentsFileBasicExtentCountLabel.setText("Number of basic extents:");

        extentsFileBasicExtentCountField.setEditable(false);
        extentsFileBasicExtentCountField.setText("jTextField1");
        extentsFileBasicExtentCountField.setBorder(null);
        extentsFileBasicExtentCountField.setOpaque(false);

        catalogFileSectionLabel.setText("Catalog file:");

        catalogFileLogicalSizeLabel.setText("Logical size:");

        catalogFileLogicalSizeField.setEditable(false);
        catalogFileLogicalSizeField.setText("jTextField1");
        catalogFileLogicalSizeField.setBorder(null);
        catalogFileLogicalSizeField.setOpaque(false);

        catalogFileClumpSizeLabel.setText("Clump size:");

        catalogFileClumpSizeField.setEditable(false);
        catalogFileClumpSizeField.setText("jTextField2");
        catalogFileClumpSizeField.setBorder(null);
        catalogFileClumpSizeField.setOpaque(false);

        catalogFileTotalBlocksLabel.setText("Total blocks:");

        catalogFileTotalBlocksField.setEditable(false);
        catalogFileTotalBlocksField.setText("jTextField3");
        catalogFileTotalBlocksField.setBorder(null);
        catalogFileTotalBlocksField.setOpaque(false);

        catalogFileBasicExtentCountLabel.setText("Number of basic extents:");

        catalogFileBasicExtentCountField.setEditable(false);
        catalogFileBasicExtentCountField.setText("jTextField1");
        catalogFileBasicExtentCountField.setBorder(null);
        catalogFileBasicExtentCountField.setOpaque(false);

        attributesFileSectionLabel.setText("Attributes file:");

        attributesFileLogicalSizeLabel.setText("Logical size:");

        attributesFileLogicalSizeField.setEditable(false);
        attributesFileLogicalSizeField.setText("jTextField1");
        attributesFileLogicalSizeField.setBorder(null);
        attributesFileLogicalSizeField.setOpaque(false);

        attributesFileClumpSizeLabel.setText("Clump size:");

        attributesFileClumpSizeField.setEditable(false);
        attributesFileClumpSizeField.setText("jTextField2");
        attributesFileClumpSizeField.setBorder(null);
        attributesFileClumpSizeField.setOpaque(false);

        attributesFileTotalBlocksLabel.setText("Total blocks:");

        attributesFileTotalBlocksField.setEditable(false);
        attributesFileTotalBlocksField.setText("jTextField3");
        attributesFileTotalBlocksField.setBorder(null);
        attributesFileTotalBlocksField.setOpaque(false);

        attributesFileBasicExtentCountLabel.setText("Number of basic extents:");

        attributesFileBasicExtentCountField.setEditable(false);
        attributesFileBasicExtentCountField.setText("jTextField1");
        attributesFileBasicExtentCountField.setBorder(null);
        attributesFileBasicExtentCountField.setOpaque(false);

        startupFileSectionLabel.setText("Startup file:");

        startupFileLogicalSizeLabel.setText("Logical size:");

        startupFileLogicalSizeField.setEditable(false);
        startupFileLogicalSizeField.setText("jTextField1");
        startupFileLogicalSizeField.setBorder(null);
        startupFileLogicalSizeField.setOpaque(false);

        startupFileClumpSizeLabel.setText("Clump size:");

        startupFileClumpSizeField.setEditable(false);
        startupFileClumpSizeField.setText("jTextField2");
        startupFileClumpSizeField.setBorder(null);
        startupFileClumpSizeField.setOpaque(false);

        startupFileTotalBlocksLabel.setText("Total blocks:");

        startupFileTotalBlocksField.setEditable(false);
        startupFileTotalBlocksField.setText("jTextField3");
        startupFileTotalBlocksField.setBorder(null);
        startupFileTotalBlocksField.setOpaque(false);

        startupFileBasicExtentCountLabel.setText("Number of basic extents:");

        startupFileBasicExtentCountField.setEditable(false);
        startupFileBasicExtentCountField.setText("jTextField1");
        startupFileBasicExtentCountField.setBorder(null);
        startupFileBasicExtentCountField.setOpaque(false);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(signatureLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(signatureField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(versionLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(versionField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE))
                    .add(attributesSectionLabel)
                    .add(layout.createSequentialGroup()
                        .add(24, 24, 24)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(hardwareLockBox)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(hardwareLockLabel))
                            .add(layout.createSequentialGroup()
                                .add(noCacheBox)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(noCacheLabel))
                            .add(layout.createSequentialGroup()
                                .add(volumeInconsistentBox)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(volumeInconsistentLabel))
                            .add(layout.createSequentialGroup()
                                .add(idsReusedBox)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(idsReusedLabel))
                            .add(layout.createSequentialGroup()
                                .add(journaledBox)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(journaledLabel))
                            .add(layout.createSequentialGroup()
                                .add(softwareLockBox)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(softwareLockLabel))
                            .add(layout.createSequentialGroup()
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(layout.createSequentialGroup()
                                        .add(volumeUnmountedBox)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(volumeUnmountedLabel))
                                    .add(layout.createSequentialGroup()
                                        .add(sparedBlocksBox)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(sparedBlocksLabel))))))
                    .add(layout.createSequentialGroup()
                        .add(lastMountedVersionLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(lastMountedVersionField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 290, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(journalInfoBlockLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(journalInfoBlockField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 296, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(fileCountLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(fileCountField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 364, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(folderCountLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(folderCountField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 347, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(blockSizeLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(blockSizeField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 363, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(totalBlocksLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(totalBlocksField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(freeBlocksLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(freeBlocksField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(nextAllocationLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(nextAllocationField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 237, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(rsrcClumpSizeLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(rsrcClumpSizeField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 219, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(dataClumpSizeLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(dataClumpSizeField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(nextCatalogIDLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(nextCatalogIDField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(writeCountLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(writeCountField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(encodingsBitmapLabel)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(encodingsBitmapField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 313, Short.MAX_VALUE))
                    .add(finderInfoSectionLabel)
                    .add(layout.createSequentialGroup()
                        .add(24, 24, 24)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(finderInfo2Label)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(finderInfo2Field, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE))
                            .add(layout.createSequentialGroup()
                                .add(finderInfo1Label)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(finderInfo1Field, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE))
                            .add(layout.createSequentialGroup()
                                .add(finderInfo3Label)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(finderInfo3Field, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE))
                            .add(layout.createSequentialGroup()
                                .add(finderInfo4Label)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(finderInfo4Field, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 201, Short.MAX_VALUE))
                            .add(layout.createSequentialGroup()
                                .add(finderInfo5Label)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(finderInfo5Field, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE))
                            .add(layout.createSequentialGroup()
                                .add(finderInfo6Label)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(finderInfo6Field, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 236, Short.MAX_VALUE))
                            .add(layout.createSequentialGroup()
                                .add(finderInfo78Label)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(finderInfo78Field, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE))))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(backupDateLabel)
                            .add(modifyDateLabel)
                            .add(createDateLabel)
                            .add(checkedDateLabel))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(checkedDateField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE)
                            .add(createDateField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE)
                            .add(modifyDateField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE)
                            .add(backupDateField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 305, Short.MAX_VALUE)))
                    .add(allocationFileSectionLabel)
                    .add(layout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(allocationFileClumpSizeLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(allocationFileClumpSizeField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE))
                            .add(layout.createSequentialGroup()
                                .add(allocationFileLogicalSizeLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(allocationFileLogicalSizeField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE))
                            .add(layout.createSequentialGroup()
                                .add(allocationFileTotalBlocksLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(allocationFileTotalBlocksField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE))
                            .add(layout.createSequentialGroup()
                                .add(allocationFileBasicExtentCountLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(allocationFileBasicExtentCountField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE))))
                    .add(extentsFileSectionLabel)
                    .add(layout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(extentsFileClumpSizeLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(extentsFileClumpSizeField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE))
                            .add(layout.createSequentialGroup()
                                .add(extentsFileLogicalSizeLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(extentsFileLogicalSizeField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE))
                            .add(layout.createSequentialGroup()
                                .add(extentsFileTotalBlocksLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(extentsFileTotalBlocksField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE))
                            .add(layout.createSequentialGroup()
                                .add(extentsFileBasicExtentCountLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(extentsFileBasicExtentCountField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE))))
                    .add(catalogFileSectionLabel)
                    .add(layout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(catalogFileClumpSizeLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(catalogFileClumpSizeField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE))
                            .add(layout.createSequentialGroup()
                                .add(catalogFileLogicalSizeLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(catalogFileLogicalSizeField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE))
                            .add(layout.createSequentialGroup()
                                .add(catalogFileTotalBlocksLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(catalogFileTotalBlocksField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE))
                            .add(layout.createSequentialGroup()
                                .add(catalogFileBasicExtentCountLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(catalogFileBasicExtentCountField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE))))
                    .add(attributesFileSectionLabel)
                    .add(layout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(attributesFileClumpSizeLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(attributesFileClumpSizeField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE))
                            .add(layout.createSequentialGroup()
                                .add(attributesFileLogicalSizeLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(attributesFileLogicalSizeField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE))
                            .add(layout.createSequentialGroup()
                                .add(attributesFileTotalBlocksLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(attributesFileTotalBlocksField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE))
                            .add(layout.createSequentialGroup()
                                .add(attributesFileBasicExtentCountLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(attributesFileBasicExtentCountField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE))))
                    .add(startupFileSectionLabel)
                    .add(layout.createSequentialGroup()
                        .add(10, 10, 10)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(startupFileClumpSizeLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(startupFileClumpSizeField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE))
                            .add(layout.createSequentialGroup()
                                .add(startupFileLogicalSizeLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(startupFileLogicalSizeField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 342, Short.MAX_VALUE))
                            .add(layout.createSequentialGroup()
                                .add(startupFileTotalBlocksLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(startupFileTotalBlocksField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE))
                            .add(layout.createSequentialGroup()
                                .add(startupFileBasicExtentCountLabel)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(startupFileBasicExtentCountField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 261, Short.MAX_VALUE)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(signatureLabel)
                    .add(signatureField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(versionLabel)
                    .add(versionField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(attributesSectionLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(hardwareLockBox)
                    .add(hardwareLockLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(volumeUnmountedBox)
                    .add(volumeUnmountedLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(sparedBlocksBox)
                    .add(sparedBlocksLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(noCacheBox)
                    .add(noCacheLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(volumeInconsistentBox)
                    .add(volumeInconsistentLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(idsReusedBox)
                    .add(idsReusedLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(journaledBox)
                    .add(journaledLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(softwareLockBox)
                    .add(softwareLockLabel))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(lastMountedVersionLabel)
                    .add(lastMountedVersionField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(journalInfoBlockLabel)
                    .add(journalInfoBlockField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(createDateLabel)
                    .add(createDateField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(modifyDateLabel)
                    .add(modifyDateField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(backupDateLabel)
                    .add(backupDateField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(checkedDateLabel)
                    .add(checkedDateField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(fileCountLabel)
                    .add(fileCountField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(folderCountLabel)
                    .add(folderCountField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(blockSizeLabel)
                    .add(blockSizeField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(totalBlocksLabel)
                    .add(totalBlocksField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(freeBlocksLabel)
                    .add(freeBlocksField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(nextAllocationLabel)
                    .add(nextAllocationField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(rsrcClumpSizeLabel)
                    .add(rsrcClumpSizeField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(dataClumpSizeLabel)
                    .add(dataClumpSizeField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(nextCatalogIDLabel)
                    .add(nextCatalogIDField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(writeCountLabel)
                    .add(writeCountField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(encodingsBitmapLabel)
                    .add(encodingsBitmapField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(finderInfoSectionLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(finderInfo1Label)
                    .add(finderInfo1Field, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(finderInfo2Label)
                    .add(finderInfo2Field, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(finderInfo3Label)
                    .add(finderInfo3Field, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(finderInfo4Label)
                    .add(finderInfo4Field, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(finderInfo5Label)
                    .add(finderInfo5Field, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(finderInfo6Label)
                    .add(finderInfo6Field, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(finderInfo78Label)
                    .add(finderInfo78Field, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(allocationFileSectionLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(allocationFileLogicalSizeLabel)
                    .add(allocationFileLogicalSizeField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(allocationFileClumpSizeLabel)
                    .add(allocationFileClumpSizeField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(allocationFileTotalBlocksLabel)
                    .add(allocationFileTotalBlocksField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(allocationFileBasicExtentCountLabel)
                    .add(allocationFileBasicExtentCountField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(extentsFileSectionLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(extentsFileLogicalSizeLabel)
                    .add(extentsFileLogicalSizeField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(extentsFileClumpSizeLabel)
                    .add(extentsFileClumpSizeField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(extentsFileTotalBlocksLabel)
                    .add(extentsFileTotalBlocksField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(extentsFileBasicExtentCountLabel)
                    .add(extentsFileBasicExtentCountField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(catalogFileSectionLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(catalogFileLogicalSizeLabel)
                    .add(catalogFileLogicalSizeField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(catalogFileClumpSizeLabel)
                    .add(catalogFileClumpSizeField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(catalogFileTotalBlocksLabel)
                    .add(catalogFileTotalBlocksField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(catalogFileBasicExtentCountLabel)
                    .add(catalogFileBasicExtentCountField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(attributesFileSectionLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(attributesFileLogicalSizeLabel)
                    .add(attributesFileLogicalSizeField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(attributesFileClumpSizeLabel)
                    .add(attributesFileClumpSizeField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(attributesFileTotalBlocksLabel)
                    .add(attributesFileTotalBlocksField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(attributesFileBasicExtentCountLabel)
                    .add(attributesFileBasicExtentCountField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(startupFileSectionLabel)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(startupFileLogicalSizeLabel)
                    .add(startupFileLogicalSizeField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(startupFileClumpSizeLabel)
                    .add(startupFileClumpSizeField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(startupFileTotalBlocksLabel)
                    .add(startupFileTotalBlocksField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(startupFileBasicExtentCountLabel)
                    .add(startupFileBasicExtentCountField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField allocationFileBasicExtentCountField;
    private javax.swing.JLabel allocationFileBasicExtentCountLabel;
    private javax.swing.JTextField allocationFileClumpSizeField;
    private javax.swing.JLabel allocationFileClumpSizeLabel;
    private javax.swing.JTextField allocationFileLogicalSizeField;
    private javax.swing.JLabel allocationFileLogicalSizeLabel;
    private javax.swing.JLabel allocationFileSectionLabel;
    private javax.swing.JTextField allocationFileTotalBlocksField;
    private javax.swing.JLabel allocationFileTotalBlocksLabel;
    private javax.swing.JTextField attributesFileBasicExtentCountField;
    private javax.swing.JLabel attributesFileBasicExtentCountLabel;
    private javax.swing.JTextField attributesFileClumpSizeField;
    private javax.swing.JLabel attributesFileClumpSizeLabel;
    private javax.swing.JTextField attributesFileLogicalSizeField;
    private javax.swing.JLabel attributesFileLogicalSizeLabel;
    private javax.swing.JLabel attributesFileSectionLabel;
    private javax.swing.JTextField attributesFileTotalBlocksField;
    private javax.swing.JLabel attributesFileTotalBlocksLabel;
    private javax.swing.JLabel attributesSectionLabel;
    private javax.swing.JTextField backupDateField;
    private javax.swing.JLabel backupDateLabel;
    private javax.swing.JTextField blockSizeField;
    private javax.swing.JLabel blockSizeLabel;
    private javax.swing.JTextField catalogFileBasicExtentCountField;
    private javax.swing.JLabel catalogFileBasicExtentCountLabel;
    private javax.swing.JTextField catalogFileClumpSizeField;
    private javax.swing.JLabel catalogFileClumpSizeLabel;
    private javax.swing.JTextField catalogFileLogicalSizeField;
    private javax.swing.JLabel catalogFileLogicalSizeLabel;
    private javax.swing.JLabel catalogFileSectionLabel;
    private javax.swing.JTextField catalogFileTotalBlocksField;
    private javax.swing.JLabel catalogFileTotalBlocksLabel;
    private javax.swing.JTextField checkedDateField;
    private javax.swing.JLabel checkedDateLabel;
    private javax.swing.JTextField createDateField;
    private javax.swing.JLabel createDateLabel;
    private javax.swing.JTextField dataClumpSizeField;
    private javax.swing.JLabel dataClumpSizeLabel;
    private javax.swing.JTextField encodingsBitmapField;
    private javax.swing.JLabel encodingsBitmapLabel;
    private javax.swing.JTextField extentsFileBasicExtentCountField;
    private javax.swing.JLabel extentsFileBasicExtentCountLabel;
    private javax.swing.JTextField extentsFileClumpSizeField;
    private javax.swing.JLabel extentsFileClumpSizeLabel;
    private javax.swing.JTextField extentsFileLogicalSizeField;
    private javax.swing.JLabel extentsFileLogicalSizeLabel;
    private javax.swing.JLabel extentsFileSectionLabel;
    private javax.swing.JTextField extentsFileTotalBlocksField;
    private javax.swing.JLabel extentsFileTotalBlocksLabel;
    private javax.swing.JTextField fileCountField;
    private javax.swing.JLabel fileCountLabel;
    private javax.swing.JTextField finderInfo1Field;
    private javax.swing.JLabel finderInfo1Label;
    private javax.swing.JTextField finderInfo2Field;
    private javax.swing.JLabel finderInfo2Label;
    private javax.swing.JTextField finderInfo3Field;
    private javax.swing.JLabel finderInfo3Label;
    private javax.swing.JTextField finderInfo4Field;
    private javax.swing.JLabel finderInfo4Label;
    private javax.swing.JTextField finderInfo5Field;
    private javax.swing.JLabel finderInfo5Label;
    private javax.swing.JTextField finderInfo6Field;
    private javax.swing.JLabel finderInfo6Label;
    private javax.swing.JTextField finderInfo78Field;
    private javax.swing.JLabel finderInfo78Label;
    private javax.swing.JLabel finderInfoSectionLabel;
    private javax.swing.JTextField folderCountField;
    private javax.swing.JLabel folderCountLabel;
    private javax.swing.JTextField freeBlocksField;
    private javax.swing.JLabel freeBlocksLabel;
    private javax.swing.JCheckBox hardwareLockBox;
    private javax.swing.JLabel hardwareLockLabel;
    private javax.swing.JCheckBox idsReusedBox;
    private javax.swing.JLabel idsReusedLabel;
    private javax.swing.JTextField journalInfoBlockField;
    private javax.swing.JLabel journalInfoBlockLabel;
    private javax.swing.JCheckBox journaledBox;
    private javax.swing.JLabel journaledLabel;
    private javax.swing.JTextField lastMountedVersionField;
    private javax.swing.JLabel lastMountedVersionLabel;
    private javax.swing.JTextField modifyDateField;
    private javax.swing.JLabel modifyDateLabel;
    private javax.swing.JTextField nextAllocationField;
    private javax.swing.JLabel nextAllocationLabel;
    private javax.swing.JTextField nextCatalogIDField;
    private javax.swing.JLabel nextCatalogIDLabel;
    private javax.swing.JCheckBox noCacheBox;
    private javax.swing.JLabel noCacheLabel;
    private javax.swing.JTextField rsrcClumpSizeField;
    private javax.swing.JLabel rsrcClumpSizeLabel;
    private javax.swing.JTextField signatureField;
    private javax.swing.JLabel signatureLabel;
    private javax.swing.JCheckBox softwareLockBox;
    private javax.swing.JLabel softwareLockLabel;
    private javax.swing.JCheckBox sparedBlocksBox;
    private javax.swing.JLabel sparedBlocksLabel;
    private javax.swing.JTextField startupFileBasicExtentCountField;
    private javax.swing.JLabel startupFileBasicExtentCountLabel;
    private javax.swing.JTextField startupFileClumpSizeField;
    private javax.swing.JLabel startupFileClumpSizeLabel;
    private javax.swing.JTextField startupFileLogicalSizeField;
    private javax.swing.JLabel startupFileLogicalSizeLabel;
    private javax.swing.JLabel startupFileSectionLabel;
    private javax.swing.JTextField startupFileTotalBlocksField;
    private javax.swing.JLabel startupFileTotalBlocksLabel;
    private javax.swing.JTextField totalBlocksField;
    private javax.swing.JLabel totalBlocksLabel;
    private javax.swing.JTextField versionField;
    private javax.swing.JLabel versionLabel;
    private javax.swing.JCheckBox volumeInconsistentBox;
    private javax.swing.JLabel volumeInconsistentLabel;
    private javax.swing.JCheckBox volumeUnmountedBox;
    private javax.swing.JLabel volumeUnmountedLabel;
    private javax.swing.JTextField writeCountField;
    private javax.swing.JLabel writeCountLabel;
    // End of variables declaration//GEN-END:variables
    
}
