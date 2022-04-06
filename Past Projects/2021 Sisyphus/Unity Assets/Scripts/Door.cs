using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Door : MonoBehaviour
{

    public float animation_speed = 4;
    public float time_to_close = 10;

    private Vector3 close_position;
    private Vector3 open_position;

    public bool is_opening = false;
    private float time_since_open = 0;
    private bool playedSound = false;

    public void Open()
    {
        is_opening = true;
        time_since_open = 0;
        gameObject.GetComponent<AudioSource>().PlayOneShot(gameObject.GetComponent<AudioSource>().clip, 1.0f);
        playedSound = false;
    }

    private void PlayCloseSound()
    {
        gameObject.GetComponent<AudioSource>().PlayOneShot(gameObject.GetComponent<AudioSource>().clip, 1.0f);
        playedSound = true;
    }

    void OnCollisionStay(Collision collision)
    {
        if (collision.collider.tag == "Player")
        {
            if (Vector3.Distance(transform.position, close_position) > 1.5) 
            { 
                Open();
            }
        }
    }

    // Start is called before the first frame update
    void Start()
    {
        close_position = transform.position;
        open_position = close_position;
        open_position.y += 4.7f;
    }

    // Update is called once per frame
    void Update()
    {
        float step = animation_speed * Time.deltaTime;

        time_since_open += Time.deltaTime;
        if (time_since_open > time_to_close)
        {
            is_opening = false;
        }

        if (is_opening) 
        { 
            // open
            transform.position = Vector3.MoveTowards(transform.position, open_position, step);
        }
        else if (!PlayerController.inCutscene)
        {
            // close
            if (!playedSound) { PlayCloseSound(); }
            transform.position = Vector3.MoveTowards(transform.position, close_position, step);
        }
    }
}
