using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using UnityEngine.SceneManagement;

public class PlayerController : MonoBehaviour
{
    public float movement_speed = 5.0f;
    public float look_sensitivity = 10.0f;
    public float jump_force = 0.01f;

    public Camera game_camera;
    public Text interaction_text;
    public static string cutsceneInteraction = "";
    public AudioSource glitchSound;
    public AudioClip glitchSoundClip;
    public AudioClip hitImpactClip;

    public Text mission_text;
    public AudioSource ansage;
    public AudioSource machine_sound;

    private Vector3 translation;
    private Vector2 rotation;
    private Rigidbody player_rigidbody;
    //private Vector3 player_velocity;
    private Vector3 jump_force_vector;

    private bool is_grounded;
    private bool running = false;
    public bool testing;

    private Color real_ambient_color;
    private Color illusion_ambient_color;
    private bool is_glitching = false;
    private bool walking = true;

    public Canvas orderDrink1;
    public Canvas orderDrink2;
    [HideInInspector] public static bool atBar = false;

    public AudioSource audioSource;
    public AudioSource barTalking;
    public AudioClip drink;
    public AudioClip wakeup;

    private bool reloading_scene = false;

    [HideInInspector] public static bool talking;
    [HideInInspector] public static bool maybetalking = false;
    [HideInInspector] public static bool inCutscene;
    [HideInInspector] public bool trigger_ca1 = false;
    private bool disable_input = false;
    private bool fixedCamera = false;
    private int startGettingUp;
    private bool going_to_sleep;

    private float timeSinceLastGlitch = 0;

    private void Awake()
    {
        Cursor.visible = false;
        GameState.Instance.screen_blackness = 1;       
        trigger_ca1 = false;
        going_to_sleep = false;
        timeSinceLastGlitch = 0;
    }

    // Start is called before the first frame update
    void Start()
    {
        translation = new Vector3();
        rotation = new Vector2();

        player_rigidbody = gameObject.GetComponent<Rigidbody>();
        //player_velocity = new Vector3();
        jump_force_vector = new Vector3(0, 1, 0);

        real_ambient_color = new Color(0.1f, 0.1f, 0.1f);
        illusion_ambient_color = new Color(0.8f, 0.8f, 0.8f);

        talking = false;

        orderDrink1.enabled = false;
        orderDrink2.enabled = false;

        fixedCamera = false;
        startGettingUp = 4;

        GameState.Instance.ResetMissions();
        StartCoroutine(FadeInScreen(5.0f));
        StartCoroutine(WakingUp());
        disable_input = false;
        AudioListener.volume = 1f;

        if (GameState.Instance.current_day == 1)
        {
            StartCoroutine(Day1AwakeGlitch());
        }
    }

    IEnumerator Day1AwakeGlitch()
    {
        yield return new WaitForSeconds(7);
        StartCoroutine(MultiGlitch(4));
    }

    IEnumerator FadeInScreen(float time_seconds)
    {
        yield return new WaitForSeconds(1.0f);
        int num_steps = 1000;
        float sleep_time = time_seconds / (float)num_steps;
        for (int i = 0; i < num_steps; i++)
        {
            yield return new WaitForSeconds(sleep_time);
            GameState.Instance.screen_blackness = 1 - (((float)(i + 1)) / num_steps);
        }
    }

    void OnCollisionStay(Collision collisionInfo)
    {
        // TODO: check if we touch the ground
        if (collisionInfo.gameObject.tag == "Floor")
        {
            is_grounded = true;
        }
        //Debug.Log("Bonjour!");
    }

    private void OnTriggerEnter(Collider collider)
    {
        if ((collider.tag == "glitch_trigger")&&(!testing) && GameState.Instance.current_day > 0)
        {
            GlitchTrigger glitchTrigger = collider.GetComponent<GlitchTrigger>();
            if (glitchTrigger != null)
            {
                bool first = glitchTrigger.is_first_contact;
                if (first)
                {
                    StartCoroutine(StopPlayer(4));
                }

                if (first || timeSinceLastGlitch > 30.0f) 
                { 
                    timeSinceLastGlitch = 0;
                    StartCoroutine(MultiGlitch(4));
                }
            }
           
        }

        if (collider.gameObject.name == "Bar")
        {
            atBar = true;
            if (transform.position.z < -18.8f)
            {
                orderDrink1.enabled = true;
            }
            else
            {
                orderDrink2.enabled = true;
            }
        }

        if ((collider.gameObject.name == "ca1_player_stand")||(collider.gameObject.name == "ca1_player_stand2"))
        {
            trigger_ca1 = true;
            Destroy(collider.gameObject);
        }
    }

    private void OnTriggerExit(Collider collider)
    {
        if (collider.gameObject.name == "Bar")
        {
            atBar = false;
            orderDrink1.enabled = false;       
            orderDrink2.enabled = false;            
        }
    }

    private IEnumerator StopPlayer(float seconds)
    {
        walking = false;
        yield return new WaitForSeconds(seconds);
        walking = true;
    }

