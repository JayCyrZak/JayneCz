using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using UnityEngine.AI;

public class NPCController : MonoBehaviour
{

    public float movement_speed = 5.0f;
    public AudioClip hello;
    public AudioClip cutscene1;
    public AudioClip cutscene2;
    public AudioSource audioSource;
    public float audioVolume = 2.0f;

    private Vector3 translation;
    private float rotationW;
    private float distPlayer;
    private Vector3 target;
    public bool walkRandom;
    private bool lookingatplayer = false;
    private RaycastHit hit;
    private GameObject hitDoor;

    private GameObject player;
    private bool saidHello;
    private bool falling = false;
    private bool standingUp = false;

    [HideInInspector] public GameObject TextBox;

    public Material EyesOpened;
    public Material EyesHalfClosed;
    public Material EyesClosed;
    private float nextActionTime = 0.0f;
    public float BlinkingPeriod = 5.0f;

    private bool wallhere = false;
    UnityEngine.AI.NavMeshAgent agent;
    private IEnumerator coroutine;
    public string headingTowards;
    public string goalHistory;
    private bool waitingfordoor;
    public GameObject beer;

    private bool already_talked_to_player = false;

    [HideInInspector] public bool inCutscene;
    [HideInInspector] public int cutscene;
    private string DiaText = "";
    private bool trigger = false;

    // Start is called before the first frame update
    void Start()
    {
        translation = new Vector3();
        already_talked_to_player = false;

        player = GameObject.FindWithTag("Player");
        TextBox = GameObject.Find("TextBox");
        if (gameObject.name != "Alfred") { beer.GetComponent<Renderer>().enabled = false; }
        //TextBox.GetComponent<Image>().enabled = false;
        //TextBoxN.GetComponent<Image>().enabled = false;

        agent = GetComponent<UnityEngine.AI.NavMeshAgent>();
        agent.updateRotation = false;
        //goalsObj = GameObject.FindWithTag("Goals");        
        inCutscene = true;
        StartCoroutine("playCutscene");
    }

    void OnCollisionEnter (Collision collision)
    {
        //Kollison mit Wand
        if ((collision.gameObject.tag != "Floor") && (collision.gameObject.tag != "Player")&&(walkRandom)) 
        {
            wallhere = true;
            StartCoroutine("WallCollision");
        }        
    }

    void OnTriggerEnter (Collider collision)
    {
        if (collision.gameObject.tag == "Door")
        {
            hitDoor = collision.gameObject;
            StartCoroutine("OpenDoor");
        }
    }

    void OnCollisionExit (Collision collision)
    {
        if ((collision.gameObject.name != "Floor") && (collision.gameObject.tag != "Player")&&(walkRandom))
        {
            wallhere = false;
            Debug.Log(gameObject.name + " ist von der Wand losgekommen.");
        }
    }

