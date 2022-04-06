using System.Collections;
using System.Collections.Generic;
using UnityEngine;


public enum MissionType
{
    TALK,
    WORK,
    SLEEP,
    DRINK,
}

public class GameState
{
    private static GameState _instance;

    public float screen_blackness = 0;

    public bool is_glitched = false;
    public float glitch_intensity = 0.0f;


    private int current_mission_index = 0;
    public int current_day = 0;

    public void NextMission()
    {
        current_mission_index++;
    }

    public string GetCurrentMission()
    {
        return missions[current_mission_index];
    }

    public void ResetMissions()
    {
        current_mission_index = 0;
    }


    public bool DoHitCutscene()
    {
        if (current_mission_index == 12 && current_day == 0)
        {
            return true;
        }
        return false;
    }


    public MissionType GetCurrentMissionType()
    {
        if (current_mission_index <= 4)
        {
            return MissionType.TALK;
        }
        else if (current_mission_index <= 14)
        {
            return MissionType.WORK;
        } else if (current_mission_index == 15)
        {
            return MissionType.DRINK;
        }
        return MissionType.SLEEP;
    }


    private string[] missions;

    public static GameState Instance
    {
        get
        {
            if (_instance == null) 
            {
                _instance = new GameState();
            }
            return _instance;
        }
    }

    private GameState()
    {
        missions = new string[20];
        missions[0] = "Sprich mit deinen Kollegen (0/5)";
        missions[1] = "Sprich mit deinen Kollegen (1/5)";
        missions[2] = "Sprich mit deinen Kollegen (2/5)";
        missions[3] = "Sprich mit deinen Kollegen (3/5)";
        missions[4] = "Sprich mit deinen Kollegen (4/5)";
        missions[5] = "Inspiziere Kisten im Lageraum (0/10)";
        missions[6] = "Inspiziere Kisten im Lageraum (1/10)";
        missions[7] = "Inspiziere Kisten im Lageraum (2/10)";
        missions[8] = "Inspiziere Kisten im Lageraum (3/10)";
        missions[9] = "Inspiziere Kisten im Lageraum (4/10)";
        missions[10] = "Inspiziere Kisten im Lageraum (5/10)";
        missions[11] = "Inspiziere Kisten im Lageraum (6/10)";
        missions[12] = "Inspiziere Kisten im Lageraum (7/10)";
        missions[13] = "Inspiziere Kisten im Lageraum (8/10)";
        missions[14] = "Inspiziere Kisten im Lageraum (9/10)";
        missions[15] = "Trinke ein Bier in der Bar";
        missions[16] = "Gehe ins Bett";
    }
}   