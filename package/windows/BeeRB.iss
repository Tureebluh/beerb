;This file will be executed next to the application bundle image
;I.e. current directory will contain folder BeeRB with application files
[Setup]
AppId={{main}}
AppName=BeeRB
AppVersion=1.0
AppVerName=BeeRB 1.0
AppPublisher=Tureebluh
AppComments=BeeRB
AppCopyright=Copyright (C) 2016
;AppPublisherURL=http://java.com/
;AppSupportURL=http://java.com/
;AppUpdatesURL=http://java.com/
DefaultDirName={userdocs}\BeeRB
DisableStartupPrompt=Yes
DisableDirPage=No
DisableProgramGroupPage=Yes
DisableReadyPage=Yes
DisableFinishedPage=Yes
DisableWelcomePage=Yes
DefaultGroupName=Tureebluh
;Optional License
LicenseFile=
;WinXP or above
MinVersion=0,5.1 
OutputBaseFilename=BeeRB-1.0
Compression=lzma
SolidCompression=yes
PrivilegesRequired=admin
SetupIconFile=BeeRB\BeeRB.ico
UninstallDisplayIcon={app}\BeeRB.ico
UninstallDisplayName=BeeRB
WizardImageStretch=No
WizardSmallImageFile=BeeRB-setup-icon.bmp   
ArchitecturesInstallIn64BitMode=x64


[Languages]
Name: "english"; MessagesFile: "compiler:Default.isl"

[Files]
Source: "BeeRB\BeeRB.exe"; DestDir: "{app}"; Flags: ignoreversion
Source: "BeeRB\*"; DestDir: "{app}"; Flags: ignoreversion recursesubdirs createallsubdirs

[Icons]
Name: "{group}\BeeRB"; Filename: "{app}\BeeRB.exe"; IconFilename: "{app}\BeeRB.ico"; Check: returnTrue()
Name: "{commondesktop}\BeeRB"; Filename: "{app}\BeeRB.exe";  IconFilename: "{app}\BeeRB.ico"; Check: returnFalse()


[Run]
Filename: "{app}\BeeRB.exe"; Parameters: "-Xappcds:generatecache"; Check: returnFalse()
Filename: "{app}\BeeRB.exe"; Description: "{cm:LaunchProgram,BeeRB}"; Flags: nowait postinstall skipifsilent; Check: returnTrue()
Filename: "{app}\BeeRB.exe"; Parameters: "-install -svcName ""BeeRB"" -svcDesc ""BeeRB"" -mainExe ""BeeRB.exe""  "; Check: returnFalse()

[UninstallRun]
Filename: "{app}\BeeRB.exe "; Parameters: "-uninstall -svcName BeeRB -stopOnUninstall"; Check: returnFalse()

[Code]
function returnTrue(): Boolean;
begin
  Result := True;
end;

function returnFalse(): Boolean;
begin
  Result := False;
end;

function InitializeSetup(): Boolean;
begin
// Possible future improvements:
//   if version less or same => just launch app
//   if upgrade => check if same app is running and wait for it to exit
//   Add pack200/unpack200 support? 
  Result := True;
end;  
