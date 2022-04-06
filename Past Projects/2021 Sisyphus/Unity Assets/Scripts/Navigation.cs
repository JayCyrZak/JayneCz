using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Navigation : MonoBehaviour
{
    [HideInInspector] public bool isTarget;
    public string comingNPC;

    [HideInInspector] public bool justVisited = false;
    // Start is called before the first frame update
    void Start()
    {
        
    }

     // Update is called once per frame
        void Update()
    {
        if (justVisited)
        {   
            justVisited = false;

            if ((gameObject.name == "Toilet1")||(gameObject.name == "Toilet2"))
            {
                gameObject.GetComponent<AudioSource>().Play();
            }
        }
    }
}
