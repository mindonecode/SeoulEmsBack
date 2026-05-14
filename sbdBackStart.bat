@echo on
cd /d D:\SBD_Proj\SeoulEmsBack\

git reset --hard
echo git reset
git pull origin master
echo git pull
call pm2 stop sbd_back
echo pm2 stop
powershell -nop -c "& {sleep 5}"
echo sleep 5 done
call gradlew.bat clean
echo gradle clean
call gradlew.bat build
echo gradle build
powershell -nop -c "& {sleep 10}"
echo sleep 10 done
call pm2 start java --name sbd_back -- -jar build/libs/ems-0.0.1-SNAPSHOT.war
echo pm2 start