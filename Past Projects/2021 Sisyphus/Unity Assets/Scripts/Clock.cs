using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using TMPro;
using System;

public class Clock : MonoBehaviour
{
    public int time_scale = 60;

    private TimeSpan game_time;
    private TextMeshPro clock_text;
    private MissionType lastMissionType = MissionType.TALK;

    // Start is called before the first frame update
    void Start()
    {
        game_time = new TimeSpan(7, 0, 0);
        clock_text = gameObject.GetComponent<TextMeshPro>();
    }

    // Update is called once per frame
    void Update()
    {
        MissionType current_type = GameState.Instance.GetCurrentMissionType();
        if (lastMissionType != MissionType.DRINK && current_type == MissionType.DRINK)
        {
            game_time = new TimeSpan(20, 0, 0);
        }
        lastMissionType = current_type;

        game_time = game_time.Add(new TimeSpan(0, 0, 0, 0, (int)(1000 * Time.deltaTime * time_scale)));

        string minutes = LeadingZero(game_time.Minutes);

        if (DateTime.Now.Second % 2 == 0) 
        { 
            clock_text.text = game_time.Hours + ":" + minutes;
        }
        else
        {
            clock_text.text = game_time.Hours + " " + minutes;
        }
        clock_text.text += "\n";
        if (GameState.Instance.is_glitched)
        {
            clock_text.text += "11.2.2081";
        }
        else
        {
            clock_text.text += "23.9.2079";
        }
    }

    string LeadingZero(int n)
    {
        return n.ToString().PadLeft(2, '0');
    }
}