    IEnumerator ToggleGlitch()
    {
        float num_steps = 16;
        for (int i = 0; i < num_steps; i++)
        {
            float intensity = i / (num_steps - 1);
            intensity *= intensity * intensity * intensity * intensity;
            GameState.Instance.glitch_intensity = intensity;
            yield return new WaitForSeconds(.2f);
        }
        GameState.Instance.is_glitched = !GameState.Instance.is_glitched;
        for (int i = (int)num_steps - 1; i >= 0; i--)
        {
            float intensity = i / (num_steps - 1);
            intensity *= intensity * intensity * intensity * intensity;
            GameState.Instance.glitch_intensity = intensity;
            yield return new WaitForSeconds(.08f);
        }
    }

    IEnumerator MultiGlitch(int number_of_glitches)
    {
        if (!is_glitching)
        {
            is_glitching = true;
            for (int i = 0; i < number_of_glitches; i++)
            {
                if (i % 2 == 0)
                {
                    glitchSound.PlayOneShot(glitchSoundClip, 0.15f);
                }
                StartCoroutine(ToggleGlitch());
                yield return new WaitForSeconds(1.2f);
            }
            is_glitching = false;
        }
    }

    // Update is called once per frame
    void Update()
    {
        timeSinceLastGlitch += Time.deltaTime;

        //Rennen mit Shift
        if ((Input.GetAxis("Run") > 0)&&(!running))
        {
            movement_speed = movement_speed * 2;
            running = true;
        }else if (running)
        {
            running = false;
            movement_speed = movement_speed / 2;
        }

        if (is_grounded && !inCutscene) //Bewegen nur am Boden außerhalb einer Cutscene
        {
            if (Input.GetAxis("Jump") > 0)
            {
                player_rigidbody.AddForce(jump_force_vector * jump_force, ForceMode.Impulse);
                Debug.Log("Jumping");
                is_grounded = false;
            }

            translation.x = Input.GetAxis("Horizontal") * Time.deltaTime * movement_speed;
            translation.z = Input.GetAxis("Vertical") * Time.deltaTime * movement_speed;

            if (Input.GetButtonDown("Horizontal") || Input.GetButtonDown("Vertical"))
            {
                GetComponent<AudioSource>().Play();
            }
            else if (!Input.GetButton("Horizontal") && !Input.GetButton("Vertical") && GetComponent<AudioSource>().isPlaying)
            {
                GetComponent<AudioSource>().Stop();
            }
        }

        if (walking && !inCutscene)
        {
            transform.Translate(translation);
        }

        if (!fixedCamera)
        {
            //Umsehen
            rotation.x += Input.GetAxis("Mouse X") * look_sensitivity;
            rotation.y -= Input.GetAxis("Mouse Y") * look_sensitivity;

            rotation.y = Mathf.Clamp(rotation.y, -60, 60);

            transform.rotation = Quaternion.Euler(0, rotation.x, 0);
            game_camera.transform.localRotation = Quaternion.Euler(rotation.y, 0, 0);
        }


        if (Input.GetKeyDown("e"))
        {
            if (atBar)
            {
                audioSource.PlayOneShot(drink, 2.0f);
                if (GameState.Instance.GetCurrentMissionType() == MissionType.DRINK)
                {
                    GameState.Instance.NextMission();
                }
            }
        }

        bool reden_text_active = false;
        if (maybetalking)
        {
            interaction_text.gameObject.SetActive(true);
            interaction_text.text = "[Q] Reden";
            reden_text_active = true;
            if (Input.GetKeyDown("q"))
            {
                interaction_text.gameObject.SetActive(false);
            }
            else
            {
                maybetalking = false;
            }
        }

        RaycastHit hit;
        if (Physics.Raycast(game_camera.transform.position, game_camera.transform.forward, out hit, 4) && !disable_input)
        {
            Door door = hit.collider.GetComponent<Door>();
            Box box = hit.collider.GetComponent<Box>();
            Bed bed = hit.collider.GetComponent<Bed>();
            if (door != null)
            {
                interaction_text.gameObject.SetActive(true);

                if (ansage.isPlaying && !testing)
                {
                    interaction_text.text = "Verschlossen";
                }
                else
                {
                    interaction_text.text = "[E] Öffnen";
                    if (Input.GetKeyDown("e"))
                    {
                        door.Open();
                    }
                }
            }
            if (box != null)
            {
                if (GameState.Instance.GetCurrentMissionType() == MissionType.WORK)
                {
                    if (!box._is_inpected)
                    {
                        interaction_text.gameObject.SetActive(true);
                        interaction_text.text = "[E] Inspizieren";
                        if (Input.GetKeyDown("e"))
                        {
                            box._is_inpected = true;
                            GameState.Instance.NextMission();
                        }
                    }
                    else
                    {
                        interaction_text.gameObject.SetActive(false);
                    }
                }
            }
            if (bed != null)
            {
                if (GameState.Instance.GetCurrentMissionType() == MissionType.SLEEP && !going_to_sleep)
                {
                    interaction_text.gameObject.SetActive(true);
                    interaction_text.text = "[E] Schlafen";
                    if (Input.GetKeyDown("e") && !going_to_sleep)
                    {

                        going_to_sleep = true;
                        disable_input = true;
                        fixedCamera = true;
                        StartCoroutine(EndGame());
                    }
                }
            }
            if (box == null && door == null && cutsceneInteraction == "" && bed == null && !reden_text_active)
            {
                interaction_text.gameObject.SetActive(false);
            }
        }
        else
        {
            if (!reden_text_active) 
            { 
                interaction_text.gameObject.SetActive(false);
            }
        }

        if (cutsceneInteraction != "")
        {
            interaction_text.gameObject.SetActive(true);
            interaction_text.text = cutsceneInteraction;
            if (Input.GetKeyDown("q"))
            {
                interaction_text.gameObject.SetActive(false);
                cutsceneInteraction = "";
            }
        }

        if (Input.GetKeyDown("g"))
        {
            StartCoroutine(ToggleGlitch());
        }

        if (GameState.Instance.is_glitched)
        {
            RenderSettings.ambientLight = real_ambient_color;
        }
        else
        {
            RenderSettings.ambientLight = illusion_ambient_color;
        }

        
        if (mission_text != null)
        {
            if ((!ansage.isPlaying || testing) && !disable_input && !going_to_sleep)
            {
                mission_text.text = "Aufgabe:\n" + GameState.Instance.GetCurrentMission();
            }
            else
            {
                mission_text.text = "";
            }
        }
        
        if (GameState.Instance.DoHitCutscene())
        {
            GameState.Instance.current_day++;
            disable_input = true;
            StartCoroutine(GetHitOnHead());
        }

        if(GameState.Instance.GetCurrentMissionType() == MissionType.DRINK)
        {
            barTalking.enabled = true;
            barTalking.Play();
        }

        ansage.mute = GameState.Instance.is_glitched;
        machine_sound.mute = !GameState.Instance.is_glitched;

        //WakingUp
        if (fixedCamera && startGettingUp == 0)
        {            
            transform.rotation = Quaternion.RotateTowards(transform.rotation, Quaternion.Euler(-90, 180, -90), 55 * Time.deltaTime);
        }
        else if (fixedCamera && startGettingUp==1)
        {
            
            transform.rotation = Quaternion.RotateTowards(transform.rotation, Quaternion.Euler(0, 0, 0), 50 * Time.deltaTime);
            GameObject.Find("bed").GetComponent<Collider>().enabled = false;
        }
        else if (fixedCamera && startGettingUp == 2)
        {
            transform.position = Vector3.Lerp(transform.position, GameObject.Find("WokeUp").transform.position, 1.0f * Time.deltaTime);
        }
    }

