//Berkay Bugra Gok
//Student ID = 2021400258
//Date = 21.03.2023

/*
This project takes two metro stations of Istanbul metro lines as input and finds the stations between them.
It reads the metro stations and their coordinates from a text file(input.txt)
It also animates which stations you should take in order to arrive your destination using the StdDraw library.
It gives the necessary errors if the input stations are not okay.
 */

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        //Check when the same station is entered twice

        //Input file is being read
        String fileName = "input.txt";
        File inputFile = new File(fileName);
        if (!inputFile.exists())
            System.exit(1);

        Scanner fileHandle1 = new Scanner(inputFile);

        int numberOfMetroLines = 0;
        // Having read the file 2 times is not so efficient, but I guess it is necessary to have the number of lines.
        // I do not assume that all lines will be given everytime.
        while  (fileHandle1.hasNextLine()) {
            String currentLine = fileHandle1.nextLine();
            if (currentLine.startsWith("*"))
                numberOfMetroLines++;
        }

        Scanner fileHandle2 = new Scanner(inputFile);

        String[] metroLineNames = new String[numberOfMetroLines];
        int[] metroLineRed = new int[numberOfMetroLines];
        int[] metroLineGreen = new int[numberOfMetroLines];
        int[] metroLineBlue = new int[numberOfMetroLines];

        String[] transferStationsNames = new String[7];
        String[][] transferStationLines = new String[7][3];

        String[][] stationsMotherList = new String[numberOfMetroLines][];
        String[][][] coordinatesMotherList = new String[numberOfMetroLines][][];

        int index1 = 0;
        int index2 = 0;
        while (fileHandle2.hasNextLine()) {
            String firstString = fileHandle2.nextLine();

            try { // Try to read lines two by two, catch when it exploits on the last line.
                String secondString = fileHandle2.nextLine();

                if (secondString.startsWith("*")) { // We haven't reached the breakpoints, we are taking metro lines
                    // Metro line name and color code is added to lists
                    String[] tempLineList1 = firstString.split(" ");
                    metroLineNames[index1] = tempLineList1[0];

                    metroLineRed[index1] = Integer.parseInt(tempLineList1[1].split(",")[0]);
                    metroLineGreen[index1] = Integer.parseInt(tempLineList1[1].split(",")[1]);
                    metroLineBlue[index1] = Integer.parseInt(tempLineList1[1].split(",")[2]);

                    //Coordination of stations is added to lists
                    // secondString manipulation is done here
                    String[] secondLineList = secondString.split(" ");
                    int tempNumOfStations = (secondLineList.length/2);

                    stationsMotherList[index1] = new String[tempNumOfStations];
                    coordinatesMotherList[index1] = new String[tempNumOfStations][];

                    for (int i = 0; i< secondLineList.length; i++) {
                        if (i % 2 == 0) {// if the index is even, meaning station names
                            String currentStationName = secondLineList[i];
                            stationsMotherList[index1][i / 2] = currentStationName;
                        } else {  // Index is odd, meaning that it is the coordinate of the previous station
                            coordinatesMotherList[index1][(i - 1) / 2] = new String[2];
                            coordinatesMotherList[index1][(i - 1) / 2][0] = secondLineList[i].split(",")[0];
                            coordinatesMotherList[index1][(i - 1) / 2][1] = secondLineList[i].split(",")[1];
                        }
                    }
                    index1++;
                }

                else { // We can still read two by two but metro lines are finished, now we are taking transfer stations
                    String[] transferList1 = firstString.split(" ");
                    String[] transferList2 = secondString.split(" ");
                    transferStationsNames[index2] = transferList1[0];
                    for (int i = 1; i<transferList1.length; i++)
                        transferStationLines[index2][i-1] = transferList1[i];
                    index2++;
                    transferStationsNames[index2] = transferList2[0];
                    for (int i = 1; i<transferList2.length; i++)
                        transferStationLines[index2][i-1] = transferList2[i];
                    index2++;
                }
            }
            catch (NoSuchElementException exception) { // reached to the last line
                String[] transferListCaught = firstString.split(" ");
                transferStationsNames[index2] = transferListCaught[0];
                for (int i = 1; i<transferListCaught.length; i++) {
                    transferStationLines[index2][i-1] = transferListCaught[i];
                }
            }
        }

        boolean initialStationProper = false;
        boolean terminalStationProper = false;

        Scanner input1 = new Scanner(System.in);
        String initialStationInput = input1.next();
        Scanner input2 = new Scanner(System.in);
        String terminalStationInput = input2.next();

        for (int i = 0; i< stationsMotherList.length; i++) {
            for (int j = 0; j< stationsMotherList[i].length; j++) {
                String currentStationName = stationsMotherList[i][j].replace("*","");
                if (initialStationInput.equals(currentStationName))
                    initialStationProper = true;
                if (terminalStationInput.equals(currentStationName))
                    terminalStationProper = true;
            }
        }

        if (!(initialStationProper & terminalStationProper)) { //Incorrect input error message
            System.out.println("No such station names in this map");
        }

        else { // Inputs are correct
            int firstStationLineIndex = 0;
            int firstStationStopIndex = 0;
            int secondStationLineIndex = 0;
            int secondStationStopIndex = 0;

            //Locations of the stations and metro lines are found
            for (int i = 0; i < stationsMotherList.length; i++) {
                for (int j = 0; j < stationsMotherList[i].length; j++) {
                    String iteratingStation = stationsMotherList[i][j].replace("*","");
                    if ((iteratingStation.equals(initialStationInput)) & (firstStationLineIndex==0)) {
                        firstStationLineIndex = i;
                        firstStationStopIndex = j;
                    }
                    if ((iteratingStation.equals(terminalStationInput)) & (secondStationLineIndex==0)) {
                        secondStationLineIndex = i;
                        secondStationStopIndex = j;
                    }
                }
            }

            // This if block checks whether the stations are connected or not.
            // Since the only separated metro line is M9, it is easy to check manually
            if (((metroLineNames[firstStationLineIndex].equals("M9")) & (!metroLineNames[secondStationLineIndex].equals("M9"))) || ((!metroLineNames[firstStationLineIndex].equals("M9")) & (metroLineNames[secondStationLineIndex].equals("M9")))) {
                System.out.println("These two stations are not connected");
            }

            else { // Stations will be found/printed and canvas will be drawn.

                // The stations between given lines will be added to a list
                String[] stationsListWBP;
                int lengthWBP = 0;

                // First we check whether the stations are on the same line
                // Note that: since one station has only one metro line index, although the indexes are different,
                // They might be on the same line.
                boolean onTheSameLine = false;
                if (firstStationLineIndex != secondStationLineIndex) {
                    for (int i = 0; i < stationsMotherList[firstStationLineIndex].length; i++) {
                        if (stationsMotherList[firstStationLineIndex][i].replace("*", "").equals(terminalStationInput)) {
                            secondStationLineIndex = firstStationLineIndex;
                            secondStationStopIndex = i;
                            onTheSameLine = true;
                        }
                    }
                }
                if (firstStationLineIndex == secondStationLineIndex)
                    onTheSameLine = true;

                //Our main list which will be printed (stationsListWBP) is going to be constructed.
                if (onTheSameLine) { // if the given stations are on the same line
                    lengthWBP = Math.abs(firstStationStopIndex-secondStationStopIndex) + 1;
                    stationsListWBP = new String[lengthWBP];
                    int tempIndex = 0;
                    if (firstStationStopIndex>secondStationStopIndex){
                        for (int i = firstStationStopIndex; i>=secondStationStopIndex; i--) {
                            stationsListWBP[tempIndex] = stationsMotherList[firstStationLineIndex][i].replace("*","");
                            tempIndex++;
                        }
                    }
                    if (firstStationStopIndex<secondStationStopIndex){
                        for (int i = firstStationStopIndex; i<=secondStationStopIndex; i++) {
                            stationsListWBP[tempIndex] = stationsMotherList[firstStationLineIndex][i].replace("*","");
                            tempIndex++;
                        }
                    }
                    int[][] pathCoordinates = new int[stationsListWBP.length][2];
                    int stationIndex = 0;
                    for (String station : stationsListWBP) {
                        for (int i = 0; i < stationsMotherList.length; i++) {
                            for (int j = 0; j < stationsMotherList[i].length; j++) {
                                if (station.equals(stationsMotherList[i][j].replace("*", ""))) {
                                    pathCoordinates[stationIndex][0] = Integer.parseInt(coordinatesMotherList[i][j][0]);
                                    pathCoordinates[stationIndex][1] = Integer.parseInt(coordinatesMotherList[i][j][1]);
                                    break;
                                }
                            }
                        }
                        stationIndex++;
                    }

                    //output is printed to the console
                    for (String station : stationsListWBP)
                        System.out.println(station);

                    //Canvas drawn here
                    canvasDrawer(numberOfMetroLines,metroLineRed,metroLineGreen,metroLineBlue,stationsMotherList,coordinatesMotherList,pathCoordinates);

                }

                else { // Stations are not on the same line, but we know that they are connected

                    ArrayList<String> transfersUpdated = new ArrayList<>(List.of(transferStationsNames));
                    if (! transfersUpdated.contains(initialStationInput))
                        transfersUpdated.add(initialStationInput);
                    String lastVisited = null;
                    ArrayList<String> allVisited = new ArrayList<>();
                    ArrayList<String> dynamicPathList = new ArrayList<>();
                    ArrayList<String> listWBR = new ArrayList<>();

                    ArrayList<String> stationsBetween = new ArrayList<>();
                    stationsBetween = recursivePathFinder(stationsMotherList, initialStationInput, terminalStationInput, transfersUpdated, lastVisited, dynamicPathList, allVisited, listWBR);

                    // path is found
                    // we will find the coordinates of the stations that construct the path
                    // we will call the canvasDrawer with that coordinates as parameter
                    int[][] pathCoordinates = new int[stationsBetween.size()][2];
                    int stationIndex = 0;
                    for (String station : stationsBetween) {
                        for (int i = 0; i < stationsMotherList.length; i++) {
                            for (int j = 0; j < stationsMotherList[i].length; j++) {
                                if (station.equals(stationsMotherList[i][j].replace("*", ""))) {
                                    pathCoordinates[stationIndex][0] = Integer.parseInt(coordinatesMotherList[i][j][0]);
                                    pathCoordinates[stationIndex][1] = Integer.parseInt(coordinatesMotherList[i][j][1]);
                                    break;
                                }
                            }
                        }
                        stationIndex++;
                    }

                    // output is printed to the console
                    for (String station : stationsBetween)
                        System.out.println(station);

                    // canvas drawn
                    canvasDrawer(numberOfMetroLines,metroLineRed,metroLineGreen,metroLineBlue,stationsMotherList,coordinatesMotherList,pathCoordinates);

                }
            }
        }
    }

    public static ArrayList<String> recursivePathFinder(String[][] stations2dList, String initialStation, String finalStation, ArrayList<String> transfersUpdated, String lastVisited, ArrayList<String> dynamicPathList, ArrayList<String> allVisited, ArrayList<String> listWBR) {
        ArrayList<String> neighbouringStations = new ArrayList<>();

        // Indexes of the stations are found in our 2d list
        int firstStationLineIndex = 0;
        int firstStationStopIndex = 0;
        int secondStationLineIndex = 0;
        int secondStationStopIndex = 0;
        for (int i = 0; i < stations2dList.length; i++) {
            for (int j = 0; j < stations2dList[i].length; j++) {
                String iteratingStation = stations2dList[i][j].replace("*", "");
                if ((iteratingStation.equals(initialStation)) & (firstStationLineIndex == 0)) {
                    firstStationLineIndex = i;
                    firstStationStopIndex = j;
                }
                if ((iteratingStation.equals(finalStation)) & (secondStationLineIndex == 0)) {
                    secondStationLineIndex = i;
                    secondStationStopIndex = j;
                }
            }
        }

        // Checking for the neighbours, first that are on the same line
        if ((firstStationStopIndex + 1) < stations2dList[firstStationLineIndex].length)
            if (!stations2dList[firstStationLineIndex][firstStationStopIndex + 1].replace("*", "").equals(lastVisited))
                if (!allVisited.contains(stations2dList[firstStationLineIndex][firstStationStopIndex + 1].replace("*", "")))
                    neighbouringStations.add(stations2dList[firstStationLineIndex][firstStationStopIndex + 1].replace("*", ""));
        if ((firstStationStopIndex - 1) >= 0)
            if (!stations2dList[firstStationLineIndex][firstStationStopIndex - 1].replace("*", "").equals(lastVisited))
                if (!allVisited.contains(stations2dList[firstStationLineIndex][firstStationStopIndex - 1].replace("*", "")))
                    neighbouringStations.add(stations2dList[firstStationLineIndex][firstStationStopIndex - 1].replace("*", ""));

        //check if the current station is a transfer station or not, if it is, find other lines that this station is on
        if (transfersUpdated.contains(stations2dList[firstStationLineIndex][firstStationStopIndex].replace("*", ""))) {
            // it means that this station is a transfer station and neighbouring stations will be more than two
            for (int i = 0; i < stations2dList.length; i++) {
                for (int j = 0; j < stations2dList[i].length; j++) {
                    if ((stations2dList[i][j].replace("*", "").equals(stations2dList[firstStationLineIndex][firstStationStopIndex].replace("*", "")))) {
                        //long if block checks the index of the current station on other metro lines
                        if ((j + 1) < stations2dList[i].length) {
                            if ((!stations2dList[i][j + 1].replace("*", "").equals(lastVisited)) & !neighbouringStations.contains(stations2dList[i][j + 1].replace("*", "")))
                                if (!allVisited.contains(stations2dList[i][j + 1].replace("*", "")))
                                    neighbouringStations.add(stations2dList[i][j + 1].replace("*", ""));
                        }
                        if ((j - 1) >= 0) {
                            if ((!stations2dList[i][j - 1].replace("*", "").equals(lastVisited)) & !neighbouringStations.contains(stations2dList[i][j - 1].replace("*", "")))
                                if (!allVisited.contains(stations2dList[i][j - 1].replace("*", "")))
                                    neighbouringStations.add(stations2dList[i][j - 1].replace("*", ""));
                        }
                    }
                }
            }
        }
        if (!dynamicPathList.contains(initialStation)) {
            dynamicPathList.add(initialStation);
        }

        if (neighbouringStations.contains(finalStation)) { // if one of the neighbours is the terminal station
            dynamicPathList.add(finalStation);
            for (String stationName : dynamicPathList) {
                listWBR.add(stationName);
                if (stationName.equals(dynamicPathList.get(dynamicPathList.size() - 1))) {
                    return listWBR;
                }
            }
        }

        else {
            if (transfersUpdated.contains(lastVisited)) { // if the station which we have come from is a transfer station
                allVisited.add(initialStation);
            }

            if (neighbouringStations.isEmpty()) { // Meaning that either we have reached to a dead-end or we are at a node which does not connect to our final station
                if (transfersUpdated.contains(initialStation)) {
                    dynamicPathList.remove(dynamicPathList.size() - 1); // We have removed the last visited station
                    dynamicPathList.remove(dynamicPathList.size() - 1); // We have removed the last visited station
                }

                // last visited station is the end of a metro line like "Haciosman" or it is a useless node
                while (!transfersUpdated.contains(dynamicPathList.get(dynamicPathList.size() - 1))) {
                    dynamicPathList.remove(dynamicPathList.size() - 1);
                }
                lastVisited = initialStation;
                recursivePathFinder(stations2dList, dynamicPathList.get(dynamicPathList.size() - 1), finalStation, transfersUpdated, lastVisited, dynamicPathList, allVisited, listWBR);
            }

            for (String neighbour : neighbouringStations) { // iterating over all neighbours
                if (transfersUpdated.contains(neighbour)) // if the current neighbour is a transfer station, we add our current station to visited stations
                    allVisited.add(initialStation);
                lastVisited = initialStation; // before calling the recursive function we change the last visited station to the station we were on
                return recursivePathFinder(stations2dList, neighbour, finalStation, transfersUpdated, lastVisited, dynamicPathList, allVisited, listWBR);
            }
        }
        return listWBR;
    }


    public static void canvasDrawer(int numberOfMetroLines, int[] metroLineRed, int []metroLineGreen, int[]metroLineBlue, String[][] stationsMotherList, String[][][] coordinatesMotherList, int[][] coordinatesAnimated) {

                //Drawing the Canvas
        StdDraw.setCanvasSize(1024, 482);
        StdDraw.setXscale(0, 1024);
        StdDraw.setYscale(0, 482);

        StdDraw.enableDoubleBuffering();
        int pauseDuration = 300;

        for (int transferNum = 0; transferNum < coordinatesAnimated.length; transferNum++) { // iterates over transfer Stations
            StdDraw.picture(512, 241, "background.jpg");

            for (int i = 0; i < numberOfMetroLines; i++) { //Iterates over Metro Lines
                int currentRedValue = metroLineRed[i];
                int currentGreenValue = metroLineGreen[i];
                int currentBlueValue = metroLineBlue[i];

                //Metro lines with colors
                for (int l = 0; l < stationsMotherList[i].length - 1; l++) {
                    int stationX1 = Integer.parseInt(coordinatesMotherList[i][l][0]);
                    int stationY1 = Integer.parseInt(coordinatesMotherList[i][l][1]);
                    int stationX2 = Integer.parseInt(coordinatesMotherList[i][l + 1][0]);
                    int stationY2 = Integer.parseInt(coordinatesMotherList[i][l + 1][1]);
                    StdDraw.setPenColor(currentRedValue, currentGreenValue, currentBlueValue);
                    StdDraw.setPenRadius(0.012);
                    StdDraw.line(stationX1, stationY1, stationX2, stationY2);
                }

                //Names and white points
                for (int j = 0; j < stationsMotherList[i].length; j++) { //Iterates over current metro line's stations
                    String tempStation = stationsMotherList[i][j];
                    int stationX = Integer.parseInt(coordinatesMotherList[i][j][0]);
                    int stationY = Integer.parseInt(coordinatesMotherList[i][j][1]);
                    StdDraw.setPenColor(Color.white);
                    StdDraw.filledCircle(stationX, stationY, 2.4);

                    StdDraw.setPenColor(Color.black);
                    if (tempStation.startsWith("*")) {
                        StdDraw.setFont(new Font("Helvetica", Font.BOLD, 8));
                        StdDraw.text(stationX, stationY + 5, tempStation.replace("*", ""));
                    }
                }
            }

            //orange points are done here
            StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE);
            for (int recentDots = 0; recentDots < transferNum; recentDots++) {
                StdDraw.filledCircle(coordinatesAnimated[recentDots][0], coordinatesAnimated[recentDots][1], 2.4);
            }

            StdDraw.filledCircle(coordinatesAnimated[transferNum][0], coordinatesAnimated[transferNum][1], 4.8);
            StdDraw.show(5);
            StdDraw.pause(pauseDuration);
            StdDraw.clear();
        }
    }
}