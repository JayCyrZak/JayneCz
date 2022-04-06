using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class JohnDialogue : MonoBehaviour
{
    [HideInInspector] public string[] day1;
    [HideInInspector] public string[] answers1;
    [HideInInspector] public string[] day2;
    [HideInInspector] public string[] answers2;

    [HideInInspector] public string[] retalk1;

    public AudioClip day1line1, day1line2, day1line3, day1line4,day1line5;
    [HideInInspector] public AudioClip[] day1Audio;

    // Start is called before the first frame update
    void Start()
    {
    }

    void Awake()
    {
        day1Audio = new AudioClip[] {day1line1, null, day1line2, day1line2, day1line3, day1line3, day1line4,day1line5, null, null};

        day1 = new string[] {"Oh hey, Conrad. Alles klar? Ja? Gut, wunderbar.","", "Was? Ja, also Nein. Ach, ich bin nur so aufgeregt.\nWeißt du, ich habe dir doch von meinem Sohn erzählt, oder? Jakob heißt der Gute, mein ganzer Stolz. Tja, aus dem kleinen Jungen ist inzwischen ein ganzer Mann geworden. Gerade vorher habe ich eine Nachricht von seiner Mutter bekommen. Er hat sein Abitur mit einer 1,3 bestanden, ist das zu glauben?", "Was? Ja, also Nein. Ach, ich bin nur so aufgeregt.\n Weißt du, ich habe dir doch von meinem Sohn erzählt, oder? Jakob heißt der Gute, mein ganzer Stolz. Tja, aus dem kleinen Jungen ist inzwischen ein ganzer Mann geworden. Gerade vorher habe ich eine Nachricht von seiner Mutter bekommen. Er hat sein Abitur mit einer 1,3 bestanden, ist das zu glauben?", "Jaja, aber das wichtigste steht erst noch an. Schon in einer Woche versammelt sich die ganze Klasse samt Familienanhang um das große Ereignis gebührend zu feiern.\nIch habe schon den Chefs Bescheid gegeben und sie haben mir doch tatsächlich einen Urlaub gewährt, ist das zu glauben? In ein paar Tagen schon sitze ich einem Shuttle Richtung Erde und darf endlich mal wieder meine Familie besuchen.","Jaja, aber das wichtigste steht erst noch an. Schon in einer Woche versammelt sich die ganze Klasse samt Familienanhang um das große Ereignis gebührend zu feiern.\nIch habe schon den Chefs Bescheid gegeben und sie haben mir doch tatsächlich einen Urlaub gewährt, ist das zu glauben? In ein paar Tagen schon sitze ich einem Shuttle Richtung Erde und darf endlich mal wieder meine Familie besuchen.", "Ja danke danke, aber jetzt muss ich erstmal die ganze Arbeit hier fertig bekommen, damit ihr das nicht alles für mich ausbaden dürft. Wir sehen uns später in der Bar, ja?", "Hey hey, ganz ruhig, ja? Ich habe alles geplant. Deswegen bin ich ja auch gerade so gestresst, ich versuche möglichst weit vorauszuarbeiten. Aber wenn du mich weiter störst, könnte das schwierig werden.","ENDE","ENDE"};
        retalk1 = new string[] { "Ich muss das hier dringend noch fertig bekommen. Wir sehen uns später, ja?", "Das… hier und das… hier…", "Wir quatschen später nochmal, ja? Keine Angst, ich werde nicht ohne einem letzten Feierabendbier in den Urlaub abzischen.", "Vielleicht sollte ich zuerst… Ach ne, eins nach dem anderen, John. Eins nach dem anderen."};
        answers1 = new string[] { "Alles in Ordnung?", "Du wirkst etwas gestresst", "Wow, gratuliere", "Wie der Vater so der Sohn, was?", "Freut mich für dich.", "Und wir dürfen dann deine Arbeit auch noch machen?", "", ""};
        
        day2 = new string[] { "Oh hey, Conrad. Alles klar? Ja? Gut, wunderbar.","", "Was? Ja, also Nein. Ach, ich bin nur so aufgeregt.\nWeißt du, ich habe dir doch von meinem Sohn erzählt, oder? Jakob heißt der Gute, mein ganzer Stolz. Tja, aus dem kleinen Jungen ist inzwischen ein ganzer Mann geworden. Gerade vorher habe ich eine Nachricht von seiner Mutter bekommen. Er hat sein Abitur mit einer 1,3 bestanden, ist das zu glauben?", "Was? Ja, also Nein. Ach, ich bin nur so aufgeregt.\nWeißt du, ich habe dir doch von meinem Sohn erzählt, oder? Jakob heißt der Gute, mein ganzer Stolz. Tja, aus dem kleinen Jungen ist inzwischen ein ganzer Mann geworden. Gerade vorher habe ich eine Nachricht von seiner Mutter bekommen. Er hat sein Abitur mit einer 1,3 bestanden, ist das zu glauben?", "Jaja, aber das wichtigste steht… du weißt es schon? Wird mal nicht albern ich habe doch gerade selbst erst davon erfahren.\nJedenfalls versammelt sich in einer Woche die ganze Klasse samt Familienanhang um das große Ereignis gebührend zu feiern. Ich habe schon den Chefs Bescheid gegeben und sie haben mir doch tatsächlich einen Urlaub gewährt, ist das zu glauben? In ein paar Tagen schon sitze ich einem Shuttle Richtung Erde und darf endlich mal wieder meine Familie besuchen.","Jedenfalls versammelt sich in einer Woche die ganze Klasse samt Familienanhang um das große Ereignis gebührend zu feiern. Ich habe schon den Chefs Bescheid gegeben und sie haben mir doch tatsächlich einen Urlaub gewährt, ist das zu glauben? In ein paar Tagen schon sitze ich einem Shuttle Richtung Erde und darf endlich mal wieder meine Familie besuchen.", "Jaja ich… was meinst du mit „nochmal“? Ich habe doch bisher durchgearbeitet. Da habe ich mir ja wohl die paar Tage verdient. Ich versuche auch möglichst weit vorauszuarbeiten. Du entschuldigst mich?", "Hey hey, tut mir leid, dass ich dich mit meinen Geschichten belästige. So weit ich weiß, hast du mich angesprochen. Naja, ich werde dann wohl mal weiterarbeiten, vielleicht trifft man sich ja später und wir quatschen nochmal darüber.","ENDE","ENDE"};
        answers2 = new string[] { "Alles in Ordnung?", "Immer noch gestresst?", "Ja, das weiß ich doch schon", "Ach was…", "Du hast nochmal um Urlaub gebeten?", "Erzählst du immer alles doppelt?", "", "" };
    }
    // Update is called once per frame
    void Update()
    {

    }
}
