@echo off
REM OneDriveBackup for TravelKilometre data files and reports

REM data files
copy *.xml %userprofile%\OneDrive\Documents\TravelKilometres

REM report files
copy out\*.txt %userprofile%\OneDrive\Documents\TravelKilometres
