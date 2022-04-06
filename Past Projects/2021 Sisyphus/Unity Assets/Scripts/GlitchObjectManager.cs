using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class GlitchObjectManager : MonoBehaviour
{

    public List<GameObject> real_objects;
    public List<GameObject> illusion_objects;

    // Start is called before the first frame update
    void Start()
    {
        
    }

    // Update is called once per frame
    void Update()
    {
        foreach (GameObject obj in real_objects)
        {
            obj.SetActive(GameState.Instance.is_glitched);
        }
        foreach (GameObject obj in illusion_objects)
        {
            obj.SetActive(!GameState.Instance.is_glitched);
        }
    }
}
