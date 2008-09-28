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
 * FolderInfoPanel.java
 *
 * Created on den 16 mars 2007, 07:37
 */
package org.catacombae.hfsexplorer.gui;

import org.catacombae.hfsexplorer.types.hfsplus.HFSPlusCatalogFolder;
import org.catacombae.hfsexplorer.types.hfsplus.HFSPlusBSDInfo;
import org.catacombae.hfsexplorer.types.finder.ExtendedFolderInfo;
import org.catacombae.hfsexplorer.types.finder.FolderInfo;
import org.catacombae.hfsexplorer.types.*;
import org.catacombae.hfsexplorer.Util;
import java.awt.Color;
import java.text.DateFormat;

/**
 *
 * @author  erik
 */
public class FolderInfoPanel extends javax.swing.JPanel {

    private final DateFormat dti = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
    private Boolean originalOpaqueness = null;
    private Color originalColor = null;

    /** Creates new form FolderInfoPanel */
    public FolderInfoPanel() {
        initComponents();
    }

    public void setFields(HFSPlusCatalogFolder cf) {
        recordTypeField.setText("0x" + Util.toHexStringBE(cf.getRecordType()));
        flagsField.setText("0x" + Util.toHexStringBE(cf.getFlags()));
        valenceField.setText("" + cf.getValence());
        folderIDField.setText(cf.getFolderID().toString());
        createDateField.setText(dti.format(cf.getCreateDateAsDate()));
        contentModifyDateField.setText(dti.format(cf.getContentModDateAsDate()));
        attributesModifyDateField.setText(dti.format(cf.getAttributeModDateAsDate()));
        accessDateField.setText(dti.format(cf.getAccessDateAsDate()));
        backupDateField.setText(dti.format(cf.getBackupDateAsDate()));

        HFSPlusBSDInfo bi = cf.getPermissions();
        permissionsOwnerIDField.setText("" + bi.getOwnerID());
        permissionsGroupIDField.setText("" + bi.getGroupID());
        permissionsAdminFlagsArchivedBox.setSelected(bi.getAdminArchivedFlag());
        permissionsAdminFlagsImmutableBox.setSelected(bi.getAdminImmutableFlag());
        permissionsAdminFlagsAppendBox.setSelected(bi.getAdminAppendFlag());
        permissionsOwnerFlagsNodumpBox.setSelected(bi.getOwnerNodumpFlag());
        permissionsOwnerFlagsImmutableBox.setSelected(bi.getOwnerImmutableFlag());
        permissionsOwnerFlagsAppendBox.setSelected(bi.getOwnerAppendFlag());
        permissionsOwnerFlagsOpaqueBox.setSelected(bi.getOwnerOpaqueFlag());
        permissionsFileModeField.setText(bi.getFileModeString());
        permissionsFileModeSUIDBox.setSelected(bi.getFileModeSetUserID());
        permissionsFileModeSGIDBox.setSelected(bi.getFileModeSetGroupID());
        permissionsFileModeSTXTBox.setSelected(bi.getFileModeSticky());
        permissionsSpecialField.setText("0x" + Util.toHexStringBE(bi.getSpecial()));

        FolderInfo ui = cf.getUserInfo();
        userInfoWindowBoundsField.setText(ui.getWindowBounds().toString());
        userInfoFinderFlagsIsOnDeskBox.setSelected(ui.getFinderFlagIsOnDesk());
        int[] rgb = ui.getFinderFlagColorRGB();
        if(originalOpaqueness == null)
            originalOpaqueness = userInfoFinderFlagsColorField.isOpaque();
        if(originalColor == null)
            originalColor = userInfoFinderFlagsColorField.getBackground();
        if(rgb != null) {
            userInfoFinderFlagsColorField.setOpaque(true);
            userInfoFinderFlagsColorField.setBackground(new Color(rgb[0], rgb[1], rgb[2]));
        }
        else {
            userInfoFinderFlagsColorField.setOpaque(originalOpaqueness);
            userInfoFinderFlagsColorField.setBackground(originalColor);
        }
        userInfoFinderFlagsColorField.setText("" + ui.getFinderFlagColor());
        userInfoFinderFlagsIsSharedBox.setSelected(ui.getFinderFlagIsShared());
        userInfoFinderFlagsHasNoINITsBox.setSelected(ui.getFinderFlagHasNoINITs());
        userInfoFinderFlagsHasBeenInitedBox.setSelected(ui.getFinderFlagHasBeenInited());
        userInfoFinderFlagsHasCustomIconBox.setSelected(ui.getFinderFlagHasCustomIcon());
        userInfoFinderFlagsIsStationeryBox.setSelected(ui.getFinderFlagIsStationery());
        userInfoFinderFlagsNameLockedBox.setSelected(ui.getFinderFlagNameLocked());
        userInfoFinderFlagsHasBundleBox.setSelected(ui.getFinderFlagHasBundle());
        userInfoFinderFlagsIsInvisibleBox.setSelected(ui.getFinderFlagIsInvisible());
        userInfoFinderFlagsIsAliasBox.setSelected(ui.getFinderFlagIsAlias());
        userInfoLocationField.setText(ui.getLocation().toString());
        userInfoReservedField.setText("0x" + Util.toHexStringBE(ui.getReservedField()));

        ExtendedFolderInfo ei = cf.getFinderInfo();
        finderInfoScrollPositionField.setText(ei.getScrollPosition().toString());
        finderInfoReserved1Field.setText("0x" + Util.toHexStringBE(ei.getReserved1()));
        finderInfoExtendedFinderFlagsExtendedFlagsAreInvalidBox.setSelected(ei.getExtendedFinderFlagExtendedFlagsAreInvalid());
        finderInfoExtendedFinderFlagsExtendedFlagHasCustomBadgeBox.setSelected(ei.getExtendedFinderFlagExtendedFlagHasCustomBadge());
        finderInfoExtendedFinderFlagsExtendedFlagHasRoutingInfoBox.setSelected(ei.getExtendedFinderFlagExtendedFlagHasRoutingInfo());
        finderInfoReserved2Field.setText("0x" + Util.toHexStringBE(ei.getReserved2()));
        finderInfoPutAwayFolderIDField.setText("" + ei.getPutAwayFolderID());

        textEncodingField.setText("" + cf.getTextEncoding());
        reservedField.setText("0x" + Util.toHexStringBE(cf.getReserved()));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        folderIDField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        permissionsAdminFlagsArchivedBox = new javax.swing.JCheckBox();
        permissionsAdminFlagsImmutableBox = new javax.swing.JCheckBox();
        permissionsAdminFlagsAppendBox = new javax.swing.JCheckBox();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        permissionsOwnerFlagsNodumpBox = new javax.swing.JCheckBox();
        permissionsOwnerFlagsImmutableBox = new javax.swing.JCheckBox();
        permissionsOwnerFlagsAppendBox = new javax.swing.JCheckBox();
        permissionsOwnerFlagsOpaqueBox = new javax.swing.JCheckBox();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        permissionsFileModeField = new javax.swing.JTextField();
        permissionsFileModeSUIDBox = new javax.swing.JCheckBox();
        permissionsFileModeSGIDBox = new javax.swing.JCheckBox();
        permissionsFileModeSTXTBox = new javax.swing.JCheckBox();
        jLabel23 = new javax.swing.JLabel();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        permissionsSpecialField = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        userInfoFinderFlagsIsOnDeskBox = new javax.swing.JCheckBox();
        userInfoFinderFlagsIsSharedBox = new javax.swing.JCheckBox();
        userInfoFinderFlagsHasNoINITsBox = new javax.swing.JCheckBox();
        userInfoFinderFlagsHasBeenInitedBox = new javax.swing.JCheckBox();
        userInfoFinderFlagsHasCustomIconBox = new javax.swing.JCheckBox();
        userInfoFinderFlagsIsStationeryBox = new javax.swing.JCheckBox();
        userInfoFinderFlagsNameLockedBox = new javax.swing.JCheckBox();
        userInfoFinderFlagsHasBundleBox = new javax.swing.JCheckBox();
        userInfoFinderFlagsIsInvisibleBox = new javax.swing.JCheckBox();
        userInfoFinderFlagsIsAliasBox = new javax.swing.JCheckBox();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        userInfoFinderFlagsColorField = new javax.swing.JTextField();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        finderInfoScrollPositionField = new javax.swing.JTextField();
        jLabel46 = new javax.swing.JLabel();
        finderInfoExtendedFinderFlagsExtendedFlagsAreInvalidBox = new javax.swing.JCheckBox();
        finderInfoExtendedFinderFlagsExtendedFlagHasCustomBadgeBox = new javax.swing.JCheckBox();
        finderInfoExtendedFinderFlagsExtendedFlagHasRoutingInfoBox = new javax.swing.JCheckBox();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        finderInfoReserved2Field = new javax.swing.JTextField();
        jLabel51 = new javax.swing.JLabel();
        finderInfoPutAwayFolderIDField = new javax.swing.JTextField();
        jLabel52 = new javax.swing.JLabel();
        textEncodingField = new javax.swing.JTextField();
        jLabel53 = new javax.swing.JLabel();
        reservedField = new javax.swing.JTextField();
        userInfoLocationField = new javax.swing.JTextField();
        userInfoReservedField = new javax.swing.JTextField();
        permissionsGroupIDField = new javax.swing.JTextField();
        permissionsOwnerIDField = new javax.swing.JTextField();
        backupDateField = new javax.swing.JTextField();
        accessDateField = new javax.swing.JTextField();
        attributesModifyDateField = new javax.swing.JTextField();
        contentModifyDateField = new javax.swing.JTextField();
        createDateField = new javax.swing.JTextField();
        jLabel62 = new javax.swing.JLabel();
        recordTypeField = new javax.swing.JTextField();
        jLabel63 = new javax.swing.JLabel();
        valenceField = new javax.swing.JTextField();
        flagsField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        userInfoWindowBoundsField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        finderInfoReserved1Field = new javax.swing.JTextField();

        jLabel1.setText("Flags:");

        jLabel4.setText("Folder ID:");

        folderIDField.setEditable(false);
        folderIDField.setText("12991");
        folderIDField.setBorder(null);
        folderIDField.setOpaque(false);

        jLabel5.setText("Created:");

        jLabel6.setText("Content modified:");

        jLabel7.setText("Attributes modified:");

        jLabel8.setText("Accessed:");

        jLabel9.setText("Backuped:");

        jLabel10.setText("POSIX permissions:");

        jLabel11.setText("Owner ID:");

        jLabel12.setText("Group ID:");

        jLabel13.setText("Admin flags:");

        permissionsAdminFlagsArchivedBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        permissionsAdminFlagsArchivedBox.setEnabled(false);
        permissionsAdminFlagsArchivedBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        permissionsAdminFlagsImmutableBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        permissionsAdminFlagsImmutableBox.setEnabled(false);
        permissionsAdminFlagsImmutableBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        permissionsAdminFlagsAppendBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        permissionsAdminFlagsAppendBox.setEnabled(false);
        permissionsAdminFlagsAppendBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jLabel14.setText("File has been archived");

        jLabel15.setText("File may not be changed");

        jLabel16.setText("Writes to file may only append");

        jLabel17.setText("Owner flags:");

        permissionsOwnerFlagsNodumpBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        permissionsOwnerFlagsNodumpBox.setEnabled(false);
        permissionsOwnerFlagsNodumpBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        permissionsOwnerFlagsImmutableBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        permissionsOwnerFlagsImmutableBox.setEnabled(false);
        permissionsOwnerFlagsImmutableBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        permissionsOwnerFlagsAppendBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        permissionsOwnerFlagsAppendBox.setEnabled(false);
        permissionsOwnerFlagsAppendBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        permissionsOwnerFlagsOpaqueBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        permissionsOwnerFlagsOpaqueBox.setEnabled(false);
        permissionsOwnerFlagsOpaqueBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jLabel18.setText("Do not dump (back up or archive) this file");

        jLabel19.setText("File may not be changed");

        jLabel20.setText("Writes to file may only append");

        jLabel21.setText("Directory is opaque");

        jLabel22.setText("File mode:");

        permissionsFileModeField.setEditable(false);
        permissionsFileModeField.setText("-rwxr--r--");
        permissionsFileModeField.setBorder(null);
        permissionsFileModeField.setOpaque(false);

        permissionsFileModeSUIDBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        permissionsFileModeSUIDBox.setEnabled(false);
        permissionsFileModeSUIDBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        permissionsFileModeSGIDBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        permissionsFileModeSGIDBox.setEnabled(false);
        permissionsFileModeSGIDBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        permissionsFileModeSTXTBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        permissionsFileModeSTXTBox.setEnabled(false);
        permissionsFileModeSTXTBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jLabel23.setText("Set user ID on execution");

        jLabel24.setText("Set group ID on execution");

        jLabel25.setText("Sticky bit");

        jLabel26.setText("Special:");

        permissionsSpecialField.setEditable(false);
        permissionsSpecialField.setText("0xFFFFFFFF");
        permissionsSpecialField.setBorder(null);
        permissionsSpecialField.setOpaque(false);

        jLabel27.setText("User info:");

        jLabel30.setText("Finder flags:");

        jLabel31.setText("Location:");

        jLabel32.setText("Reserved:");

        userInfoFinderFlagsIsOnDeskBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        userInfoFinderFlagsIsOnDeskBox.setEnabled(false);
        userInfoFinderFlagsIsOnDeskBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        userInfoFinderFlagsIsSharedBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        userInfoFinderFlagsIsSharedBox.setEnabled(false);
        userInfoFinderFlagsIsSharedBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        userInfoFinderFlagsHasNoINITsBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        userInfoFinderFlagsHasNoINITsBox.setEnabled(false);
        userInfoFinderFlagsHasNoINITsBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        userInfoFinderFlagsHasBeenInitedBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        userInfoFinderFlagsHasBeenInitedBox.setEnabled(false);
        userInfoFinderFlagsHasBeenInitedBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        userInfoFinderFlagsHasCustomIconBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        userInfoFinderFlagsHasCustomIconBox.setEnabled(false);
        userInfoFinderFlagsHasCustomIconBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        userInfoFinderFlagsIsStationeryBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        userInfoFinderFlagsIsStationeryBox.setEnabled(false);
        userInfoFinderFlagsIsStationeryBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        userInfoFinderFlagsNameLockedBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        userInfoFinderFlagsNameLockedBox.setEnabled(false);
        userInfoFinderFlagsNameLockedBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        userInfoFinderFlagsHasBundleBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        userInfoFinderFlagsHasBundleBox.setEnabled(false);
        userInfoFinderFlagsHasBundleBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        userInfoFinderFlagsIsInvisibleBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        userInfoFinderFlagsIsInvisibleBox.setEnabled(false);
        userInfoFinderFlagsIsInvisibleBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        userInfoFinderFlagsIsAliasBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        userInfoFinderFlagsIsAliasBox.setEnabled(false);
        userInfoFinderFlagsIsAliasBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jLabel33.setText("kIsOnDesk");

        jLabel34.setText("kColor:");

        jLabel35.setText("kIsShared");

        jLabel36.setText("kHasNoINITs");

        jLabel37.setText("kHasBeenInited");

        jLabel38.setText("kHasCustomIcon");

        jLabel39.setText("kIsStationery");

        jLabel40.setText("kNameLocked");

        jLabel41.setText("kHasBundle");

        jLabel42.setText("kIsInvisible");

        jLabel43.setText("kIsAlias");

        userInfoFinderFlagsColorField.setColumns(4);
        userInfoFinderFlagsColorField.setEditable(false);
        userInfoFinderFlagsColorField.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        userInfoFinderFlagsColorField.setText("0888");
        userInfoFinderFlagsColorField.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        userInfoFinderFlagsColorField.setOpaque(false);

        jLabel44.setText("Finder info:");

        jLabel45.setText("Scroll position:");

        finderInfoScrollPositionField.setColumns(19);
        finderInfoScrollPositionField.setEditable(false);
        finderInfoScrollPositionField.setText("(129, 290)");
        finderInfoScrollPositionField.setBorder(null);
        finderInfoScrollPositionField.setOpaque(false);

        jLabel46.setText("Extended finder flags:");

        finderInfoExtendedFinderFlagsExtendedFlagsAreInvalidBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        finderInfoExtendedFinderFlagsExtendedFlagsAreInvalidBox.setEnabled(false);
        finderInfoExtendedFinderFlagsExtendedFlagsAreInvalidBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        finderInfoExtendedFinderFlagsExtendedFlagHasCustomBadgeBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        finderInfoExtendedFinderFlagsExtendedFlagHasCustomBadgeBox.setEnabled(false);
        finderInfoExtendedFinderFlagsExtendedFlagHasCustomBadgeBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        finderInfoExtendedFinderFlagsExtendedFlagHasRoutingInfoBox.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        finderInfoExtendedFinderFlagsExtendedFlagHasRoutingInfoBox.setEnabled(false);
        finderInfoExtendedFinderFlagsExtendedFlagHasRoutingInfoBox.setMargin(new java.awt.Insets(0, 0, 0, 0));

        jLabel47.setText("kExtendedFlagsAreInvalid");

        jLabel48.setText("kExtendedFlagHasCustomBadge");

        jLabel49.setText("kExtendedFlagHasRoutingInfo");

        jLabel50.setText("Reserved2: ");

        finderInfoReserved2Field.setColumns(4);
        finderInfoReserved2Field.setEditable(false);
        finderInfoReserved2Field.setText("FFFF");
        finderInfoReserved2Field.setBorder(null);
        finderInfoReserved2Field.setOpaque(false);

        jLabel51.setText("Put away folder ID:");

        finderInfoPutAwayFolderIDField.setColumns(11);
        finderInfoPutAwayFolderIDField.setEditable(false);
        finderInfoPutAwayFolderIDField.setText("-1928800000");
        finderInfoPutAwayFolderIDField.setBorder(null);
        finderInfoPutAwayFolderIDField.setOpaque(false);

        jLabel52.setText("Text encoding:");

        textEncodingField.setColumns(4);
        textEncodingField.setEditable(false);
        textEncodingField.setText("UTF1");
        textEncodingField.setBorder(null);
        textEncodingField.setOpaque(false);

        jLabel53.setText("Reserved:");

        reservedField.setColumns(8);
        reservedField.setEditable(false);
        reservedField.setText("FFFFFFFF");
        reservedField.setBorder(null);
        reservedField.setOpaque(false);

        userInfoLocationField.setEditable(false);
        userInfoLocationField.setText("jTextField16");
        userInfoLocationField.setBorder(null);
        userInfoLocationField.setOpaque(false);

        userInfoReservedField.setEditable(false);
        userInfoReservedField.setText("jTextField17");
        userInfoReservedField.setBorder(null);
        userInfoReservedField.setOpaque(false);

        permissionsGroupIDField.setEditable(false);
        permissionsGroupIDField.setText("jTextField20");
        permissionsGroupIDField.setBorder(null);
        permissionsGroupIDField.setOpaque(false);

        permissionsOwnerIDField.setEditable(false);
        permissionsOwnerIDField.setText("jTextField21");
        permissionsOwnerIDField.setBorder(null);
        permissionsOwnerIDField.setOpaque(false);

        backupDateField.setEditable(false);
        backupDateField.setText("jTextField22");
        backupDateField.setBorder(null);
        backupDateField.setOpaque(false);

        accessDateField.setEditable(false);
        accessDateField.setText("jTextField23");
        accessDateField.setBorder(null);
        accessDateField.setOpaque(false);

        attributesModifyDateField.setEditable(false);
        attributesModifyDateField.setText("jTextField24");
        attributesModifyDateField.setBorder(null);
        attributesModifyDateField.setOpaque(false);

        contentModifyDateField.setEditable(false);
        contentModifyDateField.setText("jTextField25");
        contentModifyDateField.setBorder(null);
        contentModifyDateField.setOpaque(false);

        createDateField.setEditable(false);
        createDateField.setText("jTextField26");
        createDateField.setBorder(null);
        createDateField.setOpaque(false);

        jLabel62.setText("Record type:");

        recordTypeField.setColumns(4);
        recordTypeField.setEditable(false);
        recordTypeField.setText("FFFF");
        recordTypeField.setBorder(null);
        recordTypeField.setOpaque(false);

        jLabel63.setText("Valence:");

        valenceField.setColumns(8);
        valenceField.setEditable(false);
        valenceField.setText("3919929");
        valenceField.setBorder(null);
        valenceField.setOpaque(false);

        flagsField.setEditable(false);
        flagsField.setText("jTextField1");
        flagsField.setBorder(null);
        flagsField.setOpaque(false);

        jLabel2.setText("Window bounds:");

        userInfoWindowBoundsField.setEditable(false);
        userInfoWindowBoundsField.setText("800x600");
        userInfoWindowBoundsField.setBorder(null);
        userInfoWindowBoundsField.setOpaque(false);

        jLabel3.setText("Reserved1:");

        finderInfoReserved1Field.setEditable(false);
        finderInfoReserved1Field.setText("jTextField1");
        finderInfoReserved1Field.setBorder(null);
        finderInfoReserved1Field.setOpaque(false);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(layout.createSequentialGroup()
                        .add(jLabel62)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(recordTypeField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(jLabel1)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(flagsField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 321, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(24, 24, 24)
                        .add(jLabel2)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(userInfoWindowBoundsField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 246, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(jLabel63)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(valenceField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 309, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(jLabel4)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(folderIDField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 302, Short.MAX_VALUE))
                    .add(jLabel10)
                    .add(layout.createSequentialGroup()
                        .add(24, 24, 24)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel13)
                            .add(layout.createSequentialGroup()
                                .add(21, 21, 21)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(layout.createSequentialGroup()
                                        .add(permissionsAdminFlagsImmutableBox)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jLabel15))
                                    .add(layout.createSequentialGroup()
                                        .add(permissionsAdminFlagsArchivedBox)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jLabel14))
                                    .add(layout.createSequentialGroup()
                                        .add(permissionsAdminFlagsAppendBox)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jLabel16))))
                            .add(jLabel17)
                            .add(layout.createSequentialGroup()
                                .add(21, 21, 21)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(layout.createSequentialGroup()
                                        .add(permissionsOwnerFlagsImmutableBox)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jLabel19))
                                    .add(layout.createSequentialGroup()
                                        .add(permissionsOwnerFlagsNodumpBox)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jLabel18))
                                    .add(layout.createSequentialGroup()
                                        .add(permissionsOwnerFlagsAppendBox)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jLabel20))
                                    .add(layout.createSequentialGroup()
                                        .add(permissionsOwnerFlagsOpaqueBox)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jLabel21))))
                            .add(layout.createSequentialGroup()
                                .add(jLabel22)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(permissionsFileModeField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 277, Short.MAX_VALUE))
                            .add(layout.createSequentialGroup()
                                .add(21, 21, 21)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(layout.createSequentialGroup()
                                        .add(permissionsFileModeSGIDBox)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jLabel24))
                                    .add(layout.createSequentialGroup()
                                        .add(permissionsFileModeSUIDBox)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jLabel23))
                                    .add(layout.createSequentialGroup()
                                        .add(permissionsFileModeSTXTBox)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jLabel25))))
                            .add(layout.createSequentialGroup()
                                .add(jLabel26)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(permissionsSpecialField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 289, Short.MAX_VALUE))
                            .add(layout.createSequentialGroup()
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(jLabel11)
                                    .add(jLabel12))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(permissionsGroupIDField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                                    .add(permissionsOwnerIDField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)))))
                    .add(jLabel27)
                    .add(layout.createSequentialGroup()
                        .add(24, 24, 24)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel30)
                            .add(layout.createSequentialGroup()
                                .add(21, 21, 21)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                                    .add(layout.createSequentialGroup()
                                        .add(userInfoFinderFlagsIsOnDeskBox)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jLabel33))
                                    .add(layout.createSequentialGroup()
                                        .add(userInfoFinderFlagsIsSharedBox)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jLabel35))
                                    .add(layout.createSequentialGroup()
                                        .add(userInfoFinderFlagsHasNoINITsBox)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jLabel36))
                                    .add(layout.createSequentialGroup()
                                        .add(userInfoFinderFlagsHasBeenInitedBox)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jLabel37))
                                    .add(layout.createSequentialGroup()
                                        .add(userInfoFinderFlagsHasCustomIconBox)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jLabel38))
                                    .add(layout.createSequentialGroup()
                                        .add(userInfoFinderFlagsIsStationeryBox)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jLabel39))
                                    .add(layout.createSequentialGroup()
                                        .add(userInfoFinderFlagsNameLockedBox)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jLabel40))
                                    .add(layout.createSequentialGroup()
                                        .add(userInfoFinderFlagsHasBundleBox)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jLabel41))
                                    .add(layout.createSequentialGroup()
                                        .add(userInfoFinderFlagsIsInvisibleBox)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jLabel42))
                                    .add(layout.createSequentialGroup()
                                        .add(userInfoFinderFlagsIsAliasBox)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jLabel43))
                                    .add(layout.createSequentialGroup()
                                        .add(jLabel34)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(userInfoFinderFlagsColorField)))
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 212, Short.MAX_VALUE))
                            .add(layout.createSequentialGroup()
                                .add(jLabel31)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(userInfoLocationField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 282, Short.MAX_VALUE))
                            .add(layout.createSequentialGroup()
                                .add(jLabel32)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(userInfoReservedField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE))))
                    .add(jLabel44)
                    .add(layout.createSequentialGroup()
                        .add(24, 24, 24)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(layout.createSequentialGroup()
                                .add(jLabel3)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(finderInfoReserved1Field, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 270, Short.MAX_VALUE))
                            .add(layout.createSequentialGroup()
                                .add(jLabel45)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(finderInfoScrollPositionField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 257, Short.MAX_VALUE))))
                    .add(layout.createSequentialGroup()
                        .add(24, 24, 24)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel46)
                            .add(layout.createSequentialGroup()
                                .add(21, 21, 21)
                                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                                    .add(layout.createSequentialGroup()
                                        .add(finderInfoExtendedFinderFlagsExtendedFlagHasCustomBadgeBox)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jLabel48))
                                    .add(layout.createSequentialGroup()
                                        .add(finderInfoExtendedFinderFlagsExtendedFlagsAreInvalidBox)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jLabel47))
                                    .add(layout.createSequentialGroup()
                                        .add(finderInfoExtendedFinderFlagsExtendedFlagHasRoutingInfoBox)
                                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                        .add(jLabel49))))
                            .add(layout.createSequentialGroup()
                                .add(jLabel50)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(finderInfoReserved2Field, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE))
                            .add(layout.createSequentialGroup()
                                .add(jLabel51)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(finderInfoPutAwayFolderIDField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 232, Short.MAX_VALUE))))
                    .add(layout.createSequentialGroup()
                        .add(jLabel52)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(textEncodingField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(jLabel53)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(reservedField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE))
                    .add(layout.createSequentialGroup()
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(jLabel7)
                            .add(jLabel6)
                            .add(jLabel5)
                            .add(jLabel8)
                            .add(jLabel9))
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(backupDateField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                            .add(accessDateField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                            .add(createDateField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                            .add(contentModifyDateField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                            .add(attributesModifyDateField, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(layout.createSequentialGroup()
                .addContainerGap()
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel62)
                    .add(recordTypeField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel1)
                    .add(flagsField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel63)
                    .add(valenceField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel4)
                    .add(folderIDField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5)
                    .add(createDateField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel6)
                    .add(contentModifyDateField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel7)
                    .add(attributesModifyDateField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel8)
                    .add(accessDateField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel9)
                    .add(backupDateField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel10)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel11)
                    .add(permissionsOwnerIDField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel12)
                    .add(permissionsGroupIDField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel13)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(permissionsAdminFlagsArchivedBox)
                    .add(jLabel14))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(permissionsAdminFlagsImmutableBox)
                    .add(jLabel15))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(permissionsAdminFlagsAppendBox)
                    .add(jLabel16))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel17)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(permissionsOwnerFlagsNodumpBox)
                    .add(jLabel18))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(permissionsOwnerFlagsImmutableBox)
                    .add(jLabel19))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(permissionsOwnerFlagsAppendBox)
                    .add(jLabel20))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(permissionsOwnerFlagsOpaqueBox)
                    .add(jLabel21))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel22)
                    .add(permissionsFileModeField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(permissionsFileModeSUIDBox)
                    .add(jLabel23))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(permissionsFileModeSGIDBox)
                    .add(jLabel24))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(permissionsFileModeSTXTBox)
                    .add(jLabel25))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel26)
                    .add(permissionsSpecialField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel27)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel2)
                    .add(userInfoWindowBoundsField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel30)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(userInfoFinderFlagsIsOnDeskBox)
                    .add(jLabel33))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel34)
                    .add(userInfoFinderFlagsColorField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(userInfoFinderFlagsIsSharedBox)
                    .add(jLabel35))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(userInfoFinderFlagsHasNoINITsBox)
                    .add(jLabel36))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(userInfoFinderFlagsHasBeenInitedBox)
                    .add(jLabel37))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(userInfoFinderFlagsHasCustomIconBox)
                    .add(jLabel38))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(userInfoFinderFlagsIsStationeryBox)
                    .add(jLabel39))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(userInfoFinderFlagsNameLockedBox)
                    .add(jLabel40))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(userInfoFinderFlagsHasBundleBox)
                    .add(jLabel41))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(userInfoFinderFlagsIsInvisibleBox)
                    .add(jLabel42))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(userInfoFinderFlagsIsAliasBox)
                    .add(jLabel43))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel31)
                    .add(userInfoLocationField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel32)
                    .add(userInfoReservedField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel44)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel45)
                    .add(finderInfoScrollPositionField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(finderInfoReserved1Field, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jLabel46)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(finderInfoExtendedFinderFlagsExtendedFlagsAreInvalidBox)
                    .add(jLabel47))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(finderInfoExtendedFinderFlagsExtendedFlagHasCustomBadgeBox)
                    .add(jLabel48))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(finderInfoExtendedFinderFlagsExtendedFlagHasRoutingInfoBox)
                    .add(jLabel49))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel50)
                    .add(finderInfoReserved2Field, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel51)
                    .add(finderInfoPutAwayFolderIDField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel52)
                    .add(textEncodingField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(layout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel53)
                    .add(reservedField, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField accessDateField;
    private javax.swing.JTextField attributesModifyDateField;
    private javax.swing.JTextField backupDateField;
    private javax.swing.JTextField contentModifyDateField;
    private javax.swing.JTextField createDateField;
    private javax.swing.JCheckBox finderInfoExtendedFinderFlagsExtendedFlagHasCustomBadgeBox;
    private javax.swing.JCheckBox finderInfoExtendedFinderFlagsExtendedFlagHasRoutingInfoBox;
    private javax.swing.JCheckBox finderInfoExtendedFinderFlagsExtendedFlagsAreInvalidBox;
    private javax.swing.JTextField finderInfoPutAwayFolderIDField;
    private javax.swing.JTextField finderInfoReserved1Field;
    private javax.swing.JTextField finderInfoReserved2Field;
    private javax.swing.JTextField finderInfoScrollPositionField;
    private javax.swing.JTextField flagsField;
    private javax.swing.JTextField folderIDField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
    private javax.swing.JLabel jLabel51;
    private javax.swing.JLabel jLabel52;
    private javax.swing.JLabel jLabel53;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel63;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JCheckBox permissionsAdminFlagsAppendBox;
    private javax.swing.JCheckBox permissionsAdminFlagsArchivedBox;
    private javax.swing.JCheckBox permissionsAdminFlagsImmutableBox;
    private javax.swing.JTextField permissionsFileModeField;
    private javax.swing.JCheckBox permissionsFileModeSGIDBox;
    private javax.swing.JCheckBox permissionsFileModeSTXTBox;
    private javax.swing.JCheckBox permissionsFileModeSUIDBox;
    private javax.swing.JTextField permissionsGroupIDField;
    private javax.swing.JCheckBox permissionsOwnerFlagsAppendBox;
    private javax.swing.JCheckBox permissionsOwnerFlagsImmutableBox;
    private javax.swing.JCheckBox permissionsOwnerFlagsNodumpBox;
    private javax.swing.JCheckBox permissionsOwnerFlagsOpaqueBox;
    private javax.swing.JTextField permissionsOwnerIDField;
    private javax.swing.JTextField permissionsSpecialField;
    private javax.swing.JTextField recordTypeField;
    private javax.swing.JTextField reservedField;
    private javax.swing.JTextField textEncodingField;
    private javax.swing.JTextField userInfoFinderFlagsColorField;
    private javax.swing.JCheckBox userInfoFinderFlagsHasBeenInitedBox;
    private javax.swing.JCheckBox userInfoFinderFlagsHasBundleBox;
    private javax.swing.JCheckBox userInfoFinderFlagsHasCustomIconBox;
    private javax.swing.JCheckBox userInfoFinderFlagsHasNoINITsBox;
    private javax.swing.JCheckBox userInfoFinderFlagsIsAliasBox;
    private javax.swing.JCheckBox userInfoFinderFlagsIsInvisibleBox;
    private javax.swing.JCheckBox userInfoFinderFlagsIsOnDeskBox;
    private javax.swing.JCheckBox userInfoFinderFlagsIsSharedBox;
    private javax.swing.JCheckBox userInfoFinderFlagsIsStationeryBox;
    private javax.swing.JCheckBox userInfoFinderFlagsNameLockedBox;
    private javax.swing.JTextField userInfoLocationField;
    private javax.swing.JTextField userInfoReservedField;
    private javax.swing.JTextField userInfoWindowBoundsField;
    private javax.swing.JTextField valenceField;
    // End of variables declaration//GEN-END:variables
}
