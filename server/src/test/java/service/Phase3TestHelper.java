package service;

import model.User;

import java.util.HashMap;

//checks to see if two hashmaps have identical sets of usernames

public class Phase3TestHelper {

    public boolean messyIdenticalUsers(HashMap<String, User> map1, HashMap<String, User> map2) {

        boolean firstIdentical = false;

        for (User user : map1.values()) {
            String mainUsername = user.username();
            firstIdentical = false;

            for (User expectedUser : map2.values()) {
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
        for (User user : map2.values()) {
            String mainUsername = user.username();
            secondIdentical = false;

            for (User expectedUser : map1.values()) {
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
    public boolean noRepeatedUsernames(HashMap<String, User> map1, HashMap<String, User> map2) {

        for (User userFirst : map1.values()) {
            int numOfRepeats = 0;

            for (User userSecond : map1.values()) {
                if (userFirst.equals(userSecond)) {
                    numOfRepeats++;
                }
                if (numOfRepeats>=2) {
                    return false;
                }
            }
        }

        for (User userFirst : map2.values()) {
            int numOfRepeats = 0;

            for (User userSecond : map2.values()) {
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
