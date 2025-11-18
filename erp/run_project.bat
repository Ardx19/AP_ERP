@echo off
echo ==========================================
echo      University ERP Launcher
echo ==========================================

:: 1. Create bin folder if it doesn't exist
if not exist "bin" mkdir bin

:: 2. Compile Source Code
:: We compile ALL modules (Model, Auth, Student, Instructor, Admin, UI)
:: -cp "lib/*" includes external JARs
:: -d bin puts the class files in the bin folder
:: -encoding UTF-8 fixes special character errors
echo Compiling source code...
javac -d bin -cp "lib/*" -encoding UTF-8 ^
src/edu/univ/erp/model/*.java ^
src/edu/univ/erp/auth/*.java ^
src/edu/univ/erp/student/*.java ^
src/edu/univ/erp/instructor/*.java ^
src/edu/univ/erp/ui/*.java ^
src/edu/univ/erp/ui/auth/*.java ^
src/edu/univ/erp/ui/student/*.java ^
src/edu/univ/erp/ui/admin/*.java

:: 3. Check for Errors
if %ERRORLEVEL% NEQ 0 (
   echo.
   echo [ERROR] Compilation Failed! Please check the errors above.
   pause
   exit /b
)

:: 4. Run the Application
echo Starting Application...
java -cp "bin;lib/*" edu.univ.erp.ui.auth.LoginWindow

:: 5. Pause only if it crashes immediately
if %ERRORLEVEL% NEQ 0 pause