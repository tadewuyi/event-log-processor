# event-log-processor

# Project Title

This generic java application processes server logs and stores them in a file-based database, HSQLDB. 

## Prerequisites

An input file named 'input.txt' should be placed in the root directory. A sample is provided in the root directory.

## Running this application

Clone this repo:

    git clone https://github.com/tadewuyi/event-log-processor.git

For Windows users: From the root directory (where the build.gradle file is located), run in the terminal:

    .\gradlew.bat run
    
For Unix based users: From the root directory, run in the terminal:

    ./gradlew run
    
## Results

After the application has run, you'll find logs in the 'logs' directory

The db created by the application to store the results will be located in the 'eventdb' directory. It can be browsed using HSQLB's Database management system.

## Running the tests

    \gradlew.bat test

