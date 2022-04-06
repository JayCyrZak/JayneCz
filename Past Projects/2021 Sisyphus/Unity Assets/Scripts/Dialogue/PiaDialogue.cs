using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class PiaDialogue : MonoBehaviour
{
    [HideInInspector] public string[] day1;
    [HideInInspector] public string[] answers1;
    [HideInInspector] public string[] day2;
    [HideInInspector] public string[] answers2;

    [HideInInspector] public string[] retalk1;

    public AudioClip day1line1, day1line2, day1line3, day1line4, day1line5, day1line6, day1retalk1, day1retalk2, day1retalk3, day1retalk4, missing, bye;
    [HideInInspector] public AudioClip[] day1Audio;
    [HideInInspector] public AudioClip[] retalk1Audio;

    // Start is called before the first frame update
    void Start()
    {
    }

    void Awake()
    {
        day1Audio = new AudioClip[] {day1line1, null, day1line2, day1line3, day1line4, day1line5, day1line6, day1line6, null, null};
        retalk1Audio = new AudioClip[] { day1retalk1, day1retalk2, day1retalk3, day1retalk4 };

        day1 = new string[] { "Oh hey, lange nicht mehr gesehen. Wie geht’s wie steht’s?", "", "Oh, danke der Nachfrage. Ja soweit scheint alles in Ordnung zu sein. Die Ärztin meint schon, so lange kann es nicht mehr dauern. Naja, jedenfalls muss ich aber erst mal schön am Ball bleiben, damit uns hinterher nicht das Geld fehlt. Gary ist… sagen wir nicht gerade flüssig.", "Du bist mit ja ein Charmeur. Ja sie wächst immer weiter, die Ärztin meinte lange kann es nicht mehr dauern. Naja, jedenfalls muss ich aber erst mal schön am Ball bleiben, damit uns hinterher nicht das Geld fehlt. Gary ist… sagen wir nicht gerade flüssig.", "Ja, seitdem die Automatisierung immer weiter voranschreitet, ist der Bedarf an Handarbeitern stark zurückgegangen. Solange du keiner dieser versifften Informatiker-Studien-Typen bist, kann dich heutzutage keine Firma mehr gebrauchen. Aber das einzige was jetzt erstmal zählt, ist dass die Kleine gesund und munter auf die Welt kommt. Ich habe zwar schon länger keine Tritte mehr abbekommen, aber dass soll wohl normal sein, anscheinend ein Zeichen, dass es bald soweit ist.", "Das ist ja wohl die plumpste Anmache, die ich je gehört habe. Gary gibt sich enorm viel Mühe, kann er ja nichts dafür, wenn diese ganzen Technikfritzen nur noch IT-ler aus ihren Kellern holen, ist für die Handwerker eben kein Platz mehr in dieser ach so modernen Gesellschaft. Aber das einzige was jetzt erstmal zählt, ist dass die Kleine gesund und munter auf die Welt kommt. Ich habe zwar schon länger keine Tritte mehr abbekommen, aber dass soll wohl normal sein, anscheinend ein Zeichen, dass es bald soweit ist.", "Danke, danke wird schon werden. Wir sehen uns später in der Bar, ja ?", "Ach, das wird schon werden. Wir sehen uns später in der Bar, ja ?", "ENDE", "ENDE"};
        answers1 = new string[] { "Wie geht es deinem Nachwuchs?", "Du bist aber fett geworden", "Sucht er immer noch Arbeit?", "Mit jemand anderen wärst du besser dran", "Na, dann viel Erfolg", "Übertreib es aber nicht","",""};
        retalk1 = new string[] {"Emily… oder doch lieber Anna… was denkst du meine Kleine?", "Zeit ist Geld", "War das… komm mach es nochmal. Für Mami…", "Hoffentlich zahlt Sisypho ein anständiges Elterngeld" };

        day2 = new string[] { "Oh hey, lange nicht mehr gesehen.Wie geht’s wie steht’s ? ", "Oh, danke der Nachfrage. Ja soweit scheint alles in Ordnung zu sein. Die Ärztin meint schon, so lange kann es nicht mehr dauern. Jedenfalls muss ich aber erst mal schön am Ball bleiben, damit uns hinterher nicht das Geld fehlt. Gary ist… sagen wir nicht gerade flüssig.", "Ja, wenn du dich immer erst so spät aus der Kajüte traust. Ich habe dich wahrscheinlich vor einer Woche das letzte Mal in Persona sehen dürfen. Vielleicht arbeite ich ja auch einfach zu viel. Weißt du, jetzt wo ich und Gary Nachwuchs erwarten, wird es mit dem Geld etwas knapp. Er ist derzeit… sagen wir nicht gerade flüssig.", "Ja, seitdem die Automatisierung immer weiter… Wie du weißt? Hast du mit Gary Kontakt? Das hätte er mir doch bestimmt mal erzählt… Andererseits, seitdem er so erfolglos auf Jobsuche ist, redet er allgemein nicht mehr so viel…", "Seit gestern? Quatsch, seit Monaten schon. Scheinbar ist der Arbeitsmarkt mittlerweile stark umkämpft. Nur noch Techniker suchen die heutzutage, da hat es ein Schulabbrecher wie Gary, der seine Steuererklärung noch per Post einschickt, natürlich schwer.", "Aber das einzige was jetzt erstmal zählt, ist dass die Kleine gesund und munter auf die Welt kommt. Ich habe zwar schon länger keine Tritte mehr abbekommen, aber dass soll wohl normal sein, anscheinend ein Zeichen, dass es bald soweit ist.", "Danke, danke wird schon werden. Wir sehen uns später in der Bar, ja?"};
        answers2 = new string[] { "Wie geht es deinem Nachwuchs?", "Lange nicht gesehen?", "Ja, ich weiß", "Seit gestern keinen Job gefunden?", "Na, dann viel Erfolg", "Okay...", "", "" };

    }
    // Update is called once per frame
    void Update()
    {
        
    }
}
