package com.example.chiennt.musicplayer.services;

/**
 * Created by Nguyen on 12/07/2015.
 */
public class Constants {
    public interface ACTION {
        public static String MAIN_ACTION = "Action_Main";
        public static String PREV_ACTION = "Action_Prev";
        public static String PLAY_ACTION = "Action_Play";
        public static String NEXT_ACTION = "Action_Next";
        public static String STOP_ACTION ="ACTION_STOP";
        public static String STARTFOREGROUND_ACTION = "startforeground";
        public static String STOPFOREGROUND_ACTION = "stopforeground";
    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }
}