    // Update is called once per frame
    void Update()
    {
        if (!inCutscene)
        {
            distPlayer = Vector3.Distance(player.transform.position, transform.position);
            if (distPlayer > 5)
            {
                if (walkRandom)
                {
                    //Laufen
                    translation.x = Time.deltaTime * movement_speed;
                    transform.Translate(translation);
                }
                saidHello = false;
                if (!waitingfordoor)
                {
                    //agent.speed = 3.5f;
                    agent.Resume();
                }
                lookingatplayer = false;
                
                //Zurücksetzen eines durch Weggehen abgebrochenen Dialoges
                if (gameObject.GetComponent<Dialogue>().talking == true)
                {
                    //TextBox.GetComponent<Image>().enabled = false;
                    gameObject.GetComponent<Dialogue>().chapter = 0;
                    gameObject.GetComponent<Dialogue>().chapterans = 0;
                    PlayerController.talking = false;
                }

                //Bildschirmtexte und Textfelder ausblenden
                gameObject.GetComponent<Dialogue>().talking = false;
                gameObject.GetComponent<Dialogue>().waiting = false;
                gameObject.GetComponent<Dialogue>().audioSource.Stop();
                if (!PlayerController.talking)
                {
                    gameObject.GetComponent<Dialogue>().dialogueTxt.text = "";
                    gameObject.GetComponent<Dialogue>().dialogueName.text = "";
                }
            }
            else
            {   //Schon gesprochen?         
                if ((gameObject.GetComponent<Dialogue>().spokenTo) && (!gameObject.GetComponent<Dialogue>().talking))
                {
                    if (walkRandom)
                    {
                        //Weitergehen
                        translation.x = Time.deltaTime * movement_speed;
                        transform.Translate(translation);
                    }
                    saidHello = false;
                }
                else if (PlayerController.talking == gameObject.GetComponent<Dialogue>().talking)
                {   //Umdrehen zum Player
                    lookingatplayer = true;
                    //agent.speed = 0;
                    agent.Stop();
                    target = transform.position - player.transform.position;
                    target.y = 0;
                    target = Quaternion.Euler(0, 90, 0) * target;
                    float singleStep = 2.0f * Time.deltaTime;
                    Vector3 newDirection = Vector3.RotateTowards(transform.forward, target, singleStep, 0.00f);
                    transform.rotation = Quaternion.LookRotation(newDirection);
                }

                //Gespräch starten
                if ((!PlayerController.talking) && (!PlayerController.atBar))
                {
                    if (Input.GetKeyDown("q"))
                    {
                        PlayerController.maybetalking = false;
                        if (!already_talked_to_player)
                        {
                            already_talked_to_player = true;
                            if (GameState.Instance.GetCurrentMissionType() == MissionType.TALK)
                            {
                                GameState.Instance.NextMission();
                            }
                        }
                        gameObject.GetComponent<Dialogue>().dialogueTxt.text = "";
                        //TextBox.GetComponent<Image>().enabled = true;
                        //TextBoxN.GetComponent<Image>().enabled = true;
                        gameObject.GetComponent<Dialogue>().talking = true;
                        PlayerController.talking = true;
                    }
                    else if (gameObject.GetComponent<Dialogue>().dialogueTxt.text == "")
                    {
                        PlayerController.maybetalking = true;
                        //gameObject.GetComponent<ScreenText>().textName.text = "Q: Reden";
                    }
                }

                //Hallo sagen
                if ((!saidHello) && (!gameObject.GetComponent<Dialogue>().spokenTo))
                {
                    audioSource.PlayOneShot(hello, audioVolume);
                    saidHello = true;
                }
            }
        }

        //Blinzeln
        if (Time.time > nextActionTime) 
        {
                nextActionTime = Time.time + BlinkingPeriod;
                StartCoroutine("Blinzeln");
        }

        if (PlayerController.atBar)
        {
            gameObject.GetComponent<Dialogue>().dialogueName.text = "";
        }

        //Fallanimation
        if (falling)
        {
            transform.rotation = Quaternion.RotateTowards(transform.rotation, Quaternion.Euler(0, -90, -90), 200 * Time.deltaTime);
            transform.position = Vector3.Lerp(transform.position, new Vector3(transform.position.x,0,-23.5f), 5.0f * Time.deltaTime);
            player.GetComponent<AudioSource>().Play();
            if (trigger) {player.transform.position = Vector3.Lerp(player.transform.position, GameObject.Find("PlayerPos").transform.position, 2.0f * Time.deltaTime); }
        }
        else if (standingUp)
        {
            player.GetComponent<AudioSource>().Stop();
            target = transform.position - player.transform.position;
            target = Quaternion.Euler(0, 90, 0) * target;
            float singleStep = 2.0f * Time.deltaTime;
            Vector3 newDirection = Vector3.RotateTowards(transform.forward, target, singleStep, 0.0f);
            transform.rotation = Quaternion.LookRotation(newDirection);
            transform.position = Vector3.Lerp(transform.position, new Vector3(transform.position.x, 1.7f, transform.position.z), 5.0f * Time.deltaTime);
        }

        //Feierabendbier->Den Spieler ansehen
        if (GameState.Instance.GetCurrentMissionType() == MissionType.DRINK)
        {
            lookingatplayer = true;
            target = transform.position - player.transform.position;
            target = Quaternion.Euler(0, 90, 0) * target;
            float singleStep = 2.0f * Time.deltaTime;
            Vector3 newDirection = Vector3.RotateTowards(transform.forward, target, singleStep, 0.00f);
            transform.rotation = Quaternion.LookRotation(newDirection);
        }

    }

    void LateUpdate()
    {
        if ((!lookingatplayer)&&(!inCutscene))
        {
           transform.rotation = Quaternion.LookRotation(Vector3.RotateTowards(transform.forward, Quaternion.Euler(0, -90, 0) * agent.velocity.normalized, 2.0f * Time.deltaTime, 0.0f));            
        }
    }

