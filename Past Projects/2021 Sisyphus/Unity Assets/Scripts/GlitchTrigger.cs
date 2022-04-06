using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class GlitchTrigger : MonoBehaviour
{
    private bool _is_first_contact;

    private void Start()
    {
        _is_first_contact = true;
    }

    public bool is_first_contact
    {
        get 
        {
            bool value = _is_first_contact;
            _is_first_contact = false;
            return value; 
        }
    }

}
