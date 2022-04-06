using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using UnityEngine.SceneManagement;

public class StartMenuController : MonoBehaviour
{
    public Text text;
    public AudioClip[] glitch_audio_clips;

    private bool loading_game;
    private bool fading_in;
    private AudioSource audio_source;

    // Start is called before the first frame update
    void Awake()
    {
        loading_game = false;
        fading_in = true;
        Cursor.visible = false;
    }

    private void Start()
    {
        audio_source = gameObject.GetComponent<AudioSource>();
        GameState.Instance.screen_blackness = 1;
        StartCoroutine(FadeInScreen(2));
        StartCoroutine(RandomGlitches());
        AudioListener.volume = 1;
        GameState.Instance.current_day = 0;
    }

    IEnumerator RandomGlitches()
    {
        while (!loading_game)
        {
            yield return new WaitForSeconds(Random.Range(4, 9));
            int clip_index = (int)Random.Range(0, glitch_audio_clips.Length - 0.001f);
            audio_source.PlayOneShot(glitch_audio_clips[clip_index], 0.3f);
            GameState.Instance.glitch_intensity = 0.4f;
            yield return new WaitForSeconds(0.4f);
            GameState.Instance.glitch_intensity = 0;
        }
    }

    IEnumerator FadeInScreen(float time_seconds)
    {
        yield return new WaitForSeconds(1.0f);
        int num_steps = 128;
        float sleep_time = time_seconds / (float)num_steps;
        for (int i = 0; i < num_steps; i++)
        {
            yield return new WaitForSeconds(sleep_time);
            GameState.Instance.screen_blackness = 1 - (((float)(i + 1)) / num_steps);
        }
        fading_in = false;
    }

    // Update is called once per frame
    void Update()
    {
        float brightness = Mathf.Sin(Time.time * 2) * 0.5f + 0.5f;
        brightness *= Mathf.Pow(1 - GameState.Instance.screen_blackness, 4);
        text.color = new Color(brightness, brightness, brightness);

        if (fading_in)
        {
            return;
        }
        if (Input.GetKeyDown("e")) 
        {
            if (!loading_game)
            {
                loading_game = true;
                StartCoroutine(LoadGame());
            }
        }
    }

    IEnumerator LoadGame()
    {
        int num_steps = 128;
        float sleep_time = 2.0f / (float)num_steps;
        for (int i = 0; i < num_steps; i++)
        {
            yield return new WaitForSeconds(sleep_time);
            GameState.Instance.screen_blackness = (((float)(i + 1)) / num_steps);
            AudioListener.volume = 1 - GameState.Instance.screen_blackness;
        }
        yield return new WaitForSeconds(0.5f);
        SceneManager.LoadScene(1);
    }
}
