using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class HildeDialogue : MonoBehaviour
{
    [HideInInspector] public string[] day1;
    [HideInInspector] public string[] answers1;
    [HideInInspector] public string[] day2;
    [HideInInspector] public string[] answers2;

    [HideInInspector] public string[] retalk1;

    public AudioClip day1line1, day1line2, day1line3, day1line4, day1line5, day1line6, day1line7, day1line8, day1line9, day1retalk1, day1retalk2, day1retalk3, day1retalk4, bye;
    [HideInInspector] public AudioClip[] day1Audio;
    [HideInInspector] public AudioClip[] retalk1Audio;

    // Start is called before the first frame update
    void Start()
    {
    }

    void Awake()
    {
        day1Audio = new AudioClip[] { day1line1, null, day1line2, day1line3, day1line4, day1line4, day1line5, day1line6, day1line7, day1line8, day1line9, day1line9, null, null };
        retalk1Audio = new AudioClip[] { day1retalk1, day1retalk2, day1retalk3, day1retalk4 };

        day1 = new string[] {"Hach Conrad, wie schön dich auch mal wieder zu sprechen. Lass dich doch mal ansehen, du kommst ja viel zu selten aus deiner Kajüte…","", "Tatsächlich bin ich mit meinen Aufgaben schon fe-her-tig. Hier siehst du? Alles abgehackt, ich habe frei.", "Immer mit der Ruhe, ich bin mit meinen Aufgaben schon fertig. Hier siehst du? Alles abgehackt, ich habe frei.", "Naja, ich sag dir ganz ehrlich: Dieses Chip-Getue ist doch einfach nur überflüssig. Früher hatte man in Fabriken noch so ein… na wie hieß das doch gleich… Klemmbrett!\nGenau diese hölzernen Bretter mit einem Blatt Papier darauf. Da gabs auch keine Updates oder Fehler oder sonst einen Firlefanz. Einfach nur einen Stift und ein gedrucktes Formular. Hach, waren das Zeiten…", "Naja, ich sag dir ganz ehrlich: Dieses Chip-Getue ist doch einfach nur überflüssig. Früher hatte man in Fabriken noch so ein… na wie hieß das doch gleich… Klemmbrett!\nGenau diese hölzernen Bretter mit einem Blatt Papier darauf. Da gabs auch keine Updates oder Fehler oder sonst einen Firlefanz. Einfach nur einen Stift und ein gedrucktes Formular. Hach, waren das Zeiten…", "Ja so sehe ich das auch. Heutzutage wird ja alles digital erledigt und hätte man hier nicht vertraglich diesen Dingern im Kopf zustimmen müssen, könnte ich auf diese Chips auch ganz verzichten.", "Na also, hör mal, für wie alt hältst du mich? In meiner Kindheit gabs auch schon Smartphones und Tabletts oder wie das alles hieß. Bunt leuchtende Geräte mit Filmen und allem drum und dran. Habe ich schon immer gehasst.", "Ich war noch nie ein Freund von diesen undurchsichtigen Maschen dieser ganzen Konzerne. Ein Datenleck hier, eine Panne dort und wofür die deine ganzen Daten benutzen, weiß der Kuckuck. Erst recht bei diesen Chip-Dingern.", "Meinst du wirklich? Na, dann erzähl doch mal, was mit deinen Daten so passiert. Kannst du nicht, oder? Hatte ich mir fast gedacht. Und dann diese Chips. Weiß Gott was die mit uns anstellen.", "Wenn du mich fragst, nutzen die die Dinger doch für Gedankenkontrolle oder wollen auf unsere Interessen zugreifen.Zack taucht die nächste perfekt zugeschnittene Werbung in meiner Kammer auf.Faltencreme wollten die mir andrehen, als ob ich die nötig hätte.Naja, also ich werde mich jetzt auf jeden Fall erst mal aufs Ohr legen, um bei unserer Feierabendfete nicht wieder einzupennen.Wir sehen uns, ja?", "Wenn du mich fragst, nutzen die die Dinger doch für Gedankenkontrolle oder wollen auf unsere Interessen zugreifen. Zack taucht die nächste perfekt zugeschnittene Werbung in meiner Kammer auf. Faltencreme wollten die mir andrehen, als ob ich die nötig hätte. Naja, also ich werde mich jetzt auf jeden Fall erst mal aufs Ohr legen, um bei unserer Feierabendfete nicht wieder einzupennen. Wir sehen uns, ja?", "ENDE","ENDE"};
        retalk1 = new string[] { "Alles Humbug…", "Was? Nein ich will nicht wissen, welche Promis was haben machen lassen.", "Ich kann die Rente kaum erwarten", "Immer mit der Ruhe, du Jungspund" };
        answers1 = new string[] { "Hast du heute frei?", "Nichts zu tun?", "Ist dein Chip mal wieder defekt?", "Sogar ganz altmodisch auf dem Papier", "Die Technik kann schon etwas viel sein…", "Die Dinos waren aber schon ausgestorben, oder?", "Und woran liegt das?", "Du übertreibst...", "So schlimm können die doch gar nicht sein.", "Du und deine Geschichten...","",""};

        day2 = new string[] {"Hach Conrad, wie schön dich auch mal wieder zu sprechen. Lass dich doch mal ansehen, du kommst ja viel zu selten aus deiner Kajüte…","", "Ach, haben wir das? Ich hätte schwören können, wir hätten uns schon seit einer ganzen Weile nicht mehr gesehen. Jedenfalls bin ich mit meinen Aufgaben schon fe-her-tig. Hier siehst du? Alles abgehackt, ich habe frei.", "Gestern? Hmmm... Ich hätte schwören können, wir hätten uns schon seit einer ganzen Weile nicht mehr gesehen. Jedenfalls bin ich mit meinen Aufgaben schon fertig. Hier siehst du? Alles abgehackt, ich habe frei.", "Naja, ich sag dir ganz ehrlich: Dieses Chip-Getue ist doch einfach nur überflüssig. Früher hatte man in Fabriken noch so ein… nah wie hieß das doch gleich… Klemmbrett!\nGenau diese hölzernen Bretter mit einem Blatt Papier darauf. Da gabs auch keine Updates oder Fehler oder sonst einen Firlefanz. Einfach nur einen Stift und ein gedrucktes Formular. Hach, waren das Zeiten…", "So schlimm ist es noch nicht. Dieses Chip-Getue ist doch einfach nur überflüssig. Früher hatte man in Fabriken noch so ein… nah wie hieß das doch gleich… Klemmbrett!\nGenau diese hölzernen Bretter mit einem Blatt Papier darauf. Da gabs auch keine Updates oder Fehler oder sonst einen Firlefanz. Einfach nur einen Stift und ein gedrucktes Formular. Hach, waren das Zeiten…", "Hätte man hier nicht vertraglich diesen Dingern im Kopf zustimmen müssen, könnte ich auf diese Chips auch ganz verzichten.So richtig komme ich mit denen auch nicht klar…", "In meiner Kindheit gabs auch schon Smartphones und Tablets oder wie das alles hieß. Bunt leuchtende Geräte mit Filmen und allem drum und dran. Habe ich schon immer gehasst.", "Dinge sehen? Pfff, würde mich nicht wundern, wenn diese Dinger auch unsere Optik beeinflussen.\nWenn du mich fragst, nutzen die die Dinger doch für Gedankenkontrolle oder wollen auf unsere Interessen zugreifen. Zack taucht die nächste perfekt zugeschnittene Werbung in meiner Kammer auf. Faltencreme wollten die mir andrehen, als ob ich die nötig hätte. Naja, also ich werde mich jetzt auf jeden Fall erst mal aufs Ohr legen, um bei unserer Feierabendfete nicht wieder einzupennen. Wir sehen uns, ja?", "Ach was heißt schon kaputt. Ich weiß ja nicht einmal was die Dinger im funktionsfähigen Zustand überhaupt machen.\nWenn du mich fragst, nutzen die die Dinger doch für Gedankenkontrolle oder wollen auf unsere Interessen zugreifen. Zack taucht die nächste perfekt zugeschnittene Werbung in meiner Kammer auf. Faltencreme wollten die mir andrehen, als ob ich die nötig hätte. Naja, also ich werde mich jetzt auf jeden Fall erst mal aufs Ohr legen, um bei unserer Feierabendfete nicht wieder einzupennen. Wir sehen uns, ja?","ENDE","ENDE"};
        answers2 = new string[] { "Haben wir uns nicht neulich gesprochen?", "Abgesehen von Gestern…", "Ist dein Chip immer noch defekt?", "Langsam wird es eine richtige Papierflut, hm?", "Ich habe heute auch Probleme mit dem Ding…", "Moderne Technik hat aber auch ihre Vorteile…", "Ich sehe… Dinge…","Ist dein Chip auch kaputt?","",""};
    }
    // Update is called once per frame
    void Update()
    {

    }
}
