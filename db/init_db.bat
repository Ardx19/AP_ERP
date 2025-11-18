@echo off
echo ============================================
echo      Database Initializer Script
echo ============================================

:: Ask for MySQL Root Password
set /p dbpass="Enter MySQL Root Password: "

echo.
echo 1. Creating Schema (Tables)...
mysql -u root -p%dbpass% < schema.sql
if %ERRORLEVEL% NEQ 0 goto error

echo.
echo 2. Seeding Data...
mysql -u root -p%dbpass% < seeds.sql
if %ERRORLEVEL% NEQ 0 goto error

echo.
echo ============================================
echo      SUCCESS! Database initialized.
echo      Please run SeedAdminUser.java if passwords do not work.
echo ============================================
pause
exit /b

:error
echo.
echo [ERROR] An error occurred. Check if MySQL is running.
pause