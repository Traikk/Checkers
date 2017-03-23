@echo off

cd ./src
dir /s /B *.java > sources.txt
javac -d ./../bin/ @sources.txt

cd ../bin

dir /s /B *.class > sources.txt
java Checkers

cd ..


cd src
del sources.txt
cd..
