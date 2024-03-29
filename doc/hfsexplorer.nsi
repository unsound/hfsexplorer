; Script generated by the HM NIS Edit Script Wizard.

; HM NIS Edit Wizard helper defines
!define PRODUCT_NAME "HFSExplorer"
!define PRODUCT_VERSION "2021.10.9"
!define PRODUCT_PUBLISHER "Catacombae Software"
!define PRODUCT_WEB_SITE "https://www.catacombae.org/"
!define PRODUCT_UNINST_KEY "Software\Microsoft\Windows\CurrentVersion\Uninstall\${PRODUCT_NAME}"
!define PRODUCT_UNINST_ROOT_KEY "HKLM"
!define PRODUCT_STARTMENU_REGVAL "NSIS:StartMenuDir"

SetCompressor bzip2

; MUI 1.67 compatible ------
!include "MUI.nsh"

; MUI Settings
!define MUI_ABORTWARNING
!define MUI_ICON "${NSISDIR}\Contrib\Graphics\Icons\modern-install.ico"
!define MUI_UNICON "${NSISDIR}\Contrib\Graphics\Icons\modern-uninstall.ico"
!define MUI_COMPONENTSPAGE_NODESC

; Welcome page
!insertmacro MUI_PAGE_WELCOME
; License page
!insertmacro MUI_PAGE_LICENSE ".\gpl.txt"
; Components page
!insertmacro MUI_PAGE_COMPONENTS
; Directory page
!insertmacro MUI_PAGE_DIRECTORY
; Start menu page
var ICONS_GROUP
!define MUI_STARTMENUPAGE_NODISABLE
!define MUI_STARTMENUPAGE_DEFAULTFOLDER "HFSExplorer"
!define MUI_STARTMENUPAGE_REGISTRY_ROOT "${PRODUCT_UNINST_ROOT_KEY}"
!define MUI_STARTMENUPAGE_REGISTRY_KEY "${PRODUCT_UNINST_KEY}"
!define MUI_STARTMENUPAGE_REGISTRY_VALUENAME "${PRODUCT_STARTMENU_REGVAL}"
!insertmacro MUI_PAGE_STARTMENU Application $ICONS_GROUP
; Instfiles page
!insertmacro MUI_PAGE_INSTFILES
; Finish page
!insertmacro MUI_PAGE_FINISH

; Uninstaller pages
!insertmacro MUI_UNPAGE_INSTFILES

; Language files
!insertmacro MUI_LANGUAGE "English"

; Reserve files
!insertmacro MUI_RESERVEFILE_INSTALLOPTIONS


; MUI end ------
Function .onInit

  ReadRegStr $R0 HKLM \
  "${PRODUCT_UNINST_KEY}" \
  "UninstallString"
  StrCmp $R0 "" done

  MessageBox MB_OKCANCEL|MB_ICONEXCLAMATION \
  "${PRODUCT_NAME} is already installed. $\n$\nClick `OK` to remove the \
  previous version or `Cancel` to cancel this upgrade." \
  IDOK uninst
  Abort

;Run the uninstaller
uninst:
  ClearErrors
  ExecWait '$R0 _?=$INSTDIR' ;Do not copy the uninstaller to a temp file

  IfErrors no_remove_uninstaller
    ;You can either use Delete /REBOOTOK in the uninstaller or add some code
    ;here to remove the uninstaller. Use a registry key to check
    ;whether the user has chosen to uninstall. If you are using an uninstaller
    ;components page, make sure all sections are uninstalled.
  no_remove_uninstaller:

done:

FunctionEnd

Name "${PRODUCT_NAME} ${PRODUCT_VERSION}"
OutFile "hfsexplorer-current-setup.exe"
InstallDir "$PROGRAMFILES\HFSExplorer"
ShowInstDetails show
ShowUnInstDetails show
;LicenseForceSelection checkbox "Jag Godk�nner"

InstType "Typical"

