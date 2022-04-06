using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class MilesDialogue : MonoBehaviour
{
    [HideInInspector] public string[] day1;
    [HideInInspector] public string[] answers1;
    [HideInInspector] public string[] day2;
    [HideInInspector] public string[] answers2;

    [HideInInspector] public string[] retalk1;

    // Start is called before the first frame update
    void Start()
    {
    }

    void Awake()
    {
        day1 = new string[] { "Oh hey, Mann wie geht’s?","", "Merkt man es mir so deutlich an? Ja, ich bin ganz ehrlich, dieser Job macht mich noch wahnsinnig. Du wischst einen Raum und schon bevor du den nächsten erreichst, ist der letzte wieder dreckig.", "Merkt man es mir so deutlich an? (seufzt) Ja, ich bin ganz ehrlich, dieser Job macht mich noch wahnsinnig. Du wischst einen Raum und schon bevor du den nächsten erreichst, ist der letzte wieder dreckig.", "Ist es. Ich komm mir vor wie dieser eine Grieche da. Du weißt schon aus dieser Sage, wo der auch immer wieder dasselbe machen muss mit diesem Felsen oder was auch immer das war. Anders als der Typ habe ich aber einen Ausweg aus meiner Lage.", "Gefühlt nicht. Ich komm mir vor wie dieser eine Grieche da. Du weißt schon aus dieser Sage, wo der auch immer wieder dasselbe machen muss mit diesem Felsen oder was auch immer das war. Anders als der Typ habe ich aber einen Ausweg aus meiner Lage.", "Jap, auch wenn das den Bossen da oben wahrscheinlich weniger gefallen wird. Ich habe meine Kündigung schon getippt. Gleich nach Feierabend gehe ich zur Mailkammer und lasse mein Schreiben verschicken. Keine Ahnung, warum die hier im Jahre 2079 noch keine Internetverbindung hinkriegen, aber hey, so ein Brief hat ja auch was. Richtig Retro.", "Was? Oh Gott, nein nein, keine Angst so weit ist es mit meiner Frustration noch nicht gekommen. \nIch habe meine Kündigung schon getippt. Gleich nach Feierabend gehe ich zur Mailkammer und lasse mein Schreiben verschicken. Keine Ahnung, warum die hier im Jahre 2079 noch keine Internetverbindung hinkriegen, aber hey, so ein Brief hat ja auch was. Richtig Retro.", "Ja Mann, ganz im Ernst das ganze hier bringt doch auch nichts. Eine Beförderung kannst‘e knicken und viel Abwechslung kommt hier auch nicht ins Spiel.\nWir sehen uns doch hoffentlich später noch in der Bar, oder? Zumindest die Feierabende muss ich noch genießen, bevor es für mich wieder zurück auf Mutter Erde geht. Also bis später dann.", "Schön, dass du nicht groß versuchst mich davon abzuhalten. John hat vorher schon große Reden darüber geschwungen, dass das unverantwortlich sei und so weiter. Der soll sich um seinen eigenen Kram kümmern.\nWir sehen uns doch hoffentlich später noch in der Bar, oder? Zumindest die Feierabende muss ich noch genießen, bevor es für mich wieder zurück auf Mutter Erde geht. Also bis später dann.", "ENDE", "ENDE"};
        answers1 = new string[] { "Alles klar bei dir?", "Was kostet denn ein Lächeln bei dir?", "Stell ich mir frustrierend vor", "Es hört nie auf, was?", "Ach ja?", "Das Leben hat doch noch so viel zu bieten", "Bist du dir da auch sicher?", "Na, dann viel Erfolg", "", "" };
        retalk1 = new string[] { "Habe ich im Lagerraum schon… Ah fuck ist ja eh egal", "Nur noch wenige Wochen, Miles, dann wird alles anders", "Vergiss nicht die Bar später", "Ich sollte mal wieder das Wischwasser wechseln…" };

        day2 = new string[] { "Oh hey, Mann wie geht’s?","", "Merkt man es mir so deutlich an? Ja, ich bin ganz ehrlich, dieser Job macht mich noch wahnsinnig. Du wischst einen Raum und schon bevor du den nächsten erreichst, ist der letzte wieder dreckig.", "Merkt man es mir so deutlich an? Ja, ich bin ganz ehrlich, dieser Job macht mich noch wahnsinnig. Du wischst einen Raum und schon bevor du den nächsten erreichst, ist der letzte wieder dreckig.", "Wie sollte es besser werden? Dieser Job ist eine Sackgasse, wenn du mich fragst. Meine Kündigung habe ich schon geschrieben. Sie muss nur noch verschickt werden…", "Braucht es denn noch mehr? Freut mich ja, wenn dein Job so voller Abwechslung ist, aber das ständige Aufwischen und Aufräumen nervt schon auf Dauer… Die Kündigung habe ich schon getippt…", "Ich habe meine Kündigung doch heute Morgen erst getippt. Gleich nach Feierabend gehe ich damit zur Mailkammer und lasse mein Schreiben verschicken. Keine Ahnung, warum die hier im Jahre 2079 noch keine Internetverbindung hinkriegen, aber hey, so ein Brief hat ja auch was. Richtig Retro.", "Ich habe meine Kündigung doch heute Morgen erst getippt. Gleich nach Feierabend gehe ich damit zur Mailkammer und lasse mein Schreiben verschicken. Keine Ahnung, warum die hier im Jahre 2079 noch keine Internetverbindung hinkriegen, aber hey, so ein Brief hat ja auch was. Richtig Retro.", "Naja, also wenn du den Brief für mich abschicken könntest, wäre das natürlich optimal. Dann kann ich auch nicht kurz vorher noch den Schwanz einziehen. Aus dem Auge aus dem Sinn sagt man ja so schön.\n Wir sehen uns doch hoffentlich später noch in der Bar, oder? Zumindest die Feierabende muss ich noch genießen, bevor es für mich wieder zurück auf Mutter Erde geht. Also bis später dann.","Aber... wenn du den Brief für mich abschicken könntest, wäre das natürlich optimal. Dann kann ich auch nicht kurz vorher noch den Schwanz einziehen. Aus dem Auge aus dem Sinn sagt man ja so schön.\n Wir sehen uns doch hoffentlich später noch in der Bar, oder? Zumindest die Feierabende muss ich noch genießen, bevor es für mich wieder zurück auf Mutter Erde geht. Also bis später dann.","","" };
        answers2 = new string[] { "Alles klar bei dir?", "Immer noch schlecht drauf?", "Immer noch so schlimm?", "Sonst nichts?", "Hattest du nicht schon eine?", "Gestern nicht getraut sie wegzuschicken?", "Brauchst du Hilfe?", "Sachen gibt's...", "", "" };
    }
    // Update is called once per frame
    void Update()
    {

    }
}
