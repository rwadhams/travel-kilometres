@echo off
REM OneDriveBackup for TravelKilometre data files and reports

if not exist "%userprofile%\OneDrive\Documents\App_Data_and_Reporting_Backups\TravelKilometres\" mkdir %userprofile%\OneDrive\Documents\App_Data_and_Reporting_Backups\TravelKilometres

xcopy *.xml %userprofile%\OneDrive\Documents\App_Data_and_Reporting_Backups\TravelKilometres /Y

xcopy out\*.* %userprofile%\OneDrive\Documents\App_Data_and_Reporting_Backups\TravelKilometres\out /I /Y
