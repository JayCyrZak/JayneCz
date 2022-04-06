using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class Dialogue : MonoBehaviour
{    
    
    [HideInInspector] public bool talking;
    [HideInInspector] public bool spokenTo;

    [HideInInspector] public string line;
    [HideInInspector] public string answer1;
    [HideInInspector] public string answer2;
    [HideInInspector] public int chapter;
    [HideInInspector] public int chapterans;
    private int choice;
    [HideInInspector] public bool waiting;
    [HideInInspector] public bool questDone;

    public Image profilepic;
    public Sprite ProfilePicture;
    public float textSpeed = 7.0f;

    public Text dialogueTxt;
    public Text dialogueName;
    [HideInInspector] public GameObject TextBox;
    [HideInInspector] public GameObject TextBoxN;
    [HideInInspector] public string DiaText;
    [HideInInspector] public string AnsText;
    private bool finishedTyping = false;

    public AudioSource audioSource;
    public float audioVolume = 2.0f;

    // Start is called before the first frame update
    void Start()
    {
        spokenTo = false;
        line = "";
        talking = false;
        chapter = 0;
        chapterans = 0;
        choice = 1;
        waiting = false;
        questDone = false;
        //profilepic.enabled = false;

        //dialogueTxt.text = "";
        //dialogueName.text = "";
        TextBox = GameObject.Find("TextBox");
        TextBoxN = GameObject.Find("TextBoxN");
    }

    

    // Update is called once per frame
    void Update()
    {
        if (talking)
        {
            //Aktivieren der Textbox und des Screentexts
            //TextBox.GetComponent<Image>().enabled = true;
            //TextBoxN.GetComponent<Image>().enabled = true;
            //profilepic.enabled = true;
            //profilepic.sprite = ProfilePicture;
            //gameObject.GetComponent<ScreenText>().textName.text = gameObject.name;
            if (!waiting) 
            {
                //Dialog
                if (!spokenTo)
                {
                    switch (GameState.Instance.current_day)
                    {
                        case 0:
                            switch (gameObject.name)
                            {
                                case "John":
                                    line = gameObject.GetComponent<JohnDialogue>().day1[chapter];
                                    answer1 = gameObject.GetComponent<JohnDialogue>().answers1[chapterans];
                                    answer2 = gameObject.GetComponent<JohnDialogue>().answers1[chapterans + 1];
                                    audioSource.PlayOneShot(gameObject.GetComponent<JohnDialogue>().day1Audio[chapter], audioVolume);
                                    break;
                                case "Pia":
                                    line = gameObject.GetComponent<PiaDialogue>().day1[chapter];
                                    answer1 = gameObject.GetComponent<PiaDialogue>().answers1[chapterans];
                                    answer2 = gameObject.GetComponent<PiaDialogue>().answers1[chapterans + 1];
                                    audioSource.PlayOneShot(gameObject.GetComponent<PiaDialogue>().day1Audio[chapter], audioVolume);
                                    break;
                                case "Miles":
                                    line = gameObject.GetComponent<MilesDialogue>().day1[chapter];
                                    answer1 = gameObject.GetComponent<MilesDialogue>().answers1[chapterans];
                                    answer2 = gameObject.GetComponent<MilesDialogue>().answers1[chapterans + 1];
                                    break;
                                case "Hilde":
                                    line = gameObject.GetComponent<HildeDialogue>().day1[chapter];
                                    answer1 = gameObject.GetComponent<HildeDialogue>().answers1[chapterans];
                                    answer2 = gameObject.GetComponent<HildeDialogue>().answers1[chapterans + 1];
                                    audioSource.PlayOneShot(gameObject.GetComponent<HildeDialogue>().day1Audio[chapter], audioVolume);
                                    break;
                                case "Alfred":
                                    line = gameObject.GetComponent<AlfredDialogue>().day1[chapter];
                                    answer1 = gameObject.GetComponent<AlfredDialogue>().answers1[chapterans];
                                    answer2 = gameObject.GetComponent<AlfredDialogue>().answers1[chapterans + 1];
                                    audioSource.PlayOneShot(gameObject.GetComponent<AlfredDialogue>().day1Audio[chapter], audioVolume);
                                    break;
                                default:
                                    line = "ERROR";
                                    break;
                            }
                            break;
                        case 1:
                            switch (gameObject.name)
                            {
                                case "John":
                                    line = gameObject.GetComponent<JohnDialogue>().day2[chapter];
                                    answer1 = gameObject.GetComponent<JohnDialogue>().answers2[chapterans];
                                    answer2 = gameObject.GetComponent<JohnDialogue>().answers2[chapterans + 1];
                                    break;
                                case "Pia":
                                    line = gameObject.GetComponent<PiaDialogue>().day2[chapter];
                                    answer1 = gameObject.GetComponent<PiaDialogue>().answers2[chapterans];
                                    answer2 = gameObject.GetComponent<PiaDialogue>().answers2[chapterans + 1];
                                    break;
                                case "Miles":
                                    line = gameObject.GetComponent<MilesDialogue>().day2[chapter];
                                    answer1 = gameObject.GetComponent<MilesDialogue>().answers2[chapterans];
                                    answer2 = gameObject.GetComponent<MilesDialogue>().answers2[chapterans + 1];
                                    break;
                                case "Hilde":
                                    line = gameObject.GetComponent<HildeDialogue>().day2[chapter];
                                    answer1 = gameObject.GetComponent<HildeDialogue>().answers2[chapterans];
                                    answer2 = gameObject.GetComponent<HildeDialogue>().answers2[chapterans + 1];
                                    break;
                                case "Alfred":
                                    line = gameObject.GetComponent<AlfredDialogue>().day2[chapter];
                                    answer1 = gameObject.GetComponent<AlfredDialogue>().answers2[chapterans];
                                    answer2 = gameObject.GetComponent<AlfredDialogue>().answers2[chapterans + 1];
                                    break;
                                default:
                                    line = "ERROR";
                                    break;
                            }
                            break;
                        default:
                            break;

                    }
                    if (choice == 1)
                    {
                        chapter += 1;
                    }                    
                }
                else if (spokenTo)
                {
                    //One-Liners nachdem Conrad schon mit dem NPC gesprochen hat
                    switch (gameObject.name)
                    {
                        case "John":
                            line = gameObject.GetComponent<JohnDialogue>().retalk1[chapter];
                            break;
                        case "Pia":
                            line = gameObject.GetComponent<PiaDialogue>().retalk1[chapter];
                            audioSource.PlayOneShot(gameObject.GetComponent<PiaDialogue>().retalk1Audio[chapter], audioVolume);
                            break;
                        case "Miles":
                            line = gameObject.GetComponent<MilesDialogue>().retalk1[chapter];
                            break;
                        case "Hilde":
                            line = gameObject.GetComponent<HildeDialogue>().retalk1[chapter];
                            audioSource.PlayOneShot(gameObject.GetComponent<HildeDialogue>().retalk1Audio[chapter], audioVolume);
                            break;
                        case "Alfred":
                            line = gameObject.GetComponent<AlfredDialogue>().retalk1[chapter];
                            audioSource.PlayOneShot(gameObject.GetComponent<AlfredDialogue>().retalk1Audio[chapter], audioVolume);
                            break;
                        default:
                            Debug.Log("Error");
                            break;
                    }
                    answer1 = "";
                    answer2 = "";
                    if (chapter < 3)
                    {
                        chapter++;
                    }
                    else { chapter = 0; }
                }

                //Textausgabe
                if ((answer1 != "")&&(answer1 != "WEITER"))
                {
                    StopCoroutine("PlayText");
                    DiaText = (line + "\n \n");
                    AnsText = ("Q: " + answer1 + "\nE: " + answer2);
                    StartCoroutine("PlayText");
                }
                else if (answer1 == "WEITER")
                {
                    StopCoroutine("PlayText");
                    DiaText = (line + "\n");
                    AnsText = ("E:  < " + answer2 + " > ");
                    StartCoroutine("PlayText");
                }
                else
                {
                    StopCoroutine("PlayText");
                    DiaText = (line + "\n\n\n");
                    AnsText = ("Drücke E um das Gespräch zu beenden.");                    
                    StartCoroutine("PlayText");
                }

                dialogueName.text = (gameObject.name + ": ");
                waiting = true;
            }
            else
            {
                //Gespräch beenden
                if ((answer1 == "") && (Input.GetKeyDown("e"))&&(finishedTyping))
                {
                    talking = false;
                    waiting = false;
                    PlayerController.talking = false;
                    audioSource.Stop();
                    if (!spokenTo)
                    {
                        chapter = 0;
                        chapterans = 0;
                    }
                    spokenTo = true;
                    dialogueTxt.text = "";
                    dialogueName.text = "";
                    //TextBox.GetComponent<Image>().enabled = false;
                    //TextBoxN.GetComponent<Image>().enabled = false;
                }
                else if ((answer1 != "")&&(finishedTyping))
                {
                    //Antwort auswählen
                    if (Input.GetKeyDown("q"))
                    {
                        chapter += 1;
                        chapterans += 2;
                        choice = 1;
                        waiting = false;
                        audioSource.Stop();
                    }
                    else if (Input.GetKeyDown("e"))
                    {
                        chapter += 2;
                        chapterans += 2;
                        choice = 2;
                        waiting = false;
                        audioSource.Stop();
                    }
                }
            }


        }
    }

    IEnumerator PlayText()
    {
        dialogueTxt.text = "";
        finishedTyping = false;

        foreach (char c in DiaText)
        {
            dialogueTxt.text += c;
            yield return new WaitForSeconds(0.0055f * textSpeed);
        }
        dialogueTxt.text += AnsText;

        finishedTyping = true;
    }

}