    IEnumerator EndGame()
    {
       int num_steps = 128;
       float sleep_time = 1.0f / (float)num_steps;
       for (int i = 0; i < num_steps; i++)
       {
           yield return new WaitForSeconds(sleep_time);
           GameState.Instance.screen_blackness = (((float)(i + 1)) / num_steps);
           AudioListener.volume = 1 - GameState.Instance.screen_blackness;
       }
       SceneManager.LoadScene(0);
    }

    IEnumerator WakingUp()
    {
        gameObject.GetComponent<Collider>().enabled = false;
        gameObject.GetComponent<Rigidbody>().useGravity = false;
        inCutscene = true;
        fixedCamera = true;
        yield return new WaitForSeconds(3.0f);
        audioSource.PlayOneShot(wakeup);
        startGettingUp = 0;
        yield return new WaitForSeconds(3.0f);
        startGettingUp = 1;
        yield return new WaitForSeconds(3.0f);
        startGettingUp = 2;
        yield return new WaitForSeconds(2.0f);
        GameObject.Find("bed").GetComponent<Collider>().enabled = true;
        gameObject.GetComponent<Collider>().enabled = true;
        gameObject.GetComponent<Rigidbody>().useGravity = true;
        startGettingUp = 4;
        fixedCamera = false;
        inCutscene = false;
    }

    IEnumerator GetHitOnHead()
    {
        float num_steps = 16;
        glitchSound.PlayOneShot(hitImpactClip);
        yield return new WaitForSeconds(.1f);
        gameObject.GetComponent<CapsuleCollider>().enabled = false;
        yield return new WaitForSeconds(.2f);
        for (int i = 0; i < num_steps; i++)
        {
            if (i % 4 == 0) 
            { 
                glitchSound.PlayOneShot(glitchSoundClip);
            }
            float intensity = i / (num_steps - 1);
            intensity *= intensity * intensity * intensity * intensity;
            GameState.Instance.glitch_intensity = intensity;
            yield return new WaitForSeconds(.02f);
        }
        GameState.Instance.screen_blackness = 1;
        GameState.Instance.glitch_intensity = 0;
        AudioListener.volume = 0f;
        yield return new WaitForSeconds(8.0f);
        NextDay();
    }


    void NextDay()
    {
        Scene scene = SceneManager.GetActiveScene();
        SceneManager.LoadScene(scene.name);
    }
}
