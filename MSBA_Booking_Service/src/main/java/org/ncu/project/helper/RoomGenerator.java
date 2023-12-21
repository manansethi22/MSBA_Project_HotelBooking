package org.ncu.project.helper;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class RoomGenerator {
    public String generateVacantRooms(int numOfRooms) {
        Set<Integer> roomSet = new HashSet<>();
        Random random = new Random();
        while (roomSet.size() < numOfRooms) {
            int roomNumber = random.nextInt(100) + 1;
            roomSet.add(roomNumber);
        }
        String roomNumbers = roomSet.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));
        System.out.println("Generated Vacant Room Numbers: " + roomNumbers);
        return roomNumbers;
    }
}