Section "HFSExplorer" SEC01
  SectionIn RO ; Means that the section is read-only (can't be deselected by the user)
  
  ; Make sure that shell variables like SMPROGRAMS are evaluated in the 'all' context, applying to all users.
  SetShellVarContext all

  SetOutPath "$INSTDIR"
  SetOverwrite ifnewer
  File "..\dist\*.txt"

  SetOutPath "$INSTDIR\bin"
  SetOverwrite ifnewer
  File "..\dist\bin\*"
  
  SetOutPath "$INSTDIR\lib"
  SetOverwrite ifnewer
  File "..\dist\lib\*.jar"
  File "..\dist\lib\*.dll"
  
  SetOutPath "$INSTDIR\res"
  SetOverwrite ifnewer
  File "..\dist\res\*.png"

  SetOutPath "$INSTDIR\doc\html"
  SetOverwrite ifnewer
  File "..\dist\doc\html\*.html"
  
  SetOutPath "$INSTDIR\doc\html\img"
  SetOverwrite ifnewer
  File "..\dist\doc\html\img\*.png"

  ; Shortcuts
  !insertmacro MUI_STARTMENU_WRITE_BEGIN Application
  CreateDirectory "$SMPROGRAMS\$ICONS_GROUP"
  SetOutPath $INSTDIR
  CreateShortCut "$SMPROGRAMS\$ICONS_GROUP\HFSExplorer.lnk" "$INSTDIR\bin\hfsexplorer.exe"
  CreateDirectory "$SMPROGRAMS\$ICONS_GROUP\Tools"
  CreateShortCut "$SMPROGRAMS\$ICONS_GROUP\Tools\Resource Viewer.lnk" "javaw.exe" "-cp lib\hfsx.jar org.catacombae.hfsexplorer.tools.ResourceViewer" "" "" "" "" "An application for displaying the contents of resource forks."
  SetOutPath -
  !insertmacro MUI_STARTMENU_WRITE_END
SectionEnd

Section "Register .dmg file association" SEC02
  SectionIn 1
  
  !define FILE_EXTENSION ".dmg"
  !define PROGRAM_EXTENSION_ID "CatacombaeHFSExplorer.DMGFile"
  !define WINDOWS_DESCRIPTION ".dmg Disk image"
  !define ICON_FILE "$INSTDIR\bin\hfsexplorer.exe,1"
  !define OPEN_COMMAND '$INSTDIR\bin\hfsexplorer.exe "%1"'
  
  !define Index "Line${__LINE__}"
  ; Back up current extension mapping, if existent
  ReadRegStr $1 HKCR "${FILE_EXTENSION}" ""                   ; Read current extension mapping
  StrCmp $1 "" "${Index}-NoBackup"                            ; If there is none, jump to NoBackup
  StrCmp $1 "${PROGRAM_EXTENSION_ID}" "${Index}-NoBackup"     ; If it exists, and is equal to this program's PROGRAM_EXTENSION_ID, also jump to NoBackup
  WriteRegStr HKCR "${FILE_EXTENSION}" "backup_val" $1        ; Else, write its value to a new key, called 'backup_val'
  
  "${Index}-NoBackup:"
  
  ; Write our file extension mapping to registry
  WriteRegStr HKCR "${FILE_EXTENSION}" "" "${PROGRAM_EXTENSION_ID}" ; Write mapping .dmg->CatacombaeHFSExplorer.DMGFile
  ReadRegStr $0 HKCR "${PROGRAM_EXTENSION_ID}" ""                   ; Read current contents of CatacombaeHFSExplorer.DMGFile
  StrCmp $0 "" 0 "${Index}-Skip"                                    ; If the key CatacombaeHFSExplorer.DMGFile does exist, jump to Skip
  WriteRegStr HKCR "${PROGRAM_EXTENSION_ID}" "" "${WINDOWS_DESCRIPTION}"
  WriteRegStr HKCR "${PROGRAM_EXTENSION_ID}\shell" "" "open"
  WriteRegStr HKCR "${PROGRAM_EXTENSION_ID}\DefaultIcon" "" "${ICON_FILE}"
  "${Index}-Skip:"
  WriteRegStr HKCR "${PROGRAM_EXTENSION_ID}\shell\open\command" "" '${OPEN_COMMAND}'
      ;WriteRegStr HKCR "OptionsFile\shell\edit" "" "Edit Options File"
      ;WriteRegStr HKCR "OptionsFile\shell\edit\command" "" '$INSTDIR\execute.exe "%1"'

  System::Call 'Shell32::SHChangeNotify(i 0x8000000, i 0, i 0, i 0)' ; Alert Windows that is has to update its file extension list.
  !undef Index
  !undef FILE_EXTENSION
  !undef PROGRAM_EXTENSION_ID
  !undef WINDOWS_DESCRIPTION
  !undef ICON_FILE
  !undef OPEN_COMMAND
  ; Rest of script
SectionEnd

Section "Register .cdr file association" SEC03
  SectionIn 2
  
  ; Make sure that shell variables like SMPROGRAMS are evaluated in the 'all' context, applying to all users.
  SetShellVarContext all

  !define FILE_EXTENSION ".cdr"
  !define PROGRAM_EXTENSION_ID "CatacombaeHFSExplorer.CDRFile"
  !define WINDOWS_DESCRIPTION ".cdr CD/DVD image"
  !define ICON_FILE "$INSTDIR\bin\hfsexplorer.exe,1"
  !define OPEN_COMMAND '$INSTDIR\bin\hfsexplorer.exe "%1"'

  !define Index "Line${__LINE__}"
  ; Back up current extension mapping, if existent
  ReadRegStr $1 HKCR "${FILE_EXTENSION}" ""                   ; Read current extension mapping
  StrCmp $1 "" "${Index}-NoBackup"                            ; If there is none, jump to NoBackup
  StrCmp $1 "${PROGRAM_EXTENSION_ID}" "${Index}-NoBackup"     ; If it exists, and is equal to this program's PROGRAM_EXTENSION_ID, also jump to NoBackup
  WriteRegStr HKCR "${FILE_EXTENSION}" "backup_val" $1        ; Else, write its value to a new key, called 'backup_val'

  "${Index}-NoBackup:"

  ; Write our file extension mapping to registry
  WriteRegStr HKCR "${FILE_EXTENSION}" "" "${PROGRAM_EXTENSION_ID}" ; Write mapping .dmg->CatacombaeHFSExplorer.DMGFile
  ReadRegStr $0 HKCR "${PROGRAM_EXTENSION_ID}" ""                   ; Read current contents of CatacombaeHFSExplorer.DMGFile
  StrCmp $0 "" 0 "${Index}-Skip"                                    ; If the key CatacombaeHFSExplorer.DMGFile does exist, jump to Skip
  WriteRegStr HKCR "${PROGRAM_EXTENSION_ID}" "" "${WINDOWS_DESCRIPTION}"
  WriteRegStr HKCR "${PROGRAM_EXTENSION_ID}\shell" "" "open"
  WriteRegStr HKCR "${PROGRAM_EXTENSION_ID}\DefaultIcon" "" "${ICON_FILE}"
  "${Index}-Skip:"
  WriteRegStr HKCR "${PROGRAM_EXTENSION_ID}\shell\open\command" "" '${OPEN_COMMAND}'
      ;WriteRegStr HKCR "OptionsFile\shell\edit" "" "Edit Options File"
      ;WriteRegStr HKCR "OptionsFile\shell\edit\command" "" '$INSTDIR\execute.exe "%1"'

  System::Call 'Shell32::SHChangeNotify(i 0x8000000, i 0, i 0, i 0)' ; Alert Windows that is has to update its file extension list.
  !undef Index
  !undef FILE_EXTENSION
  !undef PROGRAM_EXTENSION_ID
  !undef WINDOWS_DESCRIPTION
  !undef ICON_FILE
  !undef OPEN_COMMAND
  ; Rest of script
SectionEnd

Section -AdditionalIcons
  !insertmacro MUI_STARTMENU_WRITE_BEGIN Application

  ; Make sure that shell variables like SMPROGRAMS are evaluated in the 'all' context, applying to all users.
  SetShellVarContext all

  WriteIniStr "$INSTDIR\${PRODUCT_NAME}.url" "InternetShortcut" "URL" "${PRODUCT_WEB_SITE}"
  CreateShortCut "$SMPROGRAMS\$ICONS_GROUP\Developer Web Site.lnk" "$INSTDIR\${PRODUCT_NAME}.url"
  CreateShortCut "$SMPROGRAMS\$ICONS_GROUP\Uninstall.lnk" "$INSTDIR\uninst.exe"
  !insertmacro MUI_STARTMENU_WRITE_END
SectionEnd

Section -Post
  WriteUninstaller "$INSTDIR\uninst.exe"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "DisplayName" "$(^Name)"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "UninstallString" "$INSTDIR\uninst.exe"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "DisplayVersion" "${PRODUCT_VERSION}"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "URLInfoAbout" "${PRODUCT_WEB_SITE}"
  WriteRegStr ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}" "Publisher" "${PRODUCT_PUBLISHER}"
