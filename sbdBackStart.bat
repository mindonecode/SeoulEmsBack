@echo on
cd /d D:\SBD_Proj\SeoulEmsBack\
if errorlevel 1 exit /b 1

set JAVA_HOME=C:\Program Files\Java\jdk-17
set JAVA_EXE=%JAVA_HOME%\bin\java.exe
set NODE_EXE=D:\Program Files\nodejs\node.exe
set PATH=%JAVA_HOME%\bin;D:\Program Files\nodejs;%PATH%

echo ===== ENV CHECK START =====
echo JAVA_HOME=%JAVA_HOME%
echo JAVA_EXE=%JAVA_EXE%
echo NODE_EXE=%NODE_EXE%
"%JAVA_EXE%" -version
"%NODE_EXE%" -v
call "C:\Users\hjseok\AppData\Roaming\npm\pm2.cmd" -v
call gradlew.bat --version
echo ===== ENV CHECK END =====

git reset --hard
echo git reset
git pull origin master
if errorlevel 1 exit /b 1
echo git pull

call "C:\Users\hjseok\AppData\Roaming\npm\pm2.cmd" delete sbd_back
echo pm2 delete

call gradlew.bat clean
echo gradle clean
call gradlew.bat build
if errorlevel 1 exit /b 1
echo gradle build

powershell -nop -c "& {sleep 10}"
echo sleep 10 done

call "C:\Users\hjseok\AppData\Roaming\npm\pm2.cmd" start cmd --name sbd_back -- /c ""%JAVA_EXE%" -jar D:\SBD_Proj\SeoulEmsBack\build\libs\ems-0.0.1-SNAPSHOT.war"
echo pm2 start
