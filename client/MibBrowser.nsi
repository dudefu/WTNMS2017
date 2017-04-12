Name "Java Launcher"
Caption "Java Launcher"
Icon "res/icons/logo.ico"
OutFile "MibBrowser/MibBrowser.exe"

SilentInstall silent
AutoCloseWindow true
ShowInstDetails nevershow

Section ""
  Call GetJRE
  Pop $R0

  StrCpy $0 '"$R0" -jar JHWMibBrowser.jar'

  SetOutPath $EXEDIR
  ExecWait $0
SectionEnd

Function GetJRE
  Push $R0
  Push $R1

  ClearErrors
  StrCpy $R0 "..\jre\bin\javaw.exe"
  IfFileExists $R0 JreFound
  StrCpy $R0 ""

  ClearErrors
  ReadEnvStr $R0 "JAVA_HOME"
  StrCpy $R0 "$R0\bin\javaw.exe"
  IfErrors 0 JreFound

  ClearErrors
  ReadRegStr $R1 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" "CurrentVersion"
  ReadRegStr $R0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment\$R1" "JavaHome"
  StrCpy $R0 "$R0\bin\javaw.exe"

  IfErrors 0 JreFound
  StrCpy $R0 "javaw.exe"

 JreFound:
  Pop $R1
  Exch $R0
FunctionEnd