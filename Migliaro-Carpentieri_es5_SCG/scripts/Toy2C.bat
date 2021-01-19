@echo off
setlocal EnableExtensions EnableDelayedExpansion
set inputFileName=%1
set outputFileName=%2
shift
shift
java -jar .\Toy.jar %inputFileName%
set inputC=%inputFileName:.toy=.c%
clang -w -o .\%outputFileName% %inputC%
echo Compilation terminated