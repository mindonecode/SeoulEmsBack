@echo on
cd /d D:\SBD_Proj\SeoulEmsBack\

git reset --hard
echo git reset
git pull origin main
echo git pull
pm2 stop sbd_back
echo pm2 stop
powershell -nop -c "& {sleep 5}"
echo sleep 5 done
call gradlew.bat clean
echo gradle clean
call gradlew.bat build
echo gradle build
powershell -nop -c "& {sleep 10}"
echo sleep 10 done
pm2 start java --name sbd_back -- -jar build/libs/*.jar
echo pm2 start