' finding documentation about this scripting language and its host was NOT EASY.
' Took me too much time. Time that I should have spent on studying scientific computing.
' Anyway, this script invokes the UAC-dialog and gives administrator priviligies to the
' FileSystemBrowser

Set objShell = CreateObject("Shell.Application")
Set objFolder = objShell.Namespace(WScript.ScriptFullName & "\..")
Set objFolderItem = objFolder.ParseName("runfsb.bat")
objFolderItem.InvokeVerb "runas"