    IEnumerator Blinzeln()
    {
        GetComponent<Renderer>().material = EyesHalfClosed;
        yield return new WaitForSeconds(.1f);
        GetComponent<Renderer>().material = EyesClosed;
        yield return new WaitForSeconds(.1f);
        GetComponent<Renderer>().material = EyesHalfClosed;
        yield return new WaitForSeconds(.1f);
        GetComponent<Renderer>().material = EyesOpened;
    }

    IEnumerator WallCollision()
    {
        while (wallhere)
        {
            if (rotationW > 360)
            {
                rotationW -= 360;
            }
            rotationW += Random.Range(20.0f, 40.0f);
            transform.rotation = Quaternion.Euler(0, rotationW, 0);
            Debug.Log(gameObject.name + " ist gegen eine Wand gelaufen. Rotation um " + rotationW + " Grad.");
            yield return new WaitForSeconds(2.0f);
        }
    }

    IEnumerator OpenDoor()
    {
        waitingfordoor = true;
        //agent.speed = 0;
        agent.Stop();
        hitDoor.GetComponent<Door>().Open();
        yield return new WaitForSeconds(1.0f);
        //agent.speed = 3.5f;
        agent.Resume();
        waitingfordoor = false;
    }

    IEnumerator MoveTo(GameObject goal)
    {
        agent.destination = goal.transform.position;
        Debug.Log(gameObject.name + " macht sich auf zum Zielpunkt " + goal.name + ".  Entfernung zum Ziel: " + (Mathf.Round((Vector3.Distance(transform.position, agent.destination) / 3) * 100f) / 100f) + " Meter.");

        while (Vector3.Distance(gameObject.transform.position, agent.destination) > 2.5f)
        {            
            yield return new WaitForSeconds(7.0f);
        }

        goal.GetComponent<Navigation>().isTarget = false;
        goal.GetComponent<Navigation>().comingNPC = "";
        goal.GetComponent<Navigation>().justVisited = true;
        headingTowards = "";
        goalHistory += (goal.name + " -> ");
        Debug.Log(gameObject.name + " ist bei " + goal.name + " angekommen.");        

        if ((GameState.Instance.GetCurrentMissionType() == MissionType.DRINK))
        {
            beer.GetComponent<Renderer>().enabled = true;

            if (goal.name != "Bar_Counter1" && goal.name != "Bar_Counter2" && goal.name != "Bar_Table1" && goal.name != "Bar_Table2" && goal.name != "Bar_Table3")
            {
                goal = chooseGoal(goal);
                headingTowards = goal.name;
                coroutine = MoveTo(goal);
                StartCoroutine(coroutine);
            }
        }
        else
        {
            goal = chooseGoal(goal);
            headingTowards = goal.name;
            coroutine = MoveTo(goal);
            StartCoroutine(coroutine);
        } 
    }

    //Zufällige Auswahl eines Navigationsziels
    GameObject chooseGoal(GameObject currentGoal)
    {
        int r = 1;
        if (GameState.Instance.GetCurrentMissionType() == MissionType.DRINK)
        {
            r = Random.Range(4, 9);
        }
        else
        {
            r = Random.Range(1, 14);
        }
        GameObject g;
        switch (r)
        {
            case 1:
                g = GameObject.Find("Storage1");
                break;
            case 2:
                g = GameObject.Find("Storage2");
                break;
            case 3:
                g = GameObject.Find("Center");
                break;
            case 4:
                g = GameObject.Find("Bar_Counter1");
                break;
            case 5:
                g = GameObject.Find("Bar_Counter2");
                break;
            case 6:
                g = GameObject.Find("Bar_Table1");
                break;
            case 7:
                g = GameObject.Find("Bar_Table2");
                break;
            case 8:
                g = GameObject.Find("Bar_Table3");
                break;
            case 9:
                g = GameObject.Find("Toilet1");
                break;
            case 10:
                g = GameObject.Find("Toilet2");
                break;
            case 11:
                g = GameObject.Find("Engine1");
                break;
            case 12:
                g = GameObject.Find("Engine2");
                break;
            case 13:
                g = GameObject.Find("Quartiers");
                break;
            default:
                g = GameObject.Find("Center");
                break;
        }
        if ((g.name == currentGoal.name)||(g.GetComponent<Navigation>().isTarget == true))
        {
            return chooseGoal(currentGoal);
        }
        else if ((g.name != currentGoal.name) && (g.GetComponent<Navigation>().isTarget == false))
        {
            g.GetComponent<Navigation>().isTarget = true;
            g.GetComponent<Navigation>().comingNPC = gameObject.name;
            return g;
        }
        else
        {
            return g;
        }
    }

