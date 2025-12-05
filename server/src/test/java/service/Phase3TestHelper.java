package service;

import model.User;

import java.util.HashMap;

//checks to see if two hashmaps have identical sets of usernames

public class Phase3TestHelper {

    public boolean messyIdenticalUsers(HashMap<User, String> map1, HashMap<User, String> map2) {

        boolean firstIdentical = false;

        for (User user : map1.keySet()) {
            String mainUsername = user.username();
            firstIdentical = false;

            for (User expectedUser : map2.keySet()) {
                String secondaryUsername = expectedUser.username();

                if (mainUsername.equals(secondaryUsername)) {
                    firstIdentical = true;
                    break;
                }
            }
            if (!firstIdentical) {
                break;
            }
        }

        boolean secondIdentical = false;
        for (User user : map2.keySet()) {
            String mainUsername = user.username();
            secondIdentical = false;

            for (User expectedUser : map1.keySet()) {
                String secondaryUsername = expectedUser.username();

                if (mainUsername.equals(secondaryUsername)) {
                    secondIdentical = true;
                    break;
                }
            }
            if (!secondIdentical) {
                break;
            }
        }

        return (firstIdentical && secondIdentical);
    }

    //checks to see if any usernames are repeated EEK
    public boolean noRepeatedUsernames(HashMap<User, String> map1, HashMap<User, String> map2) {

        for (User userFirst : map1.keySet()) {
            int numOfRepeats = 0;

            for (User userSecond : map1.keySet()) {
                if (userFirst.equals(userSecond)) {
                    numOfRepeats++;
                }
                if (numOfRepeats>=2) {
                    return false;
                }
            }
        }

        for (User userFirst : map2.keySet()) {
            int numOfRepeats = 0;

            for (User userSecond : map2.keySet()) {
                if (userFirst.equals(userSecond)) {
                    numOfRepeats++;
                }
                if (numOfRepeats>=2) {
                    return false;
                }
            }
        }

        return true;
    }
}