SectionEnd

Function un.onUninstSuccess
  HideWindow
  MessageBox MB_ICONINFORMATION|MB_OK "$(^Name) has been removed from your computer."
FunctionEnd

Function un.onInit
  MessageBox MB_ICONQUESTION|MB_YESNO|MB_DEFBUTTON2 "Are you sure that you want to remove $(^Name) from your computer?" IDYES +2
  Abort
FunctionEnd

Section Uninstall
  ; Make sure that shell variables like SMPROGRAMS are evaluated in the 'all' context, applying to all users.
  SetShellVarContext all

  ; Start of restoration of registry values
  ; I should make these variables global or something... make them parameters of a function, if NSIS can do that...
  !define FILE_EXTENSION ".dmg"
  !define PROGRAM_EXTENSION_ID "CatacombaeHFSExplorer.DMGFile"
  
  ; Start of restore script
  !define Index "Line${__LINE__}"
  ReadRegStr $1 HKCR "${FILE_EXTENSION}" ""
  StrCmp $1 "${PROGRAM_EXTENSION_ID}" 0 "${Index}-NoOwn" ; only do this if we own it

  ReadRegStr $1 HKCR "${FILE_EXTENSION}" "backup_val"
  StrCmp $1 "" 0 "${Index}-Restore" ; if backup="" then delete the whole key
  DeleteRegKey HKCR "${FILE_EXTENSION}"
  Goto "${Index}-NoOwn"
  
  "${Index}-Restore:"
  WriteRegStr HKCR "${FILE_EXTENSION}" "" $1
  DeleteRegValue HKCR "${FILE_EXTENSION}" "backup_val"

  ; In any case, we WILL delete HKCR\${PROGRAM_EXTENSION_ID}
  "${Index}-NoOwn:"
  DeleteRegKey HKCR "${PROGRAM_EXTENSION_ID}" ;Delete key with association settings
  System::Call 'Shell32::SHChangeNotify(i 0x8000000, i 0, i 0, i 0)'

  !undef Index
  !undef FILE_EXTENSION
  !undef PROGRAM_EXTENSION_ID
  ; End of restore script

  ; I should make these variables global or something... make them parameters of a function, if NSIS can do that...
  !define FILE_EXTENSION ".cdr"
  !define PROGRAM_EXTENSION_ID "CatacombaeHFSExplorer.CDRFile"

  ; Start of restore script
  !define Index "Line${__LINE__}"
  ReadRegStr $1 HKCR "${FILE_EXTENSION}" ""
  StrCmp $1 "${PROGRAM_EXTENSION_ID}" 0 "${Index}-NoOwn" ; only do this if we own it

  ReadRegStr $1 HKCR "${FILE_EXTENSION}" "backup_val"
  StrCmp $1 "" 0 "${Index}-Restore" ; if backup="" then delete the whole key
  DeleteRegKey HKCR "${FILE_EXTENSION}"
  Goto "${Index}-NoOwn"

  "${Index}-Restore:"
  WriteRegStr HKCR "${FILE_EXTENSION}" "" $1
  DeleteRegValue HKCR "${FILE_EXTENSION}" "backup_val"

  ; In any case, we WILL delete HKCR\${PROGRAM_EXTENSION_ID}
  "${Index}-NoOwn:"
  DeleteRegKey HKCR "${PROGRAM_EXTENSION_ID}" ;Delete key with association settings
  System::Call 'Shell32::SHChangeNotify(i 0x8000000, i 0, i 0, i 0)'

  !undef Index
  !undef FILE_EXTENSION
  !undef PROGRAM_EXTENSION_ID
  ; End of restore script
  ; End of restoration of registry values
  
  
  !insertmacro MUI_STARTMENU_GETFOLDER "Application" $ICONS_GROUP
  Delete "$INSTDIR\${PRODUCT_NAME}.url"
  Delete "$INSTDIR\uninst.exe"

  Delete "$INSTDIR\bin\*.*"
  RMDir "$INSTDIR\bin"
  Delete "$INSTDIR\doc\html\img\*.*"
  RMDir "$INSTDIR\doc\html\img"
  Delete "$INSTDIR\doc\html\*.*"
  RMDir "$INSTDIR\doc\html"
  RMDir "$INSTDIR\doc"
  Delete "$INSTDIR\lib\*.*"
  RMDir "$INSTDIR\lib"
  Delete "$INSTDIR\res\*.*"
  RMDir "$INSTDIR\res"
  Delete "$INSTDIR\*.*"
  RMDir "$INSTDIR"

  Delete "$SMPROGRAMS\$ICONS_GROUP\Uninstall.lnk"
  Delete "$SMPROGRAMS\$ICONS_GROUP\Developer Web Site.lnk"
  Delete "$SMPROGRAMS\$ICONS_GROUP\HFSExplorer.lnk"
  RMDir /r "$SMPROGRAMS\$ICONS_GROUP"

  DeleteRegKey ${PRODUCT_UNINST_ROOT_KEY} "${PRODUCT_UNINST_KEY}"
  SetAutoClose false
SectionEnd