    IEnumerator playCutscene ()
    {        
        switch (gameObject.name)
        {
            case "Hilde":
                PlayerController.talking = true;                
                gameObject.GetComponent<Animator>().enabled = false;
                yield return new WaitUntil(() => GameObject.Find("door (player)").GetComponent<Door>().is_opening); //Warte darauf, dass sich die Tür des Spielers öffnet

                PlayerController.inCutscene = true;                
                transform.position = GameObject.Find("Hilde_Start").transform.position; //An Startposition teleportieren                
                agent.destination = GameObject.Find("Hilde_Falling").transform.position; //Vor die Tür laufen
                yield return new WaitForSeconds(0.5f);
                audioSource.PlayOneShot(cutscene1, 1.0f); //Audio abspielen
                DiaText = ("Oh Gott, schon wieder zu spät...");
                StartCoroutine("PlayText");
                yield return new WaitForSeconds(2.0f);
                agent.enabled = false;
                falling = true; //Hinfallen
                PlayerController.cutsceneInteraction = "[Q] Aufhelfen";
                //gameObject.GetComponent<ScreenText>().textName.text = "Q: Aufhelfen";
                yield return new WaitUntil(() => Input.GetKeyDown("q"));
                trigger = true;
                yield return new WaitForSeconds(1.5f);
                falling = false;
                trigger = false;
                standingUp = true;//Aufstehen                
                audioSource.PlayOneShot(cutscene2, 4.0f); //Audio abspielen
                DiaText = ("Ich bin vielleicht ein Schussel. Danke dir. Wir sehen uns dann, ja?");
                StartCoroutine("PlayText");

                yield return new WaitForSeconds(4.0f);
                standingUp = false;
                agent.enabled = true;
                gameObject.GetComponent<Animator>().enabled = true;
                coroutine = MoveTo(chooseGoal(GameObject.Find("Quartiers")));
                StartCoroutine(coroutine);                
                yield return new WaitForSeconds(1.5f);
                inCutscene = false;
                PlayerController.talking = false;
                PlayerController.inCutscene = false;
                break;

            case "Alfred":                
                transform.position = GameObject.Find("Bar_Table1").transform.position;
                gameObject.GetComponent<Animator>().enabled = false;
                //Alfred wartet darauf, dass der Spieler den Raum betritt
                yield return new WaitUntil(() => player.GetComponent<PlayerController>().trigger_ca1);
                inCutscene = true;
                audioSource.PlayOneShot(cutscene1, 3.0f);
                PlayerController.talking = true;
                PlayerController.inCutscene = true;
                yield return new WaitForSeconds(1.0f);
                DiaText = ("Ja um Himmels willen!");
                StartCoroutine("PlayText");                

                agent.destination = GameObject.Find("Engine1").transform.position;
                agent.speed = 10.0f;
                yield return new WaitForSeconds(1.5f);
                agent.speed = 0.5f;
                yield return new WaitForSeconds(0.5f);
                GameObject.Find("fx_fire_a").SetActive(false);                
                yield return new WaitForSeconds(2.0f);
                gameObject.GetComponent<Animator>().enabled = true;
                agent.speed = 3.5f;
                coroutine = MoveTo(chooseGoal(GameObject.Find("Engine1")));
                StartCoroutine(coroutine);
                yield return new WaitForSeconds(1.5f);
                inCutscene = false;
                PlayerController.talking = false;
                PlayerController.inCutscene = false;
                break;

            default:
                gameObject.GetComponent<Rigidbody>().isKinematic = false;
                yield return new WaitUntil(() => GameObject.Find("door (player)").GetComponent<Door>().is_opening);
                yield return new WaitUntil(() => !PlayerController.inCutscene);                
                inCutscene = false;
                coroutine = MoveTo(chooseGoal(GameObject.Find("Center")));
                StartCoroutine(coroutine);
                break;
        }
        
    }

    IEnumerator PlayText()
    {
        PlayerController.talking = true;
        gameObject.GetComponent<Dialogue>().dialogueTxt.text = "";
        gameObject.GetComponent<Dialogue>().dialogueName.text = (gameObject.name + ":");

        foreach (char c in DiaText)
        {
            gameObject.GetComponent<Dialogue>().dialogueTxt.text += c;
            yield return new WaitForSeconds(0.0055f * 11.0f);
        }
        gameObject.GetComponent<Dialogue>().dialogueTxt.text = "";
        gameObject.GetComponent<Dialogue>().dialogueName.text = "";
    }
}
