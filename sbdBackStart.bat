@echo on
cd /d D:\SBD_Proj\SeoulEmsBack\

set JAVA_HOME=C:\Program Files\Java\jdk-17
set NODE_HOME=D:\Program Files\nodejs
set PATH=%JAVA_HOME%\bin;%NODE_HOME%;%PATH%

echo ===== ENV CHECK START =====
echo JAVA_HOME=%JAVA_HOME%
where java
where javac
java -version
javac -version
call gradlew.bat --version
echo ===== ENV CHECK END =====

git reset --hard
echo git reset
git pull origin master
echo git pull

call pm2 delete sbd_back
echo pm2 delete

call gradlew.bat clean
echo gradle clean
call gradlew.bat build
if errorlevel 1 exit /b 1
echo gradle build

powershell -nop -c "& {sleep 10}"
echo sleep 10 done

call pm2 start java --name sbd_back -- -jar build/libs/ems-0.0.1-SNAPSHOT.war
echo pm2 start